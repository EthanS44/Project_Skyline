package org.Skyline;

import javafx.stage.FileChooser;

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

    public static Model parsePackage(File directory, String user) {
        List<Attributes> attributesList = new ArrayList<>();

        try {
            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory: " + directory.getAbsolutePath());
                return null;
            }

            System.out.println("Scanning directory: " + directory.getAbsolutePath());

            // Recursively collect Java files
            List<File> javaFiles = getJavaFiles(directory);

            if (javaFiles.isEmpty()) {
                System.out.println("No Java files found.");
                return null;
            }

            for (File file : javaFiles) {
                try {
                    String code = Files.readString(file.toPath());
                    System.out.println("Parsing file: " + file.getAbsolutePath());

                    Attributes attributes = JavaCodeParser.generateModelAttributes(code);
                    attributesList.add(attributes);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid class in file: " + file.getName());
                } catch (IOException e) {
                    System.out.println("Error reading file: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Model newModel = new Model(directory.getName(), user, attributesList);
        System.out.println(newModel.showAttributes());
        return newModel;
    }

    // Helper method to recursively collect Java files
    private static List<File> getJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(getJavaFiles(file));  // Recursive call
                } else if (file.getName().toLowerCase().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }

        return javaFiles;
    }

}
