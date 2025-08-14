# Local Run (No Containers)

This guide explains how to run both services locally without Docker.

---

## 1) Prerequisites
- **Java 17 (JDK)**
- **MySQL 8** running on localhost:3306
- (Optional) Real SMTP server for emails (used by `notification-svc`)

---

## 2) Database Setup
**No manual database creation is required.**  
Both applications use:
- `spring.datasource.url=jdbc:mysql://localhost:3306/<db>?createDatabaseIfNotExist=true`
- `spring.jpa.hibernate.ddl-auto=update`

As long as your MySQL user has permission to create databases, the schemas will be created automatically on first run.

**Default DB names in the code:**
- `onlinestore`
- `notification-svc` (valid but contains a hyphen; if you prefer underscores, you can rename in properties to `notification_svc`)

---

## 3) Configure Credentials
Update the following files with your local MySQL credentials (do not commit real secrets):

**onlinestore — `apps/onlinestore/src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/onlinestore?createDatabaseIfNotExist=true
spring.datasource.username=<your-mysql-user>
spring.datasource.password=<your-mysql-password>
spring.jpa.hibernate.ddl-auto=update
