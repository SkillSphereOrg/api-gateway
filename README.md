# SkillSphere API Gateway

## Overview

SkillSphere API Gateway is the single entry point for all SkillSphere microservices. It routes requests to Auth, Skill, and Reminder services, handles CORS, and exposes health endpoints. The gateway is cloud-native, stateless, and ready for production deployment.

## Tech Stack

- Java 21, Spring Boot 3
- Spring Cloud Gateway
- Spring Boot Actuator (health checks)
- Docker/Kubernetes/CI-ready

## Configuration

All configuration is environment-based. See `src/main/resources/application.properties` for details.

## CORS

- Configured to allow requests from `localhost:3000` (React dev) and any origin for demo.

## Running the Gateway

### Local

```sh
./gradlew bootRun
```

### Docker

```sh
docker build -t skillsphere-api-gateway .
docker run --env-file .env -p 8083:8083 skillsphere-api-gateway
```

## Actuator Endpoints

- `/actuator/health` — Health check
- `/actuator/info` — App info

---

Further documentation will be added as the service is developed.
