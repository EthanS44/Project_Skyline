package org.Skyline;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasswordManagementTest {

    @Mock
    private DatabaseManager mockDatabaseManager;

    private PasswordManagement passwordManagement;

    @Before
    public void setUp() {
        passwordManagement = new PasswordManagement(mockDatabaseManager);
    }

    @Test
    public void testHashPassword_NotNullAndDifferentFromPlaintext() {
        String password = "securePassword";
        String hashed = passwordManagement.hashPassword(password);

        assertNotNull("Hashed password should not be null", hashed);
        assertNotEquals("Hashed password should not equal plain password", password, hashed);
    }

    @Test
    public void testRegisterUser_Successful() throws IOException {
        when(mockDatabaseManager.doesUserExist("testuser")).thenReturn(false);

        passwordManagement.registerUser("testuser", "password123");

        verify(mockDatabaseManager).saveUser(eq("testuser"), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUser_ThrowsExceptionWhenUserExists() throws IOException {
        when(mockDatabaseManager.doesUserExist("existinguser")).thenReturn(true);

        passwordManagement.registerUser("existinguser", "password123");
    }

    @Test
    public void testVerifyUser_ValidCredentials() throws IOException {
        when(mockDatabaseManager.verifyCredentials("validuser", "correctpass")).thenReturn(true);

        boolean result = passwordManagement.verifyUser("validuser", "correctpass");

        assertTrue("Valid credentials should return true", result);
    }

    @Test
    public void testVerifyUser_InvalidCredentials() throws IOException {
        when(mockDatabaseManager.verifyCredentials("validuser", "wrongpass")).thenReturn(false);

        boolean result = passwordManagement.verifyUser("validuser", "wrongpass");

        assertFalse("Invalid credentials should return false", result);
    }
}

