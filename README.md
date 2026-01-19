# 🏙️ CityFix API

![Status](https://img.shields.io/badge/Status-Work_in_Progress-yellow?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square)

**CityFix** is a backend platform designed to bridge the gap between citizens and city administration. It allows users to report urban issues (like potholes, broken streetlights, or trash) and enables local authorities to manage and resolve them efficiently based on geo-location.

> 🚧 **NOTE:** This project is currently under active development. Features are being rolled out following TDD (Test Driven Development) and Domain-First principles.

---

## 🚀 Key Features (Implemented)

* **📍 Geo-Spatial Search:** Find issues nearby a specific location using PostgreSQL native spatial queries with pagination support.
* **🛡️ Rich Domain Model:** Business logic is encapsulated within the Domain layer, ensuring data integrity and invariant protection (no anemic models).
* **🔄 Strict Workflow:** Finite State Machine implementation for issue lifecycles (`REPORTED` → `IN_PROGRESS` → `RESOLVED`). Prevents illegal status transitions.
* **🐳 Dockerized Environment:** Full Multi-Stage Docker build for the API and MySQL database configuration.
* **📄 Live Documentation:** Integrated OpenAPI (Swagger UI) for interactive API exploration.
* **✅ Robust Testing:** High test coverage using JUnit 5, Mockito, and **Testcontainers** for real integration testing against Dockerized MySQL.

---

## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports and Adapters)** to decouple the business logic from frameworks and external agents.

* **Domain Layer:** Core entities (`UrbanIssue`) and business rules. No dependencies on Spring or Database libraries.
* **Application Layer:** Use Cases (Interactors) and Ports (Interfaces). Orchestrates the flow of data.
* **Infrastructure
