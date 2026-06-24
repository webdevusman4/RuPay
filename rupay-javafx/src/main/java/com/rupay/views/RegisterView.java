package com.rupay.views;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import com.rupay.controllers.NavigationController;
import com.rupay.models.User;

public class RegisterView extends BaseView {
    private VBox view;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField; // Added Password Field
    private PasswordField pinField;
    private PasswordField confirmPinField;
    private Label errorLabel;

    public RegisterView(NavigationController navController) {
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

        logoBox.getChildren().addAll(logoIcon, logoText);

        // Form
        VBox form = new VBox(16);
        form.setMaxWidth(320);
        form.setAlignment(Pos.CENTER);

        Label title = new Label("Create Account");
        title.getStyleClass().add("auth-title");

        nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("auth-input");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("auth-input");

        // Initialized Password Field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-input");

        pinField = new PasswordField();
        pinField.setPromptText("4-digit PIN");
        pinField.getStyleClass().add("auth-input");

        confirmPinField = new PasswordField();
        confirmPinField.setPromptText("Confirm PIN");
        confirmPinField.getStyleClass().add("auth-input");

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button registerBtn = new Button("Create Account");
        registerBtn.getStyleClass().addAll("btn", "btn-primary", "btn-full");
        registerBtn.setOnAction(e -> handleRegister());

        HBox loginLink = new HBox(4);
        loginLink.setAlignment(Pos.CENTER);
        Label hasAccount = new Label("Already have an account?");
        hasAccount.getStyleClass().add("link-text");
        Hyperlink loginHyperlink = new Hyperlink("Sign In");
        loginHyperlink.getStyleClass().add("link");
        loginHyperlink.setOnAction(e -> navController.navigateTo("login"));
        loginLink.getChildren().addAll(hasAccount, loginHyperlink);

        // Added passwordField to the form rendering
        form.getChildren().addAll(title, nameField, emailField, passwordField, pinField,
                confirmPinField, errorLabel, registerBtn, loginLink);

        view.getChildren().addAll(logoBox, form);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText(); // Extract Password
        String pin = pinField.getText();
        String confirmPin = confirmPinField.getText();

        if (name.isEmpty()) {
            showError("Please enter your name");
            return;
        }
        // Create a boolean to check if the email ends with famous domains
        boolean isValidDomain = email.endsWith("@gmail.com") || 
                                email.endsWith("@yahoo.com") || 
                                email.endsWith("@outlook.com") ||
                                email.endsWith("@example.com");

        if (email.isEmpty() || !isValidDomain) {
            showError("Please use a valid Gmail, Yahoo, or Outlook email");
            return;
        }
        if (password.isEmpty() || password.length() < 6) { // Check Password Length
            showError("Password must be at least 6 characters");
            return;
        }
        if (pin.length() != 4 || !pin.matches("\\d+")) {
            showError("PIN must be 4 digits");
            return;
        }
        if (!pin.equals(confirmPin)) {
            showError("PINs do not match");
            return;
        }

        // Updated to send password to DataService
        User user = dataService.register(name, email, password, pin);
        if (user != null) {
            navController.navigateTo("dashboard");
        } else {
            showError("Email already registered");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @Override
    public void refresh(Object data) {
        nameField.clear();
        emailField.clear();
        passwordField.clear(); // Clear password field on refresh
        pinField.clear();
        confirmPinField.clear();
        errorLabel.setVisible(false);
    }

    @Override
    public Node getView() {
        return view;
    }
}