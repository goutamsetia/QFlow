# Architecture Decision Record: ADR-003
## Low-Level Monitor Synchronization for Concurrency Syllabus Demonstration

**Status:** APPROVED  
**Date:** 2026-11-18  
**Context:**  
The academic syllabus explicitly mandates practical mastery of:
- `wait()`, `notify()`, `notifyAll()`
- Guarded blocks inside loops
- Thread lifecycle, interruption handling, and mutual exclusion (`synchronized`)

Modern Java offers `java.util.concurrent.BlockingQueue`, which internally abstracts away these monitor primitives. Using `BlockingQueue` directly would conceal the required syllabus competencies.

**Decision:**  
We engineer a custom, thread-safe generic synchronization coordinator class: `SharedQueue<T>`.
1. The class wraps an internal `PriorityQueue<T>` guarded by a private monitor lock `private final Object lock = new Object();`.
2. `enqueue(T item)` acquires the monitor lock, enqueues the item, and invokes `lock.notifyAll()` to wake sleeping consumer threads.
3. `dequeue()` acquires the monitor lock, enters a guarded `while (queue.isEmpty() && !isShutdown)` loop, and calls `lock.wait()`.
4. When `isShutdown` is flagged, `lock.notifyAll()` wakes all waiting consumer threads, which cleanly exit with `InterruptedException` or return `null`.
5. `notify()` is demonstrated in single-consumer specific dispatching, while `notifyAll()` is used for multi-counter broadcast wakeups and system shutdowns.

**Consequences:**
- Fulfills 100% of the syllabus inter-thread communication requirements with transparent, readable Java code.
- Ensures robust, non-blocking shutdown behavior without thread leaks.
