# Group Buy Organizer

A Spring Boot 3 application for creating community group-buy deals, tracking membership, and collecting payments through Stripe Checkout.

## Features

- Account registration and secure login with BCrypt password hashing
- Role-based admin dashboard with protected admin actions
- Create deals with server-controlled organizer, member count, and status
- Deal creator is recorded as the first member
- Persistent membership records and duplicate-join prevention
- Transaction-locked membership updates to protect group capacity
- Live deal search and status filtering
- Membership progress indicators and newest-first deal history
- Personal My Deals dashboard
- Persistent local H2 database and PostgreSQL production profile
- Stripe Checkout restricted to deal members
- Stripe payment verification and signed webhook processing
- Idempotent payment records, preventing duplicate successful payments per member/deal
- Responsive layouts and accessible form controls
- GitHub Actions Maven CI

## Requirements

- Java 17
- Maven 3.9+ (or use the included Docker build)
- PostgreSQL for production deployments (H2 is used locally)
- Stripe account and webhook endpoint secret for live payments

## Run locally

    mvn spring-boot:run

Open http://localhost:8080.

The default local profile uses a file-backed H2 database (`./groupbuydb.mv.db`), so local deal and account data survives application restarts. H2 console is disabled by default. To enable it temporarily for local debugging, set `H2_CONSOLE_ENABLED=true`; do not enable it on a public deployment.

### Create an administrator

There is deliberately no hard-coded administrator password. Set an administrator password before starting the app:

PowerShell:

    $env:APP_ADMIN_USERNAME="admin"
    $env:APP_ADMIN_EMAIL="admin@example.com"
    $env:APP_ADMIN_PASSWORD="use-a-unique-long-password"
    mvn spring-boot:run

macOS / Linux:

    APP_ADMIN_USERNAME=admin APP_ADMIN_EMAIL=admin@example.com APP_ADMIN_PASSWORD='use-a-unique-long-password' mvn spring-boot:run

If `APP_ADMIN_PASSWORD` is not set, the app does not create a default admin account. Register a normal user for basic testing; an administrator must be provisioned through the environment before the admin dashboard can be accessed.

## Environment configuration

| Variable | Purpose | Local default |
|---|---|---|
| `PORT` | HTTP port | `8080` |
| `APP_URL` | Public base URL used by Stripe redirects | `http://localhost:8080` |
| `APP_ADMIN_USERNAME` | Bootstrap administrator username | `admin` |
| `APP_ADMIN_EMAIL` | Bootstrap administrator email | `admin@example.com` |
| `APP_ADMIN_PASSWORD` | Bootstrap administrator password | Empty (no admin created) |
| `STRIPE_SECRET_KEY` | Stripe secret API key | Empty (checkout disabled) |
| `STRIPE_PUBLIC_KEY` | Stripe publishable key | Empty |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret | Empty (webhooks return 503) |
| `STRIPE_CURRENCY` | Three-letter Stripe currency | `usd` |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | PostgreSQL connection details in prod profile | Not used locally |
| `DB_USERNAME`, `DB_PASSWORD` | Database credentials in prod profile | H2 defaults locally |
| `DDL_AUTO` | Hibernate schema strategy | `update` |

Never commit API keys, webhook secrets, database passwords, or administrator passwords.

## Stripe setup

1. Configure `STRIPE_SECRET_KEY`, `STRIPE_CURRENCY`, and `APP_URL`.
2. Register a Stripe webhook endpoint at `https://YOUR_HOST/webhooks/stripe`.
3. Subscribe to `checkout.session.completed` and `checkout.session.async_payment_succeeded`.
4. Set `STRIPE_WEBHOOK_SECRET` to the endpoint's signing secret.
5. Test using Stripe test-mode keys before enabling live payments.

The return page independently retrieves the Checkout Session from Stripe and validates its payment status, amount, currency, deal and member. The signed webhook records successful payments idempotently. Production order fulfilment, refunds, disputes and reconciliation still require operational policies and monitoring.

## Production deployment

The Render blueprint in `render.yaml` provisions a PostgreSQL database and runs the app with the `prod` profile. Configure `APP_ADMIN_PASSWORD`, `STRIPE_SECRET_KEY`, and `STRIPE_WEBHOOK_SECRET` in the Render dashboard.

The Render service should deploy from `main` after the pull request has been reviewed, CI passes, and the change is merged. Confirm the generated database host, port, database name, user and password are available to the web service.


## Run the full stack with Docker Compose

Set `APP_ADMIN_PASSWORD` in your shell, then run:

    docker compose up --build

Open http://localhost:8080. Docker Compose starts PostgreSQL and the Spring Boot application together. The Compose database password default is for local development only; set `POSTGRES_PASSWORD` to a strong value before exposing the database outside your machine. Stripe checkout remains disabled until Stripe keys and the webhook secret are configured.

## Build and test

    mvn --batch-mode clean verify

The GitHub Actions workflow runs this command for pushes to the working branch and pull requests targeting `main`.

## Important production follow-ups

Before accepting real customer payments, add integration tests against a staging PostgreSQL database, verify Stripe webhook delivery/retries in the deployed environment, configure backups and monitoring, define refunds/cancellations and fulfilment rules, and review privacy/terms/tax obligations for the actual business model.