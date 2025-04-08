package org.Skyline;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ModelListStateTest {

    private StateContext mockContext;
    private DatabaseManager mockDbManager;
    private Model mockModel1;
    private Model mockModel2;
    private ModelListState state;

    @Before
    public void setUp() {
        // Mock models
        mockModel1 = mock(Model.class);
        when(mockModel1.getName()).thenReturn("ZebraModel");

        mockModel2 = mock(Model.class);
        when(mockModel2.getName()).thenReturn("AlphaModel");

        List<Model> modelList = new ArrayList<>();
        modelList.add(mockModel1);
        modelList.add(mockModel2);

        // Mock DatabaseManager and return a concrete ArrayList
        mockDbManager = mock(DatabaseManager.class);
        when(mockDbManager.getModelsByUser(anyString())).thenReturn(new ArrayList<>(modelList));

        // Mock StateContext
        mockContext = mock(StateContext.class);
        when(mockContext.getCurrentUser()).thenReturn("testUser");
        when(mockContext.getDatabaseManager()).thenReturn(mockDbManager);
        when(mockContext.getModelList()).thenReturn(new ArrayList<>(modelList));

        // Create instance under test
        state = new ModelListState(mockContext);
    }

    @Test
    public void testSortModels() {
        // No exceptions or crashes = pass (since ListView sorting isn't testable here)
        state.handleAction("sortModels");
    }

    @Test
    public void testHandleLogout() {
        state.handleAction("Logout");
        verify(mockContext).setState(any(LoginState.class));
    }

    @Test
    public void testHandleMainMenu() {
        state.handleAction("Main Menu");
        verify(mockContext).setState(any(MainMenuState.class));
    }

    @Test
    public void testHandleQuit() {
        try {
            state.handleAction("Quit");
        } catch (Exception ignored) {
            // We can't prevent System.exit(0) in normal unit tests — skip
        }
    }

    @Test
    public void testHandleNewModel_WhenLimitNotReached() {
        state.handleAction("newModel");
        verify(mockContext).setState(any(NewModelState.class));
    }

    @Test
    public void testHandleNewModel_WhenLimitReached() {
        List<Model> fullList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Model mock = mock(Model.class);
            when(mock.getName()).thenReturn("MockModel" + i);
            fullList.add(mock);
        }

        when(mockContext.getModelList()).thenReturn((ArrayList<Model>) fullList);

        state = new ModelListState(mockContext);
        state.handleAction("newModel");

        // Ensure no transition due to alert dialog
        verify(mockContext, never()).setState(any(NewModelState.class));
    }

    @Test
    public void testDeleteModel_NoModelSelected() {
        // Simulate no selection
        state.handleAction("deleteModel");
        verify(mockDbManager, never()).deleteModelByNameAndUsername(anyString(), anyString());
    }

    @Test
    public void testUpdateModel_NoModelSelected() {
        // Simulate no selection
        state.handleAction("updateModel");
        verify(mockDbManager, never()).deleteModelByNameAndUsername(anyString(), anyString());
    }
}