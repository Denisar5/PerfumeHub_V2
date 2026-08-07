# PerfumeHub

PerfumeHub is a Spring Boot web application for browsing, managing, ordering, and reviewing perfumes.

The project uses a microservice architecture where the main application handles users, perfumes, orders, administration, and the web interface, while review functionality is handled by a separate Review Service.

## Architecture

The project consists of two Spring Boot applications:

```text
PerfumeHub_V2/
├── perfumehub-main/
│   ├── src/
│   └── pom.xml
│
├── review-service/
│   ├── src/
│   └── pom.xml
│
└── README.md
```

### perfumehub-main

The main application is responsible for:

- User registration and authentication
- User profiles
- Perfume catalog
- Searching and filtering perfumes
- Order creation and management
- Administration
- Review integration
- Thymeleaf web interface
- Security
- Caching
- Scheduled operations

### review-service

The Review Service is a separate Spring Boot microservice responsible for:

- Creating reviews
- Updating reviews
- Deleting reviews
- Retrieving reviews
- Retrieving approved reviews for a perfume
- Retrieving reviews belonging to a user
- Review approval/moderation
- Review statistics

The main application communicates with the Review Service through REST using Spring Cloud OpenFeign.

---

## Technologies

The project is built with:

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Thymeleaf
- Hibernate
- MySQL
- Maven
- Lombok
- Jakarta Validation
- JUnit 5
- Mockito
- MockMvc
- HTML
- CSS

---

## Main Features

### Authentication and Users

Users can:

- Register an account
- Log in
- Log out
- View their profile
- Update their profile
- View their orders
- View their reviews

The application supports role-based authorization.

Roles include:

```text
USER
ADMIN
```

Administrative functionality is protected from normal users.

---

## Perfume Catalog

Users can browse the available perfumes.

The catalog supports searching and filtering using properties such as:

- Name
- Brand
- Gender
- Minimum price
- Maximum price
- Sorting
- Pagination

Each perfume contains information such as:

- Name
- Brand
- Description
- Price
- Image
- Gender
- Volume
- Stock quantity
- Visibility

---

## Orders

Authenticated users can create orders for perfumes.

The order functionality includes:

- Creating an order
- Viewing personal orders
- Cancelling eligible orders
- Order status management
- Stock management

The application prevents invalid operations such as cancelling another user's order or deleting a perfume that is still referenced by existing orders.

---

## Review Microservice

Review functionality has been extracted into an independent Spring Boot microservice.

```text
perfumehub-main
      |
      | HTTP / OpenFeign
      v
review-service
```

The main application does not access the review database directly.

Instead, it uses a Feign client:

```text
ReviewClient
```

The client communicates with the REST API exposed by `review-service`.

DTOs are used for communication between the applications, including:

```text
ReviewCreateRequest
ReviewUpdateRequest
ReviewResponse
ReviewStatisticsResponse
```

This separates review management from the main application and keeps the two services independently structured.

---

## Review Moderation

Reviews can be moderated by an administrator.

The review workflow supports:

```text
Create Review
     |
     v
Pending Review
     |
     v
Admin Moderation
     |
     +----> Approved
     |
     +----> Rejected
```

Only approved reviews are displayed publicly for perfumes.

---

## REST API

The Review Service exposes REST endpoints for review operations.

The API supports operations for:

- Creating reviews
- Reading reviews
- Updating reviews
- Deleting reviews
- Approving reviews
- Rejecting reviews
- Finding reviews by perfume
- Finding reviews by user
- Retrieving pending reviews
- Retrieving review statistics

Example base path:

```text
/api/reviews
```

The main application consumes these endpoints through OpenFeign.

---

## Exception Handling

The Review Service contains centralized exception handling.

Examples of handled cases include:

- Review not found
- Duplicate review
- Unauthorized review operation
- Invalid operation
- Request validation errors

The REST API returns appropriate HTTP status codes and structured error responses.

Examples include:

```text
400 Bad Request
403 Forbidden
404 Not Found
409 Conflict
```

---

## Security

Spring Security is used for authentication and authorization.

