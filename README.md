# Online Library Management System

A comprehensive Spring Boot application for managing library operations including book borrowing, returns, and user management across multiple library branches.

## 📋 Table of Contents

- [Background](#background)
- [Functional Requirements](#functional-requirements)
- [Technical Stack](#technical-stack)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Scheduled Tasks](#-scheduled-tasks)
- [API Documentation](#api-documentation)

## 🎯 Background

A city's library system aims to develop an online platform that allows users to:
- Search and browse book collections across multiple library branches
- Borrow and return books through a centralized system
- Manage user accounts and library staff permissions
- Track borrowing history and overdue notifications
- Provide real-time availability status for book copies

This system modernizes traditional library operations by providing a digital interface for both library staff and patrons, improving efficiency and user experience.

## 📋 Functional Requirements

### Core Features
- **User Management**: Registration, authentication, and role-based access control
- **Book Management**: CRUD operations for books and book copies across multiple libraries
- **Borrowing System**: Check-out, return, and overdue tracking
- **Library Management**: Multi-branch library support with individual collections
- **Notification System**: Automated overdue reminders and system notifications
- **Scheduled Tasks**: Automated background processes for overdue notifications
- **Search & Discovery**: Book search by title, author, category, and availability

### User Roles
- **MEMBER**: Regular library patrons who can borrow books
- **LIBRARIAN**: Library staff with administrative privileges

### Business Rules
- Users can borrow up to 5 books simultaneously
- Books are borrowed for 30 days by default
- Overdue books trigger automatic notifications
- **Scheduled Notifications**: System automatically checks for books due in 5 days and sends reminder notifications
- Librarians can manage book collections and user accounts
- Book copies are tracked individually across different library branches

## 🛠 Technical Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL 17
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with JWT authentication
- **Scheduling**: Spring Scheduling with Cron expressions
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger)

### Development Tools
- **Build Tool**: Maven 3.8+
- **Containerization**: Docker & Docker Compose
- **Database Management**: pgAdmin 4
- **Testing**: JUnit 5, Mockito, Spring Boot Test


## 🗄 Database Schema

Complete database schema is available in [`database_schema.sql`](./database_schema.sql).

The schema includes:
- **Users & Authentication**: Roles, users, and librarian management
- **Library & Book Management**: Libraries, books, and book copies
- **Borrowing System**: Borrow records and notifications

## 📁 Project Structure

```
library/
├── src/                           # Source code
│   ├── main/java/com/library/
│   │   ├── config/               # Configuration classes
│   │   ├── constant/             # Constants and enums
│   │   ├── controller/           # REST API controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── entity/              # JPA entities
│   │   ├── exception/           # Custom exceptions
│   │   ├── repository/          # Data access layer
│   │   ├── security/            # Security configuration
│   │   └── service/             # Business logic layer
│   └── resources/
│       ├── application.yml      # Application configuration
│       └── data.sql             # Initial data
├── src/test/                    # Test classes
├── docs/                        # Documentation
│   ├── PRD_線上圖書借閱系統.md
│   ├── Database_Design_Document.md
│   ├── TODO_開發清單.md
│   └── ...                     # Other documentation files
├── education/                   # Learning materials
│   ├── java-packages/          # Java package explanations
│   ├── 01_專案初始化與環境準備.md
│   ├── 02_階段2_Entity層建立.md
│   └── ...                     # Other learning files
├── database_schema.sql         # Complete database schema
├── docker-compose.dev.yml      # Development environment
├── Dockerfile.dev             # Development Dockerfile
├── pom.xml                    # Maven configuration
├── Makefile                   # Development commands
└── README.md                 # This file
```

## 🚀 Quick Start

### Prerequisites
- Docker Desktop
- Git

### Startup Commands
```bash
# Clone and setup
git clone <repository-url>
cd library

# Build and start services
make build

# Start development environment
make start

# Initialize database
make db-init

# Clear database (drop and recreate tables)
make db-clear
```

### Access Services
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui/index.html
- **Database Admin**: http://localhost:5050 (pgAdmin)


### Database Management (pgAdmin)

#### Login Information
- **URL**: http://localhost:5050
- **Email**: `admin@library.com`
- **Password**: `admin`

#### PostgreSQL Database Connection
After logging into pgAdmin, add a server connection:

**Server Connection Info:**
- **Host**: `postgres` (Docker container name)
- **Port**: `5432`
- **Database**: `library`
- **Username**: `postgres`
- **Password**: `password`


### Test Accounts

#### Regular Users (MEMBER)
| Name | Email | Password | Status |
|------|-------|----------|--------|
| John Doe | john.doe@example.com | password | Unverified |
| Jane Smith | jane.smith@example.com | password | Unverified |

#### Librarian (LIBRARIAN)
| Name | Email | Password | Librarian ID | Status |
|------|-------|----------|--------------|--------|
| Librarian Admin | librarian@library.com | password | LIB001 | Verified |

### Testing Workflow

#### Step 1: Login via Swagger UI
1. Open the API documentation: http://localhost:8080/swagger-ui/index.html
2. Find the **Auth Controller** section
3. Click on the **POST /api/v1/auth/login** endpoint
4. Click **Try it out**
5. Use one of the test accounts above:
   ```json
   {
     "email": "librarian@library.com",
     "password": "password"
   }
   ```
6. Click **Execute** to get the authentication token

#### Step 2: Authorize in Swagger UI
1. Copy the `token` from the login response
2. Click the **Authorize** button in the top-right corner
3. Enter your token in the format: `Bearer <your-token>`
4. Click **Authorize** and close the popup

#### Step 3: Test API Operations
Now you can test all API endpoints with authentication:
- Book management (search, borrow, return)
- User management
- Library operations
- And more...

**Note**: The token will be automatically included in all subsequent API requests within the Swagger UI session.



## ⏰ Scheduled Tasks

### Overdue Notification System

Automated task that checks for books due in 5 days and sends reminder notifications.

- **File**: `src/main/java/com/library/service/ScheduledNotificationService.java`
- **Cron**: `"0 * * * * *"` (every minute - development mode)
- **Purpose**: Check borrow records due in 5 days and send notifications via `System.out.println()`

---

## 📝 License

This project is developed for educational and demonstration purposes.
---

**Ready to explore? Start with the API documentation at http://localhost:8080/swagger-ui/index.html**
