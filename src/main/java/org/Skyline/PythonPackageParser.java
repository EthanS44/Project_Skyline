package org.Skyline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class PythonPackageParser extends PackageParser {

    @Override
    public Model parsePackage(File directory, String user) {
        List<Attributes> attributesList = new ArrayList<>();
        PythonCodeParser pythonCodeParser = new PythonCodeParser();

        try {
            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory: " + directory.getAbsolutePath());
                return null;
            }

            System.out.println("Scanning directory: " + directory.getAbsolutePath());
            List<File> pythonFiles = getFiles(directory, ".py");

            if (pythonFiles.isEmpty()) {
                System.out.println("No Python files found.");
                return null;
            }

            for (File file : pythonFiles) {
                try {
                    String code = Files.readString(file.toPath());
                    System.out.println("Parsing file: " + file.getAbsolutePath());

                    // Get attributes for all classes in the file
                    List<Attributes> fileAttributes = pythonCodeParser.generateModelAttributesList(code);
                    attributesList.addAll(fileAttributes);  // Add all attributes found in this file
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid class in file: " + file.getName());
                } catch (IOException e) {
                    System.out.println("Error reading file: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the model with the collected attributes from all files
        Model newModel = new Model(directory.getName(), user, attributesList);
        System.out.println(newModel.showAttributes());
        return newModel;
    }
}
