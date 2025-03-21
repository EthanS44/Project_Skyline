package org.Skyline;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
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
        // Create ListView to display models
        modelListView = new ListView<Model>(modelList);
        modelListView.getSelectionModel().selectFirst(); // Select the first model by default

        // Create buttons for actions
        Button viewButton = new Button("View Model");
        Button deleteButton = new Button("Delete Model");
        Button sortButton = new Button("Sort Models");
        Button newButton = new Button("New Model");

        // Set button actions
        viewButton.setOnAction(event -> handleAction("viewModel"));
        deleteButton.setOnAction(event -> handleAction("deleteModel"));
        sortButton.setOnAction(event -> handleAction("sortModels"));
        newButton.setOnAction(event -> handleAction("newModel"));

        // Add ListView and buttons to the layout
        VBox layout = new VBox(10, modelListView, viewButton, deleteButton, sortButton, newButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Create the scene and set it on the primaryStage
        Scene scene = new Scene(layout, 400, 300);
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
        }
    }
}
