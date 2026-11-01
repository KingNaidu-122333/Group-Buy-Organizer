# Group Buy Organizer

A basic full-stack Java web application for organizing group buys. It includes:

- Spring Boot backend with REST-style MVC endpoints
- Thymeleaf web UI for creating and joining group buys
- H2 in-memory database so it works immediately without external setup
- Dockerfile and Render deployment config for a public URL

## Features

- Create new group buys
- View active group-buy listings
- Join a group buy as a participant
- Track current number of members vs. target members
- Good landing page for a product demo or MVP

## Run locally

```bash
./mvnw spring-boot:run
```

Then open:

http://localhost:8080

## Deploy to a public URL

This repo includes a Render config (`render.yaml`) and Dockerfile for one-click deployment.

1. Push this repository to GitHub
2. Sign in to Render and create a new Web Service from the repo
3. Choose the default branch
4. Render will expose a public URL like:
   `https://group-buy-organizer.onrender.com`

## Tech stack

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- H2 database
- Thymeleaf
- Maven
