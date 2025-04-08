package org.Skyline;

import org.junit.*;
import org.mockito.*;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseManagerTest {

    @Mock private ConnectionProvider mockProvider;
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockStatement;
    @Mock private ResultSet mockResultSet;

    private DatabaseManager dbManager;

    private final String username = "testUser";
    private final String password = "password123";
    private String hashedPassword;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        when(mockProvider.getConnection()).thenReturn(mockConnection);
        dbManager = new DatabaseManager(mockProvider);
    }

    @Test
    public void testSaveUser() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        dbManager.saveUser(username, hashedPassword);

        verify(mockStatement).setString(1, username);
        verify(mockStatement).setString(2, hashedPassword);
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testDoesUserExistTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        assertTrue(dbManager.doesUserExist(username));
    }

    @Test
    public void testDoesUserExistFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertFalse(dbManager.doesUserExist(username));
    }

    @Test
    public void testVerifyCredentialsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashedPassword);

        assertTrue(dbManager.verifyCredentials(username, password));
    }

    @Test
    public void testVerifyCredentialsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashedPassword);

        assertFalse(dbManager.verifyCredentials(username, "wrongPass"));
    }

    @Test
    public void testVerifyCredentialsUserNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertFalse(dbManager.verifyCredentials(username, password));
    }

    @Test
    public void testUpdateUserPassword() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        dbManager.updateUserPassword(username, "newPassword");

        verify(mockStatement).setString(eq(1), anyString()); // hashed password
        verify(mockStatement).setString(2, username);
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testDeleteUserByUsername() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // simulate one model_id result

        dbManager.deleteUserByUsername(username);

        verify(mockStatement, atLeastOnce()).executeUpdate();
        verify(mockStatement, atLeastOnce()).setString(anyInt(), eq(username));
    }

    @Test
    public void testGetAllUsernames() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("username")).thenReturn(username);

        ArrayList<String> usernames = dbManager.getAllUsernames();
        assertEquals(1, usernames.size());
        assertEquals(username, usernames.get(0));
    }

    @Test
    public void testDeleteModelByNameAndUsername() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("model_id")).thenReturn(1L);

        dbManager.deleteModelByNameAndUsername("TestModel", username);

        verify(mockStatement, atLeastOnce()).executeUpdate();
        verify(mockStatement, atLeastOnce()).setString(anyInt(), anyString());
    }

    @Test
    public void testSaveModel() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L); // Mock model_id

        Attributes attr = new Attributes();
        attr.setName("TestClass");
        attr.setLinesOfCode(100);
        attr.setLinesOfCodeNoBlanks(90);
        attr.setNumberOfFields(4);
        attr.setNumberOfMethods(10);
        attr.setAverageLinesPerMethod(9.0);
        attr.setMaxCyclomaticComplexity(2);
        attr.setInheritanceDepth(1);
        attr.setNumberOfAssociations(3);
        attr.setNumberOfImports(2);

        List<Attributes> attrList = new ArrayList<>();
        attrList.add(attr);

        Model model = new Model("TestModel", "testUser", attrList);

        dbManager.saveModel(model);

        verify(mockStatement, atLeastOnce()).executeUpdate();
        verify(mockStatement, atLeastOnce()).setString(anyInt(), anyString());
    }
    @Test
    public void testGetModelsByUser() throws Exception {
        // Create separate mocks
        PreparedStatement modelStmt = mock(PreparedStatement.class);
        PreparedStatement attrStmt = mock(PreparedStatement.class);
        ResultSet modelRs = mock(ResultSet.class);
        ResultSet attrRs = mock(ResultSet.class);

        // Configure connection to return the correct statement based on SQL
        when(mockConnection.prepareStatement("SELECT model_id, model_name FROM Models WHERE username = ?"))
                .thenReturn(modelStmt);
        when(mockConnection.prepareStatement("SELECT * FROM Attributes WHERE model_id = ?"))
                .thenReturn(attrStmt);

        // Configure model statement
        when(modelStmt.executeQuery()).thenReturn(modelRs);
        when(modelRs.next()).thenReturn(true, false); // 1 model
        when(modelRs.getLong("model_id")).thenReturn(1L);
        when(modelRs.getString("model_name")).thenReturn("TestModel");

        // Configure attribute statement
        when(attrStmt.executeQuery()).thenReturn(attrRs);
        when(attrRs.next()).thenReturn(true, false); // 1 attribute
        when(attrRs.getString("attribute_name")).thenReturn("TestClass");

        // Mocking all other int/double attribute columns to return dummy values
        when(attrRs.getInt(anyString())).thenReturn(1);
        when(attrRs.getDouble("averageLinesPerMethod")).thenReturn(2.5);

        // Call the method
        ArrayList<Model> models = dbManager.getModelsByUser(username);

        // Assertions
        assertEquals(1, models.size());
        Model model = models.get(0);
        assertEquals("TestModel", model.getName());
        assertEquals(username, model.getUser());
        assertEquals(1, model.getAttributesList().size());

        Attributes attr = model.getAttributesList().get(0);
        assertEquals("TestClass", attr.getName());
        assertEquals(1, attr.getLinesOfCode());
        assertEquals(2.5, attr.getAverageLinesPerMethod(), 0.01);
    }
}