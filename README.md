# 🏦 Bank POC — Secure, Observable, and Transaction-Safe Banking API

**Bank POC** is a production-minded proof-of-concept banking system built with **Spring Boot**, designed to demonstrate secure money transfers, data durability (WAL), safe concurrency handling, distributed caching, and full-stack observability — all runnable locally via Docker or Kubernetes.

This project is designed for **engineers, recruiters, and portfolio reviewers** who want to see how a single developer can build a bank-grade architecture with modern infrastructure and clean implementation.

---

## 🚀 Springboot Template
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.7&packaging=jar&configurationFileFormat=properties&jvmVersion=21&groupId=com.bankpoc&artifactId=core&name=core&description=Core%20banking%20service%20built%20with%20Spring%20Boot%2C%20providing%20foundation%20for%20account%2C%20transaction%2C%20and%20customer%20modules.&packageName=com.bankpoc.core&dependencies=devtools,lombok,modulith,web,security,flyway,data-redis,data-elasticsearch,data-jpa,batch,actuator,kafka,prometheus,restdocs,cloud-resilience4j

✅ **Secure Transfers** — Atomic debit/credit with ACID PostgreSQL transactions  
✅ **Idempotency** — Safe retry logic via unique keys  
✅ **Optimistic & Pessimistic Locking** — No race conditions, even under load  
✅ **JWT Authentication** — Stateless, secure API access  
✅ **Redis Integration** — Caching, OTPs, and distributed short locks  
✅ **Observability Stack** — Spring Boot Actuator (Micrometer) + Prometheus + Grafana  
✅ **Structured Logging** — Logback JSON → ELK ready  
✅ **Tracing** — Jaeger support  
✅ **Resilience4j** — Retry, backoff, and circuit breakers  
✅ **Docker & Kubernetes Ready** — Run anywhere with minimal setup  
<!-- ✅ **Testcontainers Integration** — True integration testing with real services   -->

---

## 🧩 Architecture Overview

```
+---------+      HTTPS       +------------+      SQL      +-----------+
|  Client |  <----------->   | Spring API |  <--------->  | PostgreSQL|
+---------+                 +-----+------+               +-----------+
                                  |
                       Redis <----+----> Kafka (optional)
                         |                     ^
                   (cache, OTP, locks,         |
                    sessions, streams)         |
                                               |
                                       Metrics/Logs/Traces
                                          /    |       \
                                 Prometheus  Grafana   ELK/Logstash
```

---

## 🏗️ Tech Stack

| Category | Technology |
|-----------|-------------|
| Language / Framework | Java (LTS) + Spring Boot |
| Database | PostgreSQL (ACID, WAL) |
| Cache / Locking | Redis ( Spring Data Redis) |
| Messaging | Kafka *(optional)* or Redis Streams |
| Observability | Spring Boot Actuator, Prometheus, Grafana, Logback, Jaeger |
| Resilience | Resilience4j (retries, rate limits) |
| Migrations | Flyway |
| Containerization | Docker, Docker Compose |
<!-- | Testing | JUnit 5 + Testcontainers | -->

---

## 🧠 Core Concepts Demonstrated

### 1. **Transaction Safety**
- All transfers are atomic (`BEGIN...COMMIT`)
- Uses `SELECT FOR UPDATE` or optimistic versioning
- Protects from double spending via **idempotency keys**

### 2. **Concurrency Control**
- Database is the source of truth (no Redis balances)
- Pessimistic or optimistic locking ensures consistency
- Redis locks only for cross-instance coordination

### 3. **Durability (WAL)**
- PostgreSQL Write-Ahead Log guarantees persistence
- Supports backup and point-in-time recovery (PITR)

### 4. **Observability**
- Spring Boot Actuator → Prometheus
- Dashboards via Grafana
- Traces via Jaeger
- JSON structured logs → ELK-ready

