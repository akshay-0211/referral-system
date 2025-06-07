# API Testing with cURL

This document contains cURL commands to test all API endpoints of the User Referral System.

## 1. User Signup

### Signup without referral code

```bash
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Signup with referral code

```bash
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "password": "password123",
    "referralCode": "ABC123"
  }'
```

## 2. Complete Profile

```bash
curl -X PUT http://localhost:8080/api/users/1/profile \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "1234567890",
    "address": "123 Main St"
  }'
```

## 3. Get Successful Referrals

```bash
curl -X GET http://localhost:8080/api/users/1/referrals
```

## 4. Get Referral Report

```bash
curl -X GET http://localhost:8080/api/users/1/referral-report
```

## 5. Get All Users

```bash
curl -X GET http://localhost:8080/api/users
```

## 6. Download Referral Report (CSV)

```bash
curl -X GET http://localhost:8080/api/users/referral-report-csv \
  -H "Accept: text/csv" \
  --output referral_report.csv
```

## Error Testing

### Invalid Email Format

```bash
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Invalid User",
    "email": "invalid-email",
    "password": "password123"
  }'
```

### Duplicate Email

```bash
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Duplicate User",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Invalid Referral Code

```bash
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Invalid Referral",
    "email": "invalid@example.com",
    "password": "password123",
    "referralCode": "INVALID123"
  }'
```

### User Not Found

```bash
curl -X PUT http://localhost:8080/api/users/999/profile \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "1234567890",
    "address": "123 Main St"
  }'
```

## Notes

1. Replace `localhost:8080` with your actual server address when deployed
2. The user number in the URLs (e.g., `/1/profile`) should be replaced with actual user numbers
3. All requests return JSON responses except the CSV download
4. Error responses include appropriate HTTP status codes and error messages
