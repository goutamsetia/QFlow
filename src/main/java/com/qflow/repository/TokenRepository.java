package com.qflow.repository;

import com.qflow.model.Token;
import com.qflow.model.enums.TokenStatus;
import java.util.List;
import java.util.Optional;

/**
 * Integration repository contract for Token persistence (Owned by Member 3).
 * 
 * Invoked by Member 1's TokenService for persisting newly issued tokens,
 * querying status by token number, and updating cancellation status.
 */
public interface TokenRepository {

    Token save(Token token);

    Optional<Token> findById(Long id);

    Optional<Token> findByTokenNumber(String tokenNumber);

    List<Token> findWaitingTokensByService(Long serviceId);

    List<Token> findTokensByCustomer(Long customerId);

    boolean updateStatus(Long tokenId, TokenStatus newStatus);
}
