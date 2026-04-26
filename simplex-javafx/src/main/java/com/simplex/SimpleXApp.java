package com.simplex;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.simplex.controllers.NavigationController;

public class SimpleXApp extends Application {

    private static Stage primaryStage;
    private static NavigationController navigationController;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        navigationController = new NavigationController();
        
        Scene scene = new Scene(navigationController.getRoot(), 400, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        stage.setTitle("RuPay Exchange");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        // Start with login screen
        navigationController.navigateTo("login");
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static NavigationController getNavigationController() {
        return navigationController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
