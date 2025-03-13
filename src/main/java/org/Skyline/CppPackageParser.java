package org.Skyline;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

class CppPackageParser extends PackageParser {
    @Override
    public Model parsePackage(File directory, String user) {
        List<Attributes> attributesList = new ArrayList<>();
        CppCodeParser cppCodeParser = new CppCodeParser();

        try {
            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory: " + directory.getAbsolutePath());
                return null;
            }

            System.out.println("Scanning directory: " + directory.getAbsolutePath());
            List<File> cppFiles = getFiles(directory, ".cpp");

            if (cppFiles.isEmpty()) {
                System.out.println("No C++ files found.");
                return null;
            }

            for (File file : cppFiles) {
                try {
                    String code = Files.readString(file.toPath());
                    System.out.println("Parsing file: " + file.getAbsolutePath());

                    Attributes attributes = cppCodeParser.generateModelAttributes(code);
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
