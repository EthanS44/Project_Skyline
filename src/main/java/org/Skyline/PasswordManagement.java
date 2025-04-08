package org.Skyline;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class PasswordManagement {
    private static DatabaseManager databaseManager = new DatabaseManager();
    // Hashes a password and returns the hash
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }


    // Stores the given password into the users table in the database
    // Throws IllegalArgumentException if username already exists
    public static void registerUser(String user, String password) throws IOException {

        if (databaseManager.doesUserExist(user)){
            throw new IllegalArgumentException("Username already exists: " + user);
        }

        String hashedPassword = hashPassword(password);

        // store user in database
        databaseManager.saveUser(user, hashedPassword);

    }

    // returns true if password exists and matches, else returns false.
    public static boolean verifyUser(String user, String password) throws IOException {
        return databaseManager.verifyCredentials(user, password);
    }
}
