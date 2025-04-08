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
        window.setResizable(false);

        // Grid Layout
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(8);
        layout.setHgap(10);

        // Dropdown options for parameters
        String[] options = {"NUMBEROFFIELDS", "LINESOFCODE", "LINESOFCODENOBLANKS", "NUMBEROFMETHODS", "AVERAGELINESPERMETHOD", "CYCLOMATICCOMPLEXITY", "INHERITANCEDEPTH"};

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

        // Maximum/Minimum Radio Buttons for X Threshold
        ToggleGroup xThresholdGroup = new ToggleGroup();
        RadioButton xMaximum = new RadioButton("Maximum");
        xMaximum.setToggleGroup(xThresholdGroup);
        xMaximum.setSelected(stateContext.isXThresholdMaximum()); // Assume a method to get this value

        RadioButton xMinimum = new RadioButton("Minimum");
        xMinimum.setToggleGroup(xThresholdGroup);
        xMinimum.setSelected(!stateContext.isXThresholdMaximum());

        // Maximum/Minimum Radio Buttons for Y Threshold
        ToggleGroup yThresholdGroup = new ToggleGroup();
        RadioButton yMaximum = new RadioButton("Maximum");
        yMaximum.setToggleGroup(yThresholdGroup);
        yMaximum.setSelected(stateContext.isYThresholdMaximum());

        RadioButton yMinimum = new RadioButton("Minimum");
        yMinimum.setToggleGroup(yThresholdGroup);
        yMinimum.setSelected(!stateContext.isYThresholdMaximum());

        // Maximum/Minimum Radio Buttons for Z Threshold
        ToggleGroup zThresholdGroup = new ToggleGroup();
        RadioButton zMaximum = new RadioButton("Maximum");
        zMaximum.setToggleGroup(zThresholdGroup);
        zMaximum.setSelected(stateContext.isZThresholdMaximum());

        RadioButton zMinimum = new RadioButton("Minimum");
        zMinimum.setToggleGroup(zThresholdGroup);
        zMinimum.setSelected(!stateContext.isZThresholdMaximum());

        // Apply Button
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(event -> {
            // Update StateContext with new values
            stateContext.setXParameterThreshold(Integer.parseInt(xThresholdField.getText()));
            stateContext.setYParameterThreshold(Integer.parseInt(yThresholdField.getText()));
            stateContext.setZParameterThreshold(Integer.parseInt(zThresholdField.getText()));

            stateContext.setXParameter(xComboBox.getValue());
            stateContext.setYParameter(yComboBox.getValue());
            stateContext.setZParameter(zComboBox.getValue());

            // Update Maximum/Minimum Threshold selections
            stateContext.setXThresholdMaximum(xMaximum.isSelected());
            stateContext.setYThresholdMaximum(yMaximum.isSelected());
            stateContext.setZThresholdMaximum(zMaximum.isSelected());

            System.out.println("Updated Parameters:");
            System.out.println("X: " + xComboBox.getValue() + ", Threshold: " + xThresholdField.getText() + ", Maximum: " + xMaximum.isSelected());
            System.out.println("Y: " + yComboBox.getValue() + ", Threshold: " + yThresholdField.getText() + ", Maximum: " + yMaximum.isSelected());
            System.out.println("Z: " + zComboBox.getValue() + ", Threshold: " + zThresholdField.getText() + ", Maximum: " + zMaximum.isSelected());

            window.close();
        });

        // Add elements to layout
        layout.add(xLabel, 0, 0);
        layout.add(xComboBox, 1, 0);
        layout.add(xThresholdLabel, 0, 1);
        layout.add(xThresholdField, 1, 1);
        layout.add(xMaximum, 1, 2);
        layout.add(xMinimum, 1, 3);

        layout.add(yLabel, 0, 4);
        layout.add(yComboBox, 1, 4);
        layout.add(yThresholdLabel, 0, 5);
        layout.add(yThresholdField, 1, 5);
        layout.add(yMaximum, 1, 6);
        layout.add(yMinimum, 1, 7);

        layout.add(zLabel, 0, 8);
        layout.add(zComboBox, 1, 8);
        layout.add(zThresholdLabel, 0, 9);
        layout.add(zThresholdField, 1, 9);
        layout.add(zMaximum, 1, 10);
        layout.add(zMinimum, 1, 11);

        layout.add(applyButton, 1, 12);

        // Set Scene
        Scene scene = new Scene(layout, 350, 400);
        window.setScene(scene);
        window.showAndWait();
    }
}