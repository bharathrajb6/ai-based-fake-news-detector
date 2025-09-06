# AI-based Fake News Detector

A full‑stack, microservices-based application that evaluates news claims for authenticity and source credibility. It combines traditional fact-check retrieval/verification, source credibility scoring, and an AI NLI (Natural Language Inference) model to assess claims.

## What you get
- API Gateway with JWT auth endpoints and routing
- User Service (MySQL, JPA) for registration, login validation, and profile management
- News Management Service (Kafka integration) to submit and track news checks
- Fact Check Service to retrieve and synthesize claim reviews
- Source Credibility Service to rate source reliability
- AI Model Service (Python/FastAPI) providing NLI-based verification
- Vite + React frontend (TypeScript)

## Architecture at a glance
- API Gateway (Spring Boot WebFlux) on port 8080
  - `/login`, `/register`
  - Proxies `/api/users/**` to User Service (8081)
  - Proxies `/api/news/**` to News Management Service (8082)
- User Service (Spring Boot, JPA) on port 8081
- News Management Service (Spring Boot) on port 8082
- Fact Check Service (Spring Boot) default 8083 (configure as needed)
- Source Credibility Service (Spring Boot) default 8094 (as per frontend default)
- AI Model Service (FastAPI) default 8000 (configure as needed)
- Frontend (Vite) on port 5173

Core data flows:
- Login → API Gateway → User Service `/api/users/validate` → JWT issued by Gateway
- Register → API Gateway → User Service `/api/users/register` → JWT issued by Gateway
- Check News → API Gateway → News Management Service
- News Management Service interacts with Fact Check Service, Source Credibility Service, and AI Model Service

## Prerequisites
- Java 17+
- Maven 3.9+ (or use included `mvnw` wrappers)
- Node.js 18+ and Yarn or npm
- Python 3.10+
- MySQL 8.x running locally
- (Optional) Kafka if you enable end-to-end eventing

## Configuration

### Database (User Service)
`user-service/src/main/resources/application.yml`
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/userService
    username: root
    password: <your_password>
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  main:
    web-application-type: reactive
    allow-bean-definition-overriding: true
server:
  port: 8081
```
Create the database once:
```sql
CREATE DATABASE userService CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### API Gateway routing
`api-gateway/src/main/resources/application.yml` controls routes. Defaults:
- Gateway on 8080
- Routes `/api/users/**` → `http://localhost:8081`
- Routes `/api/news/**` → `http://localhost:8082`

CORS is enabled for local dev in `api-gateway/config/CorsConfig.java`.

### Frontend environment
`frontend/src/lib/api.ts` reads:
- `VITE_API_BASE` (default `http://localhost:8080`)
- `VITE_CRED_BASE` (default `http://localhost:8094`)

Create `frontend/.env.local` if needed:
```
VITE_API_BASE=http://localhost:8080
VITE_CRED_BASE=http://localhost:8094
```

### AI Model Service
`AI-Model-Service/requirements.txt` defines Python deps. Default FastAPI port is typically 8000—adjust if needed and update downstream callers.

## Run locally (quick start)

Open 6 terminals (or run in background) and start in this order:

1) User Service (8081)
```
cd "user-service"
./mvnw -DskipTests spring-boot:run
```

2) API Gateway (8080)
```
cd "api-gateway"
./mvnw -DskipTests spring-boot:run
```

3) News Management Service (8082)
```
cd "news-management-service"
./mvnw -DskipTests spring-boot:run
```

4) Source Credibility Service (8094)
```
cd "source-credibility-service"
./mvnw -DskipTests spring-boot:run
```

5) Fact Check Service (8083 default)
```
cd "fact-check-service"
./mvnw -DskipTests spring-boot:run
```

6) AI Model Service (FastAPI, 8000)
```
cd "AI-Model-Service"
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```

7) Frontend (5173)
```
cd "frontend"
yarn install  # or npm install
yarn dev      # or npm run dev
```
Open `http://localhost:5173` in your browser.

## Using the app

- Register at `Register` with: firstName, lastName, email, username, password, contactNumber
- Login with username/password → API Gateway returns `{ token }`
- The frontend stores the token and sets headers for subsequent calls:
  - `Authorization: Bearer <token>`
  - `X-Username: <username>`
- Check News: submit headline/author/source site
- View History: paginated results
- Evaluate Source: call Source Credibility Service directly

## Endpoints (selected)

Gateway (8080):
- `POST /login` { username, password } → `{ token }`
- `POST /register` { user fields } → `{ token }`
- Proxies:
  - `/api/users/**` → User Service (8081)
  - `/api/news/**` → News Management (8082)

User Service (8081):
- `POST /api/users/validate` { username, password } → `true|false`
- `POST /api/users/register` { user fields } → `true`
- `GET /api/users/getUserDetails` (requires `Authorization` + `X-Username`)
- `PUT /api/users/updateUserDetails`
- `PUT /api/users/updatePassword`

Frontend defaults for Source Credibility (8094):
- `POST /credibility/evaluate` { site, headline? }

## Development notes

- Java version: 17 (pom.xml in each service)
- MapStruct and Lombok are configured for annotation processing
- Spring Boot Maven plugin repackage goal is enabled for runnable jars
- Web stack uses WebFlux (reactive) in Gateway and User Service
- CORS: `CorsWebFilter` in gateway allows typical localhost dev origins (5173/3000)

## Troubleshooting

- Frontend shows “Failed to fetch” on login
  - Ensure Gateway (8080) and User Service (8081) are running
  - Check CORS: Gateway `CorsConfig` must include your frontend origin
  - See Gateway logs for 4xx/5xx; `502 Bad Gateway` usually means the User Service isn’t reachable

- `No qualifying bean of type 'UserMapper'`
  - Make sure `user-service/pom.xml` has MapStruct + Lombok processors configured
  - Build the service (`./mvnw -DskipTests clean package`) to generate `UserMapperImpl`

- MySQL connection issues
  - Verify DB `userService` exists and credentials match `application.yml`
  - Ensure MySQL is listening on `localhost:3306`

- Ports
  - 8080 (Gateway), 8081 (User), 8082 (News), 8083 (Fact Check), 8094 (Credibility), 8000 (AI Model), 5173 (Frontend)

## Scripts and helpful commands

Build all Java services:
```
for svc in api-gateway user-service news-management-service fact-check-service source-credibility-service; do \
  (cd "$svc" && ./mvnw -DskipTests clean package); done
```

Run all Java services (each in its own shell):
```
(cd api-gateway && ./mvnw -DskipTests spring-boot:run)
(cd user-service && ./mvnw -DskipTests spring-boot:run)
(cd news-management-service && ./mvnw -DskipTests spring-boot:run)
(cd fact-check-service && ./mvnw -DskipTests spring-boot:run)
(cd source-credibility-service && ./mvnw -DskipTests spring-boot:run)
```

## License
MIT (or your preferred license)