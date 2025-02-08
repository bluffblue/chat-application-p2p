package com.chatapp;

import javafx.application.Application;
import com.chatapp.client.ClientGUI;
import com.chatapp.server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("server")) {
            Server server = new Server(5000);
            server.start();
        } else {
            Application.launch(ClientGUI.class, args);
        }
    }
} 