Examples of protected resources include:

```text
/admin/**
/profile/**
/reviews/my
```

Access depends on authentication and user role.

Examples:

| User | Resource | Result |
|---|---|---|
| Guest | Admin dashboard | Redirect to login |
| USER | Admin dashboard | Forbidden |
| ADMIN | Admin dashboard | Allowed |
| Guest | Profile | Redirect to login |
| USER | Profile | Allowed |
| Guest | My Reviews | Redirect to login |
| USER | My Reviews | Allowed |

---

## Caching

The main application uses caching where appropriate to reduce repeated database operations and improve performance.

Cached data is invalidated when relevant application data changes.

---

## Scheduled Tasks

The application contains scheduled functionality for operations that need to run automatically.

Spring scheduling is used to execute these operations without manual user interaction.

---

## Validation

Request data is validated using Jakarta Bean Validation.

Validation is used to prevent invalid data from reaching the service and persistence layers.

Examples include validation of:

- User input
- Review data
- Order data
- Perfume data

---

## Testing

The project contains unit and web-layer tests using:

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Spring Security Test

Main application tests include coverage for services and controllers such as:

```text
PerfumeServiceImplTest
OrderServiceImplTest
UserServiceImplTest
ReviewServiceImplTest
AdminServiceImplTest
PerfumeControllerTest
ReviewControllerTest
SecurityAccessTest
```

The Review Service also contains tests for its service and REST controller layers.

Tests cover successful operations as well as invalid and unauthorized operations.

### Running Tests

For the main application:

```bash
cd perfumehub-main
mvn test
```

For the Review Service:

```bash
cd review-service
mvn test
```

---

## Running the Project

Both applications must be running because the main application communicates with the Review Service.

### 1. Configure the databases

Configure the database connection in the appropriate:

```text
application.properties
```

files.

Example configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/perfumehub
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Do not commit real database passwords or other secrets to the repository.

### 2. Start the Review Service

Open:

```text
review-service
```

and run:

```text
ReviewServiceApplication
```

Alternatively:

```bash
cd review-service
mvn spring-boot:run
```

### 3. Start the Main Application

Open:

```text
perfumehub-main
```

and run:

```text
PerfumehubMainApplication
```

Alternatively:

```bash
cd perfumehub-main
mvn spring-boot:run
```

### 4. Open the application

After both services have started, open the address configured for the main application in your browser.

---

## Project Structure

### Main Application

```text
perfumehub-main/src/main/java/com/denisar5/perfumehub/

cache/
client/
config/
controller/
dto/
entity/
enums/
event/
exception/
listener/
mapper/
model/
repository/
scheduler/
security/
service/
util/
validation/
```

### Review Service

```text
review-service/src/main/java/com/denisar5/review_service/

config/
controller/
dto/
entity/
exception/
repository/
service/
```

---

## Microservice Communication

The communication flow between the applications is:

```text
Browser
   |
   v
PerfumeHub Main Application
   |
   | ReviewClient (OpenFeign)
   |
   v
Review Service REST API
   |
   v
Review Database
```

For example, when the main application needs approved reviews for a perfume:

```text
PerfumeController
       |
       v
ReviewService
       |
       v
ReviewClient
       |
       | HTTP
       v
ReviewRestController
       |
       v
ReviewService
       |
       v
ReviewRepository
```

This keeps review persistence and review business logic inside the Review Service.

---

## Screenshots

Screenshots of the application can be added here.

Suggested screenshots:

1. Home page
2. Perfume catalog
3. Perfume details
4. Login page
5. Registration page
6. User profile
7. User orders
8. User reviews
9. Admin dashboard
10. Admin review moderation

Example:

```markdown
### Perfume Catalog


```

---

## Future Improvements

Possible future improvements include:

- Dockerizing both services
- API Gateway
- Service discovery
- Centralized configuration
- JWT authentication for service communication
- Additional microservices
- Improved observability and logging
- CI/CD pipeline
- Expanded integration testing

---

## Author

**Denis Arnaudov**

Software Engineering student project.