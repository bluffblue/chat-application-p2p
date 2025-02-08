# P2P Chat Application

A basic peer-to-peer chat application built with Java and JavaFX. This is a **demonstration project** and is not suitable for production use.

## 📸 Screenshots

![Main Chat Window](image/image.png)
*Main chat interface with basic design*

## ⚠️ Important Notice

This application is for educational purposes only and should not be used in production environments due to:
- Basic encryption implementation
- Limited security features
- No user authentication
- No message persistence
- Basic error handling
- No production-grade testing

## 🎮 Running the Application

1. **Start Server**
   ```bash
   java -cp target/p2p-chat-1.0-SNAPSHOT.jar com.chatapp.Main server
   ```
   Wait until you see "Server running on port 5000"

2. **Start Client**
   ```bash
   java -cp target/p2p-chat-1.0-SNAPSHOT.jar com.chatapp.Main
   ```
   - Enter "localhost" when prompted for server IP
   - Chat GUI will appear

3. **Multiple Clients**
   - Open new terminal for each client
   - Run client command as above
   - Use "localhost" for local testing

## Features

- Real-time messaging
- Modern GUI with JavaFX
- Basic end-to-end encryption
- Multiple client support
- Cross-platform compatibility

## Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher
- JavaFX SDK
