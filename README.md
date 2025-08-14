# onlinestore-suite

This repository contains two independent Spring Boot applications that work together:

- `apps/onlinestore` — monolithic e-commerce application (auth, product management, cart/checkout, accounts).
- `apps/notification-svc` — notification microservice with scheduler and REST endpoints.

> **Current scope:** local run only (no containers). Docker/Compose, CI/CD, and production readiness are planned in the Roadmap.

## Requirements
- Java 17 (JDK)
- MySQL 8 running locally
- (Optional) Access to a real SMTP server (required if emails are sent)

## Default Ports
- onlinestore: **8080**
- notification-svc: **8081**
- MySQL: **3306**

## Service-to-service Communication
- The apps communicate over **HTTP** using **OpenFeign** from `onlinestore` to `notification-svc`.
- Base path in `notification-svc`: `/api/v1/notifications`.
- Note: In the code, the Feign client uses `localhost:8081/...`. It is recommended to include the scheme explicitly: `http://localhost:8081/api/v1/notifications`.

## Quick Start (Local, No Containers)
1. Start **MySQL 8** locally (ensure credentials you will use are valid).
2. **No manual DB creation required.** Both apps are configured to create databases automatically (`createDatabaseIfNotExist=true`) and manage schema with `spring.jpa.hibernate.ddl-auto=update`.
3. Adjust DB credentials in each app’s `application.properties`:
   - `apps/onlinestore/src/main/resources/application.properties`
   - `apps/notification-svc/src/main/resources/application.properties`
4. (Optional) Configure a real SMTP server in `notification-svc` if email sending is required.
5. Start the applications:
   - `apps/onlinestore` → `./gradlew bootRun` (port **8080**)
   - `apps/notification-svc` → `./gradlew bootRun` (port **8081**)
6. Verify:
   - `notification-svc` endpoints (examples):  
     - `POST http://localhost:8081/api/v1/notifications/welcome?userId=<UUID>`
     - `POST http://localhost:8081/api/v1/notifications/toggle?userId=<UUID>`
   - `onlinestore` will call `notification-svc` via Feign for notification features.


