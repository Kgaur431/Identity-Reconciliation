# Bitespeed Identity Reconciliation API

A backend service for unifying customer identities across multiple purchases, even when different emails and phone numbers are used. Powered by Spring Boot, Gradle, PostgreSQL, and Docker.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Business Logic](#business-logic)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Build & Run](#build--run)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Live Demo](#live-demo)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Project Overview

**Bitespeed Identity Reconciliation** is designed to help e-commerce platforms like FluxKart.com deliver personalized customer experiences, even when users make purchases with different contact details. By intelligently linking purchases made with various emails and phone numbers, the service consolidates customer data and ensures loyalty is recognized—no matter how many identities a user creates.

---

## Business Logic

- Each purchase event includes an email and/or phone number and is stored as a `Contact` in the database.
- Contacts are linked if they share either an email or a phone number.
- The **oldest contact** among linked contacts is marked as `"primary"`, while others are `"secondary"`.
- When an identify request is made via `/identify`:
    - If the email or phone number matches existing contacts, all related contacts are traversed and consolidated.
    - New information results in a new secondary contact linked to the primary.
    - If no match is found, a new primary contact is created.
    - If two primaries are merged, the older remains primary and the other is demoted to secondary.
- The API response returns the primary contact’s ID, all associated emails and phone numbers, and all secondary contact IDs.

**Example response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["lorraine@hillvalley.edu", "mcfly@hillvalley.edu"],
    "phoneNumbers": ["123456"],
    "secondaryContactIds": [23]
  }
}
```

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Gradle**
- **PostgreSQL**
- **Docker**
- **Swagger/OpenAPI 3.0** (for API docs)
- Deployed on **Render**

---

## Getting Started

### Prerequisites

- Java 17
- Gradle
- Docker (optional, for containerized deployment)
- PostgreSQL database

### Clone & Setup

```bash
git clone https://github.com/<your-username>/identity-reconciliation.git
cd identity-reconciliation
```

---

## Configuration

The application requires the following environment variables (especially when running on Render):

| Variable                  | Description                                      |
|---------------------------|--------------------------------------------------|
| JDBC_DATABASE_HOST        | PostgreSQL host (provided by Render)             |
| JDBC_DATABASE_PORT        | PostgreSQL port (usually 5432)                   |
| JDBC_DATABASE_NAME        | Database name                                    |
| JDBC_DATABASE_USERNAME    | Database user                                    |
| JDBC_DATABASE_PASSWORD    | Database password                                |

**Sample `application-dev.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://${JDBC_DATABASE_HOST}:${JDBC_DATABASE_PORT}/${JDBC_DATABASE_NAME}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
```

---

## Database Setup

**Local:**
- Run PostgreSQL locally (`docker run -p 5432:5432 ...` or use a local install).
- Ensure your database credentials match the environment variables.

**Render:**
- Use Render’s PostgreSQL service and link it to your web service.
- Environment variables will be auto-injected.

---

## Build & Run

> **Note:** The project uses a dynamic `application-dev.properties` file with environment variables for configuration.

### **Running the Project (Development Mode)**

In **PowerShell**:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew bootRun
```

### **Building the Project**

In **PowerShell**:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew build
```

- Make sure the required environment variables for your database are set before running or building.

---

## API Documentation

Interactive API docs are available via **Swagger UI**:

**[Swagger UI (deployed on Render)](https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html)**  
_Note: This may take 1-2 minutes to load on the Render free tier._

### Main Endpoint

#### `POST /identify`

- **Request Body:**
    ```json
    {
      "email": "string",
      "phoneNumber": "string"
    }
    ```
- **Response:**
    ```json
    {
      "contact": {
        "primaryContatctId": 0,
        "emails": ["string"],
        "phoneNumbers": ["string"],
        "secondaryContactIds": [0]
      }
    }
    ```
- See full schema and try it out in Swagger UI.

---

## Deployment

### Render

- Push your code to GitHub.
- Create a new **Web Service** on Render using your repo.
- Set “Runtime” to Docker.
- Make sure your environment variables (`JDBC_DATABASE_*`) are set or auto-injected from the linked database.
- On first deploy, it may take a few minutes for the service and Swagger UI to be live.

### Docker (local or other platforms)

```bash
docker build -t identity-reconciliation .
docker run -p 8080:8080 ^
  -e JDBC_DATABASE_HOST=localhost ^
  -e JDBC_DATABASE_PORT=5432 ^
  -e JDBC_DATABASE_NAME=yourdbname ^
  -e JDBC_DATABASE_USERNAME=youruser ^
  -e JDBC_DATABASE_PASSWORD=yourpassword ^
  -e SPRING_PROFILES_ACTIVE=dev ^
  identity-reconciliation
