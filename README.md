# onlinestore-suite

This repository contains two independent Spring Boot applications that work together:

- apps/onlinestore — monolithic e-commerce app (authentication, authorization, shopping flow).
- apps/notification-svc — notification microservice with scheduler and REST endpoints.

## Requirements
- Java 17
- MySQL 8 (local)
- (Optional) MailHog for email testing

## Default Ports
- onlinestore: 8080
- notification-svc: 8081

## How They Communicate
- onlinestore calls notification-svc over HTTP (REST). Ensure both apps are running.

## Quick Start (Local, no Docker)
1. Create two MySQL databases and users (see docs/LOCAL-RUN.md).
2. Configure application properties for each app (JDBC URL, username, password).
3. Run both apps from your IDE or `./gradlew bootRun`.
4. Verify:
   - onlinestore → http://localhost:8080
   - notification-svc → http://localhost:8081