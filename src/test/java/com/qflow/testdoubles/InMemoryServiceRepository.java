package com.qflow.testdoubles;

import com.qflow.model.ServiceCategory;
import com.qflow.repository.ServiceRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory test double for ServiceRepository pre-loaded with standard college departments.
 */
public class InMemoryServiceRepository implements ServiceRepository {

    private final Map<Long, ServiceCategory> services = new HashMap<>();

    public InMemoryServiceRepository() {
        seedDefaults();
    }

    public void seedDefaults() {
        services.clear();
        services.put(1L, new ServiceCategory(1L, "REG", "Registrar & Transcripts", 12));
        services.put(2L, new ServiceCategory(2L, "ACC", "Accounts & Fee Payments", 8));
        services.put(3L, new ServiceCategory(3L, "ADM", "Admissions & Verification", 15));
        services.put(4L, new ServiceCategory(4L, "EXM", "Examinations & Grade Cards", 10));
    }

    public void addService(ServiceCategory service) {
        services.put(service.getId(), service);
    }

    @Override
    public Optional<ServiceCategory> findById(Long id) {
        return Optional.ofNullable(services.get(id));
    }

    @Override
    public Optional<ServiceCategory> findByCode(String serviceCode) {
        if (serviceCode == null) return Optional.empty();
        return services.values().stream()
                .filter(s -> serviceCode.equalsIgnoreCase(s.getServiceCode()))
                .findFirst();
    }

    @Override
    public List<ServiceCategory> findAllActive() {
        return services.values().stream()
                .filter(ServiceCategory::isActive)
                .collect(Collectors.toList());
    }
}
