package com.chatapp.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import java.util.Optional;

public class ClientGUI extends Application {
    private Client client;
    private TextArea messageArea;
    private TextField messageInput;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("P2P Chat Application");

        VBox root = new VBox(15);
        root.setStyle("-fx-background-color: #f0f2f5;");
        root.setPadding(new Insets(20));

        // Header
        Label headerLabel = new Label("P2P Chat");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#1a73e8"));
        headerLabel.setStyle("-fx-padding: 0 0 10 0;");

        // Message Area
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(20);
        messageArea.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 14px;"
        );
        
        VBox.setVgrow(messageArea, Priority.ALWAYS);

        // Input Area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        
        messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 10 15;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-size: 14px;"
        );
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        Button sendButton = new Button("Send");
        sendButton.setStyle(
            "-fx-background-color: #1a73e8;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 25;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect for send button
        sendButton.setOnMouseEntered(e -> 
            sendButton.setStyle(
                "-fx-background-color: #1557b0;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 25;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            )
        );
        
        sendButton.setOnMouseExited(e -> 
            sendButton.setStyle(
                "-fx-background-color: #1a73e8;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 25;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            )
        );

        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(0.0);
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        
        messageArea.setEffect(shadow);
        inputBox.setEffect(shadow);

        inputBox.getChildren().addAll(messageInput, sendButton);

        // Event handlers
        sendButton.setOnAction(e -> sendMessage());
        messageInput.setOnAction(e -> sendMessage());

        root.getChildren().addAll(headerLabel, messageArea, inputBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        connectToServer();
        primaryStage.show();
    }

    private void connectToServer() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Connection Settings");
        dialog.setHeaderText("Enter Server IP");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipField = new TextField("localhost");
        ipField.setPromptText("Server IP");
        
        grid.add(new Label("IP Address:"), 0, 0);
        grid.add(ipField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return ipField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ip -> {
            try {
                client = new Client(ip, 5000, message -> 
                    Platform.runLater(() -> {
                        messageArea.appendText(message + "\n");
                        messageArea.setScrollTop(Double.MAX_VALUE);
                    })
                );
                client.start();
            } catch (Exception e) {
                showError("Connection Error", e.getMessage());
            }
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(message);
            messageInput.clear();
            messageInput.requestFocus();
        }
    }

    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-width: 1px;"
            );
            
            alert.showAndWait();
        });
    }

    @Override
    public void stop() {
        if (client != null) {
            client.disconnect();
        }
    }
} 