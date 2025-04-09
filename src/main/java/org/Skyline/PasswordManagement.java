package org.Skyline;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class PasswordManagement {
    private DatabaseManager databaseManager = new DatabaseManager();

    public PasswordManagement() {
    }

    public PasswordManagement(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public void registerUser(String user, String password) throws IOException {
        if (databaseManager.doesUserExist(user)) {
            throw new IllegalArgumentException("Username already exists: " + user);
        }
        databaseManager.saveUser(user, hashPassword(password));
    }

    public boolean verifyUser(String user, String password) throws IOException {
        return databaseManager.verifyCredentials(user, password);
    }
}
