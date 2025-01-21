package org.Skyline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PackageParser {

    /**
     * Parses all Java files in a given package directory and creates an `Attributes` object for each class.
     * Then creates a Model object to store the list of Attributes.
     *
     * @param packageName The package name (e.g., "com.example.models").
     * @return value of type "Model".
     */

    public static Model parsePackage(String packageName) {
        List<Attributes> attributesList = new ArrayList<>();
        try {
            // Convert package name to directory path
            String prefix = "src/main/java/";
            String path = prefix + packageName.replace('.', '/');

            File directory = new File(path);

            // Check if the directory exists
            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory for package: " + path);
                return null;
            }

            System.out.println("Scanning directory: " + directory.getAbsolutePath());

            // Iterate over all files in the package directory
            for (File file : directory.listFiles()) {
                if (file.getName().toLowerCase().endsWith(".java")) {
                    // Read the Java file content
                    String code = Files.readString(file.toPath());
                    System.out.println("Parsing file: " + file.getName());

                    // Parse the file and generate Attributes
                    try {
                        Attributes attributes = JavaCodeParser.generateModelAttributes(code);
                        attributesList.add(attributes);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Skipping invalid class in file: " + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the model and return it
        return new Model(packageName, attributesList.get(0).getUser(), attributesList);
    }
}
