package org.Skyline;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasswordManagementTest {

    @Mock
    private DatabaseManager mockDatabaseManager;

    @Before
    public void setUp() {
        PasswordManagement passwordManagement = new PasswordManagement();
        passwordManagement.setDatabaseManager(mockDatabaseManager);
    }

    @Test
    public void testHashPassword_NotNullAndDifferentFromPlaintext() {
        String password = "securePassword";
        String hashed = PasswordManagement.hashPassword(password);
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
    }

    @Test
    public void testRegisterUser_Successful() throws IOException {
        when(mockDatabaseManager.doesUserExist("testuser")).thenReturn(false);
        doNothing().when(mockDatabaseManager).saveUser(eq("testuser"), anyString());

        PasswordManagement.registerUser("testuser", "password123");

        verify(mockDatabaseManager).saveUser(eq("testuser"), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_ThrowsExceptionWhenUserExists() throws IOException {
        when(mockDatabaseManager.doesUserExist("existinguser")).thenReturn(true);

        PasswordManagement.registerUser("existinguser", "password123");
    }

    @Test
    public void testVerifyUser_ValidCredentials() throws IOException {
        when(mockDatabaseManager.verifyCredentials("validuser", "correctpass")).thenReturn(true);

        boolean result = PasswordManagement.verifyUser("validuser", "correctpass");

        assertTrue(result);
    }

    @Test
    public void testVerifyUser_InvalidCredentials() throws IOException {
        when(mockDatabaseManager.verifyCredentials("validuser", "wrongpass")).thenReturn(false);

        boolean result = PasswordManagement.verifyUser("validuser", "wrongpass");

        assertFalse(result);
    }
}

