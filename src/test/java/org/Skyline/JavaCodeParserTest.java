package org.Skyline;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class JavaCodeParserTest {

    private JavaCodeParser parser;

    @Before
    public void setUp() {
        parser = new JavaCodeParser();
    }

    @Test
    public void testGenerateModelAttributes_SimpleClass() throws Exception {
        File temp = File.createTempFile("TestClass", ".java");
        try (FileWriter writer = new FileWriter(temp)) {
            writer.write("package com.example;\n" +
                    "public class TestClass {\n" +
                    "    private int x;\n" +
                    "    public void methodA() {}\n" +
                    "}\n");
        }

        String code = new String(Files.readAllBytes(temp.toPath()));
        Attributes attr = parser.generateModelAttributes(code);

        assertEquals("TestClass", attr.getName());
        assertEquals("com.example", attr.getClassPackage());
        assertEquals(1, attr.getNumberOfMethods());
        assertEquals(1, attr.getNumberOfFields());
        assertTrue(attr.getLinesOfCode() >= 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateModelAttributes_InvalidClass() {
        String code = "NotAClass {}";
        parser.generateModelAttributes(code);
    }

    @Test
    public void testIdentifyPackage() {
        String code = "package com.example; public class A {}";
        String result = parser.identifyPackage(code);
        assertEquals("com.example", result);
    }

    @Test
    public void testFindClassName() {
        String code = "public class HelloWorld {}";
        String className = parser.findClassName(code);
        assertEquals("HelloWorld", className);
    }

    @Test
    public void testIsValidClass_True() {
        String code = "public class ValidClass {}";
        assertTrue(parser.isValidClass(code));
    }

    @Test
    public void testIsValidClass_False() {
        String code = "InvalidClass {}";
        assertFalse(parser.isValidClass(code));
    }

    @Test
    public void testCountLinesOfCode() {
        String code = "public class X {\nint a;\nvoid m() {}\n}";
        assertEquals(4, parser.countLinesOfCode(code));
    }

    @Test
    public void testCountLinesOfCodeNoBlanks() {
        String code = "\n\npublic class X {\n\nint a;\n\n}\n";
        assertEquals(3, parser.countLinesOfCodeNoBlanks(code));
    }

    @Test
    public void testCountFields() {
        String code = "public class X { int a; String name; }";
        assertEquals(2, parser.countFields(code));
    }

    @Test
    public void testCountMethods() {
        String code = "public class X { void a() {} int b() { return 1; } }";
        assertEquals(2, parser.countMethods(code));
    }

    @Test
    public void testCalculateAverageLinesPerMethod() {
        String code = "public class A { \nvoid m1() { \nint x = 1; \nint z = 4;} \nvoid m2() { \nint y = 2; } }";
        double avg = parser.calculateAverageLinesPerMethod(code);
        assertEquals(2.5, avg, 0.001);
    }

    @Test
    public void testCalculateCyclomaticComplexityForMethod_Branches() {
        String code = """
            public class Temp {
                public void test() {
                    if (true) {}
                    else if (false) {}
                    for(int i=0;i<10;i++) {}
                    while(true) {}
                    switch(x) { case 1: break; case 2: break; }
                    try { } catch(Exception e) {}
                }
            }
        """;
        int complexity = parser.calculateMaximumCyclomaticComplexity(code);
        assertEquals(7, complexity);
    }

    @Test
    public void testCalculateMaximumCyclomaticComplexity_MultipleMethods() {
        String code = """
            public class A {
                void m1() { if (a) {} }
                void m2() { while (b) {} for(int i=0;i<10;i++) {} }
                void m3() { try {} catch(Exception e) {} switch(x) { case 1: break; case 2: break; } }
            }
        """;
        int max = parser.calculateMaximumCyclomaticComplexity(code);
        assertEquals(3, max);
    }
} 