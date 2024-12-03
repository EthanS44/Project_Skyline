package org.Skyline;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class LoginState implements State {
    private StateContext context;
    private String username;
    private String password;

    public LoginState(StateContext context) {
        this.context = context;
    }

    @Override
    public void showUI() {
        // Create UI elements for login
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        // Create labels and text fields for username and password
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // Create login and guest buttons
        Button loginButton = new Button("Login");
        Button guestButton = new Button("Guest Login");

        // Error message label (hidden initially)
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        // Get the text when the user clicks login or guest login
        loginButton.setOnAction(event -> {
            username = usernameField.getText();
            password = passwordField.getText();
            context.executeAction("login");
        });
        guestButton.setOnAction(event -> {
            username = usernameField.getText();
            password = passwordField.getText();
            context.executeAction("guestLogin");
        });

        // Add all UI elements to the layout
        loginLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, guestButton, errorLabel);

        // Assuming the Stage is handled by StateContext or elsewhere
        Scene loginScene = new Scene(loginLayout, 400, 300);
        context.getPrimaryStage().setScene(loginScene);
        context.getPrimaryStage().setTitle("Skyline Login");
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        // Handle login or guest login actions
        if ("login".equals(action)) {
            try {
                PasswordManagement.verifyUser(username, password);
            } catch (IOException e) {
                System.out.println("Error: Unable to retrieve password.");
            }

            context.setUser(username);
            context.setState(new MainMenuState(context));
        } else if ("guestLogin".equals(action)) {
            try {
                PasswordManagement.registerUser(username, password);
            } catch (IOException e) {
                System.out.println("Error: Unable to create user.");
            }

            context.setUser(username);
            context.setState(new MainMenuState(context));
        }
    }
}
