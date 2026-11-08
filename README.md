src/main/resources/application.yml

# Personal Finance Aggregator

A full-stack web application that connects to real bank accounts via the Plaid API, aggregates transactions, and lets users track spending by category and set monthly budgets.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.1.1 |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Database | PostgreSQL 15, Spring Data JPA, Flyway |
| Frontend | Angular 19, NgRx, TypeScript |
| Banking API | Plaid (sandbox) |
| Build | Maven, Angular CLI |

## Features

- **JWT authentication** — register, login, stateless token-based sessions
- **Plaid Link integration** — connect sandbox bank accounts through Plaid's hosted UI
- **Transaction sync** — pulls and stores categorized transactions from connected accounts
- **Nightly background job** — `@Scheduled` task syncs all connected accounts automatically at 2am
- **Spending dashboard** — breakdown of spending by category with recent transaction list
- **Transaction browser** — paginated transaction list with category filtering
- **Budget tracker** — create and delete monthly budgets per spending category

## Prerequisites

- Java 21
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)
- Docker (for PostgreSQL)
- A free [Plaid sandbox account](https://dashboard.plaid.com)

## Getting Started

### 1. Start PostgreSQL

```bash
docker run --name finance-postgres -e POSTGRES_PASSWORD=pass -p 5432:5432 -d postgres:15
docker exec -it finance-postgres psql -U postgres -c "CREATE DATABASE financedb;"
```

### 2. Configure the backend

Edit `backend/src/main/resources/application.yml` and set your Plaid credentials:

```yaml
app:
  plaid:
    client-id: your_plaid_client_id
    secret: your_plaid_sandbox_secret
    environment: sandbox
  jwt:
    secret: your-secret-key-at-least-32-characters-long
    expiration-ms: 86400000
```

> Never commit real credentials. Add `application.yml` to `.gitignore` or use environment variables.

### 3. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

Flyway will automatically create the database schema on first startup.

### 4. Run the frontend

```bash
cd finance-frontend
ng serve --proxy-config proxy.config.json
```

Open [http://localhost:4200](http://localhost:4200).

## Project Structure

```
backend/
├── src/main/java/com/example/finance/
│   ├── auth/           # JWT filter, SecurityConfig, AuthController
│   ├── user/           # User entity and repository
│   ├── plaid/          # Plaid service, config, controller, sync job
│   ├── transaction/    # Transaction entity, repository, controller, DTO
│   └── budget/         # Budget entity, repository, controller
└── src/main/resources/
    ├── application.yml
    └── db/migration/   # Flyway SQL migrations

finance-frontend/
└── src/app/
    ├── auth/           # Login and register components
    ├── dashboard/      # Spending overview and Plaid connect
    ├── transactions/   # Paginated transaction list with filtering
    ├── budget/         # Budget CRUD
    └── services/       # HTTP services for auth, transactions, budgets, Plaid
```

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create account, returns JWT |
| POST | `/api/auth/login` | No | Login, returns JWT |
| GET | `/api/transactions` | Yes | Paginated transactions, optional `?category=` filter |
| GET | `/api/budgets` | Yes | List all budgets for current user |
| POST | `/api/budgets` | Yes | Create a budget |
| DELETE | `/api/budgets/{id}` | Yes | Delete a budget |
| POST | `/api/plaid/link-token` | Yes | Get a Plaid Link token |
| POST | `/api/plaid/exchange-token` | Yes | Exchange public token, trigger sync |

## Plaid Sandbox Test Credentials

When prompted by Plaid Link:

| Field | Value |
|---|---|
| Phone number | 415-555-0011 |
| Username | `user_good` |
| Password | `pass_good` |
| MFA code | `1234` |

## Database Schema

Four tables managed by Flyway:

- `users` — email and bcrypt-hashed password
- `plaid_items` — access tokens for connected bank accounts
- `transactions` — synced transactions with category, amount, merchant, date
- `budgets` — monthly category budgets per user

## Resume Highlights

- Integrated Plaid API OAuth flow (link token → public token → access token exchange)
- Implemented JWT stateless auth with Spring Security and a functional Angular HTTP interceptor
- Managed schema evolution with Flyway versioned migrations; `ddl-auto=validate` prevents Hibernate from modifying the schema
- Used Java 17 records for immutable DTOs
- Implemented nightly `@Scheduled` + `@Async` background job for transaction sync
- Paginated and filtered REST endpoints with Spring Data `Pageable` and `Sort`
- NgRx store with effects and entity adapter for frontend state management
