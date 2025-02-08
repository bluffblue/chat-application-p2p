package com.chatapp.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatBubble extends VBox {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final HBox reactionsBox;
    private final List<MessageReaction> reactions;
    private final String message;
    private final boolean sent;
    private final LocalDateTime timestamp;
    
    public ChatBubble(String message, boolean isSent, LocalDateTime timestamp) {
        this.message = message;
        this.sent = isSent;
        this.timestamp = timestamp;
        
        Text messageText = new Text(message);
        messageText.setWrappingWidth(300);
        
        Label timeLabel = new Label(timestamp.format(TIME_FORMATTER));
        timeLabel.getStyleClass().add("timestamp");
        
        reactionsBox = new HBox(5);
        reactionsBox.getStyleClass().add("reactions-box");
        reactions = new ArrayList<>();
        
        getChildren().addAll(messageText, timeLabel, reactionsBox);
        getStyleClass().add(isSent ? "chat-bubble-sent" : "chat-bubble-received");
        
        setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        setPadding(new Insets(5, 10, 5, 10));
        setMaxWidth(USE_PREF_SIZE);
        
        setOnMouseClicked(e -> showReactionPicker());
    }
    
    private void showReactionPicker() {
        String[] quickReactions = {"👍", "❤️", "😂", "😮", "😢", "👏"};
        for (String emoji : quickReactions) {
            if (!hasReaction(emoji)) {
                MessageReaction reaction = new MessageReaction(emoji);
                reactions.add(reaction);
                reactionsBox.getChildren().add(reaction);
            }
        }
    }
    
    private boolean hasReaction(String emoji) {
        return reactions.stream()
            .anyMatch(r -> r.getEmoji().equals(emoji));
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isSent() {
        return sent;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 