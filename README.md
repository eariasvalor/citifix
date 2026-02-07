# CityFixes: Urban Issue Management System 🏙️

![Status](https://img.shields.io/badge/Status-Finished-brightgreen?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.9-green?style=flat-square)
![Angular](https://img.shields.io/badge/Angular-18-red?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square)

**CityFix** is a full-stack platform designed to bridge the gap between citizens and city administration. It allows users to report urban infrastructure issues (such as potholes, broken streetlights, or waste) via an interactive map, enabling local authorities to manage and resolve them efficiently based on geo-location.

This project was developed as a custom "Urban Management" domain project, replacing the original "Virtual Pet" assignment in agreement with the mentor to focus on Domain-First principles and Hexagonal Architecture.

---

## 🏗️ Architecture & Tech Stack

The project is divided into two main decoupled components:

### Backend (Core API)
Built with **Java 21** and **Spring Boot 3.x**, implementing **Hexagonal Architecture (Ports and Adapters)** to ensure the business logic is independent of frameworks and external agents.
- **Domain Layer:** Pure business logic (Entities like `UrbanIssue`, `User`, and `UserStats`) without infrastructure dependencies.
- **Persistence:** PostgreSQL with native spatial queries for geo-spatial searches.
- **Security:** Stateless JWT-based authentication.
- **Testing:** Robust coverage using JUnit 5, Mockito, and **Testcontainers** for real integration testing against Dockerized MySQL/PostgreSQL.
- **Documentation:** Integrated OpenAPI (Swagger UI) for interactive API exploration.

### Frontend (Client App)
A modern **Angular 18+** application using standalone components and reactive state management.
- **Maps:** Leaflet.js integration for visualizing and reporting issues at specific coordinates.
- **Styling:** Responsive UI built with Tailwind CSS.
- **State Management:** Angular Signals for fine-grained reactivity.
- **Authentication:** JWT tokens stored in localStorage with automatic inclusion in API requests via Interceptors.

---

## 🚀 Key Features

* **📍 Geo-Spatial Reporting:** Users can click anywhere on the map to report an issue with pre-filled coordinates.
* **🔄 Strict Workflow:** Implementation of a Finite State Machine for issue lifecycles (`REPORTED` → `IN_PROGRESS` → `RESOLVED`) to prevent illegal transitions.
* **🛡️ Role-Based Access:** Protected routes and admin-only status updates.
* **📊 User Dashboard:** View personalized statistics and a list of nearby issues.
* **🐳 Dockerized:** Multi-stage Docker build for easy deployment of the API and database.

---

## 🛠️ Project Structure

```text
├── citifix-backend/          # Java Spring Boot API (Hexagonal)
│   ├── src/main/java/.../domain       # Core entities and business rules
│   ├── src/main/java/.../application  # Use cases and ports
│   └── src/main/java/.../infrastructure # Adapters (REST, JPA, Security)
└── citifix-frontend/         # Angular 18 Application
    ├── src/app/core/services          # API & Auth logic
    ├── src/app/features/dashboard     # Map & Issue components
    └── src/app/features/auth          # Login & Registration

```

---

## 🚦 Getting Started

### Prerequisites
* **Node.js** (v18+) & **Angular CLI**
* **Java 21** & **Maven**
* **Docker** (for database services)

### Backend Setup
1. Navigate to the backend directory: `cd citifix-backend`
2. Build the project: `mvn install`
3. Start the application: `mvn spring-boot:run`
4. Access API documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Frontend Setup
1. Navigate to the frontend directory: `cd citifix-frontend`
2. Install dependencies: `npm install`
3. Start the development server: `npm start`
4. The app will be available at: [http://localhost:4200](http://localhost:4200)

---

## ✅ Quality Assurance

* **Integration Tests:** The backend uses **Testcontainers** to spin up real database instances during tests to ensure 100% compatibility.
* **Validation:** Strict form and data integrity protection at both Domain and UI levels.
* **Clean Code:** Adherence to **SOLID** principles and **Domain-Driven Design (DDD)**.
