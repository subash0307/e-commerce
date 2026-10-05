# 🛍️ ApexStore - Enterprise Full-Stack E-Commerce Platform

A production-grade E-Commerce Web Application built from scratch using **Java 21**, **Spring Boot 3.3**, **Spring Data JPA**, **MySQL**, and **Bootstrap 5**.

---

## 🏛️ Architecture & Design Principles

The application is structured using the **Layered Architecture Pattern (N-Tier)**:

```
src/main/java/com/ecommerce/
│
├── config/              # Spring Configuration beans, SecurityFilterChain, CORS
├── controller/          # RESTful APIs (@RestController) & MVC Controllers (@Controller)
├── dto/                 # Data Transfer Objects (request/response validation)
├── entity/              # JPA Relational Entities mapped to MySQL tables
├── exception/           # Centralized Exception Handling (@RestControllerAdvice)
├── repository/          # Spring Data JPA interfaces for data access
└── service/             # Core Business Logic & Transaction boundaries (@Transactional)
```

---

## 🚀 Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 LTS |
| **Framework** | Spring Boot 3.3.0 |
| **ORM / Data Access** | Spring Data JPA, Hibernate ORM |
| **Connection Pooling** | HikariCP |
| **Database** | MySQL 8.x / MariaDB |
| **View Engine** | Thymeleaf + Bootstrap 5.3 + Bootstrap Icons |
| **Build & Tooling** | Apache Maven |
| **Version Control** | Git & GitHub |

---

## ⚙️ Configuration & Quick Start

### 1. Database Setup
Ensure MySQL is running on `localhost:3306`. Initialize the schema:
```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Configure your credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Build & Run

#### Option A: Quick Start (In-Memory Database, zero setup)
Run with the `local` profile (uses embedded H2 database in MySQL compatibility mode):
```powershell
.\mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
Or using installed Maven:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

#### Option B: Full MySQL Setup
Ensure MySQL is running on `localhost:3306` with the credentials configured above, then:
```powershell
.\mvnw spring-boot:run
```

---

## 🌐 URLs & Endpoints

Access the application in your browser:
* **Storefront Home**: [http://localhost:8080](http://localhost:8080)
* **REST Health Check**: [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
* **H2 Console** *(local profile)*: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  * JDBC URL: `jdbc:h2:mem:ecommerce_db`
  * Username: `sa`
  * Password: *(leave empty)*

