package com.qflow.service;

import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.dto.TokenSlipDTO;
import com.qflow.model.dto.TokenStatusDTO;

/**
 * Service contract for Token issuance, status inquiry, and cancellation (Owned by Member 1).
 */
public interface TokenService {

    /**
     * Issues a new service token:
     * 1. Validates customer and service existence.
     * 2. Evaluates scheduling priority using business rules.
     * 3. Allocates atomic daily sequence via DailySequenceRepository.
     * 4. Persists the Token record with WAITING status via TokenRepository.
     * 5. Enqueues the persisted token into QueueManager.
     * 6. Returns a populated TokenSlipDTO with queue metrics.
     * 
     * @param request token request parameters
     * @return populated TokenSlipDTO for customer printing / display
     * @throws com.qflow.exception.ValidationException if validation or existence checks fail
     */
    TokenSlipDTO issueToken(TokenRequestDTO request);

    /**
     * Queries the live status, attending counter, and wait metrics for a token.
     * 
     * @param tokenNumber unique business token identifier (e.g. "TKN-ACC-20261118-0001")
     * @return TokenStatusDTO with live status details
     * @throws com.qflow.exception.ValidationException if token is not found
     */
    TokenStatusDTO queryStatus(String tokenNumber);

    /**
     * Cancels an eligible token if it is currently in WAITING status.
     * 
     * @param tokenNumber unique business token identifier
     * @return true if successfully cancelled; false if token was already terminal/serving
     * @throws com.qflow.exception.ValidationException if token is not found
     */
    boolean cancelToken(String tokenNumber);
}
