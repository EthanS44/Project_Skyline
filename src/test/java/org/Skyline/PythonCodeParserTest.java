package org.Skyline;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PythonCodeParserTest {

    private PythonCodeParser parser;

    @Before
    public void setUp() {
        parser = new PythonCodeParser();
    }

    @Test
    public void testIsValidClass_True() {
        String code = "class MyClass:\n    def method(self):\n        pass";
        assertTrue(parser.isValidClass(code));
    }

    @Test
    public void testIsValidClass_False() {
        String code = "def function():\n    pass";
        assertFalse(parser.isValidClass(code));
    }

    @Test
    public void testFindClassName() {
        String code = "class ExampleClass:\n    def __init__(self):\n        pass";
        assertEquals("ExampleClass", parser.findClassName(code));
    }

    @Test
    public void testCountLinesOfCode() {
        String code = "class A:\n    def m(self):\n        print('Hello')\n";
        assertEquals(3, parser.countLinesOfCode(code));
    }

    @Test
    public void testCountLinesOfCodeNoBlanks() {
        String code = "\nclass A:\n\n#comment\n    def m(self):\n        print('Hello')\n\n";
        assertEquals(3, parser.countLinesOfCodeNoBlanks(code));
    }

    @Test
    public void testCountFields() {
        String code = "class A:\n    def __init__(self):\n        self.x = 1\n        self.y = 2";
        assertEquals(2, parser.countFields(code));
    }

    @Test
    public void testCountMethods() {
        String code = "class A:\n    def m1(self): pass\n    def m2(self): pass";
        assertEquals(2, parser.countMethods(code));
    }

    @Test
    public void testCalculateAverageLinesPerMethod() {
        String code = """
            class A:
                def first_function(x):
                    if x > 10:
                        print("Greater than 10")
                    elif x == 10:
                        print("Equal to 10")
                    else:
                        print("Less than 10")
                    return x
          
            class B:
                def second_function(y):
                    for i in range(5):
                        if i % 2 == 0:
                            print(i)
                    while y > 0:
                        y -= 1
                    return y
            """;
        double avg = parser.calculateAverageLinesPerMethod(code);
        assertEquals(8.5, avg, 0.0001);
    }

    @Test
    public void testCalculateMaximumCyclomaticComplexity() {
        String code = """
            def first_function(x):
                if x > 10:
                    print("Greater than 10")
                elif x == 10:
                    print("Equal to 10")
                else:
                    print("Less than 10")
                return x

            def second_function(y):
                for i in range(5):
                    if i % 2 == 0:
                        print(i)
                while y > 0:
                    y -= 1
                return y
            """;
        int max = parser.calculateMaximumCyclomaticComplexity(code);
        assertEquals(4, max);
    }

    @Test
    public void testExtractClassCode() {
        String code = """
            class A:
                def first_function(x):
                    if x > 10:
                        print("Greater than 10")
                    elif x == 10:
                        print("Equal to 10")
                    else:
                        print("Less than 10")
                    return x
          
            class B:
                def second_function(y):
                    for i in range(5):
                        if i % 2 == 0:
                            print(i)
                    while y > 0:
                        y -= 1
                    return y
            """;
        String extracted = parser.extractClassCode(code, "A");
        assertEquals("""
        def first_function(x):
                if x > 10:
                    print("Greater than 10")
                elif x == 10:
                    print("Equal to 10")
                else:
                    print("Less than 10")
                return x""", extracted);
    }


    @Test
    public void testGenerateModelAttributesList() throws Exception {
        String code = """
            class A:
                def first_function(x):
                    if x > 10:
                        print("Greater than 10")
                    elif x == 10:
                        print("Equal to 10")
                    else:
                        print("Less than 10")
                    return x
          
            class B:
                def second_function(y):
                    for i in range(5):
                        if i % 2 == 0:
                            print(i)
                    while y > 0:
                        y -= 1
                    return y
            """;
        List<Attributes> attrs = parser.generateModelAttributesList(code);
        assertEquals(2, attrs.size());
        assertEquals("A", attrs.get(0).getName());
        assertEquals(1, attrs.get(0).getNumberOfMethods());
        assertEquals(8,attrs.get(0).getLinesOfCode());
    }
}