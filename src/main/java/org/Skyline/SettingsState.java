package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsState implements State {
    private StateContext context;
    private Stage stage;
    private boolean isDarkMode;

    // Add other settings fields if needed, like color schemes, building parameters, etc.
    private ComboBox<String> colorSchemeComboBox;

    public SettingsState(StateContext context) {
        this.context = context;
        this.stage = new Stage(); // Settings screen will be in a new window
        this.isDarkMode = false; // Default to light mode
    }

    @Override
    public void showUI() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();

        // Create the File menu with a Logout option
        Menu fileMenu = new Menu("Options");
        MenuItem logoutMenuItem = new MenuItem("Logout");

        // Add main menu option
        MenuItem mainMenuItem = new MenuItem("Main Menu");

        // Add main menu option
        MenuItem quitMenuItem = new MenuItem("Quit");

        // Add the logout action to the menu items
        logoutMenuItem.setOnAction(event -> handleAction("Logout"));
        mainMenuItem.setOnAction(event -> handleAction("Main Menu"));
        quitMenuItem.setOnAction(event -> handleAction("Quit"));

        fileMenu.getItems().add(logoutMenuItem);
        fileMenu.getItems().add(mainMenuItem);
        fileMenu.getItems().add(quitMenuItem);
        menuBar.getMenus().add(fileMenu);

        // Create settings UI
        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new javafx.geometry.Insets(10));

        // Dark Mode Toggle
        CheckBox darkModeCheckBox = new CheckBox("Enable Dark Mode");
        darkModeCheckBox.setSelected(isDarkMode);
        darkModeCheckBox.setOnAction(event -> toggleDarkMode(darkModeCheckBox.isSelected()));

        // Color Scheme ComboBox (example setting)
        Label colorSchemeLabel = new Label("Select Color Scheme:");
        colorSchemeComboBox = new ComboBox<>();
        colorSchemeComboBox.getItems().addAll("Light", "Dark", "Blue", "Green");
        colorSchemeComboBox.getSelectionModel().select(isDarkMode ? "Dark" : "Light"); // Default selection

        // Apply Button to save settings and go back to Main Menu
        javafx.scene.control.Button applyButton = new javafx.scene.control.Button("Apply");
        applyButton.setOnAction(event -> applySettings());

        settingsLayout.getChildren().addAll(darkModeCheckBox, colorSchemeLabel, colorSchemeComboBox, applyButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 200);
        stage.setTitle("Settings");
        stage.setScene(settingsScene);
        stage.show();
    }

    @Override
    public void handleAction(String action) {
        // Example: Handle other possible actions, like saving settings
        if (action.equals("applySettings")) {
            applySettings();
        }else if (action.equals("Logout")) {
            // Handle logout logic
            System.out.println("Logging out...");
            context.setState(new LoginState(context)); // Transition to login state
        }   else if (action.equals("Main Menu")) {
            // Handle logout logic
            System.out.println("Going to Main Menu...");
            context.setState(new MainMenuState(context)); // Transition to main menu state
        }else if (action.equals("Quit")) {
            // Handle logout logic
            System.out.println("Quiting Application...");
            System.exit(0);
        }
    }

    private void toggleDarkMode(boolean enable) {
        this.isDarkMode = enable;
        // Change the UI appearance based on dark mode toggle
        if (enable) {
            System.out.println("Dark Mode Enabled");
        } else {
            System.out.println("Light Mode Enabled");
        }
    }

    private void applySettings() {
        // Apply selected settings
        String selectedColorScheme = colorSchemeComboBox.getSelectionModel().getSelectedItem();
        System.out.println("Applying color scheme: " + selectedColorScheme);

        // You can add logic here to apply the selected settings to the rest of the app
        // Example: Update the global theme or color scheme

        // Transition back to the Main Menu
        context.setState(new MainMenuState(context));
    }
}
