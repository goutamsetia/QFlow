# Architecture Decision Record: ADR-005
## Service Queue Partitioning, Daily Token Sequences, Controlled Heap Re-evaluation & Interrupted Token Triage

**Status:** APPROVED  
**Date:** 2026-11-18  
**Context:**  
Detailed analysis of the college administration operational flow identified five critical architectural edge cases:
1. Single shared queue introduces cross-service blocking (e.g., an Accounts counter waiting on Registrar tokens).
2. Token numbering must be human-readable, service-specific, and reset daily without colliding across JVM crashes.
3. Standard Java `PriorityQueue` does not automatically re-heapify when internal fields (such as waiting age or turns skipped) mutate in-place.
4. Tokens interrupted mid-service during a system crash or power cut cannot be safely assumed completed or blindly re-enqueued without human confirmation.
5. Syllabus evaluation requires an automated, visible multithreading demonstration without disrupting interactive day-to-day console usage.

**Decisions:**

### 1. Partitioned Logical Service Queues
Instead of a single monolithic queue, `QueueManager` maintains **four dedicated logical service queues**:
- `REG_QUEUE` (Registrar & Transcripts)
- `ACC_QUEUE` (Accounts & Fees)
- `ADM_QUEUE` (Admissions & Verification)
- `EXM_QUEUE` (Examinations)
Each service queue is an independent, thread-safe instance of `SharedQueue<Token>` guarded by its own monitor. Counter operators poll directly from their designated service queue, eliminating cross-service contention and lock thrashing.

### 2. Daily, Service-Specific Token Sequences
Token numbering format: `TKN-<SERVICE_CODE>-<YYYYMMDD>-<SEQUENCE_NUMBER>` (e.g., `TKN-ACC-20261118-0001`).
- Backed by a dedicated database table `daily_service_sequences (service_id, sequence_date, current_sequence)`.
- Sequence allocation executes under an atomic row lock (`SELECT ... FOR UPDATE` or `ON DUPLICATE KEY UPDATE`).
- Sequences reset daily at `00:00:00` per service.
- Across JVM restarts on the same date, the sequence resumes seamlessly from the database value.

### 3. Controlled Queue Heap Recalculation (Aging Re-evaluation)
Because Java’s `java.util.PriorityQueue` maintains heap invariants only on `offer()` and `poll()`, mutating a token's waiting time or skipped count does not trigger re-heapification.
- Recalculation is governed by a **Controlled Sweep Protocol**:
  - *Trigger:* Before every `callNext()` dispatch, or during periodic evaluation intervals.
  - *Execution:* Under the monitor lock, `QueueManager` evaluates whether any waiting `NORMAL` token has exceeded the aging threshold. If threshold conditions are met, the queue performs an in-monitor rebuild (or removes and re-inserts the promoted token), preserving strict heap integrity without silent ordering bugs.

### 4. Admin Triage for Interrupted `SERVING` Tokens
During crash recovery:
- Tokens in `WAITING` are automatically rehydrated into their respective service queues.
- Tokens found in `SERVING` state at boot time were disrupted mid-interaction. They are **not** re-enqueued automatically.
- Instead, the system marks their operational state as `INTERRUPTED_SERVING` and isolates them in an **Administrator Triage Review Queue**.
- The Administrator reviews interrupted tokens via the Admin Console to either:
  1. **Re-queue with Top Priority:** Assign immediate `EMERGENCY` priority to serve the displaced customer immediately.
  2. **Mark Completed:** Confirm manual paper completion with timestamp and remarks.
  3. **Mark Skipped / Cancelled:** Confirm customer departure.

### 5. Dual-Mode Operational & Simulation Architecture
The presentation layer provides two distinct top-level modes:
- **Mode A: Operational CLI Mode:** Interactive console for daily college operations (Student Kiosk, Counter Operator Consoles, Admin Dashboard).
- **Mode B: Automated Concurrency Simulation Mode:** Headless automated simulator spawning multiple Producer threads (generating arrival bursts across services) and Consumer threads (simulating counter processing speeds with variable service latencies), visibly demonstrating `wait()`, `notifyAll()`, guarded monitor loops, and thread lifecycle metrics.

**Consequences:**
- Eliminates cross-counter concurrency bottlenecks.
- Completely resolves Java `PriorityQueue` in-place heap staleness.
- Provides 100% fail-safe crash triage without data corruption.
- Guarantees seamless academic syllabus evaluation without sacrificing real-world CLI usability.
