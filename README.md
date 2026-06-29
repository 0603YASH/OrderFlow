# OrderFlow

A production-grade food order management system built with a microservices architecture.
Designed with the same decisions a senior engineer makes on day one — not bolted on later.

---

## Architecture

```
┌─────────────────┐              ┌─────────────────┐              ┌──────────────────────┐
│   User Service  │─────REST────▶│  Order Service  │────Kafka────▶│ Notification Service │
│   (port 8081)   │              │   (port 8082)   │              │     (port 8083)       │
└────────┬────────┘              └────────┬────────┘              └──────────────────────┘
         │                                │
         └──────────────┬─────────────────┘
                        │
           ┌────────────┼────────────┐
           ▼            ▼            ▼
        MySQL         Redis        Kafka
```

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 (Virtual Threads) |
| Framework | Spring Boot | 4.1.0 |
| Security | Spring Security + JWT | 6.x |
| Database | MySQL | 8.x |
| Audit | Hibernate Envers | 7.x |
| Cache | Redis | - |
| Messaging | Apache Kafka | - |
| Resilience | Resilience4j | - |
| Tracing | Micrometer + Zipkin | - |
| Docs | Swagger / OpenAPI | 3.0 |
| Containers | Docker + Compose | - |

---

## Services

### User Service — `port 8081`

**Security**
- BCrypt password hashing — passwords excluded from AOP logs via `@ToString(exclude="password")`
- JWT stateless authentication with role-based access control (`USER`, `ADMIN`)
- `AuthenticationEntryPoint` — unauthenticated requests return structured JSON 401, not Spring's default HTML
- User enumeration prevention — login returns the same error whether email is wrong or password is wrong
- `SessionCreationPolicy.STATELESS` — no server-side session, every request is self-contained

**Audit & History**
- Spring Data JPA auditing — `createdAt`, `updatedAt`, `createdBy`, `updatedBy` auto-populated on every entity
- Hibernate Envers — complete change history in `users_aud` table; every insert, update, and delete is versioned
- Soft delete — `isDeleted` + `deletedAt`; records are never permanently removed

**Observability**
- MDC request ID — every request tagged with a UUID; every log line carries that ID for end-to-end tracing
- AOP logging — automatic method entry, exit, timing, and exception logging across all service methods
- Actuator `/actuator/health` — health check endpoint for load balancer integration

**Performance & Reliability**
- Java 21 Virtual Threads — `spring.threads.virtual.enabled=true`; handles high concurrency without thread pool exhaustion
- Graceful shutdown — `server.shutdown=graceful`; in-flight requests complete before the process terminates
- GZIP compression — responses compressed above 1KB threshold
- `spring.jpa.open-in-view=false` — database connection released after service method, not after HTTP response

**Testing**
- Unit tests with Mockito — service layer tested in isolation
- Integration tests with MockMvc + H2 + Spring Security — full HTTP stack tested end-to-end
- Separate test profile — H2 in-memory database, no MySQL required to run tests

**API**

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/users/register` | Public | Register new user |
| `POST` | `/api/v1/auth/login` | Public | Login, returns JWT |
| `GET` | `/api/v1/users/me` | Bearer token | Get current user profile |
| `GET` | `/actuator/health` | Public | Health check |

Swagger UI available at `http://localhost:8081/swagger-ui/index.html`

---

### Order Service — `port 8082` *(in progress)*
Places orders, validates users via User Service, publishes `OrderPlaced` events to Kafka.

### Notification Service — `port 8083` *(planned)*
Consumes `OrderPlaced` Kafka events and sends order confirmations.

---

## Running Locally

```bash
# Clone the repository
git clone https://github.com/0603YASH/OrderFlow.git

# Start infrastructure (MySQL, Redis, Kafka)
docker-compose up -d

# Run User Service
cd user-service
mvn spring-boot:run
```

**Required environment variables:**
```
DB_URL=jdbc:mysql://localhost:3306/orderflow_users
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your-secret-key-minimum-32-characters
```

---

## Project Status

- [x] User Service — registration, login, JWT auth, Envers audit, full test suite
- [ ] Order Service — place order, Kafka producer
- [ ] Notification Service — Kafka consumer
- [ ] Redis caching
- [ ] Resilience4j circuit breaker
- [ ] Micrometer Tracing + Zipkin
- [ ] Docker Compose — full environment
- [ ] AWS deployment
