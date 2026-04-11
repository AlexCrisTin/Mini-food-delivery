
# Mini Food Delivery

A basic web application that allow users to order food from restaurants.

## Note

This repository is shared for the following courses:

- Software Analysis & Design **(CSE703048-1-3-25)**
  - Tran Ngoc An (23010283)
  - Pham Xuan Bach (23010118)
  - Bui Minh Duc (23010513)
  - Nguyen Gia Bao (23010383)
  - Vu Duc Hieu (23010226)
- Software Quality Assurance & Testing **(CSE703010-1-3-25)**
  - Tran Ngoc An (23010283)
  - Pham Xuan Bach (23010118)
  - Bui Minh Duc (23010513)

## Tech Stack

- Backend: Spring Boot (REST API)
- Frontend: Vue.js
- Database: MySQL
- ORM: Spring Data JPA
- Auth: JWT
- Build Tool: Maven

## Testing Stack

- Backend Unit Test: JUnit + Mockito
- Integration Test: Spring Boot Test
- API Testing: Postman
- Frontend testing: Vitest + Vue Test Utils
- End-to-end Testing: Cypress
- Code Coverage: JaCoCo

## Project Structure

- ```/Documents_Analysis_and_Design```
- ```/Documents_Quality_Assurance_and_Testing```
- ```SRC``` code, including:
  - ```/SRC/backend``` for backend
  - ```/SRC/frontend``` for frontend
- ```.gitignore```

## Features (placeholder)

### Users

### Shippers

### Restaurants

### Admin

## Instalation

### Prerequisites

- JDK 17 or higher
- Apache Maven 3.9.x
- Node.js 25.9.x with npm 11.12.x
- MySQL 8.x (MariaDB 12.2.2 on Linux)

### Backend

1. Navigate into backend directory

    ```bash
    cd SRC/backend
    ```

2. Build and run

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

### Frontend

1. Navigate into frontend directory

    ```bash
    cd SRC/frontend
    ```

2. Installation

    ```bash
    npm install
    ```

3. Run in developemnt mode

    ```bash
    npm run dev
    ```

*The app will be available at: <http://localhost:5173>*

> TODO: extend instalation/test
