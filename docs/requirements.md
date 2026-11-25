# QFlow – Smart Queue & Token Management System
## Software Requirements Specification (SRS) – Phase 0

**Document Version:** 1.0.0  
**Status:** DRAFT – PENDING TEAM SIGN-OFF  
**Target Environment:** College Administration Office Simulation  
**Technology Stack:** Java 17 LTS, MySQL 8, JDBC, JUnit 5, Maven  

---

## 1. Project Objective & System Vision

**QFlow** is an enterprise-grade, console-based Smart Queue and Token Management System engineered to eliminate chaotic waiting lines, optimize staff counter allocation, and maintain complete auditability in high-volume institutional environments—specifically simulated for a **College Administration Office** (e.g., Admissions, Fee Counter, Registrar, Examination Cell).

The system bridges the gap between **high-performance in-memory scheduling** (using custom priority comparators and low-level multithreaded monitor synchronization) and **robust relational persistence** (MySQL 8 via raw, secure JDBC with ACID transaction guarantees). 

QFlow is built strictly according to modular clean-architecture principles, enforcing separation of concerns across a 4-member engineering team without relying on external enterprise frameworks (such as Spring or Hibernate), directly demonstrating core Java engineering proficiencies.

---

## 2. Problem Statement & Domain Value

In conventional physical offices:
1. **Unstructured Crowding:** Customers queue physically, causing congestion, friction, and disputes over arrival order.
2. **Absence of Differentiated Priority:** Vulnerable or urgent cases (e.g., medical emergencies, elderly visitors/senior professors, students with immediate registration deadlines) wait behind routine inquiries.
3. **Counter Inefficiency:** Counters experience uneven workloads with no transparent dispatching mechanism.
4. **Zero Observability:** Administration lacks real-time insight into waiting times, counter throughput, bottleneck services, and daily volume.
5. **Data Fragility:** In makeshift or purely in-memory solutions, application crashes or restarts result in lost customer queues and duplicate token issuance.

QFlow resolves these challenges by providing:
- Deterministic, priority-aware token assignment (`EMERGENCY` > `SENIOR_CITIZEN` > `NORMAL`).
- Dual-layer queue integrity: in-memory priority scheduling synchronized with persistent MySQL audit trails.
- Real-time performance analytics (average wait time, counter throughput, peak service demand).
- Crash-resilient state recovery upon application restarts.

---

## 3. Scope of Version 1 (V1)

### 3.1 In-Scope
- **Single-Facility Office Simulation:** Multi-service college administrative building covering 4 standard service categories:
  - `REG`: Registrar & Transcripts
  - `ACC`: Accounts & Fee Payment
  - `ADM`: Admissions & Verification
  - `EXM`: Examination & Grade Cards
- **CLI/Console Presentation Layer:** Role-tailored interactive text interface supporting:
  - Customer Self-Service Kiosk (registration, token issuance, status check).
  - Staff Counter Console (call next, mark serving, complete service, skip, cancel).
  - Administrator Console (live queue monitoring, service configuration, analytics, CSV import/export, data backup).
- **Priority Queue Engine:** Monotonically sequenced, custom comparator-driven priority queue preserving strict FIFO ordering within identical priority tiers.
- **Multithreaded Producer-Consumer Simulator:** Low-level monitor synchronization (`synchronized`, `wait()`, `notify()`, `notifyAll()`) modeling concurrent customer arrivals (producers) and counter servicing (consumers).
- **Persistence & Transaction Management:** MySQL 8 integration via JDBC `PreparedStatement`, transactional state transitions, and connection lifecycle management.
- **Startup Queue Rehydration:** Automatic reconstruction of the in-memory waiting queue from persistent storage on system boot.
- **Analytics & File Integration:** Java Stream API reports, CSV customer/token bulk import with validation, CSV analytics export, and structured text audit logs.

### 3.2 Out-of-Scope (Deferred to Future Versions)
- Graphical User Interface (JavaFX / Swing / Web UI).
- Spring Framework, Hibernate, or third-party ORM/IoC frameworks.
- Distributed message brokers (Apache Kafka, RabbitMQ).
- Multi-campus / Multi-tenant physical partitioning.
- Live SMS / Email gateway integrations (simulated via console/log output).

