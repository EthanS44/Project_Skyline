package org.Skyline;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Renderer extends Application {

private final double rotationAmount = 1;
private final double moveAmount = 120;

// camera angle
private double cameraAngleX = 0;
private double cameraAngleY = 0;
private double cameraAngleZ = 0;

// camera position
private double cameraPosX = 0;
private double cameraPosY = 0;
private double cameraPosZ = 0;

// Group values
private double groupAngleX = 0;
private double groupAngleY = 0;
private final double groupRotationAmount = 5.0;
private final double groupMoveAmount = 10.0;

private double startX;
private double startY;
private final double rotationSpeed = 0.2;

private final int cameraDistance = 10000;

private final double roadWidth = 300;

private final int MULTIPLIER = 10;

private Stage primaryStage;
private StateContext context;
private Scene scene;
private Group root;

    public Renderer(StateContext context){
        this.context = context;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setUp();
        List<Box> Buildings = createBuildings();
        placeBuildings(Buildings);
        showRenderer();
    }

    private void setUp() {

        // Intersection setup
        Box intersection = new Box(600, 0, 600);
        Image intersectionTexture = new Image("intersection_texture.jpg");
        PhongMaterial intersectionMaterial = new PhongMaterial();
        intersectionMaterial.setDiffuseMap(intersectionTexture);
        intersectionMaterial.setSpecularColor(Color.rgb(50,50,50));
        intersection.getTransforms().add(new Scale(1, 1, 1));
        intersection.setMaterial(intersectionMaterial);
        intersection.setTranslateY(-4.5);

        // Roads setup
        Box road = new Box(2200, 0, roadWidth);
        Image roadTexture = new Image("road_texture.jpg");
        PhongMaterial roadMaterial = new PhongMaterial();
        roadMaterial.setDiffuseMap(roadTexture);
        roadMaterial.setSpecularMap(roadTexture);
        road.getTransforms().add(new Scale(1, 1, 1));
        road.setMaterial(roadMaterial);
        road.setScaleX(10);
        road.setScaleY(1);
        road.setScaleZ(1);

        // Roads setup
        Box road2 = new Box(roadWidth, 0, 2200);
        Image road2Texture = new Image("road_texture2.jpg");
        PhongMaterial road2Material = new PhongMaterial();
        road2Material.setDiffuseMap(road2Texture);
        road2Material.setSpecularMap(road2Texture);
        road2.getTransforms().add(new Scale(1, 1, 1));
        road2.setMaterial(road2Material);
        road2.setScaleX(1);
        road2.setScaleY(1);
        road2.setScaleZ(10);

        // Position road on top of the concrete pad
        road.setTranslateY(-3);
        road2.setTranslateY(-3);

        Sphere origin = new Sphere(10);
        PhongMaterial originmaterial = new PhongMaterial();
        originmaterial.setDiffuseColor(Color.BLACK);
        originmaterial.setSpecularColor(Color.BLACK);

        // Grass setup
        Box grass = new Box(110000, 0, 110000);
        Image grassTextureImage = new Image("grass_texture.jpg");
        PhongMaterial grassMaterial = new PhongMaterial();
        grassMaterial.setDiffuseMap(grassTextureImage);
        grassMaterial.setSpecularColor(Color.DARKGREEN);
        grass.setMaterial(grassMaterial);

        // Concrete pad setup
        Box concretePad = new Box(23000, 0, 23000); // Same length as road, 3x wider, small height
        Image concreteTextureImage = new Image("concrete_texture.jpeg");
        PhongMaterial concreteMaterial = new PhongMaterial();
        concreteMaterial.setDiffuseMap(concreteTextureImage);
        concreteMaterial.setDiffuseColor(new Color(0.65, 0.65, 0.65, 1.0));
        concreteMaterial.setSpecularColor(Color.rgb(50, 50, 50));
        concretePad.setMaterial(concreteMaterial);
        concretePad.setTranslateY(-1.5); // Slightly lower than the road
        concretePad.setTranslateX(road.getTranslateX()); // Align with the road
        concretePad.setTranslateZ(road.getTranslateZ()); // Align with the road


        // Lights and camera
        PointLight light = new PointLight();
        light.setTranslateX(150);
        light.setTranslateY(-700);
        light.setTranslateZ(-1000);
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateY(-cameraDistance);
        camera.setTranslateZ(-cameraDistance + 500);
        camera.setTranslateX(-500);
        Rotate rotateX = new Rotate(-45, Rotate.X_AXIS);
        camera.getTransforms().addAll(rotateX);

        // Set camera angle to face origin
        //matrixRotateNode(camera, 0, 0, 0);

        // Skybox setup
        Sphere skybox = new Sphere(50000);
        Image skyboxTexture = new Image("sky_texture.jpg");
        PhongMaterial skyMaterial = new PhongMaterial();
        skyMaterial.setDiffuseMap(skyboxTexture);
        skybox.setMaterial(skyMaterial);
        skybox.setCullFace(CullFace.FRONT); // Render the inside of the sphere

        root = new Group(intersection, road, road2, skybox, grass, concretePad, light, ambientLight, origin);

        // Assign scene to the class-level variable
        scene = new Scene(root, 1000, 800, true);
        scene.setCamera(camera);

        //Detect key press and move accordingly
        scene.setOnKeyPressed(event -> handleGroupMovement(event, root, camera));

        scene.setOnScroll(event -> handleScrollGroupMovement(event, camera));

        scene.setOnMousePressed(event -> {
            startX = event.getSceneX();
            startY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - startX;
            double deltaY = event.getSceneY() - startY;

            // Adjust rotation based on mouse movement
            groupAngleY -= deltaX * rotationSpeed; // Left/Right movement rotates Y-axis
            groupAngleX += deltaY * rotationSpeed; // Up/Down movement now behaves naturally

            // Clamp X-axis rotation to avoid flipping
            groupAngleX = Math.max(-40, Math.min(45, groupAngleX));

            // Apply rotations
            root.getTransforms().clear();
            root.getTransforms().addAll(
                    new Rotate(groupAngleX, Rotate.X_AXIS),
                    new Rotate(groupAngleY, Rotate.Y_AXIS)
            );

            // Update start positions for smoother dragging
            startX = event.getSceneX();
            startY = event.getSceneY();
        });
    }

    // Define min and max dimensions for buildings
    private static final double MIN_BUILDING_X = 500;  // Min width
    private static final double MAX_BUILDING_X = 1800;  // Max width
    private static final double MIN_BUILDING_Y = 600;  // Min height
    private static final double MAX_BUILDING_Y = 7000; // Max height
    private static final double MIN_BUILDING_Z = 500;  // Min depth
    private static final double MAX_BUILDING_Z = 1800;  // Max depth


    private List<Box> createBuildings() {
        Model model = context.getSelectedModel();
        String xParameter = context.getxParameter();
        String yParameter = context.getyParameter();
        String zParameter = context.getzParameter();

        List<Box> buildings = new ArrayList<>();
        Random random = new Random();

        // Load the texture image
        Image buildingTextureImage = new Image("building_texture.jpg"); // Adjust path if necessary

        // Determine the min and max values for attributes
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;

        for (Attributes attribute : model.getAttributesList()) {
            double xValue = getAttributeFromString(xParameter, attribute);
            double yValue = getAttributeFromString(yParameter, attribute);
            double zValue = getAttributeFromString(zParameter, attribute);

            minX = Math.min(minX, xValue);
            maxX = Math.max(maxX, xValue);
            minY = Math.min(minY, yValue);
            maxY = Math.max(maxY, yValue);
            minZ = Math.min(minZ, zValue);
            maxZ = Math.max(maxZ, zValue);
        }

        double scaleX = (maxX - minX) > 0 ? (MAX_BUILDING_X - MIN_BUILDING_X) / (maxX - minX) : 1;
        double scaleY = (maxY - minY) > 0 ? (MAX_BUILDING_Y - MIN_BUILDING_Y) / (maxY - minY) : 1;
        double scaleZ = (maxZ - minZ) > 0 ? (MAX_BUILDING_Z - MIN_BUILDING_Z) / (maxZ - minZ) : 1;

        for (Attributes attribute : model.getAttributesList()) {
            PhongMaterial buildingMaterial = new PhongMaterial();
            double shade = 0.10 + (random.nextDouble() * (0.35));
            buildingMaterial.setDiffuseColor(new Color(shade, shade, shade, 1.0));
            buildingMaterial.setSpecularColor(new Color(shade, shade, shade, 1.0));

            // Apply the texture to the building
            buildingMaterial.setDiffuseMap(buildingTextureImage); // Set texture as diffuse map

            Box attributeBox = attributesToBuilding(attribute, xParameter, yParameter, zParameter);

            double normalizedWidth = MIN_BUILDING_X + ((getAttributeFromString(xParameter, attribute) - minX) * scaleX);
            double normalizedHeight = MIN_BUILDING_Y + ((getAttributeFromString(yParameter, attribute) - minY) * scaleY);
            double normalizedDepth = MIN_BUILDING_Z + ((getAttributeFromString(zParameter, attribute) - minZ) * scaleZ);

            attributeBox.setWidth(normalizedWidth);
            attributeBox.setHeight(normalizedHeight);
            attributeBox.setDepth(normalizedDepth);

            if (getAttributeFromString(xParameter, attribute) > context.getxParameterThreshold() ||
                    getAttributeFromString(yParameter, attribute) > context.getyParameterThreshold() ||
                    getAttributeFromString(zParameter, attribute) > context.getzParameterThreshold()) {
                buildingMaterial.setDiffuseColor(Color.DARKRED);
                buildingMaterial.setSpecularColor(Color.DARKRED);
            }

            attributeBox.setMaterial(buildingMaterial);

            // Tooltip setup as previously
            String tooltipText = attribute.getName() + "\n" +
                    "X: " + xParameter + " = " + getAttributeFromString(xParameter, attribute) + " --> Threshold = " + context.getxParameterThreshold() + "\n" +
                    "Y: " + yParameter + " = " + getAttributeFromString(yParameter, attribute) +" --> Threshold = " + context.getyParameterThreshold() + "\n" +
                    "Z: " + zParameter + " = " + getAttributeFromString(zParameter, attribute) + " --> Threshold = " + context.getzParameterThreshold();
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setShowDelay(Duration.millis(1));
            Tooltip.install(attributeBox, tooltip);

            buildings.add(attributeBox);
        }
        return buildings;
    }

    private void placeBuildings(List<Box> buildings) {
        int currentXPixelPositive = (int) (600 + buildings.get(0).getWidth()); // Start at the intersection for the positive X-axis
        int currentXPixelNegative = (int) (600 + buildings.get(1).getWidth()); // Start at the intersection for the negative X-axis
        int currentZPixelPositive = (int) (600 + buildings.get(2).getDepth()); // Start at the intersection for the positive Z-axis
        int currentZPixelNegative = (int) (600 + buildings.get(3).getDepth()); // Start at the intersection for the negative Z-axis
        int spacing = 800; // Space between buildings
        boolean sideOfRoad = false; // For alternating side of the road
        boolean positiveDirection = true; // For alternating between positive and negative directions
        int count = 0; // To count buildings for toggling sides every two buildings

        for (Box attributeBox : buildings) {
            if (count % 3 == 0) {
                sideOfRoad = !sideOfRoad;  // Toggle side of the road every two buildings
            }

            positiveDirection = !positiveDirection; // Switch between positive and negative directions

            if (currentXPixelPositive <= currentZPixelPositive) {
                // Place along the X-axis road
                attributeBox.setTranslateZ(sideOfRoad ? (roadWidth / 2 + 20 + attributeBox.getDepth() / 2)
                        : -(roadWidth / 2 + 20 + attributeBox.getDepth() / 2));

                if (positiveDirection) {
                    attributeBox.setTranslateX(currentXPixelPositive);  // Positive X direction
                    currentXPixelPositive += attributeBox.getWidth() + spacing; // Move forward on the X-axis
                } else {
                    attributeBox.setTranslateX(-currentXPixelNegative); // Negative X direction
                    currentXPixelNegative += attributeBox.getWidth() + spacing; // Move backward on the X-axis
                }
            } else {
                // Place along the Z-axis road
                attributeBox.setTranslateX(sideOfRoad ? (roadWidth / 2 + 20 + attributeBox.getWidth() / 2)
                        : -(roadWidth / 2 + 20 + attributeBox.getWidth() / 2));

                if (positiveDirection) {
                    attributeBox.setTranslateZ(currentZPixelPositive); // Positive Z direction
                    currentZPixelPositive += attributeBox.getDepth() + spacing; // Move forward on the Z-axis
                } else {
                    attributeBox.setTranslateZ(-currentZPixelNegative); // Negative Z direction
                    currentZPixelNegative += attributeBox.getDepth() + spacing; // Move backward on the Z-axis
                }
            }

            // Adjust height and add to the scene
            attributeBox.setTranslateY(-attributeBox.getHeight() / 2);
            root.getChildren().add(attributeBox);

            count++; // Increment counter for alternating the side of the road
        }
    }

    /*private void placeBuildings2(List<Box> buildings) {
        int currentXPixel = -5000;
        int spacing = 400;
        boolean sideOfRoad = false;

        for (Box attributeBox : buildings) {
            sideOfRoad = !sideOfRoad;

            attributeBox.setTranslateZ(sideOfRoad ? (roadWidth / 2 + 20 + (attributeBox.getDepth() / 2))
                    : -(roadWidth / 2 + 20 + (attributeBox.getDepth() / 2)));
            attributeBox.setTranslateY(-attributeBox.getHeight() / 2);

            currentXPixel += attributeBox.getWidth() + spacing;
            attributeBox.setTranslateX(currentXPixel);


            root.getChildren().add(attributeBox);
        }
    }*/

    private void showRenderer() {
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Model Viewer");
        primaryStage.show();
    }

    private void handleGroupMovement(KeyEvent event, Group group, PerspectiveCamera camera) {
        double currentZ = camera.getTranslateZ();
        double currentY = camera.getTranslateY();

        switch (event.getCode()) {
            case W -> {
                // move in if it is less than 0
                if (currentY <= 0){
                    camera.setTranslateZ(currentZ + moveAmount);
                    camera.setTranslateY(currentY + moveAmount);
                }
            }
            case S -> {
                // move back if it is under maximum
                if (currentY >= -45000){
                    camera.setTranslateZ(currentZ - moveAmount);
                    camera.setTranslateY(currentY - moveAmount);
                }
            }

            // Rotate downward
            case UP -> {
                if (groupAngleX >= -40){
                    groupAngleX -= rotationAmount;
                }
            }

            // Rotate upward
            case DOWN -> {
                if (groupAngleX <= 45){
                    groupAngleX += rotationAmount;
                }
            }

            // Rotate clockwise & counterclockwise
            case LEFT -> groupAngleY -= rotationAmount;
            case RIGHT -> groupAngleY += rotationAmount;
        }

        // Apply rotation changes
        group.getTransforms().clear();
        group.getTransforms().addAll(
                new Rotate(groupAngleX, Rotate.X_AXIS),
                new Rotate(groupAngleY, Rotate.Y_AXIS)
        );

    }

    private void handleScrollGroupMovement(ScrollEvent event, PerspectiveCamera camera) {
        double currentZ = camera.getTranslateZ();
        double currentY = camera.getTranslateY();

        if (event.getDeltaY() > 0) {
            // Scroll up = zoom in
            if (currentY <= 0) {
                camera.setTranslateZ(currentZ + moveAmount);
                camera.setTranslateY(currentY + moveAmount);
            }
        } else if (event.getDeltaY() < 0) {
            // Scroll down = zoom out
            if (currentY >= -45000) {
                camera.setTranslateZ(currentZ - moveAmount);
                camera.setTranslateY(currentY - moveAmount);
            }
        }
    }

    private void handleCameraMovement2(KeyEvent event, PerspectiveCamera camera) {
        // Initialize the camera variables based on the camera's current position
        cameraPosX = camera.getTranslateX();
        cameraPosY = camera.getTranslateY();
        cameraPosZ = camera.getTranslateZ();

        Rotate rotateX = new Rotate(0, cameraPosX, cameraPosY, cameraPosZ, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, cameraPosX, cameraPosY, cameraPosZ, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(0, cameraPosX, cameraPosY, cameraPosZ, Rotate.Z_AXIS);

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
            case Q:
                // Move down along the Y-axis
                if(cameraPosY + moveAmount > -500){
                    break;
                }
                cameraPosY += moveAmount;
                break;
            case E:
                // Move up along the Y-axis
                cameraPosY -= moveAmount;
                break;

            case UP:
                // look up
                break;
            case DOWN:
                // look down
                break;
            case LEFT:
                // Rotate left (along the Y-axis)
                cameraAngleY -= rotationAmount;
                rotateY.setAngle(cameraAngleY);
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
        //Rotate rotateX = new Rotate(cameraAngleX, Rotate.X_AXIS);  // Rotate along X-axis (up/down)
        //Rotate rotateY = new Rotate(cameraAngleY, Rotate.Y_AXIS);  // Rotate along Y-axis (left/right)
        //Rotate rotateZ = new Rotate();

        // Compound rotation (local space adjustment)
        double rollAngle = -Math.sin(Math.toRadians(cameraAngleY)) * cameraAngleX; // Adjust Z-axis based on yaw
        Rotate rotateRoll = new Rotate(rollAngle, Rotate.Z_AXIS); // Roll adjustment

        // Clear previous rotations and apply new ones
        //camera.getTransforms().clear();  // Optional: clear only if needed
        camera.getTransforms().addAll(rotateX, rotateY, rotateZ);
    }

    private void handleCameraMovement(KeyEvent event, PerspectiveCamera camera) {
        double moveAmount = 10;         // Movement speed
        double rotationAmount = 5;      // Rotation speed

        // Direction Vectors for movement (relative to camera yaw)
        double forwardX = Math.sin(Math.toRadians(cameraAngleY));
        double forwardZ = -Math.cos(Math.toRadians(cameraAngleY));
        double rightX = Math.cos(Math.toRadians(cameraAngleY));
        double rightZ = Math.sin(Math.toRadians(cameraAngleY));

        switch (event.getCode()) {
            case W: // Move forward
                cameraPosX += forwardX * moveAmount;
                cameraPosZ -= forwardZ * moveAmount;
                break;
            case S: // Move backward
                cameraPosX -= forwardX * moveAmount;
                cameraPosZ += forwardZ * moveAmount;
                break;
            case A: // Strafe left
                cameraPosX -= rightX * moveAmount;
                cameraPosZ += rightZ * moveAmount;
                break;
            case D: // Strafe right
                cameraPosX += rightX * moveAmount;
                cameraPosZ -= rightZ * moveAmount;
                break;
            case Q: // Move down
                cameraPosY += moveAmount;
                break;
            case E: // Move up
                cameraPosY -= moveAmount;
                break;
            case UP: // Look up (pitch)
                cameraAngleX -= rotationAmount;
                break;
            case DOWN: // Look down (pitch)
                cameraAngleX += rotationAmount;
                break;
            case LEFT: // Rotate left (yaw)
                cameraAngleY -= rotationAmount;
                break;
            case RIGHT: // Rotate right (yaw)
                cameraAngleY += rotationAmount;
                break;
            default:
                break;
        }

        // **Apply Translations Separately**
        camera.setTranslateX(cameraPosX);
        camera.setTranslateY(cameraPosY);
        camera.setTranslateZ(cameraPosZ);

        // **Apply Rotation Separately**
        camera.getTransforms().clear();

        Rotate rotateY = new Rotate(cameraAngleY, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.Y_AXIS); // Yaw
        Rotate rotateX = new Rotate(cameraAngleX, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.X_AXIS); // Pitch
        Rotate rotateZ = new Rotate(cameraAngleZ, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.Z_AXIS); // Roll

        // **Only Rotate Without Moving**
        camera.getTransforms().addAll(rotateY, rotateX, rotateZ);
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

        newBox.setWidth(getAttributeFromString(xParameter, attributes) * MULTIPLIER);
        newBox.setDepth(getAttributeFromString(zParameter, attributes) * MULTIPLIER);
        newBox.setHeight(getAttributeFromString(yParameter, attributes) * MULTIPLIER);

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

    private void matrixRotateNode(Node n, double Z, double X, double Y){
        double A11=Math.cos(Z)*Math.cos(Y);
        double A12=Math.cos(X)*Math.sin(Z)+Math.cos(Z)*Math.sin(X)*Math.sin(Y);
        double A13=Math.sin(Z)*Math.sin(X)-Math.cos(Z)*Math.cos(X)*Math.sin(Y);
        double A21=-Math.cos(Y)*Math.sin(Z);
        double A22=Math.cos(Z)*Math.cos(X)-Math.sin(Z)*Math.sin(X)*Math.sin(Y);
        double A23=Math.cos(Z)*Math.sin(X)+Math.cos(X)*Math.sin(Z)*Math.sin(Y);
        double A31=Math.sin(Y);
        double A32=-Math.cos(Y)*Math.sin(X);
        double A33=Math.cos(X)*Math.cos(Y);

        double d = Math.acos((A11+A22+A33-1d)/2d);
        if(d!=0d){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }

    public static double getYaw(Node n) {
        double[][] matrix = getRotationMatrix(n);
        return Math.toDegrees(Math.asin(matrix[2][0])); // Y = asin(A31)
    }

    // Extract Pitch (X) Angle
    public static double getPitch(Node n) {
        double[][] matrix = getRotationMatrix(n);
        if (Math.abs(matrix[2][0]) < 0.99999) {
            return Math.toDegrees(Math.atan2(-matrix[2][1], matrix[2][2])); // X = atan2(-A32, A33)
        } else {
            return 0; // Gimbal lock case, assume X = 0
        }
    }

    // Extract Roll (Z) Angle
    public static double getRoll(Node n) {
        double[][] matrix = getRotationMatrix(n);
        if (Math.abs(matrix[2][0]) < 0.99999) {
            return Math.toDegrees(Math.atan2(-matrix[1][0], matrix[0][0])); // Z = atan2(-A21, A11)
        } else {
            return Math.toDegrees(Math.atan2(matrix[0][1], matrix[1][1])); // Gimbal lock case
        }
    }

    // Helper function: Compute rotation matrix from node's rotation axis & angle
    private static double[][] getRotationMatrix(Node n) {
        Point3D axis = n.getRotationAxis();
        double angle = Math.toRadians(n.getRotate());

        if (axis == null || angle == 0) {
            return new double[][]{
                    {1, 0, 0},
                    {0, 1, 0},
                    {0, 0, 1}
            }; // Identity matrix (no rotation)
        }

        double ux = axis.getX(), uy = axis.getY(), uz = axis.getZ();
        double c = Math.cos(angle), s = Math.sin(angle), t = 1 - c;

        return new double[][]{
                {t * ux * ux + c,      t * ux * uy - s * uz,  t * ux * uz + s * uy}, // Row 1
                {t * uy * ux + s * uz, t * uy * uy + c,      t * uy * uz - s * ux}, // Row 2
                {t * uz * ux - s * uy, t * uz * uy + s * ux, t * uz * uz + c}       // Row 3
        };
    }

}

