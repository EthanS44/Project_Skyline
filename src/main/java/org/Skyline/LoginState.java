package org.Skyline;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.IOException;

public class LoginState implements State {
    private StateContext context;
    private String username;
    private String password;
    private Label errorLabel = new Label("Error");

    public LoginState(StateContext context) {
        this.context = context;
    }

    // setters for testing purposes
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void showUI() {
        // Create UI elements for login
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        // Create the "Skyline" title text
        Text skylineText = new Text("SKYLINE");
        skylineText.setFont(Font.font("Arial", 50));  // Set font and size
        skylineText.setFill(Color.WHITE);  // White color for the text
        skylineText.setStyle(" -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 1, 1);");

        // Set alignment of the title (centered)
        HBox titleContainer = new HBox(skylineText);  // Wrap in HBox to control alignment
        titleContainer.setAlignment(Pos.CENTER);  // Center the title horizontally
        titleContainer.setPadding(new Insets(20, 0, 20, 0));  // Optional: Padding around the title

        // Create labels and text fields for username and password
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // Create login and guest buttons
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Button quitButton = new Button("Quit");

        // Error message label (hidden initially)
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        // Get the text when the user clicks login or guest login
        loginButton.setOnAction(event -> {
            username = usernameField.getText();
            password = passwordField.getText();
            context.executeAction("login");
        });
        registerButton.setOnAction(event -> {
            username = usernameField.getText();
            password = passwordField.getText();
            context.executeAction("register");
        });

        quitButton.setOnAction(event -> {System.exit(0);});

        // Add all UI elements to the layout
        loginLayout.getChildren().addAll(titleContainer, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton, errorLabel, quitButton);

        // Create the background image (cityscape image)
        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image("city_background.png"),  // Replace with actual image path or URL
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );

        // Set the background image for the login layout
        loginLayout.setBackground(new Background(backgroundImage));

        // Create the scene with the root layout and set it on the primaryStage
        Scene loginScene = new Scene(loginLayout, 600, 400);
        context.getPrimaryStage().setScene(loginScene);
        context.getPrimaryStage().setTitle("Skyline Login");
        context.getPrimaryStage().setResizable(false);
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        // Handle login or guest login actions
        if ("login".equals(action)) {
            try {
                if (PasswordManagement.verifyUser(username, password)) {
                    context.setUser(username);
                    context.setState(new MainMenuState(context));
                } else {
                    errorLabel.setText("Incorrect username or password.");
                    errorLabel.setVisible(true);
                }
            } catch (IOException e) {
                System.out.println("Error: Unable to retrieve password.");
            }

        } else if ("register".equals(action)) {
            try {
                PasswordManagement.registerUser(username, password);
                context.setUser(username);
                context.setState(new MainMenuState(context));

            } catch (IOException e) {
                System.out.println("Error: Unable to create user.");

            } catch (IllegalArgumentException e) {
                errorLabel.setText("Username already exists.");
                errorLabel.setVisible(true);
            }
        }
    }
}
