# Config-Driven Rate Limiter

A configuration-driven rate limiting service that evaluates incoming requests against one or more rate limiting policies per client.

The system is designed for correctness, determinism, and ease of extension.  
Distributed coordination, external storage, and throughput optimization are intentionally out of scope.

---

## Scope

The service supports:

- Declarative, per-client rate limiting configuration
- Multiple rate limiting algorithms evaluated per request
- Deterministic allow / reject decisions
- Thread-safe execution under concurrent access
- Explicit rejection reasons and retry guidance

The implementation favors clarity and predictable behavior over completeness.

---

## Supported Algorithms

- Fixed Window Counter
- Token Bucket

Additional algorithms are intentionally excluded to keep the system bounded and comprehensible.

---

## Non-Goals

The following concerns are explicitly not addressed:

- Distributed or multi-node rate limiting
- External state stores (e.g. Redis)
- Cross-process coordination
- Authentication or authorization
- UI or operational dashboards
- Performance benchmarking

These topics are discussed in documentation but not implemented.

---

## Configuration Model

Rate limiting behavior is defined via static configuration.

Example:

```json
{
  "clientId": "client-A",
  "policies": [
    {
      "type": "FIXED_WINDOW",
      "limit": 100,
      "windowSeconds": 60
    },
    {
      "type": "TOKEN_BUCKET",
      "capacity": 50,
      "refillRatePerSecond": 5
    }
  ]
}
```

Policies are evaluated independently.
If any policy rejects a request, the request is rejected.

---

## Documentation

Design and behavioral details are captured in the following documents:

- docs/requirements.md

- docs/architecture.md

- docs/tradeoffs.md

---

## Status

This repository represents a bounded, single-node implementation intended to explore design clarity and correctness.

Scaling, durability, and distributed execution are considered but intentionally deferred.
