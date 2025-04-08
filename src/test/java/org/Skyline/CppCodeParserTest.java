package org.Skyline;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CppCodeParserTest {

    private CppCodeParser cppCodeParser;

    @Before
    public void setUp() throws Exception {
        // Set up the CppCodeParser instance before each test
        cppCodeParser = new CppCodeParser();
    }

    @Test
    public void testIsValidClass_ValidClass() {
        // Test if a valid C++ class returns true
        String code = "class MyClass { int x; };";
        assertTrue(cppCodeParser.isValidClass(code));
    }

    @Test
    public void testIsValidClass_InvalidClass() {
        // Test if an invalid C++ class returns false
        String code = "int x;";
        assertFalse(cppCodeParser.isValidClass(code));
    }

    @Test
    public void testCountFields() {
        // Test counting fields in a C++ class
        String code = "class MyClass { int x; double y; };";
        assertEquals(2, cppCodeParser.countFields(code));
    }

    @Test
    public void testCountFields_NoFields() {
        // Test if no fields are found in the C++ class
        String code = "class MyClass { };";
        assertEquals(0, cppCodeParser.countFields(code));
    }

    @Test
    public void testCountMethods() {
        // Test counting methods in a C++ class
        String code = "class MyClass { void foo() {} void bar() {} };";
        assertEquals(2, cppCodeParser.countMethods(code));
    }

    @Test
    public void testCountMethods_NoMethods() {
        // Test if no methods are found in the C++ class
        String code = "class MyClass { };";
        assertEquals(0, cppCodeParser.countMethods(code));
    }

    @Test
    public void testCountLinesOfCode() {
        // Test counting lines of code
        String code = "class MyClass { int x; void foo() {} // method }";  // Code as one continuous string
        assertEquals(4, cppCodeParser.countLinesOfCode(code)); // Assumes countLinesOfCode handles counting based on actual lines.
    }

    @Test
    public void testCountLinesOfCodeNoBlanks() {
        // Test counting lines of code not including blank lines
        String code = "class MyClass { int x; void foo() {} // method }";  // Code as one continuous string
        assertEquals(3, cppCodeParser.countLinesOfCodeNoBlanks(code)); // Assumes countLinesOfCodeNoBlanks handles this logic.
    }

    @Test
    public void testFindClassName() {
        // Test finding the class name
        String code = "class MyClass { int x; };";
        assertEquals("MyClass", cppCodeParser.findClassName(code));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClassName_InvalidCode() {
        // Test if it throws an exception when no class is found
        String code = "int x;";
        cppCodeParser.findClassName(code); // Should throw exception
    }

    @Test
    public void testGenerateModelAttributes() {
        // Test generating model attributes for a valid C++ class
        String code = "class MyClass { int x; void foo() {} };";
        Attributes attributes = cppCodeParser.generateModelAttributes(code);

        assertNotNull(attributes);
        assertEquals("MyClass", attributes.getName());
        assertEquals(1, attributes.getNumberOfFields());
        assertEquals(1, attributes.getNumberOfMethods());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateModelAttributes_InvalidClass() {
        // Test if it throws an exception for an invalid C++ class
        String code = "int x;";
        cppCodeParser.generateModelAttributes(code); // Should throw exception
    }
}