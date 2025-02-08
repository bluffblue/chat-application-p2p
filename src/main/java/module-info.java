module com.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.base;
    
    opens com.chatapp to javafx.graphics;
    opens com.chatapp.client to javafx.graphics;
    opens com.chatapp.server to javafx.graphics;
    opens com.chatapp.model to javafx.graphics;
    opens com.chatapp.utils to javafx.graphics;
    
    exports com.chatapp;
    exports com.chatapp.client;
    exports com.chatapp.server;
    exports com.chatapp.model;
    exports com.chatapp.utils;
} 