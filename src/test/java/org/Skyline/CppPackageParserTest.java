package org.Skyline;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class CppPackageParserTest {

    private File tempDir;
    private final String user = "testUser";

    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("cpp_test_package").toFile();
    }

    @After
    public void tearDown() throws Exception {
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
        tempDir.delete();
    }

    private void createCppFile(String name, String content) throws Exception {
        File cppFile = new File(tempDir, name);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cppFile))) {
            writer.write(content);
        }
    }

    @Test
    public void testParsePackage_withValidCppFile() throws Exception {
        String code = "class TestClass { int x; };";
        createCppFile("TestClass.cpp", code);

        CppPackageParser parser = new CppPackageParser();
        Model model = parser.parsePackage(tempDir, user);

        assertNotNull(model);
        assertEquals(tempDir.getName(), model.getName());
        assertEquals(user, model.getUser());
        assertFalse(model.getAttributesList().isEmpty());
    }

    @Test
    public void testParsePackage_withEmptyDirectory() {
        CppPackageParser parser = new CppPackageParser();
        Model model = parser.parsePackage(tempDir, user);

        assertNull(model);
    }

    @Test
    public void testParsePackage_withInvalidCppFile() throws Exception {
        createCppFile("Broken.cpp", "this is not valid C++");

        CppPackageParser parser = new CppPackageParser();
        Model model = parser.parsePackage(tempDir, user);

        // May return null or an empty model depending on how generateModelAttributes handles invalid content
        assertNotNull(model);
        assertEquals(0, model.getAttributesList().size());
    }

    @Test
    public void testParsePackage_directoryDoesNotExist() {
        File nonExistent = new File("/this/path/does/not/exist");
        CppPackageParser parser = new CppPackageParser();

        Model model = parser.parsePackage(nonExistent, user);
        assertNull(model);
    }
}
