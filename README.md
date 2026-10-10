# Car Rental Management System

Enterprise-grade Car Rental Management System built from scratch for a Java Full Stack Developer portfolio.

---

## 🛠️ Technology Stack

| Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Backend Runtime** | Java | 17 (LTS) | Core application platform |
| **Backend Framework** | Spring Boot | 3.3.4 | RESTful services, dependency injection |
| **Security Framework** | Spring Security | 6.3.3 | SecurityFilterChain, stateless auth, RBAC |
| **JWT Library** | JJWT (io.jsonwebtoken) | 0.12.6 | HMAC-SHA256 token signing and verification |
| **Password Hashing** | BCrypt | Spring Crypto | Secure salted one-way password hashing |
| **Build Tool (Backend)** | Maven | 3.8.7 (with `mvnw`) | Dependency management & packaging |
| **Web Layer** | Spring Web | 3.3.4 | REST APIs & JSON serialization |
| **Persistence** | Spring Data JPA / Hibernate | 3.3.4 | ORM, repositories & schema management |
| **Query Engine** | Spring Data Specifications | 3.3.4 | Dynamic database-level search & filtering |
| **Database Driver** | MySQL Connector/J | 8.3.0 | Relational database connectivity |
| **Validation** | Jakarta Bean Validation | 3.3.4 | DTO constraint validation |
| **Testing** | JUnit 5 & Mockito | 5.10 / 5.11 | Automated unit and security slice testing |
| **Frontend Runtime** | Node.js | v22+ | Frontend development & build engine |
| **Frontend Framework** | React | 18.3.1 | Single Page Application UI |
| **Bundler / Dev Server** | Vite | 5.4.11 | Fast ESM development & production build |
| **Language** | JavaScript (ESM) | ESNext | Frontend scripting |
| **UI Framework** | Bootstrap | 5.3.3 | Responsive UI components and layout |
| **HTTP Client** | Axios | 1.7.9 | Backend API communication |
| **Version Control** | Git | 2.x | Source code management |

---

## 📁 Project Structure

```text
car-rental-system/
├── backend/
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── carrental/
│   │   │   │           ├── controller/
│   │   │   │           │   ├── AdminBookingController.java
│   │   │   │           │   ├── AuthController.java
│   │   │   │           │   ├── BookingController.java
│   │   │   │           │   └── CarController.java
│   │   │   │           ├── dto/
│   │   │   │           │   ├── AuthResponse.java
│   │   │   │           │   ├── AvailabilityResponse.java
│   │   │   │           │   ├── BookingResponse.java
│   │   │   │           │   ├── CarRequest.java
│   │   │   │           │   ├── CarResponse.java
│   │   │   │           │   ├── CreateBookingRequest.java
│   │   │   │           │   ├── LoginRequest.java
│   │   │   │           │   ├── PageResponse.java
│   │   │   │           │   ├── RegisterRequest.java
│   │   │   │           │   ├── UpdateBookingStatusRequest.java
│   │   │   │           │   └── UserResponse.java
│   │   │   │           ├── entity/
│   │   │   │           │   ├── Booking.java
│   │   │   │           │   ├── BookingStatus.java
│   │   │   │           │   ├── Car.java
│   │   │   │           │   ├── Role.java
│   │   │   │           │   └── User.java
│   │   │   │           ├── exception/
│   │   │   │           │   ├── BookingConflictException.java
│   │   │   │           │   ├── BookingNotFoundException.java
│   │   │   │           │   ├── CarNotAvailableException.java
│   │   │   │           │   ├── CarNotFoundException.java
│   │   │   │           │   ├── DuplicateEmailException.java
│   │   │   │           │   ├── DuplicateRegistrationNumberException.java
│   │   │   │           │   ├── ErrorResponse.java
│   │   │   │           │   ├── GlobalExceptionHandler.java
│   │   │   │           │   ├── InvalidBookingDateException.java
│   │   │   │           │   ├── InvalidStatusTransitionException.java
│   │   │   │           │   └── UnauthorizedBookingAccessException.java
│   │   │   │           ├── repository/
│   │   │   │           │   ├── BookingRepository.java
│   │   │   │           │   ├── CarRepository.java
│   │   │   │           │   └── UserRepository.java
│   │   │   │           ├── security/
│   │   │   │           │   ├── CustomAccessDeniedHandler.java
│   │   │   │           │   ├── CustomUserDetailsService.java
│   │   │   │           │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │   │           │   ├── JwtService.java
│   │   │   │           │   └── SecurityConfig.java
│   │   │   │           ├── service/
│   │   │   │           │   ├── impl/
│   │   │   │           │   │   ├── AuthServiceImpl.java
│   │   │   │           │   │   ├── BookingServiceImpl.java
│   │   │   │           │   │   └── CarServiceImpl.java
│   │   │   │           │   ├── AuthService.java
│   │   │   │           │   ├── BookingService.java
│   │   │   │           │   └── CarService.java
│   │   │   │           ├── specification/
│   │   │   │           │   └── CarSpecification.java
│   │   │   │           └── CarRentalApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application.properties.example
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── carrental/
│   │       │           ├── controller/
│   │       │           │   ├── AdminBookingControllerTest.java
│   │       │           │   ├── AuthControllerTest.java
│   │       │           │   ├── BookingControllerTest.java
│   │       │           │   └── CarControllerTest.java
│   │       │           ├── repository/
│   │       │           │   ├── CarRepositoryTest.java
│   │       │           │   └── UserRepositoryTest.java
│   │       │           ├── security/
│   │       │           │   ├── JwtServiceTest.java
│   │       │           │   └── SecurityIntegrationTest.java
│   │       │           ├── service/
│   │       │           │   ├── AuthServiceTest.java
│   │       │           │   ├── BookingServiceTest.java
│   │       │           │   └── CarServiceTest.java
│   │       │           └── CarRentalApplicationTests.java
│   │       └── resources/
│   │           └── application.properties
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api.js
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── .gitignore
└── README.md
```

