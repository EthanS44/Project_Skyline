package org.Skyline;

import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginStateTest {

    private StateContext mockContext;
    private PasswordManagement mockPasswordManagement;
    private LoginState loginState;

    @BeforeClass
    public static void initJavaFX() {
        new JFXPanel(); // Initializes JavaFX environment
    }

    @Before
    public void setUp() {
        mockContext = mock(StateContext.class);
        mockPasswordManagement = mock(PasswordManagement.class);

        // Inject mock PasswordManagement
        loginState = new LoginState(mockContext) {
            {
                this.setPasswordManagement(mockPasswordManagement);
            }
        };
    }

    @Test
    public void testHandleLoginSuccess() throws Exception {
        when(mockPasswordManagement.verifyUser("testuser", "testpass")).thenReturn(true);

        loginState.setUsername("testuser");
        loginState.setPassword("testpass");

        loginState.handleAction("login");

        verify(mockContext).setUser("testuser");
        verify(mockContext).setState(any(MainMenuState.class));
    }

    @Test
    public void testHandleLoginFailure() throws Exception {
        when(mockPasswordManagement.verifyUser("baduser", "wrongpass")).thenReturn(false);

        loginState.setUsername("baduser");
        loginState.setPassword("wrongpass");

        loginState.handleAction("login");

        verify(mockContext, never()).setUser(anyString());
        verify(mockContext, never()).setState(any());
    }

    @Test
    public void testHandleRegisterSuccess() throws Exception {
        doNothing().when(mockPasswordManagement).registerUser("newuser", "newpass");

        loginState.setUsername("newuser");
        loginState.setPassword("newpass");

        loginState.handleAction("register");

        verify(mockPasswordManagement).registerUser("newuser", "newpass");
        verify(mockContext).setUser("newuser");
        verify(mockContext).setState(any(MainMenuState.class));
    }
}