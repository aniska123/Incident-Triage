# 🚨 Smart Incident Triage & Auto-Routing System

> A production-grade backend system that automatically classifies, prioritizes, and routes incident tickets to the right engineering team — built with Spring Boot, PostgreSQL, Redis, and RabbitMQ.

---

## 📌 Overview

Most engineering teams waste critical minutes manually triaging incidents. This system solves that by **automatically analyzing ticket content** and routing it to the correct team with the right priority — in milliseconds.

**Example:** A ticket with title _"Payment gateway is down"_ is automatically:
- Classified as `PAYMENT` category
- Escalated to `CRITICAL` priority
- Routed to the `Payments Team`
- Given a **1-hour SLA deadline**

— all without any human intervention.

---

## ✨ Features

- **Smart Classification** — Keyword-based engine classifies tickets into PAYMENT, AUTH, INFRASTRUCTURE, or GENERAL
- **Auto Priority Boost** — Routing rules automatically escalate priority based on incident type
- **Team Auto-Routing** — Tickets are assigned to the correct team instantly on creation
- **SLA Management** — Deadlines calculated per priority (CRITICAL=1h, HIGH=4h, MEDIUM=24h, LOW=72h)
- **JWT Authentication** — Stateless auth with BCrypt password hashing
- **Async Processing** — RabbitMQ message queue for non-blocking ticket processing
- **Redis Caching** — Team and routing rule data cached for performance
- **SLA Breach Scheduler** — Background job detects and flags SLA breaches every minute
- **REST API + Swagger UI** — Fully documented API at `/swagger-ui.html`

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.4.1 |
| Security | Spring Security, JWT (JJWT), BCrypt |
| Database | PostgreSQL 17, Spring Data JPA, Hibernate |
| Migrations | Flyway |
| Caching | Redis, Spring Cache |
| Messaging | RabbitMQ, Spring AMQP |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| DevOps | Docker |

---

## 🏗️ Architecture

```
Client Request
      │
      ▼
┌─────────────────┐
│  Spring Security │  ← JWT Authentication Filter
│  Filter Chain   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  REST Controller │  ← TicketController, AuthController
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│              Service Layer              │
│  ┌──────────────┐  ┌─────────────────┐  │
│  │ Classification│  │ RoutingService  │  │
│  │   Service    │  │ (team matching) │  │
│  └──────────────┘  └─────────────────┘  │
│  ┌──────────────┐  ┌─────────────────┐  │
│  │  SlaService  │  │  TicketProducer │  │
│  │ (deadlines)  │  │  (RabbitMQ)     │  │
│  └──────────────┘  └─────────────────┘  │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────┐     ┌──────────┐     ┌───────────┐
│   PostgreSQL    │     │  Redis   │     │ RabbitMQ  │
│  (persistence)  │     │ (cache)  │     │ (async)   │
└─────────────────┘     └──────────┘     └───────────┘
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker Desktop
- Maven

### 1. Start Infrastructure

```bash
# PostgreSQL
docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=incident_triage postgres

# Redis
docker run -d -p 6379:6379 redis

# RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:management
```

### 2. Configure Application

Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/incident_triage
    username: postgres
    password: postgres
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`

### 4. Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |

### Tickets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tickets` | Create ticket (auto-routes) |
| GET | `/api/tickets` | Get all tickets |
| GET | `/api/tickets/{id}` | Get ticket by ID |
| PUT | `/api/tickets/{id}/status` | Update ticket status |

### Teams
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/teams` | Get all teams |
| POST | `/api/teams` | Create a team |

---

## 🔄 How Auto-Routing Works

```
Ticket Created
      │
      ▼
ClassificationService
  → Scans title + description for keywords
  → Assigns TicketCategory (PAYMENT/AUTH/INFRASTRUCTURE/GENERAL)
      │
      ▼
RoutingService
  → Queries routing_rules table for keyword matches
  → Finds matching team
  → Applies priority boost from matching rule
  → Falls back to "General Support" if no match
      │
      ▼
SlaService
  → Calculates SLA deadline based on final priority
  → CRITICAL → 1 hour
  → HIGH     → 4 hours
  → MEDIUM   → 24 hours
  → LOW      → 72 hours
      │
      ▼
Ticket saved with team + priority + SLA deadline
```

---

## 🔐 Security

- Passwords hashed with **BCrypt** (cost factor 10)
- **JWT tokens** with 24-hour expiry
- **Stateless sessions** — no server-side session storage
- All `/api/**` endpoints require valid Bearer token
- `/auth/**` endpoints are publicly accessible

---

## 📊 Database Schema

```sql
teams          → id, name, email, skill_tags[], created_at
users          → id, username, email, password, role, created_at
tickets        → id, title, description, status, priority, category,
                 team_id, sla_deadline, sla_breached, created_at, updated_at
routing_rules  → id, keyword, team_id, priority_boost, category, created_at
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

Test coverage includes:
- `TicketServiceTest` — ticket creation, routing validation, SLA assignment
- `RoutingServiceTest` — keyword matching, team assignment, fallback logic

---
---

## 🧪 Example API Test Cases

### Test 1: Auth — Register a User
```
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@company.com",
  "password": "secret123"
}
```
**Expected:** `201 Created` with user details

---

### Test 2: Auth — Login
```
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "secret123"
}
```
**Expected:** `200 OK` with JWT token

---

### Test 3: Create a Payment Incident
```
POST /api/tickets
Authorization: Bearer <your_token>

title=Payment gateway is down
description=Users cannot complete transactions since 3pm
```
**Expected:** Auto-routed to `Payments Team`, priority `CRITICAL`

---

### Test 4: Create an Auth Incident
```
POST /api/tickets
Authorization: Bearer <your_token>

title=Login page is broken
description=Users cannot sign in, getting 401 errors
```
**Expected:** Auto-routed to `Auth Team`, priority `HIGH`

---

### Test 5: Create an Infrastructure Incident
```
POST /api/tickets
Authorization: Bearer <your_token>

title=Server crash in production
description=Main application server is down, complete outage
```
**Expected:** Auto-routed to `Infrastructure Team`, priority `CRITICAL`

---

### Test 6: Update Ticket Status
```
PUT /api/tickets/1/status
Authorization: Bearer <your_token>

status=IN_PROGRESS
```
**Expected:** Ticket status updated to `IN_PROGRESS`

---

### Test 7: Get All Tickets
```
GET /api/tickets
Authorization: Bearer <your_token>
```
**Expected:** List of all tickets with team, priority, category

## 📁 Project Structure

```
src/main/java/com/aniska/incident_triage/
├── config/          # Security, Redis, RabbitMQ configuration
├── controller/      # REST API endpoints
├── service/         # Business logic (classification, routing, SLA)
├── repository/      # JPA repositories
├── model/           # JPA entities (Ticket, Team, User, RoutingRule)
├── dto/             # Request/Response DTOs
├── enums/           # TicketStatus, Priority, TicketCategory
├── messaging/       # RabbitMQ producer and consumer
├── scheduler/       # SLA breach detection scheduler
├── security/        # JWT filter and utility
└── exception/       # Global exception handler



## 📄 License

MIT License — feel free to use this project as a reference or starting point.
