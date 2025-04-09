package org.Skyline;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewModelStateTest {

    @Mock
    private StateContext mockContext;

    @Mock
    private DatabaseManager mockDb;

    @Before
    public void setUp() throws Exception {
        new JFXPanel(); // JavaFX toolkit initialization
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleAction_Logout() throws Exception {
        lenient().when(mockContext.getCurrentUser()).thenReturn("testuser");

        Platform.runLater(() -> {
            NewModelState newModelState = new NewModelState(mockContext);
            newModelState.handleAction("Logout");
            verify(mockContext).setState(argThat(state -> state instanceof LoginState));
        });

        Thread.sleep(200);
    }

    @Test
    public void testHandleAction_MainMenu() throws Exception {
        lenient().when(mockContext.getCurrentUser()).thenReturn("testuser");

        Platform.runLater(() -> {
            NewModelState newModelState = new NewModelState(mockContext);
            newModelState.handleAction("Main Menu");
            verify(mockContext).setState(argThat(state -> state instanceof MainMenuState));
        });

        Thread.sleep(200);
    }
}