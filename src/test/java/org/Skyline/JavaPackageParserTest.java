package org.Skyline;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JavaPackageParserTest {
    private Path testDir;
    private Path emptyDir;
    private final String testUser = "testuser";

    @Before
    public void setUp() throws IOException {
        // Set up test directories and files
        testDir = Files.createTempDirectory("java_parser_test");
        emptyDir = Files.createTempDirectory("java_parser_empty");

        // Valid Java file
        Files.writeString(testDir.resolve("Test.java"), "public class Test {}\n");

        // Invalid Java file
        Files.writeString(testDir.resolve("Invalid.java"), "class { broken");

        // Non-code file
        Files.writeString(testDir.resolve("notes.txt"), "These are just some notes.");
    }

    @After
    public void tearDown() throws IOException {
        // Clean up all created files and directories
        Files.walk(testDir).map(Path::toFile).forEach(File::delete);
        Files.walk(emptyDir).map(Path::toFile).forEach(File::delete);
    }

    @Test
    public void testParsePackage_withValidJavaFile() {
        JavaPackageParser parser = new JavaPackageParser();
        Model model = parser.parsePackage(testDir.toFile(), testUser);

        assertNotNull("Model should not be null", model);
        List<Attributes> attributes = model.getAttributesList();
        assertFalse("Attributes list should not be empty", attributes.isEmpty());
    }

    @Test
    public void testParsePackage_withEmptyDirectory() {
        JavaPackageParser parser = new JavaPackageParser();
        Model model = parser.parsePackage(emptyDir.toFile(), testUser);

        assertNull("Model should be null for empty directory", model);
    }

    @Test
    public void testParsePackage_withNoJavaFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("no_java_files");
        Files.writeString(tempDir.resolve("readme.txt"), "No Java files here");

        JavaPackageParser parser = new JavaPackageParser();
        Model model = parser.parsePackage(tempDir.toFile(), testUser);

        assertNull("Model should be null if no .java files exist", model);

        Files.walk(tempDir).map(Path::toFile).forEach(File::delete);
    }

    @Test
    public void testParsePackage_withNonExistentDirectory() {
        File fakeDir = new File("nonexistent_directory");

        JavaPackageParser parser = new JavaPackageParser();
        Model model = parser.parsePackage(fakeDir, testUser);

        assertNull("Model should be null for nonexistent directory", model);
    }
}
