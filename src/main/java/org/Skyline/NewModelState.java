package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.File;

public class NewModelState implements State {
    private final StateContext context;
    private final Stage stage;
    private TextField packageNameTextField;
    private PackageParser packageParser;
    private ModelRepository modelRepository;

    NewModelState(StateContext context, ModelRepository modelRepository) {
        this.context = context;
        this.stage = new Stage();
        this.modelRepository = modelRepository;
    }

    @Override
    public void showUI() {
        // Create the layout for the new model UI
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Label and TextField for package name
        Label packageLabel = new Label("Enter Package Name:");
        packageNameTextField = new TextField();
        packageNameTextField.setPromptText("e.g., com.example.myapp");

        // Buttons to select programming language
        Button javaButton = new Button("Create Java Model");
        javaButton.setOnAction(event -> selectLanguageAndCreateModel("Java"));

        Button pythonButton = new Button("Create Python Model");
        pythonButton.setOnAction(event -> selectLanguageAndCreateModel("Python"));

        Button cppButton = new Button("Create C++ Model");
        cppButton.setOnAction(event -> selectLanguageAndCreateModel("C++"));

        // Back button to return to the Main Menu
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> context.setState(new MainMenuState(context)));

        layout.getChildren().addAll(packageLabel, packageNameTextField, javaButton, pythonButton, cppButton, backButton);

        // Set up the scene and stage
        Scene scene = new Scene(layout, 300, 250);
        stage.setTitle("New Model");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleAction(String action) {

    }

    private void selectLanguageAndCreateModel(String language) {
        // Get the package name entered by the user
        String packageName = packageNameTextField.getText().trim();
        stage.close();

        if (packageName.isEmpty()) {
            System.out.println("Package name cannot be empty!");
            return;
        }

        // Set the correct PackageParser based on the selected language
        switch (language) {
            case "Java":
                packageParser = new JavaPackageParser();
                break;
            case "Python":
                packageParser = new PythonPackageParser();
                break;
            case "C++":
                packageParser = new CppPackageParser();
                break;
            default:
                System.out.println("Unsupported language.");
                return;
        }

        // Show the directory chooser for selecting the package directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(stage);

        if (directory == null) {
            System.out.println("No directory selected.");
            return;
        }

        // Parse the selected package and create the model
        Model newModel = packageParser.parsePackage(directory, context.getCurrentUser());
        newModel.setName(packageName);
        newModel.setUser(context.getCurrentUser());

        // Save the model to the database
        context.getDatabaseManager().saveModel(newModel);

        // Transition to the Model List after processing
        context.setState(new ModelListState(context, modelRepository));
    }
}