package org.Skyline;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.Assert.*;

public class PythonPackageParserTest {

    private File tempDir;
    private File pythonFile;
    private File venvDir;
    private File ignoredFile;
    private PythonPackageParser parser;

    @Before
    public void setUp() throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "testPythonPackage");
        tempDir.mkdir();

        // Create valid Python file
        pythonFile = new File(tempDir, "example.py");
        try (FileWriter writer = new FileWriter(pythonFile)) {
            writer.write("class Sample:\n    def __init__(self):\n        self.data = 1\n");
        }

        // Create .venv folder and a file inside it that should be ignored
        venvDir = new File(tempDir, ".venv");
        venvDir.mkdir();
        ignoredFile = new File(venvDir, "ignored.py");
        try (FileWriter writer = new FileWriter(ignoredFile)) {
            writer.write("print('This should be ignored')\n");
        }

        parser = new PythonPackageParser();
    }

    @Test
    public void testParseValidPythonPackage() {
        Model model = parser.parsePackage(tempDir, "testuser");
        assertNotNull("Model should not be null", model);
        assertEquals("Model name should match directory name", tempDir.getName(), model.getName());
        assertEquals("Model user should be testuser", "testuser", model.getUser());

        List<Attributes> attributes = model.getAttributesList();
        assertFalse("Attributes list should not be empty", attributes.isEmpty());
    }

    @Test
    public void testParseEmptyDirectory() {
        File emptyDir = new File(tempDir, "empty");
        emptyDir.mkdir();
        Model model = parser.parsePackage(emptyDir, "testuser");
        assertNull("Model should be null for empty directory", model);
    }

    @Test
    public void testSkipVenvDirectory() {
        Model model = parser.parsePackage(tempDir, "testuser");
        assertNotNull("Model should be created", model);
        assertTrue("Should not include files from .venv directory",
                model.getAttributesList().stream().noneMatch(attr -> attr.getName().contains("ignored")));
    }

    @After
    public void tearDown() {
        ignoredFile.delete();
        venvDir.delete();
        pythonFile.delete();
        tempDir.delete();
    }
}