---

## 4. User Roles & Personas

| Role | Persona / User | Key Responsibilities | Access Boundaries |
| :--- | :--- | :--- | :--- |
| **Customer** | Student, Parent, Senior Citizen, Visitor | - Self-registers personal profile<br>- Selects requested service & priority tier<br>- Receives unique token number<br>- Checks live wait time & queue position | Kiosk views only. Cannot call tokens, access other customers' records, or modify service counters. |
| **Counter Operator** | Administration Staff / Counter Officer | - Logs into assigned physical counter (e.g., Counter 1 - Fees)<br>- Calls the next eligible token from the queue<br>- Marks tokens as `SERVING`, `COMPLETED`, or `SKIPPED`<br>- Views recent counter history | Restricted to assigned counter operations. Cannot configure services or run administrative exports. |
| **Administrator** | Head of Office / Office Manager | - Configures services, counters, and priority rules<br>- Monitors global live queues across all counters<br>- Generates analytical reports (Stream API)<br>- Performs CSV bulk import/export and audit log reviews<br>- Triggers system crash recovery & state reconciliations | Full system privileges. |
| **System Worker** | Background JVM Threads | - Concurrently registers batch tokens (Producer)<br>- Concurrently processes tokens at counters (Consumer)<br>- Handles wait/notify signaling | Internal concurrency workers. |

---

## 5. Functional Requirements (FR)

### Module 1: Customer & Token Management (Member 1)
- **FR-1.1 Customer Registration:** The system shall capture customer attributes: Full Name, Contact Number, Email Address, Age, and Customer Category (`STUDENT`, `STAFF`, `SENIOR_CITIZEN`, `GUEST`).
- **FR-1.2 Customer Input Validation:**
  - Full Name: Non-empty, 2–100 alphabetic characters/spaces.
  - Contact Number: Exactly 10 digits (standard domestic format), numeric.
  - Email: Valid RFC 5322 format.
  - Age: Integer between 16 and 120.
- **FR-1.3 Unique Customer Identifier:** System shall generate an immutable Customer ID (`CUST-<TIMESTAMP>-<RANDOM>` or DB-generated sequence).
- **FR-1.4 Priority Tier Selection:** Customers are categorized into three distinct priority levels:
  - `EMERGENCY` (Priority Level 1 - Highest)
  - `SENIOR_CITIZEN` (Priority Level 2)
  - `NORMAL` (Priority Level 3 - Default)
- **FR-1.5 Unique Token Generation:** System shall generate an immutable, human-readable Token Number formatted as `TKN-<SERVICE_CODE>-<DAILY_SEQUENCE>` (e.g., `TKN-ACC-0001`).
- **FR-1.6 Estimated Wait Time Calculation:** System shall compute estimated waiting time dynamically based on the number of preceding tokens in that service queue multiplied by the historical average service duration:
  $$\text{EstimatedWaitTime} = N_{\text{ahead}} \times \text{AvgServiceTime}_{\text{service}}$$
- **FR-1.7 Token Status Tracking:** Customers can query token status by Token Number, returning current state (`WAITING`, `SERVING`, `COMPLETED`, `SKIPPED`, `CANCELLED`), counter assigned, and tokens remaining ahead.

### Module 2: Priority Queue Management (Member 2)
- **FR-2.1 Priority-Based Ordering:** Tokens shall be sorted using a custom `Comparator<Token>` satisfying:
  1. Priority level descending (`EMERGENCY` > `SENIOR_CITIZEN` > `NORMAL`).
  2. Monotonic Arrival Sequence ascending (strict FIFO tie-breaker for identical priorities).
- **FR-2.2 Queue Operations:**
  - `enqueue(Token)`: Insert token into the priority queue in $O(\log n)$ time.
  - `callNext(serviceId)`: Retrieve and dequeue the highest-priority waiting token eligible for the requesting counter's service.
  - `peekNext(serviceId)`: Inspect the next eligible token without state modification.
- **FR-2.3 Token State Transitions:**
  - `WAITING` $\rightarrow$ `SERVING` (Operator calls token)
  - `SERVING` $\rightarrow$ `COMPLETED` (Service finished successfully)
  - `SERVING` $\rightarrow$ `SKIPPED` (Customer absent when called)
  - `WAITING` $\rightarrow$ `CANCELLED` (Customer cancels before being called)
