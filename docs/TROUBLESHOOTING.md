# Troubleshooting

Common issues and quick fixes when running the suite locally.

## 1) Database connection / auto-creation

**Symptoms**
- App fails to start with `Unknown database` or `Access denied for user`.
- Tables are not created on first run.

**Checklist**
- MySQL 8 is running on `localhost:3306`.
- JDBC URL includes `createDatabaseIfNotExist=true`, e.g.:

- The MySQL user has permissions to create databases/schemas.
- `spring.jpa.hibernate.ddl-auto=update` is set (as in the project defaults).

**Extra**
- If you renamed the DB (e.g., prefer `notification_svc` instead of `notification-svc`), update the JDBC URL accordingly.

## 2) Port already in use (8080/8081)

**Symptoms**
- Startup error: `Web server failed to start. Port 8080/8081 already in use`.

**Fix**
- Stop the conflicting process or change the server port:

- Windows: `netstat -ano | findstr :8080` to locate the PID.

## 3) Service-to-service calls fail (Feign)

**Symptoms**
- onlinestore cannot reach notification-svc.
-  `Connection refused` or `Unknown host`.

**Checklist**
- Start order: run `notification-svc` first, then `onlinestore`.
- Use an explicit scheme in Feign base URL:  
`http://localhost:8081/api/v1/notifications` (recommended)
- Verify the base path hasn’t changed (`/api/v1/notifications`).

## 4) SMTP / email sending (notification-svc)

**Symptoms**
- Authentication errors or TLS handshake issues.

**Checklist**
- Correct host/port (e.g., 587 for TLS).
- `spring.mail.properties.mail.smtp.auth=true`
- `spring.mail.properties.mail.smtp.starttls.enable=true`
- Use an app-specific password if your provider requires it (e.g., Gmail with 2FA).

> Do not commit real SMTP credentials. Prefer environment variables or a local-only properties file ignored by Git.

## 5) Java / Gradle issues

**Symptoms**
- Build fails with source/target mismatch.
- Random Gradle cache errors.

**Fix**
- Ensure JDK **17** is used by the IDE and `GRADLE_HOME`.
- Clean caches:  

- If needed, remove `.gradle/` in the project root and re-run.

## 6) Windows path length / file encoding

**Symptoms**
- Strange I/O errors on Windows when building.

**Fix**
- Enable long paths in Windows settings or Git config:  
`git config --system core.longpaths true`
- Ensure UTF‑8: add `characterEncoding=UTF-8` to JDBC URL.

## 7) Quick sanity checklist

- MySQL up and reachable at `localhost:3306`.
- JDBC URLs point to the right DB names.
- Both services running: `onlinestore:8080`, `notification-svc:8081`.
- Feign base URL includes `http://` and the correct base path.


