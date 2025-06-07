# User Referral System

A Spring Boot application that implements a user referral system with profile completion tracking.

## Features

- User signup with optional referral code
- Automatic referral code generation
- Profile completion tracking
- Referral status tracking
- API endpoints for user management and referral tracking
- MongoDB integration for data persistence
- Comprehensive test coverage
- Secure password handling with BCrypt

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MongoDB Atlas account

## Setup

1. Clone the repository
2. Navigate to the project directory
3. Configure MongoDB connection:
   - Create a MongoDB Atlas account if you don't have one
   - Create a new cluster
   - Get your connection string
   - Update `application.properties` with your MongoDB URI
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8080.

## API Documentation

### 1. User Signup

```http
POST /api/users/signup
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "referralCode": "ABC123" // Optional
}
```

**Response (200 OK):**

```json
{
  "id": "123",
  "name": "John Doe",
  "email": "john@example.com",
  "referralCode": "XYZ789",
  "userNumber": 1,
  "profileCompleted": false
}
```

**Error Responses:**

- 400 Bad Request: Invalid input data
- 409 Conflict: Email already exists
- 400 Bad Request: Invalid referral code

### 2. Complete Profile

```http
PUT /api/users/{userNumber}/profile
Content-Type: application/json

{
    "phoneNumber": "1234567890",
    "address": "123 Main St"
}
```

**Response (200 OK):**

```json
{
  "id": "123",
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St",
  "profileCompleted": true
}
```

**Error Responses:**

- 400 Bad Request: Invalid input data
- 404 Not Found: User not found

### 3. Get Successful Referrals

```http
GET /api/users/{userNumber}/referrals
```

**Response (200 OK):**

```json
[
  {
    "id": "456",
    "name": "Jane Smith",
    "email": "jane@example.com",
    "profileCompleted": true
  }
]
```

### 4. Get Referral Report

```http
GET /api/users/{userNumber}/referral-report
```

**Response (200 OK):**

```json
{
  "totalReferrals": 5,
  "successfulReferrals": 3,
  "pendingReferrals": 2
}
```

### 5. Get All Users

```http
GET /api/users
```

**Response (200 OK):**

```json
[
  {
    "id": "123",
    "name": "John Doe",
    "email": "john@example.com",
    "referralCode": "XYZ789",
    "userNumber": 1,
    "profileCompleted": true
  }
]
```

### 6. Download Referral Report (CSV)

```http
GET /api/users/referral-report-csv
```

**Response (200 OK):**

- Content-Type: text/csv
- Content-Disposition: attachment; filename=referral_report.csv

## Error Handling

The application uses standard HTTP status codes and returns error messages in the following format:

```json
{
  "error": "Error message",
  "status": 400,
  "timestamp": "2024-03-14T12:00:00Z"
}
```

Common error scenarios:

- Invalid input data (400)
- Resource not found (404)
- Duplicate email (409)
- Invalid referral code (400)
- Server errors (500)

## Testing

Run the tests using:

```bash
mvn test
```

The test suite includes:

- Unit tests for service layer
- Integration tests for API endpoints
- Test coverage reports

## Security

The application implements several security measures:

1. Password encryption using BCrypt
2. Input validation and sanitization
3. MongoDB connection security
4. API endpoint security

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write or update tests
5. Submit a pull request

### Code Style Guidelines

- Follow Java coding conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Write unit tests for new features
- Update documentation for API changes

## License

This project is licensed under the MIT License - see the LICENSE file for details.
