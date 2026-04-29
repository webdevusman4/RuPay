package com.simplex.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.simplex.controllers.NavigationController;
import com.simplex.models.User;

public class LoginView extends BaseView {
    private VBox view;
    private TextField emailField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginView(NavigationController navController) {
        super(navController);
        createView();
    }

    private void createView() {
        view = new VBox(24);
        view.getStyleClass().add("auth-container");
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));

        // Logo
        VBox logoBox = new VBox(8);
        logoBox.setAlignment(Pos.CENTER);

        Label logoIcon = new Label("◈");
        logoIcon.getStyleClass().add("logo-icon");

        Label logoText = new Label("RuPay");
        logoText.getStyleClass().add("logo-text");

        Label tagline = new Label("Crypto Made Simple");
        tagline.getStyleClass().add("tagline");

        logoBox.getChildren().addAll(logoIcon, logoText, tagline);

        // Form
        VBox form = new VBox(16);
        form.setMaxWidth(320);
        form.setAlignment(Pos.CENTER);

        Label title = new Label("Welcome Back");
        title.getStyleClass().add("auth-title");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("auth-input");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-input");

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().addAll("btn", "btn-primary", "btn-full");
        loginBtn.setOnAction(e -> handleLogin());

        HBox registerLink = new HBox(4);
        registerLink.setAlignment(Pos.CENTER);
        Label noAccount = new Label("Don't have an account?");
        noAccount.getStyleClass().add("link-text");
        Hyperlink registerHyperlink = new Hyperlink("Sign Up");
        registerHyperlink.getStyleClass().add("link");
        registerHyperlink.setOnAction(e -> navController.navigateTo("register"));
        registerLink.getChildren().addAll(noAccount, registerHyperlink);

        form.getChildren().addAll(title, emailField, passwordField, errorLabel, loginBtn, registerLink);

        // Demo credentials
        VBox demoBox = new VBox(4);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.getStyleClass().add("demo-box");
        Label demoTitle = new Label("Demo Credentials");
        demoTitle.getStyleClass().add("demo-title");
        Label demoUser = new Label("User: ahmed@example.com | Pass: password123");
        demoUser.getStyleClass().add("demo-text");
        Label demoAdmin = new Label("Admin: admin@rupay.com | Pass: admin123");
        demoAdmin.getStyleClass().add("demo-text");
        demoBox.getChildren().addAll(demoTitle, demoUser, demoAdmin);

        view.getChildren().addAll(logoBox, form, demoBox);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        User user = dataService.login(email, password);
        if (user != null) {
            errorLabel.setVisible(false);
            if (user.isAdmin()) {
                navController.navigateTo("admin");
            } else {
                navController.navigateTo("dashboard");
            }
        } else {
            showError("Invalid credentials");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @Override
    public void refresh(Object data) {
        emailField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }

    @Override
    public Node getView() {
        return view;
    }
}