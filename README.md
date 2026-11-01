# Group Buy Organizer

A modern full-stack Java web application for organizing community group buys with a polished storefront, a secure login system, admin dashboard, and Stripe-ready checkout flow.

## Live public URL

This app is configured for Render deployment, which exposes a public URL such as:

https://group-buy-organizer.onrender.com

## Features

- Secure user registration and login
- Role-based admin dashboard
- Create and manage group-buy deals
- Join deals and track progress toward target members
- Stripe-ready checkout flow for payment collection
- PostgreSQL-ready configuration with H2 fallback for local development
- Responsive, modern UI for a real SaaS-style experience

## Tech stack

- Java 17
- Spring Boot 3.3.5
- Spring Security 6
- Spring Data JPA
- PostgreSQL / H2 database
- Thymeleaf
- Stripe Java SDK
- Docker + Render deployment

## Local development

1. Start PostgreSQL locally (or use the built-in H2 fallback)
2. Run:

```bash
./mvnw spring-boot:run
```

3. Open:

http://localhost:8080

## Default admin account

The app seeds an admin account on startup:

- Username: admin
- Password: admin123

## Deployment on Render

The repository includes a Render configuration that creates:

- a public web service
- a PostgreSQL database
- environment variables for database and Stripe configuration

Once connected to Render, the public URL is generated automatically.

## Environment variables

Set these in Render or your local environment:

```bash
DATABASE_URL=jdbc:postgresql://host:5432/dbname
DB_USERNAME=postgres
DB_PASSWORD=your-password
DB_DRIVER=org.postgresql.Driver
DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect
STRIPE_SECRET_KEY=sk_test_xxx
STRIPE_PUBLIC_KEY=pk_test_xxx
APP_URL=http://localhost:8080
```

If these values are not configured, the app falls back to H2 in local mode.
