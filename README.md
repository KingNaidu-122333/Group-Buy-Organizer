# Group Buy Organizer

A modern full-stack Java web application for organizing community group buys with a polished storefront, a secure login system, an admin dashboard, and a Stripe-ready checkout flow.

## Default setup (no environment variables required)

This app works out of the box without any env vars. It uses an embedded H2 database and blank Stripe keys by default.

- Local app URL: http://localhost:8080
- Login: admin / admin123
- Database: H2 in-memory database
- Payment: Stripe checkout is optional and will show a demo-safe message when not configured

## Features

- Secure user registration and login
- Role-based admin dashboard
- Create and manage group-buy deals
- Join deals and track progress toward target members
- Stripe-ready checkout flow
- Responsive, modern UI
- Data storage in a real database (H2 by default)

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

http://localhost:8080

## Optional environment variables

You can add these later if you want production features, but they are not required for the app to function in default mode.

```bash
PORT=8080
APP_URL=http://localhost:8080
DATABASE_URL=jdbc:h2:mem:groupbuydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER=org.h2.Driver
DB_DIALECT=org.hibernate.dialect.H2Dialect
STRIPE_SECRET_KEY=
STRIPE_PUBLIC_KEY=
```

On Render, you can still deploy the repo without setting the Stripe or database vars, but you will be using the default embedded database mode.
