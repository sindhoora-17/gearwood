# Gearwood — Full-Stack E-Commerce Platform

[![CI](https://github.com/sindhoora-17/gearwood/actions/workflows/ci.yml/badge.svg)](https://github.com/sindhoora-17/gearwood/actions/workflows/ci.yml)

Gearwood is a full-stack e-commerce application built with Spring Boot and Thymeleaf. It supports product browsing, cart management, checkout, order history, authentication, and admin product management, with MySQL-backed persistence and Spring Security authorization.

The project focuses on the backend concerns behind a checkout flow: transactional order creation, payment-provider abstraction, purchase-time order snapshots, validation, and role-based access control.

## Highlights

- **Transactional checkout** — validates the cart and product availability before payment, then persists the order and clears the cart within a single Spring transaction.
- **Swappable payment gateway** — checkout depends on a `PaymentProcessor` interface rather than a concrete provider, allowing the simulated processor to be replaced without changing checkout logic.
- **Payment failure handling** — the local payment simulator includes configured decline scenarios and input validation so failed payments do not create partial orders.
- **Line-item snapshotting** — `OrderItem` stores the product name and unit price at purchase time so historical orders stay accurate after catalog changes.
- **Role-based access control** — Spring Security protects authenticated cart/checkout routes and restricts admin routes to the `ADMIN` role, with BCrypt password hashing and method-level security.
- **Catalog management** — customers can browse and filter products while administrators can create, update, activate, and deactivate catalog entries.

## Checkout Flow

```text
Customer cart
    ↓
Validate cart + product availability
    ↓
PaymentProcessor
    ├── declined → return error, no order committed
    └── approved
            ↓
      Create order + snapshot line items
            ↓
      Persist order
            ↓
      Clear cart
```

`CheckoutService.checkout(...)` is transactional, so database changes made during checkout are committed together or rolled back together if the flow fails.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- MySQL
- Maven
- JUnit 5
- GitHub Actions

## Project Structure

```text
src/main/java/edu/rajasekharuni/gearwood/
├── config/          # Spring Security configuration
├── controllers/     # MVC controllers
├── dtos/            # Form and request models
├── entities/        # JPA entities
├── enums/           # Domain enums
├── initializers/    # Local seed data
├── payment/         # Payment abstraction and simulator
├── repositories/    # Spring Data repositories
└── services/        # Business logic
```

## Running Locally

### Requirements

- Java 21
- MySQL 8+

### 1. Create the database

```sql
CREATE DATABASE gearwood_db;
```

### 2. Configure database access

The application reads database settings from environment variables and provides local defaults for the URL and username:

```bash
export DB_URL=jdbc:mysql://localhost:3306/gearwood_db
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### 3. Start the application

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

On an empty database, the application initializes sample catalog data and local demo users for development.

## Testing

Run the test suite with:

```bash
./mvnw test
```

The tests include application-context verification and payment-processor behavior such as successful payments, configured declines, card-number normalization, invalid input, and invalid payment amounts.

GitHub Actions runs the Maven verification build against a MySQL service on pushes to `main` and on pull requests.

## Security Notes

- Passwords are stored using BCrypt.
- Admin functionality is protected by role-based authorization.
- Post-login redirects are restricted to local application paths.
- The included payment processor is a **simulation for development only**. No real payment provider is integrated and no production payment credentials are required.
