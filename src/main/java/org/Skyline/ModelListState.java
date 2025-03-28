package org.Skyline;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

public class ModelListState implements State {
    private StateContext context;
    private ListView<Model> modelListView;
    private ObservableList<Model> modelList;
    private ModelRepository modelRepository;
    private DatabaseManager databaseManager;


    public ModelListState(StateContext context, ModelRepository modelRepository) {
        this.context = context;
        //this.modelRepository = modelRepository;
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
                new javafx.scene.image.Image("city_background.png"),  // Replace with actual image path or URL
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );

        // Set the background image for the login layout
        layout.setBackground(new Background(backgroundImage));


        // Create a new Scene with the updated root layout (BorderPane)
        Scene scene = new Scene(root, 600, 400);  // Set root as BorderPane to avoid issues with VBox
        context.getPrimaryStage().setScene(scene);
        context.getPrimaryStage().setTitle("Model List");
        context.getPrimaryStage().show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("viewModel")) {
            // Handle model view logic
            // Open window for user to set parameters and thresholds
            new ViewModelWindow(context).show();
            // Transition to view model state
            context.setSelectedModel(modelListView.getSelectionModel().getSelectedItem());
            if (context.getSelectedModel() != null) {
                System.out.println("Viewing model: " + context.getSelectedModel());

                context.setState(new ModelViewState(context));
            }
        } else if (action.equals("deleteModel")) {
            // Handle model deletion logic
            Model selectedModel = modelListView.getSelectionModel().getSelectedItem();
            if (selectedModel != null) {
                System.out.println("Deleting model: " + selectedModel);
                databaseManager.deleteModelByName(selectedModel.getName()); //delete model from the database
                modelList.remove(selectedModel); // Remove the selected model from the list
            }
        } else if (action.equals("sortModels")) {
            // Handle sorting logic
            FXCollections.sort(modelList, Comparator.comparing(Model::getName));
            System.out.println("Models sorted");
        } else if (action.equals("newModel")) {
            // Handle new model logic
            context.setState(new NewModelState(context, context.getModelRepository()));
        } else if (action.equals("updateModel")) {
            // Handle model deletion logic
            Model selectedModel = modelListView.getSelectionModel().getSelectedItem();
            if (selectedModel != null) {
                System.out.println("Deleting model: " + selectedModel);
                databaseManager.deleteModelByName(selectedModel.getName()); //delete model from the database
                modelList.remove(selectedModel); // Remove the selected model from the list
            }
            context.setState(new NewModelState(context, context.getModelRepository()));
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
}
