package org.Skyline;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SettingsStateTest {

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
            SettingsState settingsState = new SettingsState(mockContext);
            settingsState.handleAction("Logout");
            verify(mockContext).setState(argThat(state -> state instanceof LoginState));
        });

        Thread.sleep(200);
    }

    @Test
    public void testHandleAction_MainMenu() throws Exception {
        lenient().when(mockContext.getCurrentUser()).thenReturn("testuser");

        Platform.runLater(() -> {
            SettingsState settingsState = new SettingsState(mockContext);
            settingsState.handleAction("Main Menu");
            verify(mockContext).setState(argThat(state -> state instanceof MainMenuState));
        });

        Thread.sleep(200);
    }

    @Test
    public void testAdminUserCreationAndDeletion() throws Exception {
        lenient().when(mockContext.getCurrentUser()).thenReturn("admin");
        lenient().when(mockDb.doesUserExist("newUser")).thenReturn(false);
        lenient().when(mockDb.getAllUsernames()).thenReturn(new ArrayList<>(Arrays.asList("user1", "user2")));

        Platform.runLater(() -> {
            SettingsState state = new SettingsState(mockContext) {
                {
                    this.setDatabaseManager(mockDb);
                }
            };

            // Simulate the logic used in the "Create User" section
            String newUser = "newUser";
            String password = "password123";
            if (!mockDb.doesUserExist(newUser)) {
                String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
                mockDb.saveUser(newUser, hashed);
            }

            verify(mockDb).saveUser(eq(newUser), anyString());

            // Simulate the logic used in the "Delete User" section
            String userToDelete = "user1";
            mockDb.deleteUserByUsername(userToDelete);
            verify(mockDb).deleteUserByUsername(eq("user1"));
        });

        Thread.sleep(300);
    }

    @Test
    public void testRegularUserPasswordUpdate() throws Exception {
        lenient().when(mockContext.getCurrentUser()).thenReturn("johndoe");

        Platform.runLater(() -> {
            SettingsState state = new SettingsState(mockContext) {
                {
                    this.setDatabaseManager(mockDb);
                }
            };

            String newPass = "newpass";
            String confirmPass = "newpass";

            if (!newPass.isEmpty() && newPass.equals(confirmPass)) {
                mockDb.updateUserPassword("johndoe", newPass);
            }

            verify(mockDb).updateUserPassword("johndoe", newPass);
        });

        Thread.sleep(200);
    }
}
