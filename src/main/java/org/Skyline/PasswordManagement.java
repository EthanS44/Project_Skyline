package org.Skyline;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PasswordManagement {
    private static final String PASSWORD_FILE = "src/main/resources/password.properties";
    // Hashes a password and returns the hash
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Checks password against a stored hash
    // true = match, false = no match
    public static boolean checkPassword(String password, String storedHash){
        return BCrypt.checkpw(password, storedHash);
    }

    // Stores the given password into the password.properties file.
    // Throws IllegalArgumentException if username already exists
    public static void registerUser(String user, String password) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(PASSWORD_FILE)) {
            properties.load(fis);
        }
        if (properties.containsKey(user)){
            throw new IllegalArgumentException("Username already exists: " + user);
        }

        String hashedPassword = hashPassword(password);

        properties.setProperty(user, hashedPassword);

        try (FileOutputStream fos = new FileOutputStream(PASSWORD_FILE)) {
            properties.store(fos, "User Credentials");
        }
    }

    // returns true if password exists and matches, else returns false.
    public static boolean verifyUser(String user, String password) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(PASSWORD_FILE)) {
            properties.load(fis);
        }

        String storedHash = properties.getProperty(user);

        return storedHash != null && checkPassword(password, storedHash);
    }
}
