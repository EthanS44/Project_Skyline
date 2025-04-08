package org.Skyline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class JavaPackageParser extends PackageParser {
    @Override
    public Model parsePackage(File directory, String user) {
        List<Attributes> attributesList = new ArrayList<>();
        JavaCodeParser javaCodeParser = new JavaCodeParser();

        try {
            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory: " + directory.getAbsolutePath());
                return null;
            }

            System.out.println("Scanning directory: " + directory.getAbsolutePath());
            List<File> javaFiles = getFiles(directory, ".java");

            if (javaFiles.isEmpty()) {
                System.out.println("No Java files found.");
                return null;
            }

            for (File file : javaFiles) {
                try {
                    String code = Files.readString(file.toPath());
                    System.out.println("Parsing file: " + file.getAbsolutePath());

                    Attributes attributes = javaCodeParser.generateModelAttributes(code);
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
}