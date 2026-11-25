# Architecture Decision Record: ADR-001
## Selection of Layered Clean Architecture & Dependency Inversion

**Status:** APPROVED  
**Date:** 2026-11-18  
**Context:**  
QFlow is a collaborative 4-member Java team project. Without a strict structural separation, team members risk writing SQL directly in console handlers, coupling business logic to terminal input, or passing database connections across UI screens. Furthermore, the academic syllabus demands demonstrable separation of concerns, testability, and maintainability without using enterprise frameworks like Spring.

**Decision:**  
We adopt a **6-Layer Clean Architecture** with strict unidirectional dependencies:
1. **Presentation Layer (`app.cli`):** Console menus, user prompts, input reading, table formatting.
2. **Service Layer (`service`):** Business logic, validation orchestration, transaction boundaries, coordination between repositories and the queue engine.
3. **Domain Layer (`model`):** Pure POJOs (Plain Old Java Objects) and Enums. Zero dependencies on database or UI.
4. **Repository / DAO Layer (`repository`):** Database abstraction. Pure Java interfaces decoupled from SQL implementations (`repository.impl`).
5. **Database Layer (`database`):** JDBC connection factory (`DBConnection`), schema lifecycle, resource management.
6. **Utility & Concurrency Layer (`util`, `threading`):** Cross-cutting validators, custom comparators, monitor synchronizer (`SharedQueue`), custom exceptions.

**Consequences:**
- Positive:
  - Team members can work in parallel using interface contracts (e.g., Member 1 can test `CustomerService` using mock/stub repositories before Member 3 finishes MySQL schema).
  - High testability: Domain logic and business rules can be verified via JUnit 5 without a running MySQL instance.
  - No SQL injection leakage into presentation or business logic.
- Trade-off:
  - Requires explicit Data Transfer Objects (DTOs) or mapped entities rather than passing raw `ResultSet` objects into UI controllers.
