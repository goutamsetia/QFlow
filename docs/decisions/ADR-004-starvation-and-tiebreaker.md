# Architecture Decision Record: ADR-004
## Starvation Prevention Policy & Priority Tie-Breaking Strategy

**Status:** APPROVED  
**Date:** 2026-11-18  
**Context:**  
In a static priority queue where `EMERGENCY` > `SENIOR_CITIZEN` > `NORMAL`, a continuous stream of emergency cases or senior citizens could cause low-priority (`NORMAL`) customers to suffer indefinite starvation. Furthermore, when multiple tokens belong to the same priority tier, the system must guarantee strict First-In-First-Out (FIFO) ordering without ambiguity.

**Decision:**  
1. **FIFO Tie-Breaker:** Every token receives a monotonically increasing `arrivalSequence` (long integer) assigned at creation time. When two tokens have identical priority, the token with the smaller `arrivalSequence` is dequeued first.
2. **Aging-Based Priority Escalation:**
   - Every `Token` tracks its `createdAt` timestamp and a `turnsSkipped` counter (incremented whenever a higher-priority token bypasses it).
   - The `PriorityComparator` evaluates an *effective priority score*:
     $$\text{EffectiveWeight} = \text{BasePriorityWeight} + f(\text{WaitDuration}) + g(\text{TurnsSkipped})$$
   - Once a `NORMAL` token exceeds a configured threshold (e.g., 20 minutes wait time or 5 higher-priority dispatches), its effective comparator rank is promoted to match `SENIOR_CITIZEN`, ensuring it gets served without waiting indefinitely.

**Consequences:**
- Eliminates starvation mathematically while preserving the immediate fast-track dispatch for true emergency cases.
- Provides predictable, mathematically provable FIFO ordering.
