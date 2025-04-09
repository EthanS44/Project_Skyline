
package org.Skyline;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelListStateTest {

    @Mock
    private StateContext mockContext;

    @Mock
    private DatabaseManager mockDbManager;

    private ObservableList<Model> mockModelList;

    @Before
    public void setUp() {
        new JFXPanel(); // Initializes JavaFX toolkit

        List<Model> models = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            models.add(new Model("Model" + i, "testuser", new ArrayList<>())) ;
        }
        mockModelList = FXCollections.observableArrayList(models);

        when(mockContext.getCurrentUser()).thenReturn("testuser");
        when(mockContext.getDatabaseManager()).thenReturn(mockDbManager);
        when(mockDbManager.getModelsByUser("testuser")).thenReturn(new ArrayList<>(mockModelList));
        when(mockContext.getModelList()).thenReturn(new ArrayList<>(mockModelList));
    }

    @Test
    public void testHandleAction_NewModel_UnderLimit() {
        try (MockedConstruction<NewModelState> ignored = mockConstruction(NewModelState.class)) {
            ModelListState state = new ModelListState(mockContext);
            state.handleAction("newModel");
            verify(mockContext).setState(any(NewModelState.class));
        }
    }

    @Test
    public void testHandleAction_SortModels() {
        ModelListState state = new ModelListState(mockContext);
        state.handleAction("sortModels");
    }

    @Test
    public void testHandleAction_MainMenu() {
        try (MockedConstruction<MainMenuState> ignored = mockConstruction(MainMenuState.class)) {
            ModelListState state = new ModelListState(mockContext);
            state.handleAction("Main Menu");
            verify(mockContext).setState(any(MainMenuState.class));
        }
    }

    @Test
    public void testHandleAction_Logout() {
        try (MockedConstruction<LoginState> ignored = mockConstruction(LoginState.class)) {
            ModelListState state = new ModelListState(mockContext);
            state.handleAction("Logout");
            verify(mockContext).setState(any(LoginState.class));
        }
    }
}
