package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuState implements State {
    private StateContext context;

    public MainMenuState(StateContext context) {
        this.context = context;
    }

    @Override
    public void showUI() {
        // Create buttons for the main menu
        Button modelListButton = new Button("Model List");
        Button settingsButton = new Button("Settings");
        Button helpButton = new Button("Help");
        Label userLabel = new Label("Logged in as: " + context.getCurrentUser());

        // Set button actions
        modelListButton.setOnAction(event -> handleAction("goToModelList"));
        settingsButton.setOnAction(event -> handleAction("openSettings"));
        helpButton.setOnAction(event -> handleAction("openHelp"));

        // Add buttons to the layout
        VBox menuLayout = new VBox(10, modelListButton, settingsButton, helpButton, userLabel);
        menuLayout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Create the scene and set it on the primaryStage
        Scene mainMenuScene = new Scene(menuLayout, 400, 300);
        context.getPrimaryStage().setScene(mainMenuScene);  // Access primaryStage from context
        context.getPrimaryStage().setTitle("Skyline - Main Menu");
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("goToModelList")) {
            // Transition to ModelListState
            context.setState(new ModelListState(context, context.getModelRepository()));
        } else if (action.equals("openSettings")) {
            // Transition to SettingsState
            context.setState(new SettingsState(context));
        } else if (action.equals("openHelp")) {
            // Show Help Information (You can implement a Help screen here)
            showHelp();
        }
    }

    private void showHelp() {
        // Example Help screen: could be a simple popup or new scene
        System.out.println("Displaying Help: Use the buttons to navigate through the app.");
    }
}
