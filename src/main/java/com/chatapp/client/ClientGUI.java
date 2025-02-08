package com.chatapp.client;

import com.chatapp.ui.ChatBubble;
import com.chatapp.ui.EmojiPicker;
import com.chatapp.ui.FilePreview;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

public class ClientGUI extends Application {
    private Client client;
    private VBox messageContainer;
    private ScrollPane scrollPane;
    private TextField messageInput;
    private Label typingIndicator;
    private boolean isDarkMode = false;
    private EmojiPicker emojiPicker;
    private TextField searchField;
    private VBox searchResults;
    private Stage primaryStage;
    private Label userStatusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        
        // Header with search
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(10));
        
        HBox header = createHeader();
        createSearchArea(topContainer);
        
        topContainer.getChildren().add(header);
        root.setTop(topContainer);
        
        // Messages Area
        messageContainer = new VBox(10);
        messageContainer.setPadding(new Insets(10));
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setCenter(scrollPane);
        
        // Input Area
        HBox inputArea = createInputArea();
        root.setBottom(inputArea);
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/chat-style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Modern Chat App");
        primaryStage.show();
        
        connectToServer();
    }

    private void createSearchArea(VBox container) {
        searchField = new TextField();
        searchField.setPromptText("Search messages...");
        searchField.getStyleClass().add("search-field");
        
        searchResults = new VBox(5);
        searchResults.setVisible(false);
        searchResults.getStyleClass().add("search-results");
        
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchMessages(searchField.getText());
            }
        });
        
        container.getChildren().addAll(searchField, searchResults);
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        
        userStatusLabel = new Label("●");
        userStatusLabel.setTextFill(Color.GREEN);
        
        VBox userInfo = new VBox(5);
        Label nameLabel = new Label("Chat Room");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        typingIndicator = new Label("");
        typingIndicator.getStyleClass().add("typing-indicator");
        userInfo.getChildren().addAll(nameLabel, typingIndicator);
        
        Button themeToggle = new Button("🌙");
        themeToggle.setStyle("-fx-background-color: transparent;");
        themeToggle.setOnAction(e -> toggleTheme());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(userStatusLabel, userInfo, spacer, themeToggle);
        return header;
    }
    
    private HBox createInputArea() {
        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(10));
        inputArea.setAlignment(Pos.CENTER);
        
        Button emojiButton = new Button("😊");
        emojiButton.setStyle("-fx-background-color: transparent;");
        emojiPicker = new EmojiPicker(messageInput);
        emojiButton.setOnAction(e -> showEmojiPicker(emojiButton));
        
        Button attachButton = new Button("📎");
        attachButton.setStyle("-fx-background-color: transparent;");
        attachButton.setOnAction(e -> handleFileAttachment());
        
        messageInput = new TextField();
        messageInput.getStyleClass().add("message-input");
        messageInput.setPromptText("Type a message...");
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        
        Button sendButton = new Button("➤");
        sendButton.getStyleClass().add("send-button");
        
        inputArea.getChildren().addAll(emojiButton, attachButton, messageInput, sendButton);
        
        // Event Handlers
        sendButton.setOnAction(e -> sendMessage());
        messageInput.setOnAction(e -> sendMessage());
        messageInput.setOnKeyTyped(e -> showTypingIndicator());
        
        return inputArea;
    }
    
    private void showEmojiPicker(Button emojiButton) {
        if (emojiPicker != null) {
            double x = emojiButton.localToScreen(0, 0).getX();
            double y = emojiButton.localToScreen(0, 0).getY() - 200;
            emojiPicker.show(emojiButton, x, y);
        }
    }

    private void handleFileAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"),
            new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.txt")
        );
        
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            FilePreview preview = new FilePreview(file);
            messageContainer.getChildren().add(preview);
            scrollToBottom();
            // TODO: Implement file sending logic
            sendFileMessage(file);
        }
    }
    
    private void sendFileMessage(File file) {
        String message = "📎 " + file.getName();
        ChatBubble bubble = new ChatBubble(message, true, LocalDateTime.now());
        messageContainer.getChildren().add(bubble);
        scrollToBottom();
        if (client != null) {
            client.sendMessage(message);
        }
    }

    private void searchMessages(String query) {
        searchResults.getChildren().clear();
        searchResults.setVisible(!query.isEmpty());
        
        if (!query.isEmpty()) {
            messageContainer.getChildren().stream()
                .filter(node -> node instanceof ChatBubble)
                .map(node -> (ChatBubble) node)
                .filter(bubble -> bubble.getMessage().toLowerCase().contains(query.toLowerCase()))
                .forEach(bubble -> {
                    ChatBubble result = new ChatBubble(
                        bubble.getMessage(),
                        bubble.isSent(),
                        bubble.getTimestamp()
                    );
                    result.setOnMouseClicked(e -> scrollToMessage(bubble));
                    searchResults.getChildren().add(result);
                });
        }
    }
    
    private void scrollToMessage(ChatBubble bubble) {
        int index = messageContainer.getChildren().indexOf(bubble);
        if (index >= 0) {
            double height = messageContainer.getHeight();
            double position = index * bubble.getBoundsInParent().getHeight() / height;
            scrollPane.setVvalue(position);
            highlightMessage(bubble);
        }
    }
    
    private void highlightMessage(ChatBubble bubble) {
        bubble.getStyleClass().add("highlighted-message");
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(() -> 
                    bubble.getStyleClass().remove("highlighted-message")
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        Scene scene = primaryStage.getScene();
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().remove(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        }
    }
    
    private void updateUserStatus(boolean isOnline) {
        Platform.runLater(() -> {
            userStatusLabel.setTextFill(isOnline ? Color.GREEN : Color.RED);
            userStatusLabel.setText("●");
        });
    }
    
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && client != null) {
            ChatBubble bubble = new ChatBubble(message, true, LocalDateTime.now());
            messageContainer.getChildren().add(bubble);
            messageInput.clear();
            scrollToBottom();
            client.sendMessage(message);
        }
    }
    
    private void showTypingIndicator() {
        typingIndicator.setText("Someone is typing...");
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(() -> typingIndicator.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
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
                        ChatBubble bubble = new ChatBubble(message, false, LocalDateTime.now());
                        messageContainer.getChildren().add(bubble);
                        scrollToBottom();
                    })
                );
                client.start();
                updateUserStatus(true);
            } catch (Exception e) {
                showError("Connection Error", e.getMessage());
                updateUserStatus(false);
            }
        });
    }

    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
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