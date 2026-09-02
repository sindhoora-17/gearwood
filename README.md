# Gearwood — Full-Stack E-Commerce Platform

A full-stack e-commerce platform built with Spring Boot, covering product catalog, cart, checkout, and order management.

## Highlights

- **Swappable payment gateway** — checkout logic is written against a `PaymentProcessor` interface, so swapping in a real gateway (Stripe, etc.) doesn't touch the checkout flow. Includes a simulated processor that validates against 3 decline scenarios for testing failure handling.
- **Transactional checkout** — cart contents and product availability are validated before charging, and the order only commits after payment is approved. Failed payments don't leave partial orders behind.
- **Line-item snapshotting** — `OrderItem` stores the product name and price *at the time of purchase* rather than referencing the live `Product` record, so historical orders stay accurate even after catalog changes.
- **Role-based access control** — Spring Security with BCrypt password hashing, method-level security, and admin-gated routes across 2 user roles (customer, admin).

## Tech Stack

Java · Spring Boot · Spring Security · Spring Data JPA · Thymeleaf · MySQL · Maven

## Running Locally

1. Create a MySQL database named `gearwood_db`
2. Set environment variables (or use the defaults for local dev):
   ```
   DB_URL=jdbc:mysql://localhost:3306/gearwood_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```
3. Run with Maven:
   ```
   ./mvnw spring-boot:run
   ```
4. Visit `http://localhost:8080`

## Live Demo

_Coming soon._