---

## 🔒 Security Architecture & Role-Based Access Control

The application implements a stateless **Spring Security 6.x** architecture utilizing JSON Web Tokens (**JJWT 0.12.6**) and **BCrypt** password hashing.

### Endpoint Permissions Matrix

| Endpoint | Method | Role Allowed | Unauthenticated | CUSTOMER | ADMIN |
| :--- | :--- | :--- | :---: | :---: | :---: |
| `/api/auth/register` | `POST` | Public | Allowed (`201`) | Allowed | Allowed |
| `/api/auth/login` | `POST` | Public | Allowed (`200`) | Allowed | Allowed |
| `/api/cars` | `GET` | Public / All | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) |
| `/api/cars/{id}` | `GET` | Public / All | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) |
| `/api/cars/{id}/availability` | `GET` | Public / All | Allowed (`200`) | Allowed (`200`) | Allowed (`200`) |
| `/api/cars` | `POST` | `ROLE_ADMIN` only | `401 Unauthorized` | `403 Forbidden` | `201 Created` |
| `/api/cars/{id}` | `PUT` | `ROLE_ADMIN` only | `401 Unauthorized` | `403 Forbidden` | `200 OK` |
| `/api/cars/{id}` | `DELETE` | `ROLE_ADMIN` only | `401 Unauthorized` | `403 Forbidden` | `204 No Content` |
| `/api/bookings` | `POST` | Authenticated | `401 Unauthorized` | `201 Created` | `201 Created` |
| `/api/bookings/my` | `GET` | Authenticated | `401 Unauthorized` | `200 OK` | `200 OK` |
| `/api/bookings/{id}` | `GET` | Owner or ADMIN | `401 Unauthorized` | Owner (`200`) / Other (`403`) | `200 OK` |
| `/api/bookings/{id}/cancel` | `PATCH` | Owner or ADMIN | `401 Unauthorized` | Owner (`200`) / Other (`403`) | `200 OK` |
| `/api/admin/bookings` | `GET` | `ROLE_ADMIN` only | `401 Unauthorized` | `403 Forbidden` | `200 OK` |
| `/api/admin/bookings/{id}/status` | `PATCH` | `ROLE_ADMIN` only | `401 Unauthorized` | `403 Forbidden` | `200 OK` |

---

## 📅 Booking System & Business Rules

### 1. Booking Rules
- **Rental Period Definition**: Defined as `[startDate, endDate)`. For example, renting from `2026-10-15` to `2026-10-18` charges for 3 days (October 15, 16, and 17). Return on October 18 is not charged as an additional day.
- **Price Calculation**: `totalPrice = car.pricePerDay * rentalDays`. The price is calculated authoritatively by the backend. Client-provided prices are never accepted.
- **Overlap Detection**: The system checks active bookings (`PENDING` and `CONFIRMED`) where:
  `existing.startDate < request.endDate AND existing.endDate > request.startDate`.
- **Concurrency & Transaction Isolation**: Concurrent booking requests are protected via `@Transactional(isolation = Isolation.READ_COMMITTED)` combined with Spring Data JPA queries ensuring atomic status and availability verification.
- **Status Lifecycle**:
  - Initial creation: `PENDING`
  - Admin approval: `CONFIRMED`
  - Cancellation by owner or admin: `CANCELLED` (releases availability immediately)
  - Completion after return: `COMPLETED`
  - Direct customer self-confirmation is strictly prohibited.

---

## 📡 Booking API Examples

