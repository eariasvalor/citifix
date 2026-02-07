# CityFix: Urban Issue Management System 🏙️

![Status](https://img.shields.io/badge/Status-Finished-brightgreen?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.9-green?style=flat-square)
![Angular](https://img.shields.io/badge/Angular-18-red?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square)

**CityFix** is a full-stack platform designed to bridge the gap between citizens and city administration. It allows users to report urban infrastructure issues (such as potholes, broken streetlights, or waste) via an interactive map, enabling local authorities to manage and resolve them efficiently based on geo-location.

> 💡 **Note:** This repository contains the **Backend (Core API)**. The Frontend application is hosted in a separate repository: [citifix-frontend](https://github.com/eariasvalor/citifix-frontend).

---

## 🏗️ Architecture & Tech Stack

The project is divided into two main decoupled components:

### Backend (Core API)
Built with **Java 21** and **Spring Boot 3.5.9**, implementing **Hexagonal Architecture (Ports and Adapters)** to ensure the business logic is independent of frameworks and external agents.
- **Domain Layer:** Pure business logic (Entities like `UrbanIssue`, `User`, and `UserStats`) without infrastructure dependencies.
- **Persistence:** PostgreSQL with native spatial queries for geo-spatial searches.
- **Security:** Stateless JWT-based authentication.
- **Testing:** Robust coverage using JUnit 5, Mockito, and **Testcontainers** for real integration testing against Dockerized PostgreSQL.
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

This project follows a decoupled architecture across two repositories:

**[Backend Repository](https://github.com/eariasvalor/citifix)**
```text
├── src/main/java/.../domain       # Core entities and business rules
├── src/main/java/.../application  # Use cases and ports
└── src/main/java/.../infrastructure # Adapters (REST, JPA, Security)
```
**[Frontend Repository](https://github.com/eariasvalor/citifix-frontend)**
```text
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

### Backend Setup (This Repository)
1. **Clone this repository**: `git clone https://github.com/eariasvalor/citifix.git`
2. **Build the project**: `mvn install`
3. **Start the application**: `mvn spring-boot:run`
4. **Access API documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Frontend Setup
1. **Clone the frontend repository**: `git clone https://github.com/eariasvalor/citifix-frontend.git`
2. **Install dependencies**: `npm install`
3. **Start the development server**: `npm start`
4. **The app will be available at**: [http://localhost:4200](http://localhost:4200)

---

## ✅ Quality Assurance

* **Integration Tests**: The backend uses **Testcontainers** to create real database instances during testing and ensure full compatibility.
* **Validation**: Strict validation and data integrity protection at both the **Domain** and **UI** layers.
* **Clean Code**: Full adherence to **SOLID** and **Domain-Driven Design (DDD)** principles.
