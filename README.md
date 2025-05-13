# 🚗 Revisão Carro

A full-stack application built with **Angular** (frontend) and **Spring Boot** (backend) to manage vehicle maintenance and fueling records.

---

## 📋 Features

- Register cars with details such as model, brand, year, and license plate.
- Add **maintenance records** including date, description, and cost.
- Add **fueling records** including date, cost, and mileage.
- View the history of all services and fuelings for each vehicle.
- Responsive and modern UI with **Tailwind CSS**.
- PostgreSQL used for data persistence.

---

## 🛠️ Technologies Used

### Backend
- Java 17
- Spring Boot
- Maven 4
- PostgreSQL
- JPA / Hibernate
- Docker

### Frontend
- Angular
- Tailwind CSS
- Angular Router
- HttpClient
- Docker

---

## 🐳 Running the Project with Docker

### Prerequisites

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

### 📁 Project Structure
```
revisao-carro/
├── backend/ # Spring Boot backend
│ ├── src/
│ └── Dockerfile
├── frontend/ # Angular frontend
│ ├── src/
│ └── Dockerfile
├── docker-compose.yml
└── README.md
```
### ⚙️ Dockerfiles

#### 🔧 `backend/Dockerfile`

```dockerfile
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

RUN apt-get update && apt-get install -y maven
COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 🎨 `frontend/Dockerfile`
```
FROM node:20 AS builder
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist/frontend /usr/share/nginx/html
```

### 🧩 `docker-compose.yml`
```
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: postgres-revision
    environment:
      POSTGRES_DB: revisaocarro
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  backend:
    build: ./backend
    container_name: spring-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/revisaocarro
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    container_name: angular-frontend
    ports:
      - "4200:80"
    depends_on:
      - backend

volumes:
  postgres-data:
```
### ▶️ Running the Application
Clone the repository:
```
git clone https://github.com/your-username/revisao-carro.git
cd revisao-carro
```
Build and run with Docker Compose:
```
docker-compose up --build
```
Access the app:

Frontend: http://localhost:4200

Backend (API): http://localhost:8080

### 🔐 Environment Variables (Optional)
You can configure environment variables using a .env file or define them directly inside docker-compose.yml.

### ✍️ Author
Ryan Carvalho Bernardo

### 📜 License
This project is licensed under the MIT License.
