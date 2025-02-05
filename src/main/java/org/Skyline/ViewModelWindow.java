package org.Skyline;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewModelWindow {

    private final StateContext stateContext;

    public ViewModelWindow(StateContext stateContext) {
        this.stateContext = stateContext;
    }

    public void show() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("View Model - Parameter Settings");

        // Grid Layout
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(8);
        layout.setHgap(10);

        // Dropdown options for parameters
        String[] options = {"NUMBEROFFIELDS", "LINESOFCODE", "NUMBEROFMETHODS", "CYCLOMATICCOMPLEXITY"};

        // X Parameter Dropdown
        Label xLabel = new Label("X Parameter:");
        ComboBox<String> xComboBox = new ComboBox<>();
        xComboBox.getItems().addAll(options);
        xComboBox.setValue(stateContext.getxParameter());

        // Y Parameter Dropdown
        Label yLabel = new Label("Y Parameter:");
        ComboBox<String> yComboBox = new ComboBox<>();
        yComboBox.getItems().addAll(options);
        yComboBox.setValue(stateContext.getyParameter());

        // Z Parameter Dropdown
        Label zLabel = new Label("Z Parameter:");
        ComboBox<String> zComboBox = new ComboBox<>();
        zComboBox.getItems().addAll(options);
        zComboBox.setValue(stateContext.getzParameter());

        // Threshold Fields
        Label xThresholdLabel = new Label("X Threshold:");
        TextField xThresholdField = new TextField(String.valueOf(stateContext.getxParameterThreshold()));

        Label yThresholdLabel = new Label("Y Threshold:");
        TextField yThresholdField = new TextField(String.valueOf(stateContext.getyParameterThreshold()));

        Label zThresholdLabel = new Label("Z Threshold:");
        TextField zThresholdField = new TextField(String.valueOf(stateContext.getzParameterThreshold()));

        // Apply Button
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(event -> {
            // Update StateContext with new values
            stateContext.setXParameterThreshold(Integer.parseInt(xThresholdField.getText()));
            stateContext.setYParameterThreshold(Integer.parseInt(yThresholdField.getText()));
            stateContext.setZParameterThreshold(Integer.parseInt(zThresholdField.getText()));

            System.out.println("Updated Parameters:");
            System.out.println("X: " + xComboBox.getValue() + ", Threshold: " + xThresholdField.getText());
            System.out.println("Y: " + yComboBox.getValue() + ", Threshold: " + yThresholdField.getText());
            System.out.println("Z: " + zComboBox.getValue() + ", Threshold: " + zThresholdField.getText());

            window.close();
        });

        // Add elements to layout
        layout.add(xLabel, 0, 0);
        layout.add(xComboBox, 1, 0);
        layout.add(xThresholdLabel, 0, 1);
        layout.add(xThresholdField, 1, 1);

        layout.add(yLabel, 0, 2);
        layout.add(yComboBox, 1, 2);
        layout.add(yThresholdLabel, 0, 3);
        layout.add(yThresholdField, 1, 3);

        layout.add(zLabel, 0, 4);
        layout.add(zComboBox, 1, 4);
        layout.add(zThresholdLabel, 0, 5);
        layout.add(zThresholdField, 1, 5);

        layout.add(applyButton, 1, 6);

        // Set Scene
        Scene scene = new Scene(layout, 350, 250);
        window.setScene(scene);
        window.showAndWait();
    }
}