package com.simplex.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.simplex.controllers.NavigationController;

public class PinPopupView {
    private StackPane overlay;
    private NavigationController navController;
    private Runnable onSuccess;
    private StringBuilder pinBuilder;
    private HBox dotsContainer;
    private Label errorLabel;

    public PinPopupView(NavigationController navController, Runnable onSuccess) {
        this.navController = navController;
        this.onSuccess = onSuccess;
        this.pinBuilder = new StringBuilder();
        createView();
    }

    private void createView() {
        overlay = new StackPane();
        overlay.getStyleClass().add("pin-overlay");

        VBox popup = new VBox(24);
        popup.getStyleClass().add("pin-popup");
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(32));
        popup.setMaxWidth(320);
        popup.setMaxHeight(500);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().addAll("btn", "btn-icon");
        closeBtn.setOnAction(e -> navController.closePinPopup());
        header.getChildren().add(closeBtn);

        // Title
        Label title = new Label("Enter PIN");
        title.getStyleClass().add("pin-title");
        
        Label subtitle = new Label("Enter your 4-digit PIN to confirm");
        subtitle.getStyleClass().add("pin-subtitle");

        // PIN Dots
        dotsContainer = new HBox(16);
        dotsContainer.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            Label dot = new Label("○");
            dot.getStyleClass().add("pin-dot");
            dotsContainer.getChildren().add(dot);
        }

        // Error
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Number Pad
        GridPane numPad = new GridPane();
        numPad.setAlignment(Pos.CENTER);
        numPad.setHgap(12);
        numPad.setVgap(12);

        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫"};
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                String num = numbers[index++];
                if (num.isEmpty()) {
                    numPad.add(new Region(), col, row);
                    continue;
                }
                
                Button btn = new Button(num);
                btn.getStyleClass().add("pin-num-btn");
                btn.setMinSize(64, 64);
                
                if (num.equals("⌫")) {
                    btn.setOnAction(e -> handleBackspace());
                } else {
                    btn.setOnAction(e -> handleNumber(num));
                }
                
                numPad.add(btn, col, row);
            }
        }

        popup.getChildren().addAll(header, title, subtitle, dotsContainer, errorLabel, numPad);
        overlay.getChildren().add(popup);
    }

    private void handleNumber(String num) {
        if (pinBuilder.length() >= 4) return;
        
        pinBuilder.append(num);
        updateDots();
        
        if (pinBuilder.length() == 4) {
            verifyPin();
        }
    }

    private void handleBackspace() {
        if (pinBuilder.length() > 0) {
            pinBuilder.deleteCharAt(pinBuilder.length() - 1);
            updateDots();
        }
        errorLabel.setVisible(false);
    }

    private void updateDots() {
        for (int i = 0; i < 4; i++) {
            Label dot = (Label) dotsContainer.getChildren().get(i);
            if (i < pinBuilder.length()) {
                dot.setText("●");
                dot.getStyleClass().add("pin-dot-filled");
            } else {
                dot.setText("○");
                dot.getStyleClass().remove("pin-dot-filled");
            }
        }
    }

    private void verifyPin() {
        String pin = pinBuilder.toString();
        
        if (com.simplex.services.DataService.getInstance().verifyPin(pin)) {
            navController.closePinPopup();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } else {
            errorLabel.setText("Invalid PIN");
            errorLabel.setVisible(true);
            pinBuilder.setLength(0);
            updateDots();
        }
    }

    public Node getView() {
        return overlay;
    }
}
