import org.Skyline.PasswordManagement;
import org.junit.*;
import java.io.IOException;
import static org.junit.Assert.assertTrue;

// Test for PasswordManagement
public class PasswordManagementTest {
    @Test
    public void testUserLogin() throws IOException {
        String username = "samwilson29";
        String password = "sleepingdrake";
        PasswordManagement.registerUser(username, password);

        assertTrue(PasswordManagement.verifyUser(username, password));
    }
}
