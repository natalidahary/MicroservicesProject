# Microservices Application with Dapr

## Overview
This project is a microservices-based application leveraging Spring Boot, Dapr for distributed application runtime, RabbitMQ for message brokering, and MongoDB for data storage. The services include User Service, News Service, and Notification Service. Each service exposes a REST API documented with Swagger.

## Services
### User Service:
- Manages user registration, preferences, and other user-related operations.
- Port: 8081
### News Service:
- Fetches and processes news based on user preferences and sends notifications.
- Newsdata.io is used in your project to fetch and aggregate news articles based on user preferences.
<img width="150" alt="Screenshot 2024-07-31 at 17 03 58" src="https://github.com/user-attachments/assets/7b88206c-48b0-4288-beea-3ef145679349">

- EdenAI is utilized to summarize the fetched news articles using advanced natural language processing capabilities.
<img width="150" alt="Screenshot 2024-07-31 at 17 04 18" src="https://github.com/user-attachments/assets/08d3d617-7b7a-45c1-9024-3d31cd322a31">

- Port: 8082

### Notification Service:
- Handles sending notifications to users.
- Mailtrap is used as a fake SMTP server to safely test and debug email sending functionality without sending actual emails to users.
  It captures the outgoing emails, allowing you to review and verify them in a controlled environment.
- Port: 8083
<img width="500" alt="Screenshot 2024-07-17 at 20 27 04" src="https://github.com/user-attachments/assets/2d822e7b-8445-4721-9d27-3cf3c8085fbf">


### RabbitMQ
- RabbitMQ is used as a message broker to facilitate asynchronous communication between microservices by sending and receiving messages via queues
<img width="500" alt="Screenshot 2024-07-29 at 23 58 29" src="https://github.com/user-attachments/assets/b5ed3cc4-91be-468e-ada7-9bc3db27271a">
<img width="500" alt="Screenshot 2024-07-29 at 23 58 35" src="https://github.com/user-attachments/assets/2f4b051a-ba37-4bf9-920d-21f9233af68e">

  
### Dependencies
- **Each microservice uses the following dependencies:**
- Spring Boot for application framework
- Spring Data MongoDB for MongoDB integration
<img width="500" alt="Screenshot 2024-07-17 at 20 27 38" src="https://github.com/user-attachments/assets/78769cd4-bc5c-4344-91c3-6291df652d1e">
<img width="500" alt="Screenshot 2024-07-17 at 20 27 58" src="https://github.com/user-attachments/assets/f822e34a-55b5-405a-a292-358e28f86743">

- Spring Web for building web applications
- Dapr SDK for Dapr integration
- SpringDoc OpenAPI for Swagger documentation
- Lombok for reducing boilerplate code

## Setup and Running
- Building the Application
- git clone <repository-url>
- cd <repository-directory>
- docker-compose up --build

## Access Swagger UI:
- **User Service: http://localhost:8081/swagger-ui.html**
<img width="500" alt="Screenshot 2024-07-17 at 23 19 45" src="https://github.com/user-attachments/assets/77d415a1-e6d0-4342-8eb4-bd7dd2e9497e">

- **News Service: http://localhost:8082/swagger-ui.html**
<img width="500" alt="Screenshot 2024-07-17 at 23 23 44" src="https://github.com/user-attachments/assets/a0bb59ce-5164-4d5f-b88b-6acd369d270a">

- **Notification Service: http://localhost:8083/swagger-ui.html**
<img width="500" alt="Screenshot 2024-07-17 at 23 24 00" src="https://github.com/user-attachments/assets/597782ac-2290-4bee-8060-ff2b1f0d4cf4">
