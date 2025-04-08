package org.Skyline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PackageParser {
    public abstract Model parsePackage(File directory, String user);

    protected List<File> getFiles(File dir, String extension) {
        List<File> filesList = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    filesList.addAll(getFiles(file, extension));  // Recursive call
                } else if (file.getName().toLowerCase().endsWith(extension)) {
                    filesList.add(file);
                }
            }
        }
        return filesList;
    }
}