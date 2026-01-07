# PASystem1_0 - Stray Animals Adoption Platform

## Project Overview
PASystem1_0 is a platform for managing stray animal adoption. Users can browse animals, submit adoption requests, and manage adoption posts. The project uses a front-end and back-end separation architecture.

## Tech Stack
- **Backend:** Spring Boot 3.5.5
- **Frontend:** Vue 3 + Vite
- **Database:** MySQL 8.0
- **Other:** Redis (optional)

## Project Structure
PASystem1_0/
├─ backend/ # Backend code
├─ frontend/ # Frontend code
└─ README.md # This file

## Requirements
- **JDK:** 21.0.4
- **Node.js:** 22.19.0
- **npm:** 10.9.3
- **Maven:** 4.0.0
- **Database:** MySQL 8.0

## Setup and Run

### Backend
Configure database in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pasystem_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   Make sure the database is created and schema is imported:

# Example command:
mysql -u root -p pasystem_db < backend/db/schema.sql
```bash
cd backend
mvn clean install
mvn spring-boot:run

### Frontend
cd frontend
npm install
npm run dev
