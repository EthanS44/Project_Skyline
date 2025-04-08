package org.Skyline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import java.util.Comparator;

public class ModelListState implements State {
    private StateContext context;
    private ListView<Model> modelListView;
    private ObservableList<Model> modelList;
    private DatabaseManager databaseManager;

    public ModelListState(StateContext context) {
        this.context = context;
        DatabaseManager databaseManager = context.getDatabaseManager();
        context.setModelList(databaseManager.getModelsByUser(context.getCurrentUser()));
        this.modelList = FXCollections.observableArrayList(context.getModelList());
        this.databaseManager = new DatabaseManager();
    }

    @Override
    public void showUI() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();

        // Create the File menu with a Logout option
        Menu fileMenu = new Menu("Options");
        MenuItem logoutMenuItem = new MenuItem("Logout");
        MenuItem mainMenuItem = new MenuItem("Main Menu");
        MenuItem quitMenuItem = new MenuItem("Quit");

        // Add the logout action to the menu items
        logoutMenuItem.setOnAction(event -> handleAction("Logout"));
        mainMenuItem.setOnAction(event -> handleAction("Main Menu"));
        quitMenuItem.setOnAction(event -> handleAction("Quit"));

        fileMenu.getItems().addAll(logoutMenuItem, mainMenuItem, quitMenuItem);
        menuBar.getMenus().add(fileMenu);

        // Create ListView to display models
        modelListView = new ListView<>(modelList);
        modelListView.getSelectionModel().selectFirst(); // Select the first model by default

        // Create buttons for actions
        Button viewButton = new Button("View Model");
        Button sortButton = new Button("Sort Models");
        Button newButton = new Button("New Model");
        Button deleteButton = new Button("Delete Model");
        Button updateButton = new Button("Update Model");

        // Set button actions
        viewButton.setOnAction(event -> handleAction("viewModel"));
        deleteButton.setOnAction(event -> handleAction("deleteModel"));
        sortButton.setOnAction(event -> handleAction("sortModels"));
        newButton.setOnAction(event -> handleAction("newModel"));
        updateButton.setOnAction(event -> handleAction("updateModel"));

        // Create a new VBox each time to avoid reuse across scenes
        VBox layout = new VBox(10, modelListView, viewButton, sortButton, newButton, deleteButton, updateButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Create a BorderPane layout with MenuBar at the top
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(layout);

        // Create the background image (cityscape image)
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("city_background.png"),  // Replace with actual image path or URL
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );

        // Set the background image for the layout
        layout.setBackground(new Background(backgroundImage));

        // Create a new Scene with the updated root layout
        Scene scene = new Scene(root, 600, 400);
        context.getPrimaryStage().setScene(scene);
        context.getPrimaryStage().setTitle("Model List");
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("viewModel")) {
            new ViewModelWindow(context).show();
            context.setSelectedModel(modelListView.getSelectionModel().getSelectedItem());
            if (context.getSelectedModel() != null) {
                System.out.println("Viewing model: " + context.getSelectedModel());
                context.setState(new ModelViewState(context));
            }
        } else if (action.equals("deleteModel")) {
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
            FXCollections.sort(modelList, Comparator.comparing(Model::getName));
            System.out.println("Models sorted");
        } else if (action.equals("newModel")) {
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
}