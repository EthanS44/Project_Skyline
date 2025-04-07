package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;

public class MainMenuState implements State {
    private StateContext context;

    public MainMenuState(StateContext context) {
        this.context = context;
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
        mainMenuItem.setOnAction(event -> handleAction("MainMenu"));
        quitMenuItem.setOnAction(event -> handleAction("Quit"));

        fileMenu.getItems().add(logoutMenuItem);
        fileMenu.getItems().add(mainMenuItem);
        fileMenu.getItems().add(quitMenuItem);
        menuBar.getMenus().add(fileMenu);

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

        // Create a BorderPane layout with the MenuBar at the top and buttons in the center
        BorderPane root = new BorderPane();
        root.setTop(menuBar);  // Add the MenuBar to the top
        root.setCenter(menuLayout);  // Set the button layout in the center

        // Create the background image (cityscape image)
        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image("city_background.png"),  // Replace with actual image path or URL
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );

        // Set the background image for the login layout
        menuLayout.setBackground(new Background(backgroundImage));

        // Create the scene with the root layout and set it on the primaryStage
        Scene mainMenuScene = new Scene(root, 600, 400);
        context.getPrimaryStage().setScene(mainMenuScene);  // Access primaryStage from context
        context.getPrimaryStage().setTitle("Skyline - Main Menu");
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("goToModelList")) {
            // Transition to ModelListState
            context.setState(new ModelListState(context));
        } else if (action.equals("openSettings")) {
            // Transition to SettingsState
            context.setState(new SettingsState(context));
        } else if (action.equals("openHelp")) {
            // Show Help Information (You can implement a Help screen here)
            showHelp();
        } else if (action.equals("Logout")) {
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

    private void showHelp() {
        // Example Help screen: could be a simple popup or new scene
        System.out.println("Displaying Help: Use the buttons to navigate through the app.");
    }
}
