package org.Skyline;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Renderer extends Application {
    @Override
    public void start(Stage primaryStage){
        // ArrayList<ModelAttributes> attributesList = getAttributes();

        /*Sphere box = new Sphere(300); // Create a box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.LIMEGREEN);
        box.setMaterial(material); // Apply material*/

        /*Box box2 = new Box(200, 200, 200);
        PhongMaterial material2 = new PhongMaterial();
        material2.setDiffuseColor(Color.BLUEVIOLET);
        box2.setMaterial(material2);*/

        /*box.setTranslateX(650);
        box.setTranslateY(400);

        box2.setTranslateX(150);
        box2.setTranslateY(400);*/

        PointLight light = new PointLight();
        light.setTranslateX(150);
        light.setTranslateY(400);
        light.setTranslateZ(-1000);

        PerspectiveCamera camera = new PerspectiveCamera();
        Group root = new Group(road, light);
        Scene scene = new Scene(root, 1000, 800, true); // Enable 3D
        scene.setCamera(camera);

        //move camera
        //scene.setOnKeyPressed(event -> handleCameraMovement(event, camera));

        //move group
        scene.setOnKeyPressed(event -> handleGroupMovement(event, root));


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleCameraMovement(KeyEvent event, PerspectiveCamera camera) {
        switch (event.getCode()) {
            case W:
                camera.translateZProperty().set(camera.getTranslateZ() + moveAmount);
                break;
            case S:
                camera.translateZProperty().set(camera.getTranslateZ() - moveAmount);
                break;
            case A:
                camera.translateXProperty().set(camera.getTranslateX() - moveAmount);
                break;
            case D:
                camera.translateXProperty().set(camera.getTranslateX() + moveAmount);
                break;
            case Q:
                camera.translateYProperty().set(camera.getTranslateY() - moveAmount);
                break;
            case E:
                camera.translateYProperty().set(camera.getTranslateY() + moveAmount);
                break;

            // Rotation
            case UP: // Pitch up
                cameraAngleX -= rotationAmount;
                break;
            case DOWN: // Pitch down
                cameraAngleX += rotationAmount;
                break;
            case LEFT: // Yaw left
                cameraAngleY -= rotationAmount;
                break;
            case RIGHT: // Yaw right
                cameraAngleY += rotationAmount;
                break;
        }
        // Update the camera's rotation
        camera.getTransforms().clear();
        camera.getTransforms().addAll(
                new Rotate(cameraAngleX, Rotate.X_AXIS),
                new Rotate(cameraAngleY, Rotate.Y_AXIS));
    }

    private void handleGroupMovement(KeyEvent event, Group group) {
        switch (event.getCode()) {
            // Translation
            case W: // Move forward
                group.setTranslateZ(group.getTranslateZ() + moveAmount);
                break;
            case S: // Move backward
                group.setTranslateZ(group.getTranslateZ() - moveAmount);
                break;
            case A: // Move left
                group.setTranslateX(group.getTranslateX() - moveAmount);
                break;
            case D: // Move right
                group.setTranslateX(group.getTranslateX() + moveAmount);
                break;
            case Q: // Move up
                group.setTranslateY(group.getTranslateY() - moveAmount);
                break;
            case E: // Move down
                group.setTranslateY(group.getTranslateY() + moveAmount);
                break;

            // Rotation
            case UP: // Pitch up
                groupAngleX -= rotationAmount;
                break;
            case DOWN: // Pitch down
                groupAngleX += rotationAmount;
                break;
            case LEFT: // Yaw left
                groupAngleY -= rotationAmount;
                break;
            case RIGHT: // Yaw right
                groupAngleY += rotationAmount;
                break;
        }

        group.getTransforms().clear(); // Clear previous transformations
        group.getTransforms().addAll(
                new Rotate(groupAngleX, Rotate.X_AXIS),
                new Rotate(groupAngleY, Rotate.Y_AXIS)
        );
    }

    /*
    private ArrayList<Attributes> getAttributes(long attributeID){
        return new ArrayList<Attributes>();
    } */

    private Box attributesToBuilding(Attributes attributes, String xParameter, String yParameter, String zParameter){
        Box newBox = new Box();

        newBox.setWidth(getAttributeFromString(xParameter, attributes));
        newBox.setDepth(getAttributeFromString(zParameter, attributes));
        newBox.setHeight(getAttributeFromString(yParameter, attributes));

        return newBox;
    }

    private int getAttributeFromString(String attribute, Attributes attributes){
        switch (attribute) {
            case "LINESOFCODE":
                return attributes.getLinesOfCode();
            case "LINESOFCODENOBLANKS":
                return attributes.getLinesOfCodeNoBlanks();
            case "NUMBEROFFIELDS":
                return attributes.getNumberOfFields();
            case "NUMBEROFMETHODS":
                return attributes.getNumberOfMethods();
            case "AVERAGELINESPERMETHOD":
                return (int) attributes.getAverageLinesPerMethod();
            case "MAXCYCLOMATICCOMPLEXITY":
                return attributes.getMaxCyclomaticComplexity();
            case "INHERITANCEDEPTH":
                return attributes.getInheritanceDepth();
            case "NUMBEROFASSOCIATIONS":
                return attributes.getNumberOfAssociations();
            case "NUMBEROFIMPORTS":
                return attributes.getNumberOfImports();
            default:
                return 1;
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
