package com.rupay.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.rupay.controllers.NavigationController;
import com.rupay.models.*;

public class TransferView extends BaseView {
    private VBox view;
    private ComboBox<String> cryptoSelect;
    private TextField emailField;
    private TextField amountField;
    private Label holdingLabel;
    private Label errorLabel;

    public TransferView(NavigationController navController) {
        super(navController);
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.getStyleClass().add("screen-container");
        view.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("←");
        backBtn.getStyleClass().addAll("btn", "btn-icon");
        backBtn.setOnAction(e -> navController.navigateTo("dashboard"));

        Label title = new Label("Transfer Crypto");
        title.getStyleClass().add("screen-title");

        header.getChildren().addAll(backBtn, title);

        // Crypto Selection
        VBox selectBox = new VBox(8);
        Label selectLabel = new Label("Select Cryptocurrency");
        selectLabel.getStyleClass().add("input-label");
        
        cryptoSelect = new ComboBox<>();
        cryptoSelect.getStyleClass().add("crypto-select");
        cryptoSelect.setMaxWidth(Double.MAX_VALUE);
        cryptoSelect.setOnAction(e -> updateSelection());
        
        holdingLabel = new Label("You own: --");
        holdingLabel.getStyleClass().add("holding-info");
        
        selectBox.getChildren().addAll(selectLabel, cryptoSelect, holdingLabel);

        // Recipient Email
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("Recipient Email");
        emailLabel.getStyleClass().add("input-label");
        
        emailField = new TextField();
        emailField.setPromptText("Enter recipient's email");
        emailField.getStyleClass().add("amount-input");
        
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Amount Input
        VBox amountBox = new VBox(8);
        Label amountLabel = new Label("Amount to Transfer");
        amountLabel.getStyleClass().add("input-label");
        
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.getStyleClass().add("amount-input");
        
        amountBox.getChildren().addAll(amountLabel, amountField);

        // Error
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Transfer Button
        Button transferBtn = new Button("Transfer");
        transferBtn.getStyleClass().addAll("btn", "btn-primary", "btn-full", "btn-large");
        transferBtn.setOnAction(e -> handleTransfer());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        view.getChildren().addAll(header, selectBox, emailBox, amountBox, errorLabel, spacer, transferBtn);
    }

    private void updateSelection() {
        String selected = cryptoSelect.getValue();
        if (selected == null) return;

        String symbol = selected.split(" - ")[0];
        User user = dataService.getCurrentUser();
        
        if (user != null) {
            double holding = user.getCryptoBalance(symbol);
            holdingLabel.setText(String.format("You own: %.6f %s", holding, symbol));
        }
    }

    private void handleTransfer() {
        String selected = cryptoSelect.getValue();
        if (selected == null) {
            showError("Please select a cryptocurrency");
            return;
        }

        String recipientEmail = emailField.getText().trim();
        if (recipientEmail.isEmpty() || !recipientEmail.contains("@")) {
            showError("Please enter a valid email");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
            return;
        }

        String symbol = selected.split(" - ")[0];
        User user = dataService.getCurrentUser();
        
        if (user.getCryptoBalance(symbol) < amount) {
            showError("Insufficient " + symbol + " balance");
            return;
        }

        if (recipientEmail.equalsIgnoreCase(user.getEmail())) {
            showError("Cannot transfer to yourself");
            return;
        }

        navController.showPinPopup(() -> {
            if (dataService.transferCrypto(symbol, amount, recipientEmail)) {
                navController.navigateTo("dashboard");
            } else {
                showError("Transfer failed. User not found.");
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @Override
    public void refresh(Object data) {
        cryptoSelect.getItems().clear();
        for (Crypto crypto : dataService.getAllCryptos()) {
            cryptoSelect.getItems().add(crypto.getSymbol() + " - " + crypto.getName());
        }
        if (!cryptoSelect.getItems().isEmpty()) {
            cryptoSelect.setValue(cryptoSelect.getItems().get(0));
        }

        emailField.clear();
        amountField.clear();
        errorLabel.setVisible(false);
        updateSelection();
    }

    @Override
    public Node getView() {
        return view;
    }
}
