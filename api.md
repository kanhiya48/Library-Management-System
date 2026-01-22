# Library Management System - API Documentation

This document provides comprehensive API documentation for frontend engineers integrating with the Library Management System backend.

## Base URL

```
http://localhost:8080/api/v1
```

---

## Table of Contents

1. [Standard Response Format](#standard-response-format)
2. [Authentication](#authentication)
3. [Books](#books)
4. [Authors](#authors)
5. [Borrow Operations](#borrow-operations)
6. [Donations](#donations)
7. [Fines](#fines)
8. [Admin Operations](#admin-operations)

---

## Standard Response Format

Most API endpoints return responses wrapped in a standard `ApiResponse` format:

```json
{
  "status": "SUCCESS" | "ERROR",
  "message": "Human-readable message",
  "data": { ... },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

## Authentication

### Login

Authenticate a user and receive a JWT token.

| Property | Value |
|----------|-------|
| **URL** | `/auth/login` |
| **Method** | `POST` |
| **Auth Required** | No |

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Success Response (200 OK)

```json
{
  "jwString": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Error Response (400 Bad Request)

```json
"Incorrect Credentials"
```

---

### Sign Up

Register a new user account.

| Property | Value |
|----------|-------|
| **URL** | `/auth/signup` |
| **Method** | `POST` |
| **Auth Required** | No |

#### Request Body

```json
{
  "customerName": "John Doe",
  "email": "john@example.com",
  "countryCode": "+1",
  "mobileNumber": "1234567890",
  "address": "123 Main Street",
  "addressLine1": "Apt 4B",
  "addressLine2": "",
  "city": "New York",
  "state": "NY",
  "pinCode": "10001",
  "dateOfBirth": "1990-05-15",
  "password": "password123",
  "confirmPassword": "password123",
  "secretQuestion": "What is your pet's name?",
  "secretAnswer": "Fluffy"
}
```

#### Success Response (200 OK)

```json
{
  "client_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Error Response (400 Bad Request)

```json
{
  "status": "ERROR",
  "message": "User Already Exists / Validation Error Message",
  "data": null,
  "timestamp": "2026-01-17T12:05:00"
}
```

---

## Books

### Get All Books

Retrieve a list of all books in the library.

| Property | Value |
|----------|-------|
| **URL** | `/api/books` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "All books retrieved",
  "data": [
    {
      "id": 1,
      "publicId": "BK-ABC123",
      "bookTitle": "Clean Code",
      "category": "TECHNOLOGY",
      "coverUrl": "https://example.com/cover.jpg",
      "totalCopies": 5,
      "authors": [...]
    }
  ],
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Get Book by Public ID

Retrieve a specific book by its public ID.

| Property | Value |
|----------|-------|
| **URL** | `/api/books/{publicId}` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `publicId` | String | The public identifier of the book |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Book found",
  "data": {
    "id": 1,
    "publicId": "BK-ABC123",
    "bookTitle": "Clean Code",
    "category": "TECHNOLOGY",
    "coverUrl": "https://example.com/cover.jpg",
    "totalCopies": 5,
    "authors": [...]
  },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Search Books

Search books by keyword (searches both title and author name).

| Property | Value |
|----------|-------|
| **URL** | `/api/books/search` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `q` | String | Yes | Search keyword |

#### Example Request

```
GET /api/books/search?q=clean
```

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Found 3 books",
  "data": [...],
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Search Books by Title

Search books by title only.

| Property | Value |
|----------|-------|
| **URL** | `/api/books/search/title` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `title` | String | Yes | Book title to search |

---

### Search Books by Author

Search books by author name only.

| Property | Value |
|----------|-------|
| **URL** | `/api/books/search/author` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | Yes | Author name to search |

---

### Get Books by Author ID

Get all books by a specific author.

| Property | Value |
|----------|-------|
| **URL** | `/api/books/author/{id}` |
| **Method** | `GET` |
| **Auth Required** | No |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | The author's ID |

---

## Authors

### Register Author

Register a new author in the system.

| Property | Value |
|----------|-------|
| **URL** | `/author/register` |
| **Method** | `POST` |
| **Auth Required** | No |

#### Request Body

```json
{
  "name": "Robert C. Martin",
  "email": "uncle.bob@example.com"
}
```

---

## Borrow Operations

### Issue Book

Issue a book to a user.

| Property | Value |
|----------|-------|
| **URL** | `/api/borrow/issue` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `STAFF`, `ADMIN` |

#### Request Body

```json
{
  "bookPublicId": "BK-ABC123",
  "userPublicId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Book issued successfully",
  "data": {
    "id": 1,
    "bookCopy": {...},
    "user": {...},
    "issueDate": "2026-01-17",
    "dueDate": "2026-02-17",
    "returnDate": null
  },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Return Book

Return a borrowed book.

| Property | Value |
|----------|-------|
| **URL** | `/api/borrow/return/{bookCopyId}` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `STAFF`, `ADMIN` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `bookCopyId` | Long | The ID of the book copy being returned |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Book returned successfully",
  "data": {
    "id": 1,
    "bookCopy": {...},
    "user": {...},
    "issueDate": "2026-01-17",
    "dueDate": "2026-02-17",
    "returnDate": "2026-01-25"
  },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

## Donations

### Submit Donation Request

Submit a book donation request.

| Property | Value |
|----------|-------|
| **URL** | `/api/donations` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `ADMIN` |

#### Request Body

```json
{
  "bookTitle": "Design Patterns",
  "author": "Gang of Four",
  "quantityOffered": 2
}
```

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Donation request submitted",
  "data": {
    "id": 1,
    "bookTitle": "Design Patterns",
    "author": "Gang of Four",
    "quantityOffered": 2,
    "status": "PENDING",
    "submittedAt": "2026-01-17T12:00:00"
  },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Get My Donations

Retrieve the current user's donation requests.

| Property | Value |
|----------|-------|
| **URL** | `/api/donations/my` |
| **Method** | `GET` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `ADMIN` |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Your donations retrieved",
  "data": [...],
  "timestamp": "2026-01-17T12:00:00"
}
```

---

## Fines

### Get My Fines

Retrieve all fines for the current user.

| Property | Value |
|----------|-------|
| **URL** | `/api/fines` |
| **Method** | `GET` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `ADMIN` |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Fines retrieved",
  "data": [
    {
      "id": 1,
      "amount": 5.00,
      "reason": "Late return",
      "paid": false,
      "createdAt": "2026-01-17T12:00:00"
    }
  ],
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Get Unpaid Fines

Retrieve only unpaid fines for the current user.

| Property | Value |
|----------|-------|
| **URL** | `/api/fines/unpaid` |
| **Method** | `GET` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `ADMIN` |

---

### Pay Fine

Pay a specific fine.

| Property | Value |
|----------|-------|
| **URL** | `/api/fines/{fineId}/pay` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `USER`, `ADMIN` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `fineId` | Long | The ID of the fine to pay |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Fine paid successfully",
  "data": {
    "id": 1,
    "amount": 5.00,
    "reason": "Late return",
    "paid": true,
    "paidAt": "2026-01-17T12:00:00"
  },
  "timestamp": "2026-01-17T12:00:00"
}
```

---

## Admin Operations

> **Note:** All admin endpoints require the `ADMIN` role.

### Get Pending Donations

Retrieve all pending donation requests.

| Property | Value |
|----------|-------|
| **URL** | `/api/admin/donations` |
| **Method** | `GET` |
| **Auth Required** | Yes |
| **Required Roles** | `ADMIN` |

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Pending donations retrieved",
  "data": [...],
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Approve Donation

Approve a pending donation request.

| Property | Value |
|----------|-------|
| **URL** | `/api/admin/donations/{donationId}/approve` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `ADMIN` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `donationId` | Long | The ID of the donation to approve |

#### Request Body

```json
{
  "quantityApproved": 2,
  "adminNotes": "Books in good condition"
}
```

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Donation approved",
  "data": {...},
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Reject Donation

Reject a pending donation request.

| Property | Value |
|----------|-------|
| **URL** | `/api/admin/donations/{donationId}/reject` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `ADMIN` |

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `donationId` | Long | The ID of the donation to reject |

#### Request Body (Optional)

```json
"Books are in poor condition"
```

---

### Add Book (Admin)

Add a new book to the library with copies.

| Property | Value |
|----------|-------|
| **URL** | `/api/admin/books` |
| **Method** | `POST` |
| **Auth Required** | Yes |
| **Required Roles** | `ADMIN` |

#### Request Body

```json
{
  "bookTitle": "Clean Architecture",
  "category": "TECHNOLOGY",
  "coverUrl": "https://example.com/cover.jpg",
  "quantity": 5,
  "authorId": [1, 2]
}
```

#### Book Categories

The `category` field accepts the following values:
- `TECHNOLOGY`
- `FICTION`
- `NON_FICTION`
- `SCIENCE`
- `HISTORY`
- `BIOGRAPHY`
- *Other categories as defined in `BookType` enum*

#### Success Response (200 OK)

```json
{
  "status": "SUCCESS",
  "message": "Book added with 5 copies",
  "data": {...},
  "timestamp": "2026-01-17T12:00:00"
}
```

---

### Get All Books (Admin)

Retrieve all books (admin view).

| Property | Value |
|----------|-------|
| **URL** | `/api/admin/books` |
| **Method** | `GET` |
| **Auth Required** | Yes |
| **Required Roles** | `ADMIN` |

---

### Add Book (Alternative Endpoint)

Add a new book using the alternative admin endpoint.

| Property | Value |
|----------|-------|
| **URL** | `/admin/books/add-book` |
| **Method** | `POST` |
| **Auth Required** | Yes |

#### Request Body

Same as [Add Book (Admin)](#add-book-admin)

---

## Authentication Headers

For protected endpoints, include the JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

---

## Error Handling

The API returns appropriate HTTP status codes:

| Status Code | Description |
|-------------|-------------|
| `200` | Success |
| `400` | Bad Request - Invalid input |
| `401` | Unauthorized - Missing or invalid token |
| `403` | Forbidden - Insufficient permissions |
| `404` | Not Found - Resource doesn't exist |
| `500` | Internal Server Error |

Error responses follow the standard `ApiResponse` format:

```json
{
  "status": "ERROR",
  "message": "Description of what went wrong",
  "data": null,
  "timestamp": "2026-01-17T12:00:00"
}
```