- **FR-2.4 Starvation Mitigation:** The queue engine shall enforce a dynamic promotion rule (Aging Policy) to prevent lower-priority starvation during extended peak bursts of emergency/senior tokens.
- **FR-2.5 Live Queue Display:** Format and print active queue depth, breakdown by priority tier, and currently serving tokens per counter.

### Module 3: Database & JDBC Persistence (Member 3)
- **FR-3.1 Relational Storage:** Store and maintain relational entities in MySQL 8: `customers`, `services`, `counters`, `tokens`, `service_history`.
- **FR-3.2 Connection Lifecycle:** Encapsulate database connectivity via `DBConnection` using connection pooling or safe singleton factory patterns with externalized credentials.
- **FR-3.3 Prepared Statements & SQL Safety:** 100% of SQL interactions must execute via `PreparedStatement` with typed parameters to eliminate SQL injection vulnerabilities.
- **FR-3.4 Transactional Integrity (ACID):** Atomic multi-step operations (e.g., calling next token + updating token status to `SERVING` + recording service start in history) must execute under manual transaction control (`setAutoCommit(false)` $\rightarrow$ `commit()` / `rollback()`).
- **FR-3.5 Startup State Recovery:** On application startup, the system shall query all tokens in `WAITING` state ordered by arrival sequence and rehydrate the in-memory PriorityQueue to guarantee zero token loss across restarts.

### Module 4: Analytics, File I/O & Multithreading (Member 4)
- **FR-4.1 Real-Time Analytics via Stream API:**
  - Total tokens generated, completed, skipped, and cancelled.
  - Average waiting time per service category and overall.
  - Service demand distribution (most requested to least requested services using `groupingBy`, `counting`, `sorted`).
  - Priority breakdown and counter utilization metrics.
- **FR-4.2 CSV Bulk Import:** Import customer lists from CSV files with line-by-line schema validation, error trapping, and batch DB insertion.
- **FR-4.3 CSV Export:** Export daily summary reports and completed token audit trails into structured CSV format.
- **FR-4.4 File Logging & Stream Handling:** Implement audit logging and binary state backups utilizing standard Java I/O (`FileInputStream`, `FileOutputStream`, `FileReader`, `FileWriter`, `StreamTokenizer`).
- **FR-4.5 Multithreaded Producer-Consumer Simulator:**
  - A thread-safe bounded/guarded monitor `SharedQueue`.
  - Producer threads simulating incoming customer arrivals.
  - Consumer threads simulating counter operators processing tokens.
  - Explicit synchronization using `synchronized`, `wait()`, `notify()`, and `notifyAll()` in guarded while loops.

---

## 6. Non-Functional Requirements (NFR)

| ID | Category | Requirement Specification |
| :--- | :--- | :--- |
| **NFR-1** | **Thread Safety** | The shared queue must be strictly thread-safe. All state-mutating operations must synchronize on private monitor locks. No race conditions, deadlocks, or lost wake-ups permitted under concurrent access. |
| **NFR-2** | **Data Consistency** | Dual-state consistency must be guaranteed between the in-memory queue and MySQL 8. Database records represent the immutable source of truth. |
| **NFR-3** | **Performance** | In-memory priority insertion and extraction must operate in $O(\log n)$ time. Console menu rendering must respond in $<100\text{ ms}$. Database indexing must keep status lookup queries $<50\text{ ms}$. |
| **NFR-4** | **Reliability & Recovery** | Unplanned JVM restarts or shutdowns must not corrupt state or lose pending tokens. Startup reconciliation must restore the waiting queue to exact pre-crash state. |
| **NFR-5** | **Security & Integrity** | All input from CLI and CSV must be validated against strict whitelists. Zero SQL concatenation. Database credentials must never be hardcoded in version-controlled files. |
| **NFR-6** | **Maintainability & Modularity** | Clean 6-layer architecture with zero circular dependencies. Strict separation of presentation, business logic, domain entities, and data access objects. |

---

## 7. Technical Risks & Mitigation Strategies

