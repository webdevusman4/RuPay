package com.rupay.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.rupay.controllers.NavigationController;
import com.rupay.models.*;

public class BuyView extends BaseView {
    private VBox view;
    private ComboBox<String> cryptoSelect;
    private TextField amountField;
    private Label priceLabel;
    private Label totalLabel;
    private Label balanceLabel;
    private Label errorLabel;
    private Button buyBtn;

    public BuyView(NavigationController navController) {
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

        Label title = new Label("Buy Crypto");
        title.getStyleClass().add("screen-title");

        header.getChildren().addAll(backBtn, title);

        // Balance info
        balanceLabel = new Label("Available: PKR 0");
        balanceLabel.getStyleClass().add("balance-info");

        // Crypto Selection
        VBox selectBox = new VBox(8);
        Label selectLabel = new Label("Select Cryptocurrency");
        selectLabel.getStyleClass().add("input-label");
        
        cryptoSelect = new ComboBox<>();
        cryptoSelect.getStyleClass().add("crypto-select");
        cryptoSelect.setMaxWidth(Double.MAX_VALUE);
        cryptoSelect.setOnAction(e -> updateCalculation());
        
        priceLabel = new Label("Price: --");
        priceLabel.getStyleClass().add("price-info");
        
        selectBox.getChildren().addAll(selectLabel, cryptoSelect, priceLabel);

        // Amount Input
        VBox amountBox = new VBox(8);
        Label amountLabel = new Label("Amount to Buy");
        amountLabel.getStyleClass().add("input-label");
        
        amountField = new TextField();
        amountField.setPromptText("Enter amount (e.g., 0.001)");
        amountField.getStyleClass().add("amount-input");
        amountField.textProperty().addListener((obs, old, newVal) -> updateCalculation());
        
        amountBox.getChildren().addAll(amountLabel, amountField);

        // Total
        VBox totalBox = new VBox(8);
        totalBox.getStyleClass().add("total-box");
        totalBox.setPadding(new Insets(16));
        
        Label totalTitle = new Label("Total Cost");
        totalTitle.getStyleClass().add("total-title");
        
        totalLabel = new Label("PKR 0");
        totalLabel.getStyleClass().add("total-amount");
        
        totalBox.getChildren().addAll(totalTitle, totalLabel);

        // Error
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Buy Button
        buyBtn = new Button("Buy Now");
        buyBtn.getStyleClass().addAll("btn", "btn-success", "btn-full", "btn-large");
        buyBtn.setOnAction(e -> handleBuy());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        view.getChildren().addAll(header, balanceLabel, selectBox, amountBox, totalBox, 
                                  errorLabel, spacer, buyBtn);
    }

    private void updateCalculation() {
        String selected = cryptoSelect.getValue();
        if (selected == null) return;

        String symbol = selected.split(" - ")[0];
        Crypto crypto = dataService.getCrypto(symbol);
        if (crypto == null) return;

        priceLabel.setText("Price: " + crypto.getFormattedPrice());

        try {
            double amount = Double.parseDouble(amountField.getText());
            double total = amount * crypto.getPriceInPKR();
            totalLabel.setText(String.format("PKR %,.0f", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("PKR 0");
        }
    }

    private void handleBuy() {
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
        Crypto crypto = dataService.getCrypto(symbol);
        double total = amount * crypto.getPriceInPKR();

        User user = dataService.getCurrentUser();
        if (user.getPkrBalance() < total) {
            showError("Insufficient balance");
            return;
        }

        // Show PIN popup
        navController.showPinPopup(() -> {
            if (dataService.buyCrypto(symbol, amount)) {
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
        User user = dataService.getCurrentUser();
        if (user != null) {
            balanceLabel.setText(String.format("Available: PKR %,.0f", user.getPkrBalance()));
        }

        // Populate crypto select
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
        updateCalculation();
    }

    @Override
    public Node getView() {
        return view;
    }
}
