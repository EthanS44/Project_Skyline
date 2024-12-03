package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
