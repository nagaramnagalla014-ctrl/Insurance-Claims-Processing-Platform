# Insurance Claims Processing Platform

**Duration:** March 2019 – February 2020

## Description

A digital claims platform where policyholders can upload accident documents and track claim status in real time. Claims adjusters review evidence, communicate with customers, and process settlements through an integrated workflow. Features Elasticsearch-powered claim search for instant retrieval across all claims.

## Technologies

- **Backend:** Java 11, Spring Boot 2.2.6, Spring Security, Hibernate, Oracle DB
- **Search:** Elasticsearch 7.6 (Spring Data Elasticsearch)
- **Frontend:** React 16.13, React Router 5, Axios, Bootstrap 4
- **Infrastructure:** Docker, Docker Compose, Jenkins CI/CD, SonarQube
- **Auth:** JWT Bearer tokens, Role-Based Access Control

## Architecture

```
Insurance-Claims-Processing-Platform/
├── backend/
│   └── src/main/java/com/insurance/claims/
│       ├── config/         # JWT, Security, Elasticsearch, CORS, DataInitializer
│       ├── model/          # JPA Entities (7)
│       ├── elasticsearch/  # ClaimSearchDocument + Repository
│       ├── repository/     # Spring Data JPA Repositories (7)
│       ├── service/        # Business Logic (8 services)
│       ├── controller/     # REST Controllers (7)
│       ├── dto/            # Request/Response DTOs (8)
│       └── exception/      # Global Exception Handler
├── frontend/
│   └── src/
│       ├── api/            # Axios instance with JWT interceptor
│       ├── context/        # AuthContext (React Context API)
│       ├── components/     # Navbar, PrivateRoute
│       └── pages/          # Login, Register, Dashboard, Claims, AdjusterQueue, Admin
├── Dockerfile.backend
├── Dockerfile.frontend
├── docker-compose.yml
├── nginx.conf
├── Jenkinsfile
├── sonar-project.properties
└── README.md
```

## Claim Lifecycle

```
Policyholder Submits → SUBMITTED
          ↓
Adjuster Assigned → UNDER_REVIEW
          ↓
Adjuster Assesses Damage → ASSESSMENT → PENDING_SETTLEMENT
          ↓
Manager Settles → SETTLED  OR  Manager Rejects → REJECTED
```

## User Roles

| Role        | Capabilities |
|-------------|--------------|
| POLICYHOLDER | Submit claims, upload documents, track status, message adjuster |
| ADJUSTER    | Pick up open claims, review documents, submit damage assessment |
| MANAGER     | Process settlements, reject claims, full adjuster access + admin |
| ADMIN       | Full access — user management, all claims, system dashboard |

## Elasticsearch Features

- Full-text search across claim description, incident location, claimant name
- Filter by status, policy number, email
- Async indexing (non-blocking — claim save succeeds even if ES is unavailable)
- Index: `claims` (ES 7.x single-type `_doc`)

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register policyholder |
| POST | /api/auth/login | Login → JWT |
| GET | /api/auth/me | Current user |

### Claims
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/claims | Submit claim |
| GET | /api/claims/my | My claims |
| GET | /api/claims/{id} | Claim detail |
| GET | /api/claims/track/{claimNumber} | Public tracking |
| GET | /api/claims/open | Open claims queue |
| POST | /api/claims/{id}/assign | Adjuster self-assign |
| GET | /api/claims/search?q=... | Elasticsearch search |

### Documents
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/documents/upload/{claimId} | Upload document |
| GET | /api/documents/claim/{claimId} | List documents |
| PUT | /api/documents/{docId}/verify | Verify/reject document |

### Assessment (Adjuster+)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/assessments/queue | Claims ready for assessment |
| POST | /api/assessments/claim/{claimId} | Submit assessment |
| GET | /api/assessments/claim/{claimId} | Get assessment |

### Settlements (Manager+)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/settlements/pending | Pending settlements |
| POST | /api/settlements/claim/{claimId} | Process settlement |
| POST | /api/settlements/claim/{claimId}/reject | Reject claim |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/dashboard | Stats overview |
| GET | /api/admin/claims | All claims |
| GET | /api/admin/users | All users |
| PUT | /api/admin/users/{id}/toggle | Activate/deactivate user |

## Setup

### Docker (Full Stack)
```bash
cd backend && mvn clean package -DskipTests && cd ..
docker-compose up --build -d
# App: http://localhost
# Backend: http://localhost:8080
# Elasticsearch: http://localhost:9200
```

### Local Development
1. Start Oracle XE 21c (port 1521)
2. Start Elasticsearch 7.6 (port 9200)
3. `cd backend && mvn spring-boot:run`
4. `cd frontend && npm install && npm start` (port 3000, proxies to 8080)

### SonarQube Analysis
```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=$SONAR_TOKEN
```

## Demo Accounts

| Email | Password | Role |
|-------|----------|------|
| john.doe@insurance.com | password123 | POLICYHOLDER |
| jane.smith@insurance.com | password123 | POLICYHOLDER |
| adjuster@insurance.com | admin123 | ADJUSTER |
| manager@insurance.com | admin123 | MANAGER |
| admin@insurance.com | admin123 | ADMIN |

Seeded policies:
- POL-2019-001: Auto (John Doe, ₹5L)
- POL-2019-002: Home (John Doe, ₹20L)
- POL-2019-003: Health (Jane Smith, ₹3L)
- POL-2019-004: Travel (Jane Smith, ₹1L)
