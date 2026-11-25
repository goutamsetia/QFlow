# Architecture Decision Record: ADR-002
## Dual-State Reconciliation (Database-First with In-Memory PriorityQueue)

**Status:** APPROVED  
**Date:** 2026-11-18  
**Context:**  
QFlow maintains two representations of active queues:
1. An in-memory `PriorityQueue<Token>` for high-throughput, $O(\log n)$ priority sorting and consumer monitor synchronization.
2. A persistent MySQL database representing the historical source of truth.

A naive architecture might add to the in-memory queue first and write to the database asynchronously, or vice versa. If an application crashes or an exception occurs between these two actions, the queue state becomes corrupted (ghost tokens in memory, or unserved tokens forgotten in the database).

**Decision:**  
We enforce the **Database-First Persistence Pattern** coupled with **Boot-Time Queue Rehydration**:
1. **Enqueue Flow:** 
   - Step 1: Open DB transaction.
   - Step 2: Insert Token record with status `WAITING` and obtain generated atomic token ID and arrival sequence.
   - Step 3: Commit DB transaction.
   - Step 4: Offer token to in-memory `SharedQueue`.
   - *Error Handling:* If in-memory enqueue fails (e.g. queue capacity overflow), the token remains in the DB as `WAITING` and will be rehydrated or cancelled via compensating action.
2. **State Transition Flow (Serving/Completed):**
   - Step 1: Dequeue from in-memory queue.
   - Step 2: Execute atomic DB update: `UPDATE tokens SET status = 'SERVING', counter_id = ? WHERE id = ?`.
   - Step 3: If DB update fails, re-insert token to head of in-memory queue or mark error.
3. **Application Crash Recovery:**
   - On application startup, before opening the CLI, `QueueManager.initialize()` queries:
     `SELECT * FROM tokens WHERE status = 'WAITING' ORDER BY arrival_sequence ASC`
   - Every active token is re-enqueued into the in-memory PriorityQueue. Active state is completely restored without duplicate token numbers.

**Consequences:**
- Guarantees zero token loss across power failures or JVM crashes.
- In-memory queue remains an ephemeral, reconstructed operational cache of the persistent database state.
