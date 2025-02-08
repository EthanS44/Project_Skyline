package org.Skyline;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://viaduct.proxy.rlwy.net:17205/railway";
    private static final String USER = "postgres";
    private static final String PASSWORD = "SqyAeJuIfQNzpFFxyxczlwTlrcYCwUEc";


    public void saveModel(Model model) {
        String insertModelSQL = "INSERT INTO Models (model_name, username) VALUES (?, ?) RETURNING model_id";
        String insertAttributeSQL = "INSERT INTO Attributes (model_id, linesOfCode, linesOfCodeNoBlanks, numberOfFields, numberOfMethods, " +
                "averageLinesPerMethod, maxCyclomaticComplexity, inheritanceDepth, numberOfAssociations, numberOfImports) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement modelStatement = connection.prepareStatement(insertModelSQL);
             PreparedStatement attributeStatement = connection.prepareStatement(insertAttributeSQL)) {

            // Insert model
            modelStatement.setString(1, model.getName());
            modelStatement.setString(2, model.getUser()); // Use the username directly as a String
            var resultSet = modelStatement.executeQuery();

            if (resultSet.next()) {
                long modelId = resultSet.getLong(1); // Retrieve the model_id generated by the database

                // Insert attributes
                List<Attributes> attributesList = model.getAttributesList();
                for (Attributes attr : attributesList) {
                    attributeStatement.setLong(1, modelId);
                    attributeStatement.setInt(2, attr.getLinesOfCode());
                    attributeStatement.setInt(3, attr.getLinesOfCodeNoBlanks());
                    attributeStatement.setInt(4, attr.getNumberOfFields());
                    attributeStatement.setInt(5, attr.getNumberOfMethods());
                    attributeStatement.setDouble(6, attr.getAverageLinesPerMethod());
                    attributeStatement.setInt(7, attr.getMaxCyclomaticComplexity());
                    attributeStatement.setInt(8, attr.getInheritanceDepth());
                    attributeStatement.setInt(9, attr.getNumberOfAssociations());
                    attributeStatement.setInt(10, attr.getNumberOfImports());
                    attributeStatement.executeUpdate();
                }
            }

            System.out.println("Model and attributes saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // returns models by username
    public ArrayList<Model> getModelsByUser(String username) {
        String selectModelsSQL = "SELECT model_id, model_name FROM Models WHERE username = ?";
        String selectAttributesSQL = "SELECT * FROM Attributes WHERE model_id = ?";

        List<Model> models = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement modelStatement = connection.prepareStatement(selectModelsSQL);
             PreparedStatement attributesStatement = connection.prepareStatement(selectAttributesSQL)) {

            // Fetch all models for the given username
            modelStatement.setString(1, username);
            ResultSet modelResult = modelStatement.executeQuery();

            while (modelResult.next()) {
                long modelId = modelResult.getLong("model_id");
                String modelName = modelResult.getString("model_name");

                // Fetch attributes for the current model
                List<Attributes> attributesList = new ArrayList<>();
                attributesStatement.setLong(1, modelId);
                ResultSet attributesResult = attributesStatement.executeQuery();

                while (attributesResult.next()) {
                    Attributes attr = new Attributes();
                    attr.setLinesOfCode(attributesResult.getInt("linesOfCode"));
                    attr.setLinesOfCodeNoBlanks(attributesResult.getInt("linesOfCodeNoBlanks"));
                    attr.setNumberOfFields(attributesResult.getInt("numberOfFields"));
                    attr.setNumberOfMethods(attributesResult.getInt("numberOfMethods"));
                    attr.setAverageLinesPerMethod(attributesResult.getDouble("averageLinesPerMethod"));
                    attr.setMaxCyclomaticComplexity(attributesResult.getInt("maxCyclomaticComplexity"));
                    attr.setInheritanceDepth(attributesResult.getInt("inheritanceDepth"));
                    attr.setNumberOfAssociations(attributesResult.getInt("numberOfAssociations"));
                    attr.setNumberOfImports(attributesResult.getInt("numberOfImports"));
                    attributesList.add(attr);
                }

                // Create and add the model object to the list
                models.add(new Model(modelName, username, attributesList));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ArrayList<Model>) models;
    }


    public static void saveUser(String username, String passwordHash) {
        String query = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly in production
        }
    }

    // checks if user with this username already exists
    public boolean doesUserExist(String username) {
        String query = "SELECT 1 FROM Users WHERE username = ? LIMIT 1"; // Efficient existence check

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If there is a result, user exists

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }

    // helper function for next method
    // Checks password against a stored hash
    // true = match, false = no match
    public static boolean checkPassword(String password, String storedHash){
        return BCrypt.checkpw(password, storedHash);
    }

    // Method to verify the username and password
    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT password FROM Users WHERE username = ? LIMIT 1";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Fetch the hashed password from the database
                String storedHash = resultSet.getString("password");

                // Use checkPassword to verify the entered password against the stored hash
                return checkPassword(password, storedHash);
            }
            return false; // If no matching username, return false

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }
}
