package com.chatapp.ui;

import javafx.scene.control.PopupControl;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class EmojiPicker extends PopupControl {
    private static final String[] EMOJIS = {
        "😀", "😂", "😊", "😍", "🥰", "😎", "😴", "🤔", "😅",
        "👍", "👎", "❤️", "💔", "🎉", "🔥", "⭐", "💡", "💪",
        "🙏", "🤝", "✌️", "👋", "🎵", "🎮", "🌈", "🌞", "🌙"
    };
    
    private final TextField targetTextField;
    
    public EmojiPicker(TextField targetTextField) {
        this.targetTextField = targetTextField;
        
        FlowPane emojiPane = new FlowPane();
        emojiPane.setPadding(new Insets(5));
        emojiPane.setHgap(5);
        emojiPane.setVgap(5);
        emojiPane.setPrefWrapLength(200);
        emojiPane.getStyleClass().add("emoji-picker");
        
        for (String emoji : EMOJIS) {
            Button emojiButton = new Button(emoji);
            emojiButton.getStyleClass().add("emoji-button");
            emojiButton.setOnAction(e -> {
                targetTextField.insertText(targetTextField.getCaretPosition(), emoji);
                hide();
            });
            emojiPane.getChildren().add(emojiButton);
        }
        
        setAutoHide(true);
        getScene().setRoot(emojiPane);
    }
} 