```

> **Tip:** Use `^` for multi-line commands in PowerShell, or `\` in bash.

---

## Live Demo

You can access the deployed application here:  
**[https://identity-reconciliation-api-vtdk.onrender.com](https://identity-reconciliation-api-vtdk.onrender.com)**

Swagger docs:  
**[https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html](https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html)**

---

## Troubleshooting

- **Swagger UI slow to load?**  
  This is normal on Render’s free tier—please wait 1-2 minutes after starting.
- **Database connection refused?**  
  Check your environment variables and ensure the DB host is correct (not `localhost` on Render).
- **No contacts returned?**  
  Ensure your database is up and your request body matches the expected format.

---

## Contributing

Contributions, issues, and feature requests are welcome!  
Feel free to fork the repo and submit a pull request.

---

## License

[MIT](LICENSE) (or specify your license)

---

## Contact

Project by **Kartik Gour**  
Email: [sharmakartik541@gmail.com](mailto:sharmakartik541@gmail.com)
# Bitespeed Identity Reconciliation API

A backend service for unifying customer identities across multiple purchases, even when different emails and phone numbers are used. Powered by Spring Boot, Gradle, PostgreSQL, and Docker.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Business Logic](#business-logic)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Build & Run](#build--run)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Live Demo](#live-demo)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Project Overview

**Bitespeed Identity Reconciliation** is designed to help e-commerce platforms like FluxKart.com deliver personalized customer experiences, even when users make purchases with different contact details. By intelligently linking purchases made with various emails and phone numbers, the service consolidates customer data and ensures loyalty is recognized—no matter how many identities a user creates.

---

## Business Logic

- Each purchase event includes an email and/or phone number and is stored as a `Contact` in the database.
- Contacts are linked if they share either an email or a phone number.
- The **oldest contact** among linked contacts is marked as `"primary"`, while others are `"secondary"`.
- When an identify request is made via `/identify`:
    - If the email or phone number matches existing contacts, all related contacts are traversed and consolidated.
    - New information results in a new secondary contact linked to the primary.
    - If no match is found, a new primary contact is created.
    - If two primaries are merged, the older remains primary and the other is demoted to secondary.
- The API response returns the primary contact’s ID, all associated emails and phone numbers, and all secondary contact IDs.

**Example response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["lorraine@hillvalley.edu", "mcfly@hillvalley.edu"],
    "phoneNumbers": ["123456"],
    "secondaryContactIds": [23]
  }
}
```

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Gradle**
- **PostgreSQL**
- **Docker**
- **Swagger/OpenAPI 3.0** (for API docs)
- Deployed on **Render**

---

## Getting Started

### Prerequisites

- Java 17
- Gradle
- Docker (optional, for containerized deployment)
- PostgreSQL database

### Clone & Setup

```bash
git clone https://github.com/<your-username>/identity-reconciliation.git
cd identity-reconciliation
```

---

## Configuration

The application requires the following environment variables (especially when running on Render):

| Variable                  | Description                                      |
|---------------------------|--------------------------------------------------|
| JDBC_DATABASE_HOST        | PostgreSQL host (provided by Render)             |
| JDBC_DATABASE_PORT        | PostgreSQL port (usually 5432)                   |
| JDBC_DATABASE_NAME        | Database name                                    |
| JDBC_DATABASE_USERNAME    | Database user                                    |
| JDBC_DATABASE_PASSWORD    | Database password                                |

**Sample `application-dev.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://${JDBC_DATABASE_HOST}:${JDBC_DATABASE_PORT}/${JDBC_DATABASE_NAME}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
```

---

## Database Setup

**Local:**
- Run PostgreSQL locally (`docker run -p 5432:5432 ...` or use a local install).
- Ensure your database credentials match the environment variables.

**Render:**
- Use Render’s PostgreSQL service and link it to your web service.
- Environment variables will be auto-injected.

---

## Build & Run

> **Note:** The project uses a dynamic `application-dev.properties` file with environment variables for configuration.

### **Running the Project (Development Mode)**

In **PowerShell**:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew bootRun
```

### **Building the Project**

In **PowerShell**:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew build
```

- Make sure the required environment variables for your database are set before running or building.

---

## API Documentation

Interactive API docs are available via **Swagger UI**:

**[Swagger UI (deployed on Render)](https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html)**  
_Note: This may take 1-2 minutes to load on the Render free tier._

### Main Endpoint

#### `POST /identify`

- **Request Body:**
    ```json
    {
      "email": "string",
      "phoneNumber": "string"
    }
    ```
- **Response:**
    ```json
    {
      "contact": {
        "primaryContatctId": 0,
        "emails": ["string"],
        "phoneNumbers": ["string"],
        "secondaryContactIds": [0]
      }
    }
    ```
- See full schema and try it out in Swagger UI.

---

## Deployment

### Render

- Push your code to GitHub.
- Create a new **Web Service** on Render using your repo.
- Set “Runtime” to Docker.
- Make sure your environment variables (`JDBC_DATABASE_*`) are set or auto-injected from the linked database.
- On first deploy, it may take a few minutes for the service and Swagger UI to be live.

### Docker (local or other platforms)

```bash
docker build -t identity-reconciliation .
docker run -p 8080:8080 ^
  -e JDBC_DATABASE_HOST=localhost ^
  -e JDBC_DATABASE_PORT=5432 ^
  -e JDBC_DATABASE_NAME=yourdbname ^
  -e JDBC_DATABASE_USERNAME=youruser ^
  -e JDBC_DATABASE_PASSWORD=yourpassword ^
  -e SPRING_PROFILES_ACTIVE=dev ^
  identity-reconciliation
```

> **Tip:** Use `^` for multi-line commands in PowerShell, or `\` in bash.

---

## Live Demo

You can access the deployed application here:  
**[https://identity-reconciliation-api-vtdk.onrender.com](https://identity-reconciliation-api-vtdk.onrender.com)**

Swagger docs:  
**[https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html](https://identity-reconciliation-api-vtdk.onrender.com/swagger-ui/index.html)**

---

## Troubleshooting

- **Swagger UI slow to load?**  
  This is normal on Render’s free tier—please wait 1-2 minutes after starting.
- **Database connection refused?**  
  Check your environment variables and ensure the DB host is correct (not `localhost` on Render).
- **No contacts returned?**  
  Ensure your database is up and your request body matches the expected format.

---

## Contributing

Contributions, issues, and feature requests are welcome!  
Feel free to fork the repo and submit a pull request.

---

## License

Copyright (c) 2025 Kartik Gour

---

## Contact

Project by **Kartik Gour**  
Email: [sharmakartik541@gmail.com](mailto:sharmakartik541@gmail.com)
