package org.Skyline;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.Random;

public class ModelViewState extends Application implements State {
    // Camera values
    private final double rotationAmount = 5.0;
    private final double moveAmount = 60.0;
    private double cameraAngleX = 0;
    private double cameraAngleY = 0;
    private double cameraPosX = 0;
    private double cameraPosY = 0;
    private double cameraPosZ = 0;

    // Group values
    private double groupAngleX = 0;
    private double groupAngleY = 0;
    private final double groupRotationAmount = 5.0;
    private final double groupMoveAmount = 10.0;

    private final double roadWidth = 600;

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
        addBuildings();
        showRenderer();
    }

    private void setUp() {
        Box road = new Box(1500, 0, roadWidth);
        Image roadTexture = new Image("road_texture.jpg");
        PhongMaterial roadMaterial = new PhongMaterial();
        roadMaterial.setDiffuseMap(roadTexture);
        roadMaterial.setSpecularMap(roadTexture);
        road.getTransforms().add(new Scale(1, 1, 1));
        road.setMaterial(roadMaterial);
        road.setScaleX(10);
        road.setScaleY(1);
        road.setScaleZ(1);

        Box grass = new Box(10000, 0, 10000);
        //Image grassTexture = new Image("grass_texture.jpg");
        PhongMaterial grassMaterial = new PhongMaterial();
        grassMaterial.setDiffuseColor(Color.DARKGREEN);
        grassMaterial.setSpecularColor(Color.DARKGREEN);
        grass.setMaterial(grassMaterial);

        road.setTranslateX(0);
        road.setTranslateY(-1);

        PointLight light = new PointLight();
        light.setTranslateX(150);
        light.setTranslateY(-700);
        light.setTranslateZ(-1000);
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateY(-500);
        camera.setTranslateX(-450);
        camera.setTranslateZ(1150);

        Sphere skybox = new Sphere(10000);
        Image skyboxTexture = new Image("sky_texture.jpg");
        PhongMaterial skyMaterial = new PhongMaterial();
        skyMaterial.setDiffuseMap(skyboxTexture);
        skybox.setMaterial(skyMaterial);
        // Invert sphere so can be seen from inside
        skybox.setCullFace(CullFace.FRONT);  // Render the inside of the sphere

        root = new Group(road, skybox, grass, light, ambientLight);

        // Assign scene to the class-level variable
        scene = new Scene(root, 1000, 800, true);
        scene.setCamera(camera);

        //move camera
        scene.setOnKeyPressed(event -> handleCameraMovement(event, camera));

        //scene.setOnKeyPressed(event -> handleGroupMovement(event, root, camera));
    }

    private void addBuildings(){
        Model model = context.getSelectedModel();
        String xParameter = context.getxParameter();
        String yParameter = context.getyParameter();
        String zParameter = context.getzParameter();

        PhongMaterial buildingMaterial;
        Random random = new Random();

        int currentXPixel = 0;
        int currentZPixel = 0;
        int previousBuildingWidth = 0;

        for (Attributes attribute: model.getAttributesList()){

            // set random greyscale shade for building
            buildingMaterial = new PhongMaterial();
            double minShade = 0.0;   // Black
            double maxShade = 0.30;  // Light gray (where 1.0 would be white)

            double shade = minShade + (random.nextDouble() * (maxShade - minShade));
            /*
            uncomment this for random color instead
            double red = random.nextDouble();   // Random value between 0.0 and 1.0
            double green = random.nextDouble(); // Random value between 0.0 and 1.0
            double blue = random.nextDouble();  // Random value between 0.0 and 1.0
            */
            buildingMaterial.setDiffuseColor(new Color(shade, shade, shade, 1.0));
            Box attributeBox = attributesToBuilding(attribute, xParameter, yParameter, zParameter);
            attributeBox.setMaterial(buildingMaterial);
            root.getChildren().add(attributeBox);


            attributeBox.setTranslateZ(roadWidth/2 + 20 + (attributeBox.getDepth()/2));
            attributeBox.setTranslateY(0 - (attributeBox.getHeight()/2));

            currentXPixel += attributeBox.getWidth() + previousBuildingWidth;
            attributeBox.setTranslateX(currentXPixel);
            previousBuildingWidth = (int) attributeBox.getWidth();
        }
    }

    private void showRenderer() {
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Model Viewer");
        primaryStage.show();
    }

    private void handleGroupMovement(KeyEvent event, Group group, PerspectiveCamera camera) {
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
        // Initialize the camera variables based on the camera's current position
        cameraPosX = camera.getTranslateX();
        cameraPosY = camera.getTranslateY();
        cameraPosZ = camera.getTranslateZ();

        // Calculate direction vectors based on camera's rotation (yaw)
        double forwardX = Math.sin(Math.toRadians(cameraAngleY));
        double forwardZ = -Math.cos(Math.toRadians(cameraAngleY));
        double rightX = Math.cos(Math.toRadians(cameraAngleY));
        double rightZ = Math.sin(Math.toRadians(cameraAngleY));

        // Use switch statement to handle key events
        switch (event.getCode()) {
            case W:
                // Move forward (relative to camera facing direction)
                cameraPosX += forwardX * moveAmount;
                cameraPosZ -= forwardZ * moveAmount;
                break;
            case S:
                // Move backward (relative to camera facing direction)
                cameraPosX -= forwardX * moveAmount;
                cameraPosZ += forwardZ * moveAmount;
                break;
            case A:
                // Strafe left (relative to camera facing direction)
                cameraPosX -= rightX * moveAmount;
                cameraPosZ += rightZ * moveAmount;
                break;
            case D:
                // Strafe right (relative to camera facing direction)
                cameraPosX += rightX * moveAmount;
                cameraPosZ -= rightZ * moveAmount;
                break;
            case UP:
                // Move up along the Y-axis
                cameraPosY -= moveAmount;
                break;
            case DOWN:
                // Move down along the Y-axis
                cameraPosY += moveAmount;
                break;
            case LEFT:
                // Rotate left (along the Y-axis)
                cameraAngleY -= rotationAmount;
                break;
            case RIGHT:
                // Rotate right (along the Y-axis)
                cameraAngleY += rotationAmount;
                break;
            default:
                break;
        }

        // Apply camera position transformations
        camera.setTranslateX(cameraPosX);
        camera.setTranslateY(cameraPosY);
        camera.setTranslateZ(cameraPosZ);

        // Create new rotation transforms based on the accumulated rotation angles
        Rotate rotateX = new Rotate(cameraAngleX, Rotate.X_AXIS);  // Rotate along X-axis (up/down)
        Rotate rotateY = new Rotate(cameraAngleY, Rotate.Y_AXIS);  // Rotate along Y-axis (left/right)

        // Compound rotation (local space adjustment)
        double rollAngle = -Math.sin(Math.toRadians(cameraAngleY)) * cameraAngleX; // Adjust Z-axis based on yaw
        Rotate rotateRoll = new Rotate(rollAngle, Rotate.Z_AXIS); // Roll adjustment


        // Clear previous rotations and apply new ones
        camera.getTransforms().clear();  // Optional: clear only if needed
        camera.getTransforms().addAll(rotateX, rotateY);
    }

    private void updateCameraRotation(PerspectiveCamera camera) {
        camera.getTransforms().clear();
        camera.getTransforms().addAll(
                new Rotate(cameraAngleX, Rotate.X_AXIS),
                new Rotate(cameraAngleY, Rotate.Y_AXIS)
        );
    }

    private Box attributesToBuilding(Attributes attributes, String xParameter, String yParameter, String zParameter){
        Box newBox = new Box();

        newBox.setWidth(getAttributeFromString(xParameter, attributes) * 10);
        newBox.setDepth(getAttributeFromString(zParameter, attributes) * 10);
        newBox.setHeight(getAttributeFromString(yParameter, attributes) * 10);

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

