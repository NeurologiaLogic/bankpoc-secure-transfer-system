# 🏦 Bank POC — Secure, Observable, and Transaction-Safe Banking API

**Bank POC** is a production-minded proof-of-concept banking system built with **Spring Boot**, designed to demonstrate secure money transfers, data durability (WAL), safe concurrency handling, distributed caching, and full-stack observability — all runnable locally via Docker or Kubernetes.

This project is designed for **engineers, recruiters, and portfolio reviewers** who want to see how a single developer can build a bank-grade architecture with modern infrastructure and clean implementation.

---
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

### 🧭 Core User Flows

### **Flow 1: New User Onboarding & Account Creation**
> Goal: A new user registers, but their account remains inactive until KYC is verified.

1. **Registration**  
   `POST /auth/register`
    - Creates a new `User` record with:
        - `kyc_status = 'PENDING'`
        - `status = 'INACTIVE'`
    - Automatically creates a linked `Account` record:
        - `status = 'INACTIVE'`
    - Automatically provisions a virtual card:
        - `pin_hash = NULL` (cannot set PIN until KYC verified)

✅ **Outcome:**  
User is created but cannot set a PIN or transact until KYC verification is approved.

---

### **Flow 2: KYC Verification & Account Activation**
> Goal: Admin verifies the user's identity, activating their account.

1. **User submits KYC documents**  
   `POST /users/me/kyc`
    - Uploads ID card, selfie, or required info.

2. **Admin review and approval**  
   `PUT /admin/users/{userId}/kyc-approve`
    - Admin verifies documents and sets:
        - `users.kyc_status = 'VERIFIED'`
        - `users.status = 'ACTIVE'`
        - All linked accounts `status = 'ACTIVE'`

✅ **Outcome:**  
User’s account is now fully active and eligible to set a PIN and transact.

---

### **Flow 3: PIN Setup Wall (After KYC)**
> Goal: Enforce PIN setup for card-based authentication after KYC.

1. **Login**  
   `POST /auth/login`
    - If user is **inactive**, reject with message:  
      _"Account inactive. Complete KYC verification."_

    - If user is **active but has no PIN**, issue JWT with claim:  
      `"pin_status": "REQUIRED"`

2. **Set PIN**  
   `PUT /cards/{cardId}/pin`
    - User sets a secure transaction PIN.
    - System hashes and stores the PIN.

✅ **Outcome:**  
User’s account is now PIN-enabled and ready for transactions.

---

### **Flow 4: Returning User on a New Device (Device Trust via OTP)**
> Goal: Prevent unauthorized logins from untrusted devices.

1. **Login attempt**  
   `POST /auth/login`
    - System checks if the device (via fingerprint or hash of `User-Agent`) is trusted.

2. **If device is untrusted or first login:**  
   `POST /auth/otp/request` with  
   `{"purpose": "DEVICE_TRUST"}`
    - OTP sent to user’s registered phone/email.

3. **Verify OTP**  
   `POST /auth/otp/verify`
    - Upon success:
        - Adds record to `user_devices` table:
            - `is_trusted = TRUE`
            - `first_login_at` and `last_login_at` timestamps updated
        - JWT issued for session access

✅ **Outcome:**  
Device is marked trusted. Future logins skip OTP unless device changes.

---
### **Flow 5: Secure Money Transfer**
> User transfers funds to a beneficiary using a PIN — no OTP per transfer.

1. Optional: Add beneficiary

    `POST /users/me/beneficiaries`  

    `{"name": "Jane", "accountNumber": "...", "accountId": "..."}`

2. Initiate transfer
`POST /transactions`  
Request body:
    ```json
    {
      "type": "TRANSFER",
      "fromAccountId": "<user-account-uuid>",
      "toAccountId": "<beneficiary-account-uuid>",
      "amount": 100000,
      "currency": "IDR",
      "idempotencyKey": "<unique-key>",
      "security": {
        "pin": "4321"
      }
    }
    ```

    #### Backend Atomic Process:
   - Validate card PIN.
   - Lock both accounts (e.g., `FOR UPDATE`).
   - Debit & Credit in a single database transaction.
   - Write to `transactions` and `account_ledgers` tables.

✅ **Outcome:** Fast, PIN-secured transfer; no OTP friction for everyday usage.

---

### **Flow 6: View Dashboard & Transaction History**
> User opens the app to view balances and recent activity.

`GET /users/me` → User info  
`GET /accounts` → List of accounts + balances  
`GET /accounts/{accountId}/transactions` → Paginated list  
`GET /transactions/{transactionId}` → Detailed info

✅ **Outcome:** Real-time overview and history.

---

### **Flow 7: Card Management**
> User manages their card lifecycle (issue, block, PIN change).

`GET /accounts/{accountId}/cards` → List cards  
`POST /accounts/{accountId}/cards` → Request new virtual/physical card  
`PUT /cards/{cardId}/pin` → Change PIN  (This probably won't be implemented)
`PUT /cards/{cardId}/status` → Example: `{"action": "BLOCK"}`

✅ **Outcome:** Full card lifecycle control.

---

### **Flow 8: Observability & Audit**
> Admin or ops teams monitor status, metrics, and logs.

`GET /health` → Application health (Actuator)  
`GET /actuator/prometheus` → Metrics endpoint  
`GET /admin/audit-logs` → Immutable audit trail

✅ **Outcome:** Operational visibility, audit readiness.

---

## ⚙️ API Endpoint Groups

| Group | Endpoints | Purpose |
|--------|------------|----------|
| **Auth** | `POST /auth/register`<br>`POST /auth/login`<br>`POST /auth/otp/request`<br>`POST /auth/otp/verify` | Registration, login, OTP verification, and device trust |
| **Users** | `GET /users/me`<br>`PUT /users/me`<br>`POST /users/me/kyc` | Profile management and KYC submission |
| **Admin** | `PUT /admin/users/{userId}/kyc-approve` | Admin verifies KYC and activates user accounts |
| **Devices** | `GET /users/me/devices`<br>`DELETE /users/me/devices/{deviceId}` | View and revoke trusted devices |
| **Accounts** | `POST /accounts`<br>`GET /accounts`<br>`GET /accounts/{id}` | Account lifecycle management |
| **Cards** | `GET /accounts/{id}/cards`<br>`POST /accounts/{id}/cards`<br>`PUT /cards/{id}/pin`<br>`PUT /cards/{id}/status` | Card creation, PIN setup, blocking |
| **Transactions** | `POST /transactions`<br>`GET /accounts/{id}/transactions`<br>`GET /transactions/{id}` | Transfers, history, detail view |
| **Beneficiaries** | `GET /users/me/beneficiaries`<br>`POST /users/me/beneficiaries`<br>`DELETE /users/me/beneficiaries/{id}` | Manage trusted recipients |
| **System / Observability** | `GET /health`<br>`GET /actuator/prometheus`<br>`GET /admin/audit-logs` | Monitoring, metrics, audit logs |

---

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

Inside monitoring/prometheus change the Target IP
Inside the application.yaml at the resource folder 
change the jaeger ip to your local ip
# Start infrastructure (Postgres, Redis, Prometheus, Grafana)
docker compose up -d

Grafana -> 11378 (Spring Boot: https://grafana.com/grafana/dashboards/11378-justai-system-monitor/)

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
