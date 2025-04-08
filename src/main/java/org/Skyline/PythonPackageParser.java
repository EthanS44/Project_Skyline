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
            List<File> pythonFiles = getFiles2(directory, ".py");

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

    // Helper method to recursively get .py files, skipping .venv directories
    private List<File> getFiles2(File directory, String extension) {
        List<File> fileList = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files == null) return fileList;

        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equals(".venv")) {
                    System.out.println("Skipping .venv directory: " + file.getAbsolutePath());
                    continue;
                }
                fileList.addAll(getFiles(file, extension));
            } else if (file.getName().endsWith(extension)) {
                fileList.add(file);
            }
        }

        return fileList;
    }
}
