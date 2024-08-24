
# Consume Message API

## Overview

This API (microservices) is a Spring Boot application designed to handle user management with integrated WebSocket messaging support. It features secure user authentication and real-time message consuming through WebSockets. This microservice is part of the Livestock Event Information Sharing Architecture (LEISA), facilitating efficient and standardised data exchange in the livestock industry.

## Related Projects

- [LEI Schema](https://github.com/mahirgamal/LEI-schema): Defines the standardized schema for livestock event information.
- [LEISA](https://github.com/mahirgamal/LEISA): The architecture framework for sharing livestock event information.
- [LEI2JSON](https://github.com/mahirgamal/LEI2JSON): A tool to convert LEI data into JSON format for easy processing.
- [AgriVet Treatment Grapher](https://github.com/mahirgamal/AgriVet-Treatment-Grapher): A Python-based tool designed to visualise treatment data for animals, helping veterinarians and researchers analyse treatment patterns and dosages.
- [Cattle Location Monitor](https://github.com/mahirgamal/Cattle-Location-Monitor): A system that monitors cattle location using GPS data to provide real-time insights into cattle movements and positioning.



## Features

- **User Management**: Provides user registration, authentication, and retrieval functionalities.
- **Security**: Implements Spring Security for user data protection and authentication.
- **WebSocket Messaging**: Facilitates real-time client communication using WebSockets.
- **Database Integration**: Uses repositories for persisting user and message data.
- **Configurable Broker Settings**: Supports configuration via `config.json` for message broker settings.

## Architecture

The application is structured using a layered architecture:

1. **Domain Layer**: Contains the core business logic, including entities, value objects, and domain services that encapsulate the business rules and operations for message consumption and processing.
2. **Application Layer**: Orchestrates the application’s use cases, coordinating the interaction between the domain layer and other parts of the application. This layer contains application services and use case classes.
3. **Infrastructure Layer**: Handles technical details such as database interactions, messaging systems, and external service integrations. This layer implements repository interfaces defined in the domain layer to manage message data.

## Project Structure

```
/project-root
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── leisa
│   │   │           └── microservice
│   │   │               └── consume
│   │   │                   ├── config
│   │   │                   │   └── SecurityConfig.java
│   │   │                   │   └── WebSocketConfig.java
│   │   │                   │
│   │   │                   ├── controller
│   │   │                   │   ├── MessageController.java
│   │   │                   │   ├── WebSocketController.java
│   │   │                   │   └── WebSocketMessageService.java
│   │   │                   │
│   │   │                   ├── model
│   │   │                   │   ├── QueueMapping.java
│   │   │                   │   ├── QueueMappingKey.java
│   │   │                   │   ├── QueueMappingRequest.java
│   │   │                   │   └── User.java
│   │   │                   │
│   │   │                   ├── repository
│   │   │                   │   ├── QueueMappingRepository.java
│   │   │                   │   └── UserRepository.java
│   │   │                   │
│   │   │                   └── service
│   │   │                   │   ├── CustomUserDetailsService.java
│   │   │                   │   └── UserService.java
│   │   │                   │
│   │   │                   └── ConsumeMessageApplication.java
│   │   │
│   │   ├── resources
│   │   │   ├── application.properties
│   │   │   └── config.json
│   │   │
│   │   └── webapp
│   │       └── js
│   │           └── WebSocketController.js
│   │
│   └── test
│       └── java
│           └── com
│               └── leisa
│                   └── microservice
│                       └── consume
│                           └── ExampleTest.java
│
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8.x
- A relational database (e.g., MySQL, PostgreSQL)
- A web browser for testing WebSocket functionality

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/mahirgamal/consume_message_API.git
    ```
2. **Navigate to the project directory**:
    ```bash
    cd consume_message_API
    ```
3. **Configure the application**:
   - Edit `application.properties` for database settings.
   - Update `config.json` with your message broker details.

4. **Build the project**:
    ```bash
    mvn clean install
    ```

5. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```



## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## Acknowledgments
We acknowledge that this work originates from the Trakka project and builds on the existing TerraCipher Trakka implementation. We appreciate the support and resources provided by the Trakka project team. Also, we thank Dave Swain and Will Swain from TerraCipher for their guidance and assistance throughout this project.


## License
This project is licensed under Apache License 2.0 - see the [LICENSE][lic] file for details.

## Contact
If you have any questions, suggestions or need assistance, please don't hesitate to contact us at mhabib@csu.edu.au, akabir@csu.edu.au.

[//]: #
  [lic]: <https://github.com/mahirgamal/consume_message_API/blob/main/LICENSE>


