package org.Skyline;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelTest {

    Model testModel;
    List<Attributes> testAttributesList;

    @Before
    public void modelTestSetUp(){
        Attributes testAttributes1 = new Attributes();
        Attributes testAttributes2 = new Attributes();
        testAttributesList = new ArrayList<>();
        testAttributesList.add(testAttributes1);
        testAttributesList.add(testAttributes2);
        testModel = new Model("test", "testuser", testAttributesList);
    }

    @Test
    public void testGetName() {
        assertEquals("test", testModel.getName());
    }

    @Test
    public void testSetUser() {
        testModel.setUser("newuser");
        assertEquals("newuser", testModel.getUser());
    }

    @Test
    public void testGetAttributesList() {
        assertEquals(testAttributesList, testModel.getAttributesList());
    }

    @Test
    public void testGetUser() {
        assertEquals("testuser", testModel.getUser());
    }

    @Test
    public void testSetName() {
        testModel.setName("newname");
        assertEquals("newname", testModel.getName());
    }

    @Test
    public void testToString() {
        assertEquals("test", testModel.toString());
    }

    @Test
    public void testShowAttributes() {
        assertEquals("Model{id=null, name='test', AttributesList=[\n" +
                        "    Attributes{id=0, name='null', user='null', linesOfCode=0, linesOfCodeNoBlanks=0, numberOfFields=0, numberOfMethods=0, averageLinesPerMethod=0.0, maxCyclomaticComplexity=0, inheritanceDepth=0, numberOfAssociations=0, numberOfImports=0, classPackage=''}\n" +
                        "    Attributes{id=0, name='null', user='null', linesOfCode=0, linesOfCodeNoBlanks=0, numberOfFields=0, numberOfMethods=0, averageLinesPerMethod=0.0, maxCyclomaticComplexity=0, inheritanceDepth=0, numberOfAssociations=0, numberOfImports=0, classPackage=''}\n" +
                        "]}", testModel.showAttributes());
    }
}