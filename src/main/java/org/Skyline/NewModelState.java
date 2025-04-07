package org.Skyline;

import javafx.scene.Scene;
import javafx.scene.control.*;
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

    NewModelState(StateContext context) {
        this.context = context;
        this.stage = new Stage();
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

        layout.getChildren().addAll(packageLabel, packageNameTextField, javaButton, pythonButton, cppButton);

        // Set up the scene and stage
        Scene scene = new Scene(layout, 300, 250);
        stage.setTitle("New Model");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("Logout")) {
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
        context.setState(new ModelListState(context));
    }
}