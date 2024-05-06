Based on the project requirements detailed in your document regarding a Java application for email service management, here is a structured `README.md` file for your project:

```markdown
# Java Email Service Application

## Overview
This project develops a Java-based email service application that simulates the operation of an email system with a mail server managing email boxes and mail clients allowing users to access their email. The application is designed to be scalable and uses JavaFX for the UI components, adhering to the MVC architecture and the Observer Observable pattern without using deprecated classes.

## Features
- **Mail Server**: Manages a list of email boxes and logs actions performed by the mail clients.
- **Mail Client**: Allows users to send, read, reply, forward, and delete emails. It supports multiple recipients for a single email.
- **User Interface**: Implemented with JavaFXML, providing a transparent and efficient user experience.

## Technical Requirements
- The application handles email operations such as sending and receiving emails concurrently.
- It uses Java Sockets for distributed operation over multiple JVMs.
- Error handling is robust, providing clear notifications to users regarding any errors or misoperations.
- The server uses text or binary files (not databases) to persistently store messages.

## Installation
Clone the repository and navigate to the project directory:
```bash
git clone https://github.com/your-repository/java-email-service.git
cd java-email-service
```

## Running the Application
Ensure you have Java installed on your system. You can run the server and client applications using the provided Maven wrapper scripts:
```bash
./mvnw compile exec:java -Dexec.mainClass="com.yourpackage.ServerMain"
./mvnw compile exec:java -Dexec.mainClass="com.yourpackage.ClientMain"
```

## Usage
1. Start the mail server application.
2. Launch the mail client applications.
3. Use the client UI to perform email operations such as sending, receiving, or deleting emails.

## Contributing
Contributions are welcome. Please fork the repository and submit pull requests for any enhancements.

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Contact
- Your Name - [email](mailto:fedepasche6@gmail.com)
- Project Link: [https://github.com/your-repository/java-email-service](https://github.com/your-repository/java-email-service)
