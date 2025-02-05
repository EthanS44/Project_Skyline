package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;


public class NewModelState implements State {
    private final StateContext context;
    private final Stage stage;
    private TextField packageNameTextField;
    private PackageParser packageParser;
    private ModelRepository modelRepository;


    NewModelState(StateContext context, ModelRepository modelRepository) {
        this.context = context;
        this.stage = new Stage();
        this.packageParser = new PackageParser();
        this.modelRepository = modelRepository;
    }

    @Override
    public void showUI() {
        // Create the layout for the new model UI
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Label and TextField for package name
        Label packageLabel = new Label("Enter Java Package Name:");
        packageNameTextField = new TextField();
        packageNameTextField.setPromptText("e.g., com.example.myapp");

        // Button to create the model
        Button createButton = new Button("Create Model");
        createButton.setOnAction(event -> createModel());

        // Back button to return to the Main Menu
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> context.setState(new MainMenuState(context)));

        layout.getChildren().addAll(packageLabel, packageNameTextField, createButton, backButton);

        // Set up the scene and stage
        Scene scene = new Scene(layout, 300, 200);
        stage.setTitle("New Model");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleAction(String action) {
        // Example: Handle other possible actions if needed
        if (action.equals("createModel")) {
            createModel();
        }
    }


    private void createModel() {
        // Get the package name entered by the user
        String packageName = packageNameTextField.getText().trim();

        if (packageName.isEmpty()) {
            System.out.println("Package name cannot be empty!");
            return;
        }

        // Pass the package name to the context or another system for processing
        System.out.println("Creating model for package: " + packageName);

        // Parse java package and create model
        Model newModel = packageParser.parsePackage(packageName);
        newModel.setUser(context.getCurrentUser());

        // Save model to repository
        context.getDatabaseManager().saveModel(newModel);
        context.getModelList().add(newModel);

        // Transition back to the Model List after processing
        context.setState(new ModelListState(context, context.getModelRepository()));
    }
}