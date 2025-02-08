package com.chatapp.ui;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.io.File;

public class FilePreview extends VBox {
    public FilePreview(File file) {
        getStyleClass().add("file-preview");
        
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        if (isImage(extension)) {
            Image image = new Image(file.toURI().toString());
            ImageView preview = new ImageView(image);
            preview.setFitWidth(200);
            preview.setFitHeight(150);
            preview.setPreserveRatio(true);
            getChildren().add(preview);
        }
        
        Label nameLabel = new Label(fileName);
        nameLabel.getStyleClass().add("file-name");
        getChildren().add(nameLabel);
    }
    
    private boolean isImage(String extension) {
        return extension.matches("jpg|jpeg|png|gif");
    }
} 