package org.Skyline;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Renderer extends Application {
    @Override
    public void start(Stage primaryStage){
        // ArrayList<ModelAttributes> attributesList = getAttributes();

        Sphere box = new Sphere(300); // Create a box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.LIMEGREEN);
        box.setMaterial(material); // Apply material

        box.setTranslateX(500);
        box.setTranslateY(400);

        PointLight light = new PointLight();
        light.setTranslateX(250);
        light.setTranslateY(400);
        light.setTranslateZ(-1000);

        Group root = new Group(box, light);
        Scene scene = new Scene(root, 1000, 800, true); // Enable 3D
        scene.setCamera(new PerspectiveCamera());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ArrayList<ModelAttributes> getAttributes(){
        return new ArrayList<ModelAttributes>();
    }

    private Box attributesToBuilding(ModelAttributes attributes){
        Box newBox = new Box();
        return newBox;
    }

    public static void main(String[] args){
        launch(args);
    }
}
