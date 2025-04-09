package org.Skyline;

import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StateContextTest {

    private Stage mockStage;
    private StateContext context;

    @Before
    public void setUp() {
        mockStage = mock(Stage.class);
        context = new StateContext(mockStage);
    }

    @Test
    public void testSetAndGetUser() {
        context.setUser("testUser");
        assertEquals("testUser", context.getCurrentUser());
    }

    @Test
    public void testSetAndGetSelectedModel() {
        Model model = new Model("TestModel", "testUser", new ArrayList<>());
        context.setSelectedModel(model);
        assertEquals(model, context.getSelectedModel());
    }

    @Test
    public void testSetAndGetModelList() {
        ArrayList<Model> modelList = new ArrayList<>();
        context.setModelList(modelList);
        assertEquals(modelList, context.getModelList());
    }

    @Test
    public void testSetAndGetThresholds() {
        context.setXParameterThreshold(123);
        context.setYParameterThreshold(456);
        context.setZParameterThreshold(789);

        assertEquals(123, context.getxParameterThreshold());
        assertEquals(456, context.getyParameterThreshold());
        assertEquals(789, context.getzParameterThreshold());
    }

    @Test
    public void testSetAndGetThresholdMaximums() {
        context.setXThresholdMaximum(false);
        context.setYThresholdMaximum(false);
        context.setZThresholdMaximum(true);

        assertFalse(context.isXThresholdMaximum());
        assertFalse(context.isYThresholdMaximum());
        assertTrue(context.isZThresholdMaximum());
    }

    @Test
    public void testSetAndGetParameters() {
        context.setXParameter("FIELDS");
        context.setYParameter("LINES");
        context.setZParameter("METHODS");

        assertEquals("FIELDS", context.getxParameter());
        assertEquals("LINES", context.getyParameter());
        assertEquals("METHODS", context.getzParameter());
    }

    @Test
    public void testSetStateDelegatesToState() {
        State mockState = mock(State.class);
        context.setState(mockState);
        verify(mockState).showUI();
    }

    @Test
    public void testExecuteActionDelegatesToCurrentState() {
        State mockState = mock(State.class);
        context.setState(mockState);
        context.executeAction("Logout");
        verify(mockState).handleAction("Logout");
    }

    @Test
    public void testPrimaryStageAccess() {
        assertEquals(mockStage, context.getPrimaryStage());
        Stage anotherStage = mock(Stage.class);
        context.setPrimaryStage(anotherStage);
        assertEquals(anotherStage, context.getPrimaryStage());
    }

    @Test
    public void testGetDatabaseManager_NotNull() {
        assertNotNull(context.getDatabaseManager());
    }
}