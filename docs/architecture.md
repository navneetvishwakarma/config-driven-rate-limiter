# Architecture

## 1. Overview

The system evaluates incoming requests against a set of configured rate limiting policies associated with a client.

Evaluation is synchronous, deterministic, and side-effect free beyond in-memory state updates.  
Each request results in a single allow or reject decision.

The architecture is intentionally single-node and in-memory.

---

## 2. High-Level Components

The system is composed of the following logical components:

- **RateLimiter**  
  Entry point for request evaluation.

- **Policy**  
  Encapsulates a single rate limiting algorithm and its state.

- **PolicyEvaluator**  
  Coordinates policy evaluation for a request.

- **PolicyStateStore**  
  Manages in-memory state associated with policies.

- **ConfigurationRegistry**  
  Holds validated, immutable configuration.

---

## 3. Component Responsibilities

### 3.1 RateLimiter

- Accepts evaluation requests
- Resolves client configuration
- Delegates evaluation to the PolicyEvaluator
- Returns a decision with contextual information

The RateLimiter does not contain algorithm-specific logic.

---

### 3.2 Policy

- Defines a common interface for all rate limiting algorithms
- Encapsulates algorithm-specific state and behavior
- Evaluates a request and returns a policy-level decision

Policies are isolated from each other and do not share state.

---

### 3.3 PolicyEvaluator

- Evaluates policies in deterministic order
- Aggregates policy decisions
- Short-circuits on rejection
- Produces a single, final decision

PolicyEvaluator contains no algorithm-specific logic.

---

### 3.4 PolicyStateStore

- Maintains in-memory state per client and policy
- Provides thread-safe access to policy state
- Ensures state isolation across clients

No durability guarantees are provided.

---

### 3.5 ConfigurationRegistry

- Loads and validates configuration at startup
- Exposes immutable configuration during runtime
- Rejects invalid or conflicting policies

Configuration is not mutable after initialization.

---

## 4. Request Evaluation Flow

1. A request is submitted to the RateLimiter with a client identifier.
2. The client configuration is resolved from the ConfigurationRegistry.
3. The PolicyEvaluator evaluates configured policies in order.
4. Each Policy updates its internal state as part of evaluation.
5. If any policy rejects the request, evaluation stops.
6. A final allow or reject decision is returned.

---

## 5. State Model

- State is maintained per client and per policy.
- Policies manage their own state.
- State updates occur atomically within policy evaluation.
- No state is shared across policies or clients.

All state is held in memory.

---

## 6. Concurrency Model

- The RateLimiter supports concurrent request evaluation.
- No global locks are used.
- Synchronization is limited to per-policy state.
- Policy evaluation is thread-safe and deterministic.

The system favors correctness over maximal parallelism.

---

## 7. Error Handling Strategy

- Configuration errors fail fast during startup.
- Runtime errors during policy evaluation result in request rejection.
- Errors are logged with sufficient context for diagnosis.

The system fails closed by default.

---

## 8. Extension Model

New rate limiting algorithms can be added by:

1. Implementing the Policy interface
2. Defining a corresponding configuration schema
3. Registering the policy type during initialization

No changes to the RateLimiter or PolicyEvaluator are required.

---

## 9. Deferred Concerns

The following concerns are intentionally deferred:

- Distributed state coordination
- Persistent state storage
- Horizontal scaling
- Policy hot-reloading

These are discussed in `tradeoffs.md`.

---

## 10. Architectural Invariants

- Policy evaluation order is deterministic
- Policy state is isolated per client
- Core evaluation logic is algorithm-agnostic
- Configuration is immutable at runtime
- The system fails closed on error
