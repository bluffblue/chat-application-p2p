package com.chatapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import com.chatapp.utils.EncryptionUtil;

public class Client {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Consumer<String> messageHandler;
    private volatile boolean running;
    private final String host;
    private final int port;

    public Client(String host, int port, Consumer<String> messageHandler) {
        this.host = host;
        this.port = port;
        this.messageHandler = messageHandler;
    }

    public void start() throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        running = true;

        Thread readerThread = new Thread(this::readMessages);
        readerThread.setDaemon(true);
        readerThread.start();
        
        messageHandler.accept("Connected to server!");
    }

    public void sendMessage(String message) {
        try {
            if (socket != null && !socket.isClosed() && writer != null) {
                String encryptedMessage = EncryptionUtil.encrypt(message);
                writer.println(encryptedMessage);
                writer.flush();
                messageHandler.accept("You: " + message);
            } else {
                messageHandler.accept("Error: Not connected to server!");
            }
        } catch (Exception e) {
            messageHandler.accept("Failed to send message: " + e.getMessage());
        }
    }

    private void readMessages() {
        try {
            String message;
            while (running && (message = reader.readLine()) != null) {
                String decryptedMessage = EncryptionUtil.decrypt(message);
                messageHandler.accept("Received: " + decryptedMessage);
            }
        } catch (Exception e) {
            if (running) {
                messageHandler.accept("Connection lost: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            messageHandler.accept("Error disconnecting: " + e.getMessage());
        }
    }
} 