### 5. **Resilience**
- Resilience4j for retries/backoffs
- Circuit breakers for external APIs 
- Rate limiting for login/OTP

---

## ⚙️ API Endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/users/register` | Register new user |
| POST | `/users/login` | Authenticate (returns JWT) |
| POST | `/otp/request` | Request OTP (Redis-backed) |
| POST | `/accounts` | Create new account |
| GET | `/accounts/{id}` | View account details |
| GET | `/accounts/{id}/transactions` | Paginated transaction history |
| POST | `/transactions/transfer` | Perform transfer (idempotent) |
| POST | `/transactions/deposit` | Deposit funds |
| POST | `/transactions/withdraw` | Withdraw funds |
| GET | `/health` | Health check (Actuator) |
| GET | `/actuator/prometheus` | Prometheus metrics endpoint |

---

## 🧱 Project Structure

| Folder | Purpose |
|--------|----------|
| `domain/` | Pure business logic — entities, repositories, and services grouped by feature |
| `controller/` | Entry points (REST API endpoints) |
| `dto/` | Transport objects (request/response models) |
| `config/` | Application configurations (Spring Security, Kafka, Redis, etc.) |
| `exception/` | Centralized error handling |
| `util/` | Generic helper or utility classes |
| `resources/db/migration/` | Flyway scripts for database initialization |

## 🧰 Entity List
| Entity | Description |
|--------|-------------|
|User|	Represents a registered customer (with encrypted password, KYC info, etc.)
|Account|	Bank account (unique number, balance, currency, type)
|Card|	Virtual or physical card linked to an account
|Transaction|	Records all money movement (debit/credit, type, status, timestamps)
|OTPRequest|	Stores one-time passwords for login, transfer verification
|AuditLog|	Immutable log of all actions for traceability
|IdempotencyKey|	Stores processed API keys to avoid double charges

---
<!-- ## 🧪 Testing Strategy

- **Unit tests:** services, controllers, utils  
- **Integration tests:** Postgres + Redis + Kafka (via Testcontainers)  
- **Load testing:** k6 or Gatling scripts to simulate high-concurrency transfers  
---
 -->

## 📊 Observability Demo Dashboard (Grafana)

- Live Transactions/sec  
- Average Transfer Latency  
- Failed Transfer Rate  
- DB Connections & Pool Health  
- Redis Ops/sec & Memory  
- JVM Memory / GC Time  
- Queue Depth (Kafka / Stream / Outbox)

---

## 🐳 Local Development (Docker)

```bash
# Clone repo
git clone https://github.com/neurologialogic/bank-poc.git
cd bank-poc

# Start infrastructure (Postgres, Redis, Prometheus, Grafana)
docker compose up -d

# Run Spring Boot app
./mvnw spring-boot:run
```

Grafana → `http://localhost:3000`  
Prometheus → `http://localhost:9090`  
Jaeger → `http://localhost:16686`  
API → `http://localhost:8080`

---

<!-- ## ☸️ Kubernetes (Kind / Minikube)

```bash
# Build and deploy to local cluster
kubectl apply -f k8s/
```

Includes manifests for:
- Deployment
- Service
- ConfigMap / Secret
- Prometheus + Grafana integration
- Persistent Volumes for PostgreSQL

--- -->



<!-- ## 📈 Future Extensions

- 🔄 **Outbox Pattern Publisher** — guaranteed message delivery to Kafka  
- 🌍 **Multi-currency support**  
- 💳 **Simulated payments / transaction API product**  
- 🧑‍💼 **Admin dashboard for observability and audit logs**  
- ☁️ **Cloud-ready Helm charts for deployment**

--- -->

## 🧑‍💻 Author

**Patrick Kwon**  
📧 [patrickkwon.dev@gmail.com](mailto:patrickkwon.dev@gmail.com)  
🌐 [https://www.patrickkwon.my.id](https://www.patrickkwon.my.id)

---

## 📜 License

MIT License © 2025 Patrick Kwon
