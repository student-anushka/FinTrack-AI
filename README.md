# FinTrack-AI

## Problem Statement

## Features

## Architecture

## Technology Stack

## Roadmap

Our First Technical Decisions
Let's lock these in:

Primary database: PostgreSQL
Document database: MongoDB
Cache: Redis
Messaging: Kafka
Backend: Java 21 + Spring Boot
Frontend: React + TypeScript
Build tool: Maven
Version control: Git
Containerization: Docker


FintrackBackendApplication.java
@SpringBootApplication
public class FintrackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintrackBackendApplication.class, args);
    }

}

What happens internally?
When you click Run:

JVM starts.
main() is called.
SpringApplication.run() starts.
Spring creates an Application Context (its container for managing objects).
It scans your project for Spring components.
It creates and wires objects (called Beans) together.
Embedded Tomcat starts.
Server listens on port 8080 (by default).
Your application is ready to receive HTTP requests.



✅ Phase 1 : Project Setup
✅ Phase 2 : Hello API
-------------------------------------------------
➡ Phase 3 : Dynamic Hello API
➡ Phase 4 : Connect MySQL
➡ Phase 5 : Create User Entity
➡ Phase 6 : Save User API
➡ Phase 7 : Get User API
➡ Phase 8 : Update User API
➡ Phase 9 : Delete User API
➡ Phase 10 : Login API
➡ Phase 11 : JWT Authentication
➡ Phase 12 : Expense Module
➡ Phase 13 : Budget Module
➡ Phase 14 : Dashboard APIs
➡ Phase 15 : React Frontend
➡ Phase 16 : Docker
➡ Phase 17 : Microservices
➡ Phase 18 : AWS Deployment