### 1. Check Car Availability (`GET /api/cars/{id}/availability`)
```http
GET /api/cars/1/availability?startDate=2026-10-15&endDate=2026-10-18 HTTP/1.1
Host: localhost:8080
```
#### Success Response (`200 OK`)
```json
{
  "carId": 1,
  "carBrand": "Toyota",
  "carModel": "Camry",
  "startDate": "2026-10-15",
  "endDate": "2026-10-18",
  "available": true,
  "rentalDays": 3,
  "pricePerDay": 65.00,
  "estimatedTotalPrice": 195.00,
  "message": "Vehicle is available for the requested dates"
}
```

### 2. Create Booking (`POST /api/bookings`)
```http
POST /api/bookings HTTP/1.1
Host: localhost:8080
Authorization: Bearer <customer_jwt_token>
Content-Type: application/json

{
  "carId": 1,
  "startDate": "2026-10-15",
  "endDate": "2026-10-18"
}
```
#### Success Response (`201 Created`)
```json
{
  "id": 1,
  "userId": 2,
  "userFullName": "Jane Doe",
  "userEmail": "jane.doe@example.com",
  "carId": 1,
  "carBrand": "Toyota",
  "carModel": "Camry",
  "carRegistrationNumber": "CAR-1001",
  "pricePerDay": 65.00,
  "startDate": "2026-10-15",
  "endDate": "2026-10-18",
  "rentalDays": 3,
  "totalPrice": 195.00,
  "status": "PENDING",
  "createdAt": "2026-10-10T09:00:00",
  "updatedAt": "2026-10-10T09:00:00"
}
```

#### Overlap Conflict Response (`409 Conflict`)
```json
{
  "timestamp": "2026-10-10T09:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "The selected car is not available for the requested period.",
  "path": "/api/bookings"
}
```

### 3. Customer Bookings (`GET /api/bookings/my`)
```http
GET /api/bookings/my?page=0&size=10 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <customer_jwt_token>
```

### 4. Cancel Booking (`PATCH /api/bookings/{id}/cancel`)
```http
PATCH /api/bookings/1/cancel HTTP/1.1
Host: localhost:8080
Authorization: Bearer <customer_jwt_token>
```

### 5. Admin List Bookings (`GET /api/admin/bookings`)
```http
GET /api/admin/bookings?status=PENDING&page=0&size=10 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <admin_jwt_token>
```

### 6. Admin Update Status (`PATCH /api/admin/bookings/{id}/status`)
```http
PATCH /api/admin/bookings/1/status HTTP/1.1
Host: localhost:8080
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

---

## 🧪 Automated Testing

Automated test suites run completely offline using an in-memory H2 database and test JWT signing keys:
- **`BookingServiceTest` (16 tests)**: Complete booking creation rules, dates validation, price calculations, overlap prevention, ownership checks, status transitions, and admin status updates.
- **`BookingControllerTest` (7 tests)**: Customer booking endpoints, date constraints, 401 unauthenticated, 409 conflict, and 400 validation.
- **`AdminBookingControllerTest` (5 tests)**: Role-based authorization (`401` unauthenticated, `403` customer, `200` admin), status updating, and invalid state transitions.
- **`AuthServiceTest` (5 tests)**: Registration flow, BCrypt hashing, duplicate email handling, and login authentication.
- **`AuthControllerTest` (5 tests)**: Web slice tests for registration validation, duplicate email rejection (`409`), valid login (`200`), and invalid credentials (`401`).
- **`JwtServiceTest` (4 tests)**: Token generation, signature verification, claims extraction, expiration rejection, and secret key length guards.
- **`UserRepositoryTest` (4 tests)**: Email lookup and existence verification on the database layer.
- **`SecurityIntegrationTest` (8 tests)**: End-to-end security filter chain verifying public reading, write endpoint 401 on missing/invalid tokens, CUSTOMER 403 Forbidden, and ADMIN 201 Created.
- **`CarControllerTest` (14 tests)**: Paginated search, filters, availability check endpoint, validation, and administrative access checks.
- **`CarServiceTest` (19 tests)**: Complete car CRUD and search logic.
- **`CarRepositoryTest` (9 tests)**: JPA Specifications and sorting.
- **`CarRentalApplicationTests` (1 test)**: Spring Boot application context bootstrap.

### Run All 100 Automated Tests:
```bash
./backend/mvnw -f backend/pom.xml test
```

### Clean Package Executable JAR:
```bash
./backend/mvnw -f backend/pom.xml clean package
```

---

## 🚀 Running the Applications

### 1. Backend Application (Spring Boot)
```bash
DB_URL="jdbc:mysql://localhost:3306/car_rental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
DB_USERNAME="root" \
DB_PASSWORD="your_password" \
JWT_SECRET="your_secure_random_jwt_secret_key_minimum_32_characters" \
./backend/mvnw -f backend/pom.xml spring-boot:run
```
Backend starts on `http://localhost:8080`.

### 2. Frontend Application (React 18 + Vite)
```bash
npm --prefix frontend run dev
```
Frontend runs on `http://localhost:5173`.
