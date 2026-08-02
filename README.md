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
