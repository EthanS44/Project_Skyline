package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsState implements State {
    private StateContext context;
    private Stage stage;
    private String username;

    private DatabaseManager db = new DatabaseManager();

    public SettingsState(StateContext context) {
        this.context = context;
        this.username = context.getCurrentUser();
        this.stage = new Stage(); // Settings screen will be in a new window
    }

    @Override
    public void showUI() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Options");

        MenuItem logoutMenuItem = new MenuItem("Logout");
        MenuItem mainMenuItem = new MenuItem("Main Menu");
        MenuItem quitMenuItem = new MenuItem("Quit");

        logoutMenuItem.setOnAction(event -> handleAction("Logout"));
        mainMenuItem.setOnAction(event -> handleAction("Main Menu"));
        quitMenuItem.setOnAction(event -> handleAction("Quit"));

        fileMenu.getItems().addAll(logoutMenuItem, mainMenuItem, quitMenuItem);
        menuBar.getMenus().add(fileMenu);

        // Create layout
        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new javafx.geometry.Insets(10));
        settingsLayout.getChildren().add(menuBar);

        if (username.equals("admin")) {
            // ------------------ ADMIN PANEL ------------------
            Label adminLabel = new Label("Admin Panel:");

            // --- User Creation Section ---
            TextField newUserField = new TextField();
            newUserField.setPromptText("Enter new username");

            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("Enter password");

            Button createUserButton = new Button("Create User");
            createUserButton.setOnAction(e -> {
                String newUser = newUserField.getText();
                String newPass = newPasswordField.getText();

                if (!newUser.isEmpty() && !newPass.isEmpty()) {
                    if (!db.doesUserExist(newUser)) {
                        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(newPass, org.mindrot.jbcrypt.BCrypt.gensalt());
                        db.saveUser(newUser, hashed);
                        showAlert("Success", "User created.");
                        newUserField.clear();
                        newPasswordField.clear();
                    } else {
                        showAlert("Error", "User already exists.");
                    }
                } else {
                    showAlert("Error", "Username and password cannot be empty.");
                }
            });

            // --- User Deletion Section ---
            ComboBox<String> userDropdown = new ComboBox<>();
            userDropdown.getItems().addAll(db.getAllUsernames());
            userDropdown.setPromptText("Select user to delete");

            Button deleteUserButton = new Button("Delete User");
            deleteUserButton.setOnAction(e -> {
                String userToDelete = userDropdown.getValue();
                if (userToDelete != null && !userToDelete.equals("admin")) {
                    db.deleteUserByUsername(userToDelete);
                    userDropdown.getItems().setAll(db.getAllUsernames()); // Refresh
                    showAlert("Deleted", "User '" + userToDelete + "' deleted.");
                } else {
                    showAlert("Error", "Cannot delete admin or no user selected.");
                }
            });

            settingsLayout.getChildren().addAll(
                    adminLabel,
                    new Label("Create User:"),
                    newUserField, newPasswordField, createUserButton,
                    new Separator(),
                    new Label("Delete User:"),
                    userDropdown, deleteUserButton
            );

        } else {
            // ------------------ REGULAR USER PANEL ------------------
            Label changePassLabel = new Label("Change Your Password");

            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("New Password");

            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Confirm New Password");

            Button updatePasswordBtn = new Button("Update Password");
            Label feedbackLabel = new Label();

            updatePasswordBtn.setOnAction(e -> {
                String newPass = newPasswordField.getText();
                String confirmPass = confirmPasswordField.getText();

                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    feedbackLabel.setText("Please fill in both fields.");
                } else if (!newPass.equals(confirmPass)) {
                    feedbackLabel.setText("Passwords do not match.");
                } else {
                    db.updateUserPassword(username, newPass);
                    feedbackLabel.setText("Password updated successfully.");
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                }
            });

            settingsLayout.getChildren().addAll(
                    changePassLabel,
                    newPasswordField,
                    confirmPasswordField,
                    updatePasswordBtn,
                    feedbackLabel
            );
        }

        Scene settingsScene = new Scene(settingsLayout, 350, 400);
        stage.setTitle("Settings");
        stage.setScene(settingsScene);
        stage.show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("Logout")) {
            System.out.println("Logging out...");
            context.setState(new LoginState(context));
        } else if (action.equals("Main Menu")) {
            System.out.println("Going to Main Menu...");
            context.setState(new MainMenuState(context));
        } else if (action.equals("Quit")) {
            System.out.println("Quitting Application...");
            System.exit(0);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //method for testing purposes
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.db = databaseManager;
    }
}