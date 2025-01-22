package org.Skyline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class ModelListState implements State {
    private StateContext context;
    private ListView<Model> modelListView;
    private ObservableList<Model> modelList;
    private ModelRepository modelRepository;


    public ModelListState(StateContext context) {
        this.context = context;
        modelList = FXCollections.observableArrayList(modelRepository.findByUser(context.getCurrentUser()));
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
                modelList.remove(selectedModel); // Remove the selected model from the list
            }
        } else if (action.equals("sortModels")) {
            // Handle sorting logic
            modelList.sort(String::compareTo); // Sort models alphabetically
            System.out.println("Models sorted");
        } else if (action.equals("newModel")) {
            // Handle new model logic
            context.setState(new NewModelState(context, context.getModelRepository()));
        }
    }
}
