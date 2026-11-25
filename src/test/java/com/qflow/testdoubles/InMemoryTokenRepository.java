package com.qflow.testdoubles;

import com.qflow.model.Token;
import com.qflow.model.enums.TokenStatus;
import com.qflow.repository.TokenRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory test double for TokenRepository.
 */
public class InMemoryTokenRepository implements TokenRepository {

    private final Map<Long, Token> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Token save(Token token) {
        if (token.getId() == null) {
            token.setId(idGenerator.getAndIncrement());
        }
        storage.put(token.getId(), token);
        return token;
    }

    @Override
    public Optional<Token> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Token> findByTokenNumber(String tokenNumber) {
        return storage.values().stream()
                .filter(t -> Objects.equals(t.getTokenNumber(), tokenNumber))
                .findFirst();
    }

    @Override
    public List<Token> findWaitingTokensByService(Long serviceId) {
        return storage.values().stream()
                .filter(t -> Objects.equals(t.getServiceId(), serviceId) && t.getStatus() == TokenStatus.WAITING)
                .collect(Collectors.toList());
    }

    @Override
    public List<Token> findTokensByCustomer(Long customerId) {
        return storage.values().stream()
                .filter(t -> Objects.equals(t.getCustomerId(), customerId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(Long tokenId, TokenStatus newStatus) {
        Token token = storage.get(tokenId);
        if (token != null) {
            token.setStatus(newStatus);
            return true;
        }
        return false;
    }

    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
