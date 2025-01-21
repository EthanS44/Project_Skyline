package org.Skyline;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class ModelViewState extends Application implements State {
    // Camera values
    private final double rotationAmount = 5.0;
    private final double moveAmount = 20.0;
    private double cameraAngleX = 0;
    private double cameraAngleY = 0;

    // Group values
    private double groupAngleX = 0;
    private double groupAngleY = 0;
    private final double groupRotationAmount = 5.0;
    private final double groupMoveAmount = 10.0;

    private StateContext context;
    private Stage primaryStage;
    private Scene scene;
    private Group root;

    public ModelViewState(StateContext context) {
        this.context = context;
        primaryStage = new Stage();
    }

    @Override
    public void showUI() {
        System.out.println("Displaying Model View with interactive controls");

        // JavaFX must be launched properly
        //new Thread(() -> Application.launch(ModelViewState.class)).start();

        start(primaryStage);
    }

    @Override
    public void handleAction(String action) {
        if (action.equals("rotateModel")) {
            System.out.println("Rotating Model...");
        } else if (action.equals("panModel")) {
            System.out.println("Panning Model...");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setUp();
        showRenderer();
    }

    private void setUp() {
        Box road = new Box(1500, 50, 0);
        PhongMaterial material3 = new PhongMaterial();
        material3.setDiffuseColor(Color.GRAY);
        road.setMaterial(material3);

        road.setTranslateX(650);
        road.setTranslateY(400);

        PointLight light = new PointLight();
        light.setTranslateX(150);
        light.setTranslateY(400);
        light.setTranslateZ(-1000);

        PerspectiveCamera camera = new PerspectiveCamera();
        root = new Group(road, light);

        // Assign scene to the class-level variable
        scene = new Scene(root, 1000, 800, true);
        scene.setCamera(camera);

        //move camera
        //scene.setOnKeyPressed(event -> handleCameraMovement(event, camera));

        scene.setOnKeyPressed(event -> handleGroupMovement(event, root));
    }

    private void addBuildings(){
        Model model = context.getSelectedModel();
        String xParameter = context.getxParameter();
        String yParameter = context.getyParameter();
        String zParameter = context.getzParameter();

        int currentXPixel = 0;
        int currentZPixel = 0;

        for (Attributes attribute: model.getAttributesList()){
            int xSize = getAttributeFromString(xParameter, attribute);
            int ySize = getAttributeFromString(yParameter, attribute);
            int zSize = getAttributeFromString(zParameter, attribute);
            Box attributeBox = new Box(xSize, ySize, zSize);

            // add box to the root
        }
    }

    private void showRenderer() {
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Model Viewer");
        primaryStage.show();
    }

    private void handleGroupMovement(KeyEvent event, Group group) {
        switch (event.getCode()) {
            case W -> group.setTranslateZ(group.getTranslateZ() + moveAmount);
            case S -> group.setTranslateZ(group.getTranslateZ() - moveAmount);
            case A -> group.setTranslateX(group.getTranslateX() - moveAmount);
            case D -> group.setTranslateX(group.getTranslateX() + moveAmount);
            case Q -> group.setTranslateY(group.getTranslateY() - moveAmount);
            case E -> group.setTranslateY(group.getTranslateY() + moveAmount);
            case UP -> groupAngleX -= rotationAmount;
            case DOWN -> groupAngleX += rotationAmount;
            case LEFT -> groupAngleY -= rotationAmount;
            case RIGHT -> groupAngleY += rotationAmount;
        }

        group.getTransforms().clear();
        group.getTransforms().addAll(
                new Rotate(groupAngleX, Rotate.X_AXIS),
                new Rotate(groupAngleY, Rotate.Y_AXIS)
        );
    }

    private void handleCameraMovement(KeyEvent event, PerspectiveCamera camera) {
        switch (event.getCode()) {
            case W -> camera.translateZProperty().set(camera.getTranslateZ() + moveAmount);
            case S -> camera.translateZProperty().set(camera.getTranslateZ() - moveAmount);
            case A -> camera.translateXProperty().set(camera.getTranslateX() - moveAmount);
            case D -> camera.translateXProperty().set(camera.getTranslateX() + moveAmount);
            case Q -> camera.translateYProperty().set(camera.getTranslateY() - moveAmount);
            case E -> camera.translateYProperty().set(camera.getTranslateY() + moveAmount);
            case UP -> cameraAngleX -= rotationAmount;
            case DOWN -> cameraAngleX += rotationAmount;
            case LEFT -> cameraAngleY -= rotationAmount;
            case RIGHT -> cameraAngleY += rotationAmount;
        }

        camera.getTransforms().clear();
        camera.getTransforms().addAll(
                new Rotate(cameraAngleX, Rotate.X_AXIS),
                new Rotate(cameraAngleY, Rotate.Y_AXIS)
        );
    }

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
}

