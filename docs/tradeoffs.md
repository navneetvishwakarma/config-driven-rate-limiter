# Trade-offs and Design Decisions

This document captures the key design decisions made in this system, along with the alternatives that were considered and intentionally rejected.

The goal is to make constraints, priorities, and omissions explicit.

---

## 1. Single-Node Execution

### Decision
The system is designed as a single-node, in-memory service.

### Rationale
- Keeps failure modes simple and observable
- Allows focus on correctness and determinism
- Avoids premature coupling to distributed infrastructure

### Trade-off
- State is not shared across nodes
- Horizontal scaling is not supported

### Alternatives Considered
- Redis-backed distributed rate limiting
- Sharded state with consistent hashing

These were rejected to avoid introducing coordination complexity that would dominate the system design.

---

## 2. In-Memory State Only

### Decision
All policy state is maintained in memory and discarded on restart.

### Rationale
- Simplifies state management
- Makes lifecycle and failure behavior explicit
- Avoids partial durability semantics

### Trade-off
- Rate limit state resets on restart
- Not suitable for strict quota enforcement across restarts

### Alternatives Considered
- Persistent storage
- Write-ahead logging

These were deferred to keep the core execution model small and predictable.

---

## 3. Limited Algorithm Set

### Decision
The system supports exactly two algorithms:
- Fixed Window Counter
- Token Bucket

### Rationale
- Provides sufficient contrast in behavior and state complexity
- Demonstrates extensibility without excessive code volume
- Keeps the system comprehensible

### Trade-off
- Does not cover all known rate limiting strategies

### Alternatives Considered
- Sliding Window
- Leaky Bucket

These were excluded because they add incremental complexity without improving the system’s expressive power.

---

## 4. Synchronous Evaluation

### Decision
All request evaluations are synchronous.

### Rationale
- Ensures deterministic behavior
- Simplifies concurrency and reasoning
- Avoids callback or future-based complexity

### Trade-off
- Evaluation latency is tied to policy execution

### Alternatives Considered
- Asynchronous evaluation
- Queue-based processing

These were rejected due to added complexity without clear benefit at this scope.

---

## 5. Fail-Closed Error Handling

### Decision
Runtime errors during evaluation result in request rejection.

### Rationale
- Preserves system safety
- Avoids accidental quota bypass
- Aligns with defensive infrastructure behavior

### Trade-off
- Transient internal errors may block legitimate requests

### Alternatives Considered
- Fail-open behavior
- Partial evaluation

These were rejected due to risk of uncontrolled access.

---

## 6. Immutable Configuration

### Decision
Configuration is loaded at startup and remains immutable during runtime.

### Rationale
- Guarantees consistent behavior
- Avoids race conditions during configuration changes
- Simplifies concurrency model

### Trade-off
- Requires restart to apply configuration changes

### Alternatives Considered
- Hot-reloading configuration
- Dynamic policy updates

These were deferred to avoid complexity around consistency and partial updates.

---

## 7. Deterministic Policy Evaluation Order

### Decision
Policies are evaluated in a deterministic, predefined order.

### Rationale
- Makes behavior predictable
- Simplifies debugging and reasoning
- Avoids ambiguous rejection causes

### Trade-off
- Policy ordering must be explicitly managed

### Alternatives Considered
- Parallel policy evaluation
- Priority-based dynamic ordering

These were rejected due to increased complexity and reduced observability.

---

## 8. Minimal Observability Surface

### Decision
Observability is limited to structured logs and state inspection endpoints.

### Rationale
- Keeps the system lightweight
- Avoids embedding monitoring frameworks
- Leaves instrumentation choices to deployment context

### Trade-off
- No built-in metrics aggregation

### Alternatives Considered
- Metrics exporters
- Tracing frameworks

These were intentionally excluded to avoid coupling the core logic to tooling concerns.

---

## 9. No Authentication or Authorization

### Decision
The system assumes trusted callers.

### Rationale
- Focuses on rate limiting behavior, not access control
- Keeps the API surface small

### Trade-off
- Not suitable as a public-facing service without additional layers

### Alternatives Considered
- API key enforcement
- Role-based access control

These are considered orthogonal concerns.

---

## 10. Summary

This system prioritizes:

- Predictable behavior over flexibility
- Clarity over completeness
- Explicit constraints over hidden assumptions

Many commonly expected features are intentionally omitted to preserve a small, understandable core.
