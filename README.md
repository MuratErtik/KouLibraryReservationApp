# KOU Library Reservation System

A desk reservation system for Kocaeli University (KOU) libraries. Students can reserve study desks in advance, join a waitlist when a slot is full, check in with a QR code, and receive notifications about their reservation status.

---

## Table of Contents

1. [What This Project Does](#1-what-this-project-does)
2. [Technology Stack & Why We Chose Each One](#2-technology-stack--why-we-chose-each-one)
3. [System Architecture](#3-system-architecture)
4. [Main Application Flow](#4-main-application-flow)
   - [Authentication Flow](#41-authentication-flow)
   - [Reservation Flow](#42-reservation-flow)
   - [Waitlist Flow](#43-waitlist-flow)
   - [Check-in & Checkpoint Flow](#44-check-in--checkpoint-flow)
   - [Penalty Flow](#45-penalty-flow)
   - [Notification Flow](#46-notification-flow)
5. [Database Design Overview](#5-database-design-overview)
6. [API Overview](#6-api-overview)
7. [Running the Project](#7-running-the-project)

---

## 1. What This Project Does

Students at Kocaeli University often have trouble finding a free desk in the library, especially during exam season. This application solves that problem by letting students **reserve a desk online before they arrive**.

Here are the main features:

- **Desk Reservation** — Reserve a specific desk in a specific time slot, up to 30 days in advance.
- **Waitlist** — If all desks are full, join a waitlist. When a desk becomes free, you get a notification and have 15 minutes to claim it.
- **QR Code Check-in** — After you arrive at the library, scan the QR code on the desk to confirm your presence.
- **Presence Checkpoints** — The system checks periodically that you are still at your desk. If you miss a checkpoint, your reservation can be cancelled.
- **Penalty System** — If you reserve a desk but never show up (no-show), you receive a penalty and cannot make new reservations until it expires.
- **Email Notifications** — The system sends emails for account verification, reservation reminders, waitlist offers, and penalty notices.
- **Admin Panel** — Administrators can manage libraries, study halls (saloons), desks, time slots, users, and penalties.

---

## 2. Technology Stack & Why We Chose Each One

### Java 21 + Spring Boot 4

**What it is:** Java is the programming language. Spring Boot is a framework that makes it easier and faster to build Java web applications.

**Why we chose it:**
- Spring Boot handles a lot of setup automatically (database connections, security, REST API creation), so developers can focus on business logic.
- Java 21 includes **Virtual Threads**, which help the application handle many requests at the same time without using a lot of memory.
- The Spring ecosystem has mature libraries for everything we need: security, email, scheduling, and data access.

---

### PostgreSQL

**What it is:** A powerful, open-source relational database.

**Why we chose it:**
- Reservation systems need strong **ACID guarantees** — this means that if two students try to reserve the same desk at the same moment, the database will correctly give it to only one of them. PostgreSQL handles this reliably.
- It supports **row-level locking**, which we use in the reservation service to prevent race conditions (two requests changing the same row at the same time).
- It is free, well-documented, and trusted in production systems worldwide.

---

### Flyway

**What it is:** A database migration tool. Instead of changing the database schema by hand, you write numbered SQL files (V1, V2, V3...) and Flyway applies them in order.

**Why we chose it:**
- Every team member always has the same database schema.
- Changes to the database are tracked in version control just like code changes.
- When deploying to a new environment (or a Docker container), Flyway runs all migrations automatically — no manual setup needed.

---

### Keycloak

**What it is:** An open-source **Identity and Access Management** (IAM) server. It handles user login, logout, token refresh, and role management.

**Why we chose it:**
- Security is complex and easy to get wrong. Keycloak is a battle-tested solution used by many organizations. Instead of building authentication from scratch, we delegate it to Keycloak.
- It supports **OAuth 2.0 and OpenID Connect (OIDC)** — industry-standard protocols for secure authentication.
- It provides **JWT (JSON Web Tokens)** — the application does not need to call the database to verify every request; it just validates the token signature.
- Role-based access control (ADMIN vs USER) is managed in Keycloak and automatically reflected in the application via Spring Security.

---

### Spring Security + OAuth2 Resource Server

**What it is:** The security layer of the application. It intercepts every HTTP request and checks if the user has a valid JWT token and the right permissions.

**Why we chose it:**
- It integrates directly with Keycloak — it automatically fetches Keycloak's public keys and validates incoming JWT tokens with them.
- It makes it easy to protect endpoints: for example, only an ADMIN can create a library, but any USER can make a reservation.

---

### Spring Data JPA + Hibernate

**What it is:** A framework that lets you work with the database using Java objects instead of writing raw SQL. Hibernate is the underlying implementation (ORM — Object Relational Mapper).

**Why we chose it:**
- It greatly reduces repetitive code. For example, finding a reservation by ID is just `reservationRepository.findById(id)` — no SQL needed.
- It handles transactions automatically with `@Transactional`.
- We also use **Hibernate Envers** for audit logging — it automatically saves a history of every change made to important entities.

---

### MapStruct

**What it is:** A code generator that automatically creates code to convert between **Entity** objects (database models) and **DTO** objects (data transfer objects used in the API).

**Why we chose it:**
- Without MapStruct, you write a lot of repetitive mapping code like `dto.setName(entity.getName())` for every field.
- MapStruct generates this code at compile time (not at runtime), so there is no performance cost.
- It keeps the API response clean — for example, internal fields like `keycloakId` or password hashes are never accidentally sent to the client.

---

### Google ZXing (QR Code Library)

**What it is:** A library for generating and reading QR codes.

**Why we chose it:**
- QR codes are the simplest way to let a student prove they are physically at the desk. The student scans the QR code on the desk, and the app knows exactly which desk and which reservation to check in.
- ZXing is a well-known, free library that generates QR codes as PNG images with just a few lines of code.

---

### Spring Mail + Gmail SMTP

**What it is:** Spring Mail is a module that makes sending emails easy. Gmail SMTP is the email server we use to actually deliver the emails.

**Why we chose it:**
- Email is the most reliable way to reach university students (they all have institutional email addresses).
- Spring Mail provides a simple API (`JavaMailSender`) and integrates well with Spring's async processing — emails are sent in a background thread so they do not slow down the main request.

---

### SpringDoc OpenAPI (Swagger)

**What it is:** A library that automatically generates interactive API documentation from your code.

**Why we chose it:**
- Developers and testers can open a browser, see all available API endpoints, and test them directly — without writing a single line of documentation by hand.
- The documentation always stays up to date because it is generated from the actual code.

---

### Docker + Docker Compose

**What it is:** Docker packages the application and its dependencies into containers. Docker Compose lets you define and run multiple containers together.

**Why we chose it:**
- The project needs three services to run: the application, PostgreSQL, and Keycloak. Docker Compose starts all three with a single command: `docker compose up`.
- It eliminates the "works on my machine" problem — everyone runs the exact same environment.
- Deployment to a server is simple: the same `docker compose up` command works in production too.

---

### Lombok

**What it is:** A Java library that generates boilerplate code (getters, setters, constructors, builders) automatically using annotations.

**Why we chose it:**
- Java classes tend to be very verbose. With Lombok's `@Getter`, `@Setter`, `@Builder`, and `@Data` annotations, hundreds of lines of boilerplate disappear.
- This makes the code easier to read and maintain.

---

## 3. System Architecture

The application is a **monolith** — all the code runs in one single process. Inside this process, the code is organized into clear layers:

```
┌──────────────────────────────────────────────────────┐
│                   Client (Browser / App)              │
└────────────────────────┬─────────────────────────────┘
                         │ HTTP Requests (JWT in header)
┌────────────────────────▼─────────────────────────────┐
│              Spring Security (JWT Validation)         │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│               REST Controllers (10 controllers)       │
│  AuthController, ReservationController, WaitlistCtrl  │
│  LibraryController, DeskController, PenaltyController │
│  UserController, NotificationController, ...          │
└────────────────────────┬─────────────────────────────┘
                         │ method calls
┌────────────────────────▼─────────────────────────────┐
│             Service Layer (Business Logic)            │
│  ReservationService, WaitlistService, PenaltyService  │
│  AuthService, NotificationService, EmailService, ...  │
└──────────┬─────────────────────────┬─────────────────┘
           │ Spring Events            │ JPA calls
           ▼                         ▼
┌──────────────────┐    ┌────────────────────────────┐
│ Event Listeners  │    │   Repository Layer (JPA)   │
│ (Async Email /   │    │   (15 Repositories)        │
│  Notifications)  │    └────────────┬───────────────┘
└──────────────────┘                 │ SQL
                         ┌───────────▼───────────────┐
                         │       PostgreSQL           │
                         └───────────────────────────┘

External Services:
  ┌─────────────┐    ┌──────────────────┐
  │  Keycloak   │    │   Gmail SMTP     │
  │ (Auth/JWT)  │    │  (Email delivery)│
  └─────────────┘    └──────────────────┘
```

**Key architectural decisions:**

- **Controllers** only receive requests and return responses. They do not contain any business logic.
- **Services** contain all business logic. They are `@Transactional` — if something fails in the middle, the database rolls back to the previous state.
- **Repositories** are Spring Data JPA interfaces. They talk to the database.
- **Events** are used for notifications. When a reservation is created, the service publishes an event. A listener picks it up *after the transaction commits* and sends the email/notification. This way, an email is never sent for a failed reservation.
- **Schedulers** run background tasks periodically — for example, every 60 seconds the system checks for no-shows and expired penalties.

---

## 4. Main Application Flow

### 4.1 Authentication Flow

```
1. Student opens the registration page.
2. POST /auth/register → AuthService validates the email domain (@kocaeli.edu.tr required).
3. AuthService creates the user in Keycloak (via KeycloakAdminService)
   and in the local PostgreSQL users table.
4. A 6-digit verification code is sent to the student's email.
5. Student enters the code → POST /auth/verify-email → Account is activated.
6. Student logs in → POST /auth/login → Keycloak returns an access token (JWT)
   and a refresh token.
7. For every subsequent request, the client sends the JWT in the Authorization header.
8. Spring Security validates the JWT automatically (checks signature, expiry, roles).
9. When the access token expires (10 min), POST /auth/refresh → Keycloak returns a new token.
10. POST /auth/logout → Keycloak invalidates the refresh token.
```

---

### 4.2 Reservation Flow

```
1. Student searches for available time slots:
   GET /libraries/{libraryId}/saloons/{saloonId}/timeslots

2. Student chooses a time slot and gets available desks:
   GET /timeslots/{slotId}/available-desks

3. Student makes a reservation:
   POST /reservations  →  ReservationService checks:
     ✓ User is ACTIVE (not BLOCKED or PENDING_VERIFICATION)
     ✓ User has no active penalty
     ✓ User has not exceeded the max active reservation limit
     ✓ The selected desk is available in that time slot
     (Uses pessimistic locking to prevent race conditions)
   → Reservation is created with status PENDING.
   → A check-in reminder email is scheduled.

4. When the time slot starts, the student arrives at the library.
5. Student scans the QR code on the desk:
   POST /reservations/check-in  →  ReservationService checks:
     ✓ The reservation belongs to this user
     ✓ Check-in is within the allowed time window (configurable per library)
   → Reservation status changes: PENDING → ACTIVE.

6. If the student does NOT check in within the time window:
   → ReservationScheduler (runs every 60 sec) detects this.
   → Reservation status changes: PENDING → NO_SHOW.
   → A penalty is automatically created for the user.

7. When the time slot ends:
   → ReservationScheduler auto-completes the reservation: ACTIVE → COMPLETED.

8. A student can also cancel before the slot starts:
   PATCH /reservations/{id}/cancel  →  Status: PENDING → CANCELLED.
   → If there are users in the waitlist, they are notified immediately.
```

---

### 4.3 Waitlist Flow

```
1. Student tries to reserve a desk but all desks in that time slot are taken.
2. Student joins the waitlist:
   POST /waitlists  →  WaitlistService checks:
     ✓ The time slot is actually full
     ✓ Student is not already on the waitlist for this slot
     ✓ Student has not exceeded the waitlist limit
   → WaitlistEntry created with status WAITING.

3. Another student cancels their reservation (or gets a NO_SHOW).
4. ReservationScheduler detects the free desk.
5. WaitlistService picks the first WAITING entry (by join time).
6. Entry status changes: WAITING → NOTIFIED.
7. A notification is sent to the waiting student (email + in-app notification).
8. The student has 15 minutes to claim the desk:
   a. Student makes a reservation → Entry: NOTIFIED → CONVERTED.
   b. If 15 minutes pass without action → Entry: NOTIFIED → EXPIRED.
      → The next student on the waitlist is notified.

9. A student can leave the waitlist at any time:
   PATCH /waitlists/{id}/cancel  →  Status: WAITING → CANCELLED.
```

---

### 4.4 Check-in & Checkpoint Flow

```
INITIAL CHECK-IN (when student arrives):
1. Student scans QR code on the desk.
2. POST /reservations/check-in
3. Reservation: PENDING → ACTIVE.

PRESENCE CHECKPOINTS (during the reservation):
1. Every N minutes (configurable, e.g., every 30 minutes), a checkpoint is created.
2. The student has a grace period (e.g., 10 minutes) to scan the QR code again.
3. If the student scans within the grace period → Checkpoint: PASSED.
4. If the student does NOT scan → Checkpoint: MISSED.
   → ReservationScheduler can cancel the reservation after missed checkpoints.

WHY CHECKPOINTS?
They make sure the desk is actually being used. Without them, a student could
check in and then leave, blocking the desk for others all day.
```

---

### 4.5 Penalty Flow

```
AUTOMATIC PENALTY (No-Show):
1. ReservationScheduler detects a reservation that passed the check-in deadline.
2. Reservation status → NO_SHOW.
3. PenaltyService creates a penalty with reason: NO_SHOW.
4. User status → BLOCKED (cannot make new reservations).
5. A penalty notification email is sent.

PENALTY EXPIRY:
1. Each library has a configured penalty duration (e.g., 7 days).
2. ReservationScheduler checks for expired penalties every 60 seconds.
3. When the penalty duration ends → Penalty: ACTIVE → RESOLVED.
4. User status → ACTIVE (can make reservations again).

PENALTY APPEAL:
1. Student submits an appeal.
2. Admin reviews and can revoke the penalty:
   PATCH /penalties/{id}/revoke → Penalty status: ACTIVE → RESOLVED.
   → User is unblocked immediately.

MANUAL PENALTY:
   Admins can also create a penalty manually for rule violations:
   POST /penalties  (with reason: MANUAL_PENALTY)
```

---

### 4.6 Notification Flow

```
The notification system uses Spring Application Events to separate concerns:

1. Something happens (reservation created, penalty issued, waitlist offer, etc.).
2. The service publishes a NotificationEvent.
3. @TransactionalEventListener picks it up AFTER the main transaction commits.
   (This ensures: if the reservation fails, no email is sent.)
4. NotificationService saves a Notification record to the database (in-app).
5. EmailService.sendEmail() is called with @Async — it runs in a background thread,
   so the original request is not slowed down.
6. The student can view their notifications:
   GET /notifications/me
   GET /notifications/me/unread-count
   PATCH /notifications/{id}/read
```

---

## 5. Database Design Overview

The database has 16 main tables. Here is a simplified view of the relationships:

```
libraries
  ├── library_working_hours
  ├── library_closures
  └── saloon (study halls within a library)
        ├── saloon_working_hours
        ├── saloon_closures
        ├── saloon_time_slot  ──────────────────────┐
        │     └── (available time slots per saloon) │
        └── desks                                   │
              ├── qr_codes                          │
              └── reservations ─────────── (desk + time slot + user)
                    ├── reservation_status_logs
                    └── checkpoints

users
  ├── verification_codes
  ├── reservations
  ├── waitlist
  ├── penalties
  └── notifications
```

**Important design decisions:**

- `saloon_time_slot` tracks how many desks are occupied per slot. This allows the waitlist service to quickly know if a slot is full.
- `reservation_status_logs` keeps a full history of every status change for a reservation — useful for auditing and debugging.
- **Hibernate Envers** automatically creates `*_aud` (audit) tables for the most important entities, recording who changed what and when.
- **Pessimistic locking** is used in `ReservationRepository` to prevent two students from reserving the last available desk at the same moment.

---

## 6. API Overview

All endpoints are under the base path `/dev/v1`.

| Area | Method | Endpoint | Who |
|------|--------|----------|-----|
| **Auth** | POST | `/auth/register` | Anyone |
| | POST | `/auth/login` | Anyone |
| | POST | `/auth/refresh` | Anyone |
| | POST | `/auth/logout` | User |
| | POST | `/auth/verify-email` | Anyone |
| | POST | `/auth/forgot-password` | Anyone |
| | POST | `/auth/reset-password` | Anyone |
| **Reservations** | POST | `/reservations` | User |
| | GET | `/reservations/me` | User |
| | PATCH | `/reservations/{id}/cancel` | User |
| | POST | `/reservations/check-in` | User |
| | GET | `/reservations` | Admin |
| | PATCH | `/reservations/{id}/admin-cancel` | Admin |
| **Waitlist** | POST | `/waitlists` | User |
| | GET | `/waitlists/me` | User |
| | PATCH | `/waitlists/{id}/cancel` | User |
| **Libraries** | GET | `/libraries/search` | User |
| | GET | `/libraries/{id}` | User |
| | POST | `/libraries` | Admin |
| | PATCH | `/libraries/{id}` | Admin |
| | DELETE | `/libraries/{id}` | Admin |
| **Time Slots** | GET | `/libraries/{lId}/saloons/{sId}/timeslots` | User |
| | GET | `/timeslots/{id}/available-desks` | User |
| **Penalties** | GET | `/penalties/me` | User |
| | GET | `/penalties` | Admin |
| | POST | `/penalties` | Admin |
| | PATCH | `/penalties/{id}/revoke` | Admin |
| **Notifications** | GET | `/notifications/me` | User |
| | GET | `/notifications/me/unread-count` | User |
| | PATCH | `/notifications/{id}/read` | User |

Full interactive documentation is available at `/swagger-ui.html` when the application is running.

---

## 7. Running the Project

### Prerequisites

- Docker and Docker Compose installed on your machine.
- A `.env` file in the project root (ask a team member for the values).

### Start Everything

```bash
docker compose up --build
```

This single command starts:
1. **PostgreSQL** on port `5432` — the database.
2. **Keycloak** on port `8081` — the authentication server.
3. **The Application** on port `8080` — the Spring Boot API.

Flyway will automatically create and populate the database schema on first start.

### Useful URLs

| URL | What it is |
|-----|-----------|
| `http://localhost:8080/swagger-ui.html` | Interactive API documentation |
| `http://localhost:8081` | Keycloak admin console (admin / admin) |

### Stop Everything

```bash
docker compose down
```

To also delete the database data:

```bash
docker compose down -v
```

---

## Background Tasks (Schedulers)

Two scheduled tasks run automatically in the background:

| Scheduler | Schedule | What it does |
|-----------|----------|--------------|
| `ReservationScheduler` | Every 60 seconds | Detects no-shows, auto-completes ended reservations, expires penalties, sends check-in reminders, processes waitlist notifications |
| `TimeSlotScheduler` | Daily at 03:00 AM (Istanbul) | Generates time slots for upcoming days for all active saloons |

---

*This project is developed for Kocaeli University Library Services.*
