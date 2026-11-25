package com.qflow.repository;

import com.qflow.model.ServiceCategory;
import java.util.List;
import java.util.Optional;

/**
 * Integration repository contract for service category metadata (Owned by Member 3).
 */
public interface ServiceRepository {

    Optional<ServiceCategory> findById(Long id);

    Optional<ServiceCategory> findByCode(String serviceCode);

    List<ServiceCategory> findAllActive();
}
