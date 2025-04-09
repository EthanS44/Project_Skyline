package org.Skyline;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.*;

public class RendererTest {

    private Renderer renderer;
    private StateContext mockContext;

    @Before
    public void setUp() throws Exception {
        // Initialize JavaFX environment
        new JFXPanel();

        // Wait for JavaFX Application Thread to be ready
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mockContext = new StateContext(new Stage());
            List<Attributes> attributesList = new ArrayList<>();
            attributesList.add(new Attributes("A", 100, 90, 10, 3, 30, 1, 0, 2));
            attributesList.add(new Attributes("B", 300, 200, 50, 5, 15, 2, 1, 4));
            attributesList.add(new Attributes("C", 10, 8, 1, 1, 10, 0, 0, 1));
            Model testModel = new Model("TestModel", "testuser", attributesList);
            mockContext.setSelectedModel(testModel);
            mockContext.setXParameter("NUMBER OF FIELDS");
            mockContext.setYParameter("LINES OF CODE");
            mockContext.setZParameter("NUMBER OF METHODS");
            renderer = new Renderer(mockContext);
            latch.countDown();
        });
        latch.await(); // Wait for JavaFX initialization
    }

    @Test
    public void testGetAttributeFromString() {
        Attributes attributes = new Attributes();
        attributes.setLinesOfCode(100);
        attributes.setNumberOfFields(5);
        attributes.setNumberOfMethods(10);

        assertEquals(100, renderer.getAttributeFromString("LINES OF CODE", attributes));
        assertEquals(5, renderer.getAttributeFromString("NUMBER OF FIELDS", attributes));
        assertEquals(10, renderer.getAttributeFromString("NUMBER OF METHODS", attributes));
    }

    @Test
    public void testAttributesToBuildingDimensions() {
        Attributes attributes = new Attributes();
        attributes.setNumberOfFields(3);
        attributes.setLinesOfCode(50);
        attributes.setNumberOfMethods(5);

        Box box = renderer.attributesToBuilding(attributes, "NUMBER OF FIELDS", "LINES OF CODE", "NUMBER OF METHODS");
        assertEquals(3 * 10, (int) box.getWidth());
        assertEquals(50 * 10, (int) box.getHeight());
        assertEquals(5 * 10, (int) box.getDepth());
    }

    @Test
    public void testBuildingDimensionsAreWithinExpectedRange() {
        List<Box> buildings = renderer.rendererTestExposeCreateBuildings(renderer);
        for (Box box : buildings) {
            assertTrue(box.getWidth() >= 500 && box.getWidth() <= 1800);
            assertTrue(box.getHeight() >= 600 && box.getHeight() <= 7000);
            assertTrue(box.getDepth() >= 500 && box.getDepth() <= 1800);
        }
    }

    @Test
    public void testThresholdFlaggingTurnsBuildingsRed() {
        mockContext.setXParameterThreshold(5);
        mockContext.setYParameterThreshold(5);
        mockContext.setZParameterThreshold(5);
        mockContext.setXThresholdMaximum(true);
        mockContext.setYThresholdMaximum(true);
        mockContext.setZThresholdMaximum(true);

        List<Box> buildings = renderer.rendererTestExposeCreateBuildings(renderer);
        for (Box box : buildings) {
            PhongMaterial material = (PhongMaterial) box.getMaterial();
            assertEquals("Red buildings expected", Color.DARKRED, material.getDiffuseColor());
        }
    }
}