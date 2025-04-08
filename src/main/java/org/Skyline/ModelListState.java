package org.Skyline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Comparator;

// This class represents the state for displaying and managing a list of models in the Skyline application.
public class ModelListState implements State {
    private StateContext context;
    private ListView<Model> modelListView;
    private ObservableList<Model> modelList;
    private DatabaseManager databaseManager;

    // Constructor initializes the state and fetches the models for the current user
    public ModelListState(StateContext context) {
        this.context = context;

        // Retrieve models from database and set them into the state context
        DatabaseManager databaseManager = context.getDatabaseManager();
        context.setModelList(databaseManager.getModelsByUser(context.getCurrentUser()));

        // Initialize the observable list for the ListView
        this.modelList = FXCollections.observableArrayList(context.getModelList());

        // This line creates a new instance instead of using the existing one above (may be redundant)
        this.databaseManager = new DatabaseManager();
    }

    // Displays the UI for listing, viewing, deleting, sorting, creating, and updating models
    @Override
    public void showUI() {
        // Create a menu bar with options like Logout, Main Menu, and Quit
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Options");
        MenuItem logoutMenuItem = new MenuItem("Logout");
        MenuItem mainMenuItem = new MenuItem("Main Menu");
        MenuItem quitMenuItem = new MenuItem("Quit");

        // Set actions for the menu items
        logoutMenuItem.setOnAction(event -> handleAction("Logout"));
        mainMenuItem.setOnAction(event -> handleAction("Main Menu"));
        quitMenuItem.setOnAction(event -> handleAction("Quit"));
        fileMenu.getItems().addAll(logoutMenuItem, mainMenuItem, quitMenuItem);
        menuBar.getMenus().add(fileMenu);

        // Create a ListView to show the user's models
        modelListView = new ListView<>(modelList);
        modelListView.getSelectionModel().selectFirst(); // Automatically select the first model

        // Create buttons for each user action
        Button viewButton = new Button("View Model");
        Button sortButton = new Button("Sort Models");
        Button newButton = new Button("New Model");
        Button deleteButton = new Button("Delete Model");
        Button updateButton = new Button("Update Model");

        // Set action handlers for the buttons
        viewButton.setOnAction(event -> handleAction("viewModel"));
        deleteButton.setOnAction(event -> handleAction("deleteModel"));
        sortButton.setOnAction(event -> handleAction("sortModels"));
        newButton.setOnAction(event -> handleAction("newModel"));
        updateButton.setOnAction(event -> handleAction("updateModel"));

        // Arrange the components in a vertical layout
        VBox layout = new VBox(10, modelListView, viewButton, sortButton, newButton, deleteButton, updateButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Set the menu at the top and the buttons in the center
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(layout);

        // Set a background image
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("city_background.png"),  // Background image file
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );
        layout.setBackground(new Background(backgroundImage));

        // Create and show the scene
        Scene scene = new Scene(root, 600, 400);
        context.getPrimaryStage().setScene(scene);
        context.getPrimaryStage().setTitle("Model List");
        context.getPrimaryStage().show();
    }

    // Handles user actions triggered by menu items or buttons
    @Override
    public void handleAction(String action) {
        if (action.equals("viewModel")) {
            // Open a new window to view the selected model
            new ViewModelWindow(context).show();
            context.setSelectedModel(modelListView.getSelectionModel().getSelectedItem());

            if (context.getSelectedModel() != null) {
                System.out.println("Viewing model: " + context.getSelectedModel());
                context.setState(new ModelViewState(context));
            }

        } else if (action.equals("deleteModel")) {
            // Delete the selected model after confirmation
            Model selectedModel = modelListView.getSelectionModel().getSelectedItem();

            if (selectedModel != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Deletion");
                confirmation.setHeaderText("Are you sure you want to delete this model?");
                confirmation.setContentText("Model: " + selectedModel.getName());

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("Deleting model: " + selectedModel);
                        databaseManager.deleteModelByNameAndUsername(selectedModel.getName(), context.getCurrentUser());
                        modelList.remove(selectedModel);
                    } else {
                        System.out.println("Model deletion cancelled.");
                    }
                });
            }

        } else if (action.equals("sortModels")) {
            // Sort models alphabetically by name
            FXCollections.sort(modelList, Comparator.comparing(Model::getName));
            System.out.println("Models sorted");

        } else if (action.equals("newModel")) {
            // Create a new model if the user hasn't reached the limit
            if (modelList.size() >= 10) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Model Limit Reached");
                alert.setHeaderText("You have reached the maximum number of models allowed.");
                alert.setContentText("Please delete an existing model before creating a new one.");
                alert.showAndWait();
            } else {
                context.setState(new NewModelState(context));
            }

        } else if (action.equals("updateModel")) {
            // Update an existing model by removing it and going to the new model screen
            Model selectedModel = modelListView.getSelectionModel().getSelectedItem();

            if (selectedModel != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Update");
                confirmation.setHeaderText("Are you sure you want to update this model?");
                confirmation.setContentText("Model: " + selectedModel.getName());

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("Updating model: " + selectedModel);
                        databaseManager.deleteModelByNameAndUsername(selectedModel.getName(), context.getCurrentUser());
                        modelList.remove(selectedModel);
                        context.setState(new NewModelState(context));
                    } else {
                        System.out.println("Model update cancelled.");
                    }
                });
            }

        } else if (action.equals("Logout")) {
            // Logout and return to login screen
            System.out.println("Logging out...");
            context.setState(new LoginState(context));

        } else if (action.equals("Main Menu")) {
            // Go back to main menu state
            System.out.println("Going to Main Menu...");
            context.setState(new MainMenuState(context));

        } else if (action.equals("Quit")) {
            // Exit the application
            System.out.println("Quitting Application...");
            System.exit(0);
        }
    }
}