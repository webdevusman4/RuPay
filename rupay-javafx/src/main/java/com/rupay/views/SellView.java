package com.rupay.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.rupay.controllers.NavigationController;
import com.rupay.models.*;

public class SellView extends BaseView {
    private VBox view;
    private ComboBox<String> cryptoSelect;
    private TextField amountField;
    private Label priceLabel;
    private Label holdingLabel;
    private Label totalLabel;
    private Label errorLabel;

    public SellView(NavigationController navController) {
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

        Label title = new Label("Sell Crypto");
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
        
        priceLabel = new Label("Price: --");
        priceLabel.getStyleClass().add("price-info");
        
        holdingLabel = new Label("You own: --");
        holdingLabel.getStyleClass().add("holding-info");
        
        selectBox.getChildren().addAll(selectLabel, cryptoSelect, priceLabel, holdingLabel);

        // Amount Input
        VBox amountBox = new VBox(8);
        Label amountLabel = new Label("Amount to Sell");
        amountLabel.getStyleClass().add("input-label");
        
        HBox amountRow = new HBox(8);
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.getStyleClass().add("amount-input");
        amountField.textProperty().addListener((obs, old, newVal) -> updateCalculation());
        HBox.setHgrow(amountField, Priority.ALWAYS);
        
        Button maxBtn = new Button("MAX");
        maxBtn.getStyleClass().addAll("btn", "btn-secondary");
        maxBtn.setOnAction(e -> setMaxAmount());
        
        amountRow.getChildren().addAll(amountField, maxBtn);
        amountBox.getChildren().addAll(amountLabel, amountRow);

        // Total
        VBox totalBox = new VBox(8);
        totalBox.getStyleClass().add("total-box");
        totalBox.setPadding(new Insets(16));
        
        Label totalTitle = new Label("You Will Receive");
        totalTitle.getStyleClass().add("total-title");
        
        totalLabel = new Label("PKR 0");
        totalLabel.getStyleClass().add("total-amount-success");
        
        totalBox.getChildren().addAll(totalTitle, totalLabel);

        // Error
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Sell Button
        Button sellBtn = new Button("Sell Now");
        sellBtn.getStyleClass().addAll("btn", "btn-danger", "btn-full", "btn-large");
        sellBtn.setOnAction(e -> handleSell());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        view.getChildren().addAll(header, selectBox, amountBox, totalBox, errorLabel, spacer, sellBtn);
    }

    private void updateSelection() {
        String selected = cryptoSelect.getValue();
        if (selected == null) return;

        String symbol = selected.split(" - ")[0];
        Crypto crypto = dataService.getCrypto(symbol);
        User user = dataService.getCurrentUser();
        
        if (crypto != null) {
            priceLabel.setText("Price: " + crypto.getFormattedPrice());
        }
        if (user != null) {
            double holding = user.getCryptoBalance(symbol);
            holdingLabel.setText(String.format("You own: %.6f %s", holding, symbol));
        }
        updateCalculation();
    }

    private void updateCalculation() {
        String selected = cryptoSelect.getValue();
        if (selected == null) return;

        String symbol = selected.split(" - ")[0];
        Crypto crypto = dataService.getCrypto(symbol);
        if (crypto == null) return;

        try {
            double amount = Double.parseDouble(amountField.getText());
            double total = amount * crypto.getPriceInPKR();
            totalLabel.setText(String.format("PKR %,.0f", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("PKR 0");
        }
    }

    private void setMaxAmount() {
        String selected = cryptoSelect.getValue();
        if (selected == null) return;

        String symbol = selected.split(" - ")[0];
        User user = dataService.getCurrentUser();
        if (user != null) {
            double holding = user.getCryptoBalance(symbol);
            amountField.setText(String.format("%.6f", holding));
        }
    }

    private void handleSell() {
        String selected = cryptoSelect.getValue();
        if (selected == null) {
            showError("Please select a cryptocurrency");
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

        navController.showPinPopup(() -> {
            if (dataService.sellCrypto(symbol, amount)) {
                navController.navigateTo("dashboard");
            } else {
                showError("Transaction failed");
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

        amountField.clear();
        totalLabel.setText("PKR 0");
        errorLabel.setVisible(false);
        updateSelection();
    }

    @Override
    public Node getView() {
        return view;
    }
}
