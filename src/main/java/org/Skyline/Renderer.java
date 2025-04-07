package org.Skyline;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Renderer extends Application {

private final double rotationAmount = 1;
private final double moveAmount = 200;

// Group values
private double groupAngleX = 0;
private double groupAngleY = 0;

// Define min and max dimensions for buildings
private static final double MIN_BUILDING_X = 500;  // Min width
private static final double MAX_BUILDING_X = 1800;  // Max width
private static final double MIN_BUILDING_Y = 600;  // Min height
private static final double MAX_BUILDING_Y = 7000; // Max height
private static final double MIN_BUILDING_Z = 500;  // Min depth
private static final double MAX_BUILDING_Z = 1800;  // Max depth

private double startX;
private double startY;
private final double rotationSpeed = 0.2;

private final int cameraDistance = 10000;

private final double roadWidth = 300;

private final int MULTIPLIER = 10;

private final int TREE_NUMBER = 4000;

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
        // size is equal to the position of the furthest building from the origin.
        int size = placeBuildings(Buildings);
        slabSetup(size);
        roadSetup(size);
        treeSetup(TREE_NUMBER, size);
        showRenderer();
    }

    private void showRenderer() {
        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Model Viewer");
        primaryStage.show();
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

        // Skybox setup
        Sphere skybox = new Sphere(50000);
        Image skyboxTexture = new Image("sky_texture.jpg");
        PhongMaterial skyMaterial = new PhongMaterial();
        skyMaterial.setDiffuseMap(skyboxTexture);
        skybox.setMaterial(skyMaterial);
        skybox.setCullFace(CullFace.FRONT); // Render the inside of the sphere

        root = new Group(intersection, skybox, grass, light, ambientLight, origin);


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

        scene.setOnMouseDragged(event -> handleDragGroupMovement(event, root));
    }


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

    private int placeBuildings(List<Box> buildings) {
        int currentXPixelNegative = 0;
        int currentZPixelNegative = 0;
        int currentZPixelPositive = 0;
        int currentXPixelPositive = (int) (600 + buildings.get(0).getWidth()); // Start at the intersection for the positive X-axis

        if (buildings.size() > 1) {
            currentXPixelNegative = (int) (600 + buildings.get(1).getWidth()); // Start at the intersection for the negative X-axis
        }if (buildings.size() > 2) {
            currentZPixelPositive = (int) (600 + buildings.get(2).getDepth()); // Start at the intersection for the positive Z-axis
        }
        if (buildings.size() > 3) {
            currentZPixelNegative = (int) (600 + buildings.get(3).getDepth()); // Start at the intersection for the negative Z-axis
        }
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
        int maxX = Math.max(currentXPixelNegative, currentXPixelPositive);
        int maxZ = Math.max(currentZPixelNegative, currentZPixelPositive);
        return Math.max(maxX, maxZ);
    }

    private void roadSetup(int roadLength){
        // Roads setup
        Box road = new Box(roadLength*2, 0, roadWidth);
        Image roadTexture = new Image("road_texture.jpg");
        PhongMaterial roadMaterial = new PhongMaterial();
        roadMaterial.setDiffuseMap(roadTexture);
        roadMaterial.setSpecularMap(roadTexture);
        road.getTransforms().add(new Scale(1, 1, 1));
        road.setMaterial(roadMaterial);
        //road.setScaleX(10);
        road.setScaleY(1);
        road.setScaleZ(1);

        // Roads setup
        Box road2 = new Box(roadWidth, 0, roadLength*2);
        Image road2Texture = new Image("road_texture2.jpg");
        PhongMaterial road2Material = new PhongMaterial();
        road2Material.setDiffuseMap(road2Texture);
        road2Material.setSpecularMap(road2Texture);
        road2.getTransforms().add(new Scale(1, 1, 1));
        road2.setMaterial(road2Material);
        road2.setScaleX(1);
        road2.setScaleY(1);
        //road2.setScaleZ(10);

        // Position road on top of the concrete pad
        road.setTranslateY(-3);
        road2.setTranslateY(-3);

        root.getChildren().addAll(road, road2);
    }

    private void slabSetup(int slabSize){
        Box concretePad = new Box(slabSize*2 + 200, 0, slabSize*2 + 200); // diagonal is same length as building range
        Image concreteTextureImage = new Image("concrete_texture.jpeg");
        PhongMaterial concreteMaterial = new PhongMaterial();
        concreteMaterial.setDiffuseMap(concreteTextureImage);
        concreteMaterial.setDiffuseColor(new Color(0.65, 0.65, 0.65, 1.0));
        concreteMaterial.setSpecularColor(Color.rgb(50, 50, 50));
        concretePad.setMaterial(concreteMaterial);
        concretePad.setTranslateY(-1.5); // Slightly lower than the road
        //concretePad.setTranslateX(road.getTranslateX()); // Align with the road
        //concretePad.setTranslateZ(road.getTranslateZ()); // Align with the road
        root.getChildren().add(concretePad);
    }

    private void treeSetup(int numberOfTrees, int cityBound){
        Group treeGroup = new Group();
        Random random = new Random();

        // Tree setup
        for (int i = 0; i < numberOfTrees; i++) {
            double x;
            double z;
            // keep generating x and z until a valid coordinate is found (50000 is skybox limit, 200 is buffer area)
            do {
                x = random.nextInt(50000);
                z = random.nextInt(50000);
            } while ((x < cityBound + 200) && (z < cityBound + 200));

            // distribute the trees evenly among the four quadrants
            if (i % 4 == 1){
                x = -x;
            } else if (i % 4 == 2){
                z = -z;
            } else if (i % 4 == 3){
                x = -x;
                z = -z;
            }

            // create tree and place at determined location
            Tree tree = new Tree(200 + random.nextInt(200), 60 + random.nextInt(60), 160 + random.nextInt(160));
            tree.setTranslateX(x);
            tree.setTranslateZ(z);

            treeGroup.getChildren().add(tree);

        }
        root.getChildren().add(treeGroup);
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
                if (currentY >= -35000){
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
            if (currentY >= -35000) {
                camera.setTranslateZ(currentZ - moveAmount);
                camera.setTranslateY(currentY - moveAmount);
            }
        }
    }

    private void handleDragGroupMovement(MouseEvent event, Group root) {
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
}

