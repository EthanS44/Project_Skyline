package org.Skyline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class PackageParser {

    /**
     * Parses all Java files in a given package directory and creates an `Attributes` object for each class.
     *
     * @param packageName The package name (e.g., "com.example.models").
     * @return List of `Attributes` objects representing all classes in the package.
     */

    public static List<Attributes> parsePackage(String packageName) {
        List<Attributes> attributesList = new ArrayList<>();
        try {
            // Convert package name to a directory path
            String path = packageName.replace('.', '/');
            File directory = new File(Thread.currentThread().getContextClassLoader().getResource(path).toURI());

            // Iterate over all files in the package directory
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".java")) {
                    // Read the Java file content
                    String code = Files.readString(Path.of(file.getAbsolutePath()));

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
        return attributesList;
    }
}
