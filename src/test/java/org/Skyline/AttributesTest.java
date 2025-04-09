package org.Skyline;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AttributesTest {

    private Attributes attributes;

    @Before
    public void setUp() throws Exception {
        // Set up a new Attributes instance before each test
        attributes = new Attributes("testUser", 100, 90, 10, 3, 30, 1, 0, 2);
    }

    @Test
    public void testGetUser() {
        // Test if the user field is correctly set
        assertEquals("testUser", attributes.getUser());
    }

    @Test
    public void testGetAndSetName() {
        // Test getter and setter for the name field
        attributes.setName("TestClass");
        assertEquals("TestClass", attributes.getName());
    }

    @Test
    public void testGetAndSetLinesOfCode() {
        // Test getter and setter for linesOfCode
        attributes.setLinesOfCode(100);
        assertEquals(100, attributes.getLinesOfCode());
    }

    @Test
    public void testGetAndSetLinesOfCodeNoBlanks() {
        // Test getter and setter for linesOfCodeNoBlanks
        attributes.setLinesOfCodeNoBlanks(90);
        assertEquals(90, attributes.getLinesOfCodeNoBlanks());
    }

    @Test
    public void testGetAndSetNumberOfFields() {
        // Test getter and setter for numberOfFields
        attributes.setNumberOfFields(10);
        assertEquals(10, attributes.getNumberOfFields());
    }

    @Test
    public void testGetAndSetNumberOfMethods() {
        // Test getter and setter for numberOfMethods
        attributes.setNumberOfMethods(5);
        assertEquals(5, attributes.getNumberOfMethods());
    }

    @Test
    public void testGetAndSetMaxCyclomaticComplexity() {
        // Test getter and setter for maxCyclomaticComplexity
        attributes.setMaxCyclomaticComplexity(15);
        assertEquals(15, attributes.getMaxCyclomaticComplexity());
    }

    @Test
    public void testGetAndSetInheritanceDepth() {
        // Test getter and setter for inheritanceDepth
        attributes.setInheritanceDepth(3);
        assertEquals(3, attributes.getInheritanceDepth());
    }

    @Test
    public void testGetAndSetNumberOfAssociations() {
        // Test getter and setter for numberOfAssociations
        attributes.setNumberOfAssociations(7);
        assertEquals(7, attributes.getNumberOfAssociations());
    }

    @Test
    public void testGetAndSetNumberOfImports() {
        // Test getter and setter for numberOfImports
        attributes.setNumberOfImports(8);
        assertEquals(8, attributes.getNumberOfImports());
    }

    @Test
    public void testGetAndSetClassPackage() {
        // Test getter and setter for classPackage
        attributes.setClassPackage("org.Skyline");
        assertEquals("org.Skyline", attributes.getClassPackage());
    }

    @Test
    public void testGetAndSetAverageLinesPerMethod() {
        // Test getter and setter for averageLinesPerMethod
        attributes.setAverageLinesPerMethod(20.5);
        assertEquals(20.5, attributes.getAverageLinesPerMethod(), 0.001);
    }

    @Test
    public void testToString() {
        // Test the toString method to ensure proper output
        attributes.setName("TestClass");
        attributes.setLinesOfCode(100);
        attributes.setLinesOfCodeNoBlanks(90);
        attributes.setNumberOfFields(10);
        attributes.setNumberOfMethods(5);
        attributes.setMaxCyclomaticComplexity(15);
        attributes.setInheritanceDepth(3);
        attributes.setNumberOfAssociations(7);
        attributes.setNumberOfImports(8);
        attributes.setClassPackage("org.Skyline");
        attributes.setAverageLinesPerMethod(20.5);

        String expected = "Attributes{id=0, name='TestClass', user='testUser', linesOfCode=100, linesOfCodeNoBlanks=90, numberOfFields=10, numberOfMethods=5, averageLinesPerMethod=20.5, maxCyclomaticComplexity=15, inheritanceDepth=3, numberOfAssociations=7, numberOfImports=8, classPackage='org.Skyline'}";
        assertEquals(expected, attributes.toString());
    }
}