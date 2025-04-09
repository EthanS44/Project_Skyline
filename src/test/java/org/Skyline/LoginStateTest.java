package org.Skyline;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginStateTest {

    private StateContext mockContext;
    private LoginState loginState;

    @BeforeClass
    public static void initJavaFX() {
        // This initializes the JavaFX toolkit
        new javafx.embed.swing.JFXPanel();
    }

    @Before
    public void setUp() {
        mockContext = mock(StateContext.class);
        loginState = new LoginState(mockContext);
    }

    @Test
    public void testHandleLoginSuccess() throws Exception {
        // Mock PasswordManagement behavior
        mockStatic(PasswordManagement.class);
        when(PasswordManagement.verifyUser("testuser", "testpass")).thenReturn(true);

        // Set username/password manually
        loginState.setUsername("testuser");
        loginState.setPassword("testpass");

        loginState.handleAction("login");

        verify(mockContext).setUser("testuser");
        verify(mockContext).setState(any(MainMenuState.class));
    }

    @Test
    public void testHandleLoginFailure() throws Exception {
        when(PasswordManagement.verifyUser("baduser", "wrongpass")).thenReturn(false);

        loginState.setUsername("baduser");
        loginState.setPassword("wrongpass");

        loginState.handleAction("login");

        // No state transition should happen
        verify(mockContext, never()).setUser(anyString());
        verify(mockContext, never()).setState(any());

    }

    @Test
    public void testHandleRegisterSuccess() throws Exception {

        // Register will succeed
        doNothing().when(PasswordManagement.class);
        PasswordManagement.registerUser("newuser", "newpass");

        loginState.setUsername("newuser");
        loginState.setPassword("newpass");

        loginState.handleAction("register");

        verify(mockContext).setUser("newuser");
        verify(mockContext).setState(any(MainMenuState.class));
    }
}