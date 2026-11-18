package com.qflow.service;

import com.qflow.exception.ValidationException;
import com.qflow.model.Customer;
import com.qflow.model.ServiceCategory;
import com.qflow.model.Token;
import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.dto.TokenSlipDTO;
import com.qflow.model.dto.TokenStatusDTO;
import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import com.qflow.queue.QueueManager;
import com.qflow.repository.CustomerRepository;
import com.qflow.repository.DailySequenceRepository;
import com.qflow.repository.ServiceRepository;
import com.qflow.repository.TokenRepository;
import com.qflow.util.InputValidator;
import com.qflow.util.TokenGenerator;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Production implementation of TokenService (Member 1).
 * 
 * Orchestrates token issuance, priority evaluation, persistent sequence formatting,
 * persistence, queue handoff, status queries, and cancellations.
 */
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final DailySequenceRepository dailySequenceRepository;
    private final QueueManager queueManager;

    public TokenServiceImpl(TokenRepository tokenRepository,
                            CustomerRepository customerRepository,
                            ServiceRepository serviceRepository,
                            DailySequenceRepository dailySequenceRepository,
                            QueueManager queueManager) {
        this.tokenRepository = Objects.requireNonNull(tokenRepository, "TokenRepository cannot be null.");
        this.customerRepository = Objects.requireNonNull(customerRepository, "CustomerRepository cannot be null.");
        this.serviceRepository = Objects.requireNonNull(serviceRepository, "ServiceRepository cannot be null.");
        this.dailySequenceRepository = Objects.requireNonNull(dailySequenceRepository, "DailySequenceRepository cannot be null.");
        this.queueManager = Objects.requireNonNull(queueManager, "QueueManager cannot be null.");
    }

    @Override
    public TokenSlipDTO issueToken(TokenRequestDTO request) {
        if (request == null) {
            throw new ValidationException("TokenRequestDTO cannot be null.");
        }

        // 1. Validate Customer ID & ensure customer exists
        Long customerId = InputValidator.validateId(request.getCustomerId(), "customerId");
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ValidationException("customerId", "Customer not found with ID: " + customerId));

        // 2. Validate Service ID & ensure service exists and is active
        Long serviceId = InputValidator.validateId(request.getServiceId(), "serviceId");
        ServiceCategory service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ValidationException("serviceId", "Service category not found with ID: " + serviceId));

        if (!service.isActive()) {
            throw new ValidationException("serviceId", "Requested service department is currently inactive: " + service.getServiceName());
        }

        // 3. Priority Determination (enforces rules & staff emergency authorization)
        Priority priority = InputValidator.determinePriority(customer, request);

        // 4. Allocate persistent daily sequence number from database
        LocalDate today = LocalDate.now();
        int sequenceNum = dailySequenceRepository.getNextDailySequence(service.getId(), today);

        // 5. Format human-readable business token number (TKN-<CODE>-<YYYYMMDD>-<SEQ>)
        String tokenNumber = TokenGenerator.formatTokenNumber(service.getServiceCode(), today, sequenceNum);

        // 6. Instantiate and persist Token record with initial WAITING status
        Token token = new Token(tokenNumber, customer.getId(), service.getId(), priority);
        Token persistedToken = tokenRepository.save(token);

        // 7. Handoff to Member 2's in-memory QueueManager
        queueManager.enqueue(persistedToken);

        // 8. Retrieve queue metrics from QueueManager for token slip printing
        int positionAhead = queueManager.getPositionAhead(persistedToken);
        int estimatedWaitMinutes = queueManager.getEstimatedWaitMinutes(persistedToken);

        return new TokenSlipDTO(
                persistedToken.getTokenNumber(),
                customer.getFullName(),
                service.getServiceName(),
                priority,
                positionAhead,
                estimatedWaitMinutes,
                persistedToken.getCreatedAt()
        );
    }

    @Override
    public TokenStatusDTO queryStatus(String tokenNumber) {
        if (tokenNumber == null || tokenNumber.trim().isEmpty()) {
            throw new ValidationException("tokenNumber", "Token number cannot be null or empty.");
        }
        String cleanTokenNum = tokenNumber.trim();

        Token token = tokenRepository.findByTokenNumber(cleanTokenNum)
                .orElseThrow(() -> new ValidationException("tokenNumber", "Token not found: " + cleanTokenNum));

        int positionAhead = 0;
        int estimatedWait = 0;
        Integer counterNum = token.getCounterId() != null ? token.getCounterId().intValue() : null;
        String operatorName = null;

        if (token.getStatus() == TokenStatus.WAITING) {
            positionAhead = queueManager.getPositionAhead(token);
            estimatedWait = queueManager.getEstimatedWaitMinutes(token);
        } else if (token.getStatus() == TokenStatus.SERVING) {
            operatorName = "Counter Desk " + counterNum;
        }

        return new TokenStatusDTO(
                token.getTokenNumber(),
                token.getStatus(),
                counterNum,
                operatorName,
                positionAhead,
                estimatedWait
        );
    }

    @Override
    public boolean cancelToken(String tokenNumber) {
        if (tokenNumber == null || tokenNumber.trim().isEmpty()) {
            throw new ValidationException("tokenNumber", "Token number cannot be null or empty.");
        }
        String cleanTokenNum = tokenNumber.trim();

        Token token = tokenRepository.findByTokenNumber(cleanTokenNum)
                .orElseThrow(() -> new ValidationException("tokenNumber", "Token not found: " + cleanTokenNum));

        // Only tokens in WAITING state can be cancelled by the customer
        if (token.getStatus() != TokenStatus.WAITING) {
            return false;
        }

        // Cancel from active in-memory queue
        queueManager.cancel(token);

        // Update persistent status
        token.setStatus(TokenStatus.CANCELLED);
        tokenRepository.updateStatus(token.getId(), TokenStatus.CANCELLED);

        return true;
    }
}
