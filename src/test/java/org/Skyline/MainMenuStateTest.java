package org.Skyline;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MainMenuStateTest {

    @Mock
    private StateContext mockContext;

    @Mock
    private DatabaseManager mockDbManager;

    private MainMenuState mainMenuState;


    @Before
    public void setUp() {
        when(mockContext.getDatabaseManager()).thenReturn(mockDbManager);
        mainMenuState = new MainMenuState(mockContext);
        new javafx.embed.swing.JFXPanel();
    }

    @Test
    public void testHandleAction_GoToModelList() {
        mainMenuState.handleAction("goToModelList");
        verify(mockContext).setState(argThat(state -> state instanceof ModelListState));
    }


    @Test
    public void testHandleAction_Logout() {
        mainMenuState = spy(new MainMenuState(mockContext));
        doNothing().when(mockContext).setState(any());
        mainMenuState.handleAction("Logout");
        verify(mockContext).setState(argThat(state -> state instanceof LoginState));
    }

    @Test
    public void testHandleAction_MainMenu() {
        mainMenuState.handleAction("Main Menu");
        verify(mockContext).setState(argThat(state -> state instanceof MainMenuState));
    }
}