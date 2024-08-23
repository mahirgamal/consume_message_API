
# User Management and WebSocket Messaging Application

## Overview

This project is a Spring Boot application designed to handle user management with integrated WebSocket messaging support. It features secure user authentication and real-time message broadcasting through WebSockets.

## Features

- **User Management**: Provides user registration, authentication, and retrieval functionalities.
- **Security**: Implements Spring Security for user data protection and authentication.
- **WebSocket Messaging**: Facilitates real-time client communication using WebSockets.
- **Database Integration**: Uses repositories for persisting user and message data.
- **Configurable Broker Settings**: Supports configuration via `config.json` for message broker settings.

## Architecture

The application is structured using a layered architecture:

1. **Controller Layer**: Handles HTTP and WebSocket requests, routing them to the appropriate services.
2. **Service Layer**: Contains business logic for managing users and WebSocket messages.
3. **Repository Layer**: Manages database interactions for user and message data.

## Project Structure

### Java Files

- **`UserService.java`**: Manages user-related services.
- **`CustomUserDetailsService.java`**: Custom implementation for Spring Security's user details.
- **`UserRepository.java`**: Interface for user-related database operations.
- **`QueueMappingRepository.java`**: Manages user-queue mappings for messaging.
- **`User.java`**: Entity class representing a user.
- **`WebSocketMessageService.java`**: Handles WebSocket message processing.
- **`MessageController.java`**: REST controller for managing message endpoints.
- **`WebSocketConfig.java`**: Configures WebSocket endpoints.
- **`WebSocketController.java`**: Manages WebSocket message flow.
- **`SecurityConfig.java`**: Configures security settings, including authentication.

### Additional Files

- **`application.properties`**: Contains Spring Boot application configuration settings.
- **`config.json`**: Defines message broker settings, such as protocol, host, port, and credentials.
- **`WebSocketController.js`**: JavaScript controller for managing WebSocket connections and messages on the client side.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8.x
- A relational database (e.g., MySQL, PostgreSQL)
- A web browser for testing WebSocket functionality

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/your-repo-name.git
    ```
2. **Navigate to the project directory**:
    ```bash
    cd your-repo-name
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


