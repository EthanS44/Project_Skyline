package org.Skyline;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final ConnectionProvider connectionProvider;

    public DatabaseManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public DatabaseManager() {
        this(new HikariConnectionProvider(
                "jdbc:postgresql://viaduct.proxy.rlwy.net:17205/railway",
                "postgres",
                "SqyAeJuIfQNzpFFxyxczlwTlrcYCwUEc"
        ));
    }

    public Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }

    // All methods use getConnection() now — unchanged logic inside

    public void saveModel(Model model) {
        String insertModelSQL = "INSERT INTO Models (model_name, username) VALUES (?, ?) RETURNING model_id";
        String insertAttributeSQL = "INSERT INTO Attributes (model_id, linesOfCode, linesOfCodeNoBlanks, numberOfFields, numberOfMethods, " +
                "averageLinesPerMethod, maxCyclomaticComplexity, inheritanceDepth, numberOfAssociations, numberOfImports, attribute_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement modelStatement = connection.prepareStatement(insertModelSQL);
             PreparedStatement attributeStatement = connection.prepareStatement(insertAttributeSQL)) {

            modelStatement.setString(1, model.getName());
            modelStatement.setString(2, model.getUser());
            ResultSet resultSet = modelStatement.executeQuery();

            if (resultSet.next()) {
                long modelId = resultSet.getLong(1);
                for (Attributes attr : model.getAttributesList()) {
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
                    attributeStatement.setString(11, attr.getName());
                    attributeStatement.executeUpdate();
                }
            }

            System.out.println("Model and attributes saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Model> getModelsByUser(String username) {
        String selectModelsSQL = "SELECT model_id, model_name FROM Models WHERE username = ?";
        String selectAttributesSQL = "SELECT * FROM Attributes WHERE model_id = ?";

        List<Model> models = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement modelStatement = connection.prepareStatement(selectModelsSQL);
             PreparedStatement attributesStatement = connection.prepareStatement(selectAttributesSQL)) {

            modelStatement.setString(1, username);
            ResultSet modelResult = modelStatement.executeQuery();

            while (modelResult.next()) {
                long modelId = modelResult.getLong("model_id");
                String modelName = modelResult.getString("model_name");

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
                    attr.setName(attributesResult.getString("attribute_name"));
                    attributesList.add(attr);
                }

                models.add(new Model(modelName, username, attributesList));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(models);
    }

    public void deleteModelByNameAndUsername(String modelName, String username) {
        String getModelIdSQL = "SELECT model_id FROM Models WHERE model_name = ? AND username = ?";
        String deleteAttributesSQL = "DELETE FROM Attributes WHERE model_id = ?";
        String deleteModelSQL = "DELETE FROM Models WHERE model_name = ? AND username = ?";

        try (Connection connection = getConnection();
             PreparedStatement getModelIdStatement = connection.prepareStatement(getModelIdSQL);
             PreparedStatement deleteAttributesStatement = connection.prepareStatement(deleteAttributesSQL);
             PreparedStatement deleteModelStatement = connection.prepareStatement(deleteModelSQL)) {

            getModelIdStatement.setString(1, modelName);
            getModelIdStatement.setString(2, username);
            ResultSet resultSet = getModelIdStatement.executeQuery();

            if (resultSet.next()) {
                long modelId = resultSet.getLong("model_id");
                deleteAttributesStatement.setLong(1, modelId);
                deleteAttributesStatement.executeUpdate();
            }

            deleteModelStatement.setString(1, modelName);
            deleteModelStatement.setString(2, username);
            int rowsDeleted = deleteModelStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Model and associated attributes deleted successfully!");
            } else {
                System.out.println("No model found with the given name for this user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesUserExist(String username) {
        String query = "SELECT 1 FROM Users WHERE username = ? LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT password FROM Users WHERE username = ? LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                return checkPassword(password, storedHash);
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

    public void saveUser(String username, String passwordHash) {
        String query = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserByUsername(String username) {
        String getModelIdsSQL = "SELECT model_id FROM Models WHERE username = ?";
        String deleteAttributesSQL = "DELETE FROM Attributes WHERE model_id = ?";
        String deleteModelsSQL = "DELETE FROM Models WHERE username = ?";
        String deleteUserSQL = "DELETE FROM Users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement getModelIdsStatement = connection.prepareStatement(getModelIdsSQL);
             PreparedStatement deleteAttributesStatement = connection.prepareStatement(deleteAttributesSQL);
             PreparedStatement deleteModelsStatement = connection.prepareStatement(deleteModelsSQL);
             PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserSQL)) {

            getModelIdsStatement.setString(1, username);
            ResultSet resultSet = getModelIdsStatement.executeQuery();

            while (resultSet.next()) {
                long modelId = resultSet.getLong("model_id");
                deleteAttributesStatement.setLong(1, modelId);
                deleteAttributesStatement.executeUpdate();
            }

            deleteModelsStatement.setString(1, username);
            deleteModelsStatement.executeUpdate();

            deleteUserStatement.setString(1, username);
            deleteUserStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllUsernames() {
        String query = "SELECT username FROM Users";
        ArrayList<String> usernames = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usernames;
    }

    public void updateUserPassword(String username, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}