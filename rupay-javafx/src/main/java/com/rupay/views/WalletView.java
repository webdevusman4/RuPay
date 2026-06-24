package com.rupay.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.rupay.controllers.NavigationController;
import com.rupay.models.*;
import java.util.Map;

public class WalletView extends BaseView {
    private VBox view;
    private Label balanceLabel;
    private VBox holdingsList;
    private TextField depositField;
    private TextField withdrawField;
    private TextField bankField;
    private Label messageLabel;

    public WalletView(NavigationController navController) {
        super(navController);
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.getStyleClass().add("screen-container");
        view.setPadding(new Insets(20));

        // Header
        Label title = new Label("Wallet");
        title.getStyleClass().add("screen-title");

        // Balance Card
        VBox balanceCard = new VBox(8);
        balanceCard.getStyleClass().add("wallet-card");
        balanceCard.setAlignment(Pos.CENTER);
        balanceCard.setPadding(new Insets(24));

        Label balanceTitle = new Label("PKR Balance");
        balanceTitle.getStyleClass().add("balance-title");
        
        balanceLabel = new Label("PKR 0");
        balanceLabel.getStyleClass().add("balance-amount");

        balanceCard.getChildren().addAll(balanceTitle, balanceLabel);

        // Deposit Section
        TitledPane depositPane = createDepositSection();
        
        // Withdraw Section
        TitledPane withdrawPane = createWithdrawSection();

        // Holdings Section
        Label holdingsTitle = new Label("Your Holdings");
        holdingsTitle.getStyleClass().add("section-title");

        holdingsList = new VBox(8);

        // Message
        messageLabel = new Label();
        messageLabel.getStyleClass().add("success-label");
        messageLabel.setVisible(false);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        VBox scrollContent = new VBox(16);
        scrollContent.getChildren().addAll(balanceCard, depositPane, withdrawPane, 
                                           holdingsTitle, holdingsList, messageLabel);
        scrollPane.setContent(scrollContent);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        view.getChildren().addAll(title, scrollPane);
    }

    private TitledPane createDepositSection() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(12));

        depositField = new TextField();
        depositField.setPromptText("Enter amount in PKR");
        depositField.getStyleClass().add("amount-input");

        Button depositBtn = new Button("Deposit");
        depositBtn.getStyleClass().addAll("btn", "btn-success", "btn-full");
        depositBtn.setOnAction(e -> handleDeposit());

        content.getChildren().addAll(depositField, depositBtn);

        TitledPane pane = new TitledPane("Deposit PKR", content);
        pane.setExpanded(false);
        pane.getStyleClass().add("accordion-pane");
        return pane;
    }

    private TitledPane createWithdrawSection() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(12));

        withdrawField = new TextField();
        withdrawField.setPromptText("Enter amount in PKR");
        withdrawField.getStyleClass().add("amount-input");

        bankField = new TextField();
        bankField.setPromptText("Bank Account Number");
        bankField.getStyleClass().add("amount-input");

        Button withdrawBtn = new Button("Request Withdrawal");
        withdrawBtn.getStyleClass().addAll("btn", "btn-danger", "btn-full");
        withdrawBtn.setOnAction(e -> handleWithdraw());

        Label note = new Label("Withdrawals require admin approval");
        note.getStyleClass().add("note-text");

        content.getChildren().addAll(withdrawField, bankField, withdrawBtn, note);

        TitledPane pane = new TitledPane("Withdraw PKR", content);
        pane.setExpanded(false);
        pane.getStyleClass().add("accordion-pane");
        return pane;
    }

    private HBox createHoldingItem(String symbol, double amount) {
        HBox item = new HBox(12);
        item.getStyleClass().add("holding-item");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));

        Crypto crypto = dataService.getCrypto(symbol);
        
        Label icon = new Label(symbol.substring(0, 1));
        icon.getStyleClass().add("crypto-icon");
        if (crypto != null) {
            icon.setStyle("-fx-background-color: " + crypto.getIconColor() + ";");
        }

        VBox info = new VBox(2);
        Label name = new Label(crypto != null ? crypto.getName() : symbol);
        name.getStyleClass().add("holding-name");
        Label amountLabel = new Label(String.format("%.6f %s", amount, symbol));
        amountLabel.getStyleClass().add("holding-amount");
        info.getChildren().addAll(name, amountLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        double value = crypto != null ? amount * crypto.getPriceInPKR() : 0;
        Label valueLabel = new Label(String.format("PKR %,.0f", value));
        valueLabel.getStyleClass().add("holding-value");

        item.getChildren().addAll(icon, info, spacer, valueLabel);
        return item;
    }

    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(depositField.getText());
            if (amount <= 0) throw new NumberFormatException();

            if (dataService.depositPKR(amount)) {
                showMessage("Deposit successful!", true);
                refresh(null);
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid amount", false);
        }
    }

    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            String bank = bankField.getText().trim();
            
            if (amount <= 0) throw new NumberFormatException();
            if (bank.isEmpty()) {
                showMessage("Please enter bank account", false);
                return;
            }

            User user = dataService.getCurrentUser();
            if (user.getPkrBalance() < amount) {
                showMessage("Insufficient balance", false);
                return;
            }

            navController.showPinPopup(() -> {
                if (dataService.requestWithdraw(amount, bank)) {
                    showMessage("Withdrawal request submitted!", true);
                    refresh(null);
                } else {
                    showMessage("Request failed", false);
                }
            });
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid amount", false);
        }
    }

    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-label", "error-label");
        messageLabel.getStyleClass().add(isSuccess ? "success-label" : "error-label");
        messageLabel.setVisible(true);
    }

    @Override
    public void refresh(Object data) {
        User user = dataService.getCurrentUser();
        if (user == null) return;

        balanceLabel.setText(String.format("PKR %,.0f", user.getPkrBalance()));

        holdingsList.getChildren().clear();
        Map<String, Double> holdings = user.getCryptoHoldings();
        boolean hasHoldings = false;
        
        for (Map.Entry<String, Double> entry : holdings.entrySet()) {
            if (entry.getValue() > 0) {
                holdingsList.getChildren().add(createHoldingItem(entry.getKey(), entry.getValue()));
                hasHoldings = true;
            }
        }

        if (!hasHoldings) {
            Label empty = new Label("No crypto holdings yet");
            empty.getStyleClass().add("empty-text");
            holdingsList.getChildren().add(empty);
        }

        depositField.clear();
        withdrawField.clear();
        bankField.clear();
    }

    @Override
    public Node getView() {
        return view;
    }
}