```
+-----------------------------------+---------------------------------------------------+
| Identified Technical Risk         | Mitigation Strategy                               |
+-----------------------------------+---------------------------------------------------+
| 1. Dual-State Desynchronization   | Database-First Persistence Pattern: Token is      |
|    (DB Write vs In-Memory Queue)  | committed to MySQL first. Only upon successful    |
|                                   | commit is the token enqueued in memory. If memory |
|                                   | enqueue fails, DB transaction is rolled back/sync |
|                                   | flagged. Startup recovery heals any discrepancy.  |
|                                   |                                                   |
| 2. Monitor Synchronization Bugs   | Strict guarded block pattern:                      |
|    (Lost wakeups / Spurious wake) | while (queue.isEmpty()) { monitor.wait(); }       |
|                                   | Always notify with notifyAll() on state changes   |
|                                   | affecting multiple consumer counters.             |
|                                   |                                                   |
| 3. Low-Priority Starvation        | Configurable Aging Policy: Tokens waiting > T     |
|    (NORMAL tokens starved by peak | minutes or after K higher-priority services are   |
|    EMERGENCY / SENIOR arrivals)   | dynamically promoted to the next priority tier.   |
|                                   |                                                   |
| 4. Token Collision on Restart     | Token sequences driven by persistent DB sequences |
|    (Duplicate token numbers)      | or composite keys (YYYYMMDD + Auto-Increment ID).  |
|                                   | In-memory counters never act as the source of     |
|                                   | truth for unique identity generation.             |
|                                   |                                                   |
| 5. Team Contract Drift            | Freeze shared domain models, interfaces, and DTOs |
|    (Conflicting class definitions)| in Phase 3 before any module implementation       |
|    between Member 1, 2, 3, and 4) | begins. No signature changes allowed without      |
|                                   | formal cross-team approval.                       |
+-----------------------------------+---------------------------------------------------+
```

---

## 8. Open Architectural Questions & Proposed Defaults

1. **Starvation Prevention Policy:**
   - *Question:* Should we use strict priority, dynamic age-based priority escalation, or a quota-based scheduling ratio (e.g., 3 High : 1 Normal)?
   - *Proposed Default:* **Age-Based Priority Escalation.** If a `NORMAL` token waits longer than 20 minutes (or 5 consecutive higher-priority dispatches), its effective comparator weight promotes it to `SENIOR_CITIZEN` status.

2. **Token Numbering Format & Scope:**
   - *Question:* Should tokens reset daily (`TKN-ACC-20261118-0001`) or run on a global monotonically increasing ID?
   - *Proposed Default:* **Daily Service-Prefixed Format:** `TKN-<SERVICE_CODE>-<SEQUENCE>` backed by database auto-increment ID to prevent collision across restarts.

3. **Multithreading Integration vs. Console Interactivity:**
   - *Question:* How should the academic multithreading requirement (`wait()`, `notifyAll()`, producer-consumer) coexist with manual console operator inputs?
   - *Proposed Default:* **Dual-Mode System:**
     - **Mode 1 (Operational CLI Mode):** Manual operator actions invoke synchronized queue methods.
     - **Mode 2 (Automated Simulation Mode):** Spawns dedicated Producer (arrival generator) and Consumer (counter worker) threads to visibly demonstrate monitor coordination, guarded blocks, and graceful thread shutdown.

4. **Customer Uniqueness Constraint:**
   - *Question:* What uniquely identifies a customer across multiple visits?
   - *Proposed Default:* Customer `phone_number` and `email` have unique constraints in MySQL. If an existing customer registers again, their existing `customer_id` is reused rather than creating duplicate profiles.

---

## 9. Phase 0 Review Checklist

- [ ] Project vision and problem statement accurately reflect college administrative domain.
- [ ] Scope of Version 1 clearly established with realistic console boundaries.
- [ ] User personas (Customer, Operator, Admin, System Worker) and access limits agreed.
- [ ] Functional requirements mapped cleanly to Member 1, 2, 3, and 4 boundaries.
- [ ] Non-functional requirements (thread safety, consistency, recovery) verified against syllabus.
- [ ] Critical technical risks (dual-state sync, starvation, concurrency bugs) identified with mitigations.
- [ ] Proposed architectural defaults accepted by all 4 team members.
- [ ] Ready to transition to Phase 1 (Complete System Architecture & UML Design).
