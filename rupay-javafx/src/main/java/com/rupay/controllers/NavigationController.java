package com.rupay.controllers;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.rupay.services.DataService;
import com.rupay.views.*;

import java.util.HashMap;
import java.util.Map;

public class NavigationController {
    private StackPane root;
    private VBox mainContainer;
    private StackPane contentArea;
    private HBox bottomNav;
    private Map<String, BaseView> views;
    private String currentView;
    private boolean showNav;

    public NavigationController() {
        root = new StackPane();
        root.getStyleClass().add("root-container");
        
        mainContainer = new VBox();
        mainContainer.getStyleClass().add("main-container");
        
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        bottomNav = createBottomNav();
        bottomNav.setVisible(false);
        bottomNav.setManaged(false);
        
        mainContainer.getChildren().addAll(contentArea, bottomNav);
        root.getChildren().add(mainContainer);
        
        views = new HashMap<>();
        initializeViews();
    }

    private void initializeViews() {
        views.put("login", new LoginView(this));
        views.put("register", new RegisterView(this));
        views.put("dashboard", new DashboardView(this));
        views.put("buy", new BuyView(this));
        views.put("sell", new SellView(this));
        views.put("transfer", new TransferView(this));
        views.put("wallet", new WalletView(this));
        views.put("history", new HistoryView(this));
        views.put("admin", new AdminView(this));
    }

    private HBox createBottomNav() {
        HBox nav = new HBox();
        nav.getStyleClass().add("bottom-nav");
        nav.setAlignment(Pos.CENTER);
        nav.setSpacing(0);

        String[][] navItems = {
            {"Home", "dashboard", "⌂"},
            {"Wallet", "wallet", "◉"},
            {"History", "history", "☰"},
        };

        for (String[] item : navItems) {
            Button btn = createNavButton(item[0], item[1], item[2]);
            HBox.setHgrow(btn, Priority.ALWAYS);
            nav.getChildren().add(btn);
        }

        return nav;
    }

    private Button createNavButton(String label, String viewName, String icon) {
        VBox btnContent = new VBox(4);
        btnContent.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("nav-icon");
        
        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("nav-text");
        
        btnContent.getChildren().addAll(iconLabel, textLabel);
        
        Button btn = new Button();
        btn.setGraphic(btnContent);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> navigateTo(viewName));
        
        return btn;
    }

    public void navigateTo(String viewName) {
        navigateTo(viewName, null);
    }

    public void navigateTo(String viewName, Object data) {
        BaseView view = views.get(viewName);
        if (view == null) return;

        currentView = viewName;
        
        // Show/hide bottom nav based on view
        boolean shouldShowNav = !viewName.equals("login") && 
                               !viewName.equals("register") && 
                               !viewName.equals("admin");
        
        bottomNav.setVisible(shouldShowNav);
        bottomNav.setManaged(shouldShowNav);

        // Update nav button states
        for (int i = 0; i < bottomNav.getChildren().size(); i++) {
            Button btn = (Button) bottomNav.getChildren().get(i);
            String[] viewNames = {"dashboard", "wallet", "history"};
            if (viewNames[i].equals(viewName)) {
                btn.getStyleClass().add("nav-button-active");
            } else {
                btn.getStyleClass().remove("nav-button-active");
            }
        }

        // Refresh and show view
        view.refresh(data);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view.getView());
    }

    public void showPinPopup(Runnable onSuccess) {
        PinPopupView pinPopup = new PinPopupView(this, onSuccess);
        root.getChildren().add(pinPopup.getView());
    }

    public void closePinPopup() {
        if (root.getChildren().size() > 1) {
            root.getChildren().remove(root.getChildren().size() - 1);
        }
    }

    public StackPane getRoot() {
        return root;
    }

    public String getCurrentView() {
        return currentView;
    }
}
