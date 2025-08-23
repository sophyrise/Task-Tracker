# Task Tracker API

A comprehensive RESTful Spring Boot application with role-based access control (RBAC) for managing projects and tasks collaboratively.

## 🚀 Features

- **User Management**: Registration, authentication, and role-based access control
- **Project Management**: Create, read, update, delete projects with ownership
- **Task Management**: Comprehensive task operations with assignment and status tracking
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN, MANAGER, and USER roles with different permissions
- **Pagination & Filtering**: Advanced querying capabilities for tasks
- **Swagger Documentation**: Interactive API documentation
- **Global Error Handling**: Centralized exception handling
- **Database Support**: H2 (development) and PostgreSQL (production) ready

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.3.10**
- **Spring Security** with JWT
- **Spring Data JPA** with Hibernate
- **H2 Database** (development)
- **PostgreSQL** (production ready)
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **Swagger/OpenAPI** for API documentation
- **Maven** for build management

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, VS Code)
- PostgreSQL (optional, for production)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd task-tracker
```

### 2. Run the Application

#### Option A: Using IDE
1. Open the project in your IDE
2. Run `TaskTrackerApplication.java`
3. Application starts on `http://localhost:8080`

#### Option B: Using Maven
```bash
mvn spring-boot:run
```

### 3. Access the Application

- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sopho`
  - Password: `password`

## 🔐 Authentication

### JWT Token Format
```
Authorization: Bearer <your-jwt-token>
```

### Register a User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "password123",
  "role": "ADMIN"
}
```

### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "password123"
}
```

## 👥 Roles and Permissions

### ADMIN
- Full access to all resources
- Can manage all users, projects, and tasks
- Can assign tasks to any user

### MANAGER
- Can create and manage their own projects
- Can create tasks in their projects
- Can assign tasks to users
- Can view and update tasks in their projects

### USER
- Can view and update only their assigned tasks
- Can update task status for assigned tasks
- Cannot create projects or assign tasks

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Users
- `GET /api/users` - Get all users (ADMIN only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/role/{role}` - Get users by role (ADMIN only)
- `DELETE /api/users/{id}` - Delete user (ADMIN only)

### Projects
- `POST /api/projects` - Create project (MANAGER/ADMIN)
- `GET /api/projects` - Get all projects (filtered by role)
- `GET /api/projects/{id}` - Get project by ID
- `GET /api/projects/my-projects` - Get user's own projects
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Tasks
- `POST /api/tasks` - Create task
- `GET /api/tasks/{id}` - Get task by ID
- `GET /api/tasks/project/{projectId}` - Get tasks by project
- `GET /api/tasks/assigned/{userId}` - Get tasks by assigned user
- `GET /api/tasks/status/{status}` - Get tasks by status
- `GET /api/tasks/priority/{priority}` - Get tasks by priority
- `GET /api/tasks/due-before?date=2024-12-31` - Get tasks due before date
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

## 📊 Task Status and Priority

### Task Status
- `TODO` - Task not started
- `IN_PROGRESS` - Task in progress
- `DONE` - Task completed

### Task Priority
- `LOW` - Low priority
- `MEDIUM` - Medium priority (default)
- `HIGH` - High priority

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sopho
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# JWT Configuration
app.jwt.secret=MySuperSecretKeyForJwtGeneration123456
app.jwt.expiration=3600000

# Swagger Configuration
springdoc.api-docs.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🧪 Testing the API

### 1. Register an Admin User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123",
    "role": "ADMIN"
  }'
```

### 2. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123"
  }'
```

### 3. Create a Project
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "My First Project",
    "description": "A sample project for testing"
  }'
```

### 4. Create a Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Sample Task",
    "description": "A sample task for testing",
    "projectId": 1,
    "priority": "HIGH",
    "dueDate": "2024-12-31"
  }'
```

## 📁 Project Structure

```
src/main/java/com/sophie/task_tracker/
├── config/
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── OpenApiConfig.java
│   └── PasswordConfig.java
├── controllers/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   └── TestController.java
├── dto/
│   ├── AuthResponseDto.java
│   ├── LoginDto.java
│   ├── ProjectCreateDto.java
│   ├── ProjectDto.java
│   ├── TaskCreateDto.java
│   ├── TaskDto.java
│   ├── TaskUpdateDto.java
│   ├── UserDto.java
│   └── UserRegistrationDto.java
├── entities/
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Project.java
│   └── Task.java
├── enums/
│   ├── Role.java
│   ├── TaskStatus.java
│   └── TaskPriority.java
├── exception/
│   └── GlobalExceptionHandler.java
├── mappers/
│   ├── UserMapper.java
│   ├── ProjectMapper.java
│   └── TaskMapper.java
├── repositories/
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   └── TaskRepository.java
├── services/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProjectService.java
│   ├── TaskService.java
│   └── JwtService.java
└── TaskTrackerApplication.java
```

