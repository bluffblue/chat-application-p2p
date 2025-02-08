package com.chatapp.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class MessageReaction extends HBox {
    private final Label emojiLabel;
    private final Label countLabel;
    private int count;
    private final String emoji;
    
    public MessageReaction(String emoji) {
        this.emoji = emoji;
        getStyleClass().add("message-reaction");
        setPadding(new Insets(2, 5, 2, 5));
        setSpacing(3);
        
        emojiLabel = new Label(emoji);
        countLabel = new Label("0");
        count = 0;
        
        getChildren().addAll(emojiLabel, countLabel);
        
        setOnMouseClicked(e -> incrementCount());
    }
    
    public void incrementCount() {
        count++;
        countLabel.setText(String.valueOf(count));
    }
    
    public String getEmoji() {
        return emoji;
    }
} 