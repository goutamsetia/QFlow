# QFlow – Integration Contracts & Shared Domain Specifications
## Phase 3: Member 1 (Customer & Token Management) Contract Specification (Version 1.1 - FROZEN)

**Document Version:** 1.1.0  
**Module Owner:** Member 1 (Customer & Token Management)  
**Status:** FROZEN – INCORPORATED MEMBER 1 REVISIONS  

---

## 1. Domain Enums

### 1.1 `Priority` (`com.qflow.model.enums.Priority`)
```java
package com.qflow.model.enums;

public enum Priority {
    EMERGENCY(1, "Emergency"),
    SENIOR_CITIZEN(2, "Senior Citizen"),
    NORMAL(3, "Normal");

    private final int level;
    private final String displayName;

    Priority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

### 1.2 `TokenStatus` (`com.qflow.model.enums.TokenStatus`)
```java
package com.qflow.model.enums;

public enum TokenStatus {
    WAITING("Waiting in Queue"),
    SERVING("Currently Being Served"),
    COMPLETED("Service Completed"),
    SKIPPED("Customer Absent / Skipped"),
    CANCELLED("Cancelled by Customer"),
    INTERRUPTED("Service Interrupted (Pending Triage)");

    private final String description;

    TokenStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Identifies if a token has reached a permanent end state.
     * Note: INTERRUPTED is strictly non-terminal as it requires Administrator triage.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == SKIPPED || this == CANCELLED;
    }
}
```

### 1.3 `CustomerCategory` (`com.qflow.model.enums.CustomerCategory`)
```java
package com.qflow.model.enums;

public enum CustomerCategory {
    STUDENT,
    STAFF,
    SENIOR_CITIZEN,
    GUEST
}
```

---

## 2. Finalized Domain Models (Field-by-Field Design)

### 2.1 `Customer` (`com.qflow.model.Customer`)
Pure domain entity. Represents a student, staff member, senior citizen, or visitor.
- **Lookup Key:** `phoneNumber` is the primary human-facing lookup key.
- **Optional Email:** `email` is optional (nullable or empty allowed).

```java
package com.qflow.model;

import com.qflow.model.enums.CustomerCategory;
import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {
    private Long id;                     // DB generated primary key
    private String customerCode;         // Business ID: e.g. "CUST-2026-0001"
    private String fullName;             // Required, 2-100 characters
    private String phoneNumber;          // Required, exactly 10 digits, unique lookup key
    private String email;                // Optional (nullable), validated if present
    private int age;                     // Required, 16-120
    private CustomerCategory category;   // Required (STUDENT, STAFF, SENIOR_CITIZEN, GUEST)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Customer() {}

    public Customer(String customerCode, String fullName, String phoneNumber, 
                    String email, int age, CustomerCategory category) {
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Customer(Long id, String customerCode, String fullName, String phoneNumber, 
                    String email, int age, CustomerCategory category, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerCode = customerCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public CustomerCategory getCategory() { return category; }
    public void setCategory(CustomerCategory category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(phoneNumber, customer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", customerCode='" + customerCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", category=" + category +
                '}';
    }
}
```

---

### 2.2 `Token` (`com.qflow.model.Token`)
Clean domain entity.
*Note: Queue-internal scheduling metadata (`arrivalSequence`, `turnsSkipped`) have been removed from Member 1's entity.* Scheduling wrappers are owned by Member 2.

```java
package com.qflow.model;

import com.qflow.model.enums.Priority;
import com.qflow.model.enums.TokenStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class Token {
    private Long id;                     // DB generated primary key
    private String tokenNumber;          // e.g. "TKN-ACC-20261118-0001"
    private Long customerId;             // Foreign Key to Customer
    private Long serviceId;              // Foreign Key to Service (1=REG, 2=ACC, etc.)
    private Long counterId;              // Nullable until claimed by counter
    private Priority priority;           // EMERGENCY, SENIOR_CITIZEN, NORMAL
    private TokenStatus status;          // WAITING, SERVING, etc.
    private LocalDateTime createdAt;
    private LocalDateTime servedAt;
    private LocalDateTime completedAt;

    // Constructors
    public Token() {}

    public Token(String tokenNumber, Long customerId, Long serviceId, Priority priority) {
        this.tokenNumber = tokenNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.priority = priority;
        this.status = TokenStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public Token(Long id, String tokenNumber, Long customerId, Long serviceId, Long counterId,
                 Priority priority, TokenStatus status, LocalDateTime createdAt,
                 LocalDateTime servedAt, LocalDateTime completedAt) {
        this.id = id;
        this.tokenNumber = tokenNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.counterId = counterId;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.servedAt = servedAt;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getCounterId() { return counterId; }
    public void setCounterId(Long counterId) { this.counterId = counterId; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getServedAt() { return servedAt; }
    public void setServedAt(LocalDateTime servedAt) { this.servedAt = servedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return Objects.equals(tokenNumber, token.tokenNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenNumber);
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", tokenNumber='" + tokenNumber + '\'' +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
}
```

---

## 3. Data Transfer Objects (DTOs)

### 3.1 `CustomerRegistrationDTO`
```java
package com.qflow.model.dto;

import com.qflow.model.enums.CustomerCategory;

public class CustomerRegistrationDTO {
    private final String fullName;
    private final String phoneNumber;
    private final String email;          // Optional (can be null/empty)
    private final int age;
    private final CustomerCategory category;

    public CustomerRegistrationDTO(String fullName, String phoneNumber, String email, 
                                   int age, CustomerCategory category) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.category = category;
    }

    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public CustomerCategory getCategory() { return category; }
}
```

### 3.2 `TokenRequestDTO`
Prevents customers from self-granting emergency priority. Emergency requires staff authorization.
```java
package com.qflow.model.dto;

public class TokenRequestDTO {
    private final Long customerId;
    private final Long serviceId;
    private final boolean emergencyAuthorized; // Must be false for kiosk; true only if staff override
    private final String staffAuthCode;        // Optional code/reason if emergencyAuthorized is true

    // Factory method for standard customer kiosk request (emergency strictly forbidden)
    public static TokenRequestDTO forKiosk(Long customerId, Long serviceId) {
        return new TokenRequestDTO(customerId, serviceId, false, null);
    }

    // Factory method for authorized staff override
    public static TokenRequestDTO forStaffOverride(Long customerId, Long serviceId, String authCode) {
        return new TokenRequestDTO(customerId, serviceId, true, authCode);
    }

    public TokenRequestDTO(Long customerId, Long serviceId, boolean emergencyAuthorized, String staffAuthCode) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.emergencyAuthorized = emergencyAuthorized;
        this.staffAuthCode = staffAuthCode;
    }

    public Long getCustomerId() { return customerId; }
    public Long getServiceId() { return serviceId; }
    public boolean isEmergencyAuthorized() { return emergencyAuthorized; }
    public String getStaffAuthCode() { return staffAuthCode; }
}
```

### 3.3 `TokenSlipDTO` (Output for Printing / Display)
```java
package com.qflow.model.dto;

import com.qflow.model.enums.Priority;
import java.time.LocalDateTime;

public class TokenSlipDTO {
    private final String tokenNumber;
    private final String customerName;
    private final String serviceName;
    private final Priority priority;
    private final int queuePositionAhead;
    private final int estimatedWaitMinutes;
    private final LocalDateTime issuedAt;

    public TokenSlipDTO(String tokenNumber, String customerName, String serviceName,
                        Priority priority, int queuePositionAhead, int estimatedWaitMinutes,
                        LocalDateTime issuedAt) {
        this.tokenNumber = tokenNumber;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.priority = priority;
        this.queuePositionAhead = queuePositionAhead;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.issuedAt = issuedAt;
    }

    public String getTokenNumber() { return tokenNumber; }
    public String getCustomerName() { return customerName; }
    public String getServiceName() { return serviceName; }
    public Priority getPriority() { return priority; }
    public int getQueuePositionAhead() { return queuePositionAhead; }
    public int getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
}
```

### 3.4 `TokenStatusDTO` (Output for Status Inquiry)
```java
package com.qflow.model.dto;

import com.qflow.model.enums.TokenStatus;

public class TokenStatusDTO {
    private final String tokenNumber;
    private final TokenStatus status;
    private final Integer counterNumber;       // Nullable if still waiting
    private final String operatorName;         // Nullable if still waiting
    private final int queuePositionAhead;
    private final int estimatedWaitMinutes;

    public TokenStatusDTO(String tokenNumber, TokenStatus status, Integer counterNumber,
                          String operatorName, int queuePositionAhead, int estimatedWaitMinutes) {
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.counterNumber = counterNumber;
        this.operatorName = operatorName;
        this.queuePositionAhead = queuePositionAhead;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public String getTokenNumber() { return tokenNumber; }
    public TokenStatus getStatus() { return status; }
    public Integer getCounterNumber() { return counterNumber; }
    public String getOperatorName() { return operatorName; }
    public int getQueuePositionAhead() { return queuePositionAhead; }
    public int getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
}
```

---

## 4. Business Validation & Priority Inference (`InputValidator`)

Located in `com.qflow.util.InputValidator`:

### Priority Decision Rules:
1. **Rule 1 (Senior Citizen Priority):** If customer `age >= 60` OR customer `category == CustomerCategory.SENIOR_CITIZEN`, assign **`Priority.SENIOR_CITIZEN`**.
2. **Rule 2 (Emergency Priority):** Assigned **ONLY IF** `request.isEmergencyAuthorized() == true` AND `staffAuthCode` is valid/non-empty. Self-service customers can NEVER receive `EMERGENCY`.
3. **Rule 3 (Normal Priority):** All other customers receive **`Priority.NORMAL`**.

```java
public static Priority determinePriority(Customer customer, TokenRequestDTO request) {
    if (request.isEmergencyAuthorized()) {
        if (request.getStaffAuthCode() == null || request.getStaffAuthCode().trim().isEmpty()) {
            throw new ValidationException("Staff authorization code required for Emergency priority.");
        }
        return Priority.EMERGENCY;
    }
    if (customer.getAge() >= 60 || customer.getCategory() == CustomerCategory.SENIOR_CITIZEN) {
        return Priority.SENIOR_CITIZEN;
    }
    return Priority.NORMAL;
}
```

### Input Validation Rules:
- `validateName(String name)`: Must be non-null, length between 2 and 100, matches `^[a-zA-Z\\s.\\-']+$`.
- `validatePhoneNumber(String phone)`: Must be non-null, exactly 10 digits, matches `^[6-9]\\d{9}$`.
- `validateEmail(String email)`: Optional! If null or trimmed empty, passes. If non-empty, matches `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`.
- `validateAge(int age)`: Must be between 16 and 120.

---

## 5. Token Formatting Utility (`TokenGenerator`)

Located in `com.qflow.util.TokenGenerator`:
- `formatCustomerCode(long customerId, int year)`:
  $$\text{Code} = \text{"CUST-" + year + "-" + String.format("%04d", customerId)}$$
- `formatTokenNumber(String serviceCode, LocalDate date, int dailySequence)`:
  $$\text{TokenNumber} = \text{"TKN-" + serviceCode + "-" + date.format(BASIC\_ISO\_DATE) + "-" + String.format("%04d", dailySequence)}$$
  *(e.g., `TKN-ACC-20261118-0001`).*

---

## 6. Member 1 Service Contracts (Clean, Zero `Connection` Leaks)

Notice: **Zero `java.sql.Connection` parameters!** Services interact strictly through clean repository abstractions.

### 6.1 `CustomerService` (`com.qflow.service.CustomerService`)
```java
package com.qflow.service;

import com.qflow.model.Customer;
import com.qflow.model.dto.CustomerRegistrationDTO;
import java.util.Optional;

public interface CustomerService {
    /**
     * Registers a new customer or returns the existing customer record if phone matches.
     * Validates input fields and handles optional email.
     */
    Customer registerOrGetCustomer(CustomerRegistrationDTO dto);

    Optional<Customer> findByPhone(String phoneNumber);
    Optional<Customer> findById(Long customerId);
}
```

### 6.2 `TokenService` (`com.qflow.service.TokenService`)
```java
package com.qflow.service;

import com.qflow.model.dto.TokenRequestDTO;
import com.qflow.model.dto.TokenSlipDTO;
import com.qflow.model.dto.TokenStatusDTO;

public interface TokenService {
    /**
     * Issues a token:
     * 1. Looks up customer and service.
     * 2. Evaluates priority using determinePriority rule.
     * 3. Allocates daily sequence via DailySequenceRepository.
     * 4. Persists token via TokenRepository.
     * 5. Handsoff to QueueManager.enqueue(token).
     * 6. Returns populated TokenSlipDTO.
     */
    TokenSlipDTO issueToken(TokenRequestDTO request);

    /**
     * Queries status, position, and wait time for a given token number.
     */
    TokenStatusDTO queryStatus(String tokenNumber);

    /**
     * Cancels a WAITING token.
     */
    boolean cancelToken(String tokenNumber);
}
```

---

## 7. Confirmed External Interfaces (What Member 1 Calls)

These are the exact external contracts Member 1 will invoke. Note that none of them require Member 1 to pass or manage a `Connection`:

```java
// 1. CustomerRepository (from Member 3)
Customer save(Customer customer);
Optional<Customer> findByPhoneNumber(String phoneNumber);
Optional<Customer> findById(Long id);

// 2. TokenRepository (from Member 3)
Token save(Token token);
Optional<Token> findByTokenNumber(String tokenNumber);
boolean updateStatus(Long tokenId, TokenStatus newStatus);

// 3. DailySequenceRepository (from Member 3)
// Notice: Member 3 encapsulates transaction/connection inside the implementation!
int getNextDailySequence(Long serviceId, LocalDate date);

// 4. ServiceRepository (from Member 3)
Optional<ServiceCategory> findById(Long serviceId);

// 5. QueueManager (from Member 2)
void enqueue(Token token);
int getPositionAhead(Token token);
int getEstimatedWaitMinutes(Token token);
boolean cancel(Token token);
```

---

## 8. Summary of Revisions Applied

- [x] **1. Enums:** `INTERRUPTED` confirmed as non-terminal in `TokenStatus.isTerminal()`.
- [x] **2. Customer Model:** Phone number confirmed as primary lookup key; email confirmed as optional.
- [x] **3. Token Model:** Removed `arrivalSequence` and `turnsSkipped` (delegated to Member 2's queue engine).
- [x] **4. Priority Rules:** Formalized strict priority inference. Self-service customers cannot choose `EMERGENCY`. Emergency requires staff authorization.
- [x] **5. DTOs:** `TokenRequestDTO` prevents emergency self-granting; `TokenSlipDTO` and `TokenStatusDTO` confirmed.
- [x] **6. Service Contracts:** Eliminated all `Connection` parameter leakage. Pure, decoupled interfaces.
- [x] **7. Independent Test Doubles:** In-memory mocks ready for Phase 5 unit testing.
