package com.simplex.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.simplex.controllers.NavigationController;
import com.simplex.models.*;
import java.util.Collection;
import java.util.List;

public class AdminView extends BaseView {
    private VBox view;
    private VBox usersTable;
    private VBox withdrawalsTable;

    public AdminView(NavigationController navController) {
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
        
        Label title = new Label("Admin Panel");
        title.getStyleClass().add("screen-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().addAll("btn", "btn-secondary");
        logoutBtn.setOnAction(e -> {
            dataService.logout();
            navController.navigateTo("login");
        });

        header.getChildren().addAll(title, spacer, logoutBtn);

        // Users Section
        Label usersTitle = new Label("All Users");
        usersTitle.getStyleClass().add("section-title");

        usersTable = new VBox(8);
        usersTable.getStyleClass().add("admin-table");

        // Pending Withdrawals Section
        Label withdrawTitle = new Label("Pending Withdrawals");
        withdrawTitle.getStyleClass().add("section-title");

        withdrawalsTable = new VBox(8);
        withdrawalsTable.getStyleClass().add("admin-table");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(usersTitle, usersTable, withdrawTitle, withdrawalsTable);
        scrollPane.setContent(scrollContent);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        view.getChildren().addAll(header, scrollPane);
    }

    private HBox createUserRow(User user) {
        HBox row = new HBox(12);
        row.getStyleClass().add("admin-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));

        VBox info = new VBox(4);
        Label name = new Label(user.getName());
        name.getStyleClass().add("admin-row-title");
        Label email = new Label(user.getEmail());
        email.getStyleClass().add("admin-row-subtitle");
        info.getChildren().addAll(name, email);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox balances = new VBox(4);
        balances.setAlignment(Pos.CENTER_RIGHT);
        Label pkr = new Label(String.format("PKR %,.0f", user.getPkrBalance()));
        pkr.getStyleClass().add("admin-balance");
        
        StringBuilder cryptoStr = new StringBuilder();
        for (String symbol : new String[]{"BTC", "ETH", "USDT"}) {
            double bal = user.getCryptoBalance(symbol);
            if (bal > 0) {
                if (cryptoStr.length() > 0) cryptoStr.append(" | ");
                cryptoStr.append(String.format("%.4f %s", bal, symbol));
            }
        }
        Label crypto = new Label(cryptoStr.length() > 0 ? cryptoStr.toString() : "No crypto");
        crypto.getStyleClass().add("admin-crypto");
        
        balances.getChildren().addAll(pkr, crypto);

        row.getChildren().addAll(info, spacer, balances);
        return row;
    }

    private HBox createWithdrawalRow(WithdrawRequest request) {
        HBox row = new HBox(12);
        row.getStyleClass().add("admin-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));

        VBox info = new VBox(4);
        Label name = new Label(request.getUserName());
        name.getStyleClass().add("admin-row-title");
        Label details = new Label(request.getFormattedAmount() + " • " + request.getBankAccount());
        details.getStyleClass().add("admin-row-subtitle");
        Label date = new Label(request.getFormattedDate());
        date.getStyleClass().add("admin-row-date");
        info.getChildren().addAll(name, details, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(8);
        
        Button approveBtn = new Button("✓");
        approveBtn.getStyleClass().addAll("btn", "btn-success", "btn-small");
        approveBtn.setOnAction(e -> {
            dataService.approveWithdrawal(request.getId());
            refresh(null);
        });

        Button rejectBtn = new Button("✕");
        rejectBtn.getStyleClass().addAll("btn", "btn-danger", "btn-small");
        rejectBtn.setOnAction(e -> {
            dataService.rejectWithdrawal(request.getId());
            refresh(null);
        });

        actions.getChildren().addAll(approveBtn, rejectBtn);

        row.getChildren().addAll(info, spacer, actions);
        return row;
    }

    @Override
    public void refresh(Object data) {
        // Update users table
        usersTable.getChildren().clear();
        Collection<User> users = dataService.getAllUsers();
        
        for (User user : users) {
            if (!user.isAdmin()) {
                usersTable.getChildren().add(createUserRow(user));
            }
        }

        if (usersTable.getChildren().isEmpty()) {
            Label empty = new Label("No users registered");
            empty.getStyleClass().add("empty-text");
            usersTable.getChildren().add(empty);
        }

        // Update withdrawals table
        withdrawalsTable.getChildren().clear();
        List<WithdrawRequest> requests = dataService.getPendingWithdrawals();
        
        for (WithdrawRequest request : requests) {
            withdrawalsTable.getChildren().add(createWithdrawalRow(request));
        }

        if (requests.isEmpty()) {
            Label empty = new Label("No pending withdrawals");
            empty.getStyleClass().add("empty-text");
            withdrawalsTable.getChildren().add(empty);
        }
    }

    @Override
    public Node getView() {
        return view;
    }
}
