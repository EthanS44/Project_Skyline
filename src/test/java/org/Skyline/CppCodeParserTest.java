package org.Skyline;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CppCodeParserTest {

    private CppCodeParser parser;

    @Before
    public void setUp() {
        parser = new CppCodeParser();
    }

    @Test
    public void testIsValidClass_True() {
        String code = "class MyClass { public: void method(); };";
        assertTrue(parser.isValidClass(code));
    }

    @Test
    public void testIsValidClass_False() {
        String code = "void func() {}";
        assertFalse(parser.isValidClass(code));
    }

    @Test
    public void testFindClassName() {
        String code = "class TestName { public: int x; };";
        assertEquals("TestName", parser.findClassName(code));
    }

    @Test
    public void testCountLinesOfCode() {
        String code = "class A {\nint x;\nvoid m() {}\n};";
        assertEquals(4, parser.countLinesOfCode(code));
    }

    @Test
    public void testCountLinesOfCodeNoBlanks() {
        String code = "\n\nclass A {\n\nint x;\n\n};\n";
        assertEquals(3, parser.countLinesOfCodeNoBlanks(code));
    }

    @Test
    public void testCountFields() {
        String code = "class A { int a; float b; };";
        assertEquals(2, parser.countFields(code));
    }

    @Test
    public void testCountMethods() {
        String code = "class A { void m1(); int m2() { return 0; } };";
        assertEquals(2, parser.countMethods(code));
    }

    @Test
    public void testCalculateAverageLinesPerMethod() {
        String code = "class A { void m1() { int x = 0; } void m2() { int y = 1; int z = 2; } };";
        double avg = parser.calculateAverageLinesPerMethod(code);
        assertTrue(avg > 0);
    }

    @Test
    public void testCalculateMaximumCyclomaticComplexity() {
        String code = """
        class A {
            void m() {
                if (x > 0) {
                    // condition 1
                } else if (x < 0) {
                    // condition 2
                }

                for (int i = 0; i < 5; ++i) {
                    if (i % 2 == 0) {
                        continue;
                    }
                }

                while (y != 0) {
                    y--;
                }

                switch(z) {
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }

                try {
                    riskyOperation();
                } catch (const std::exception& e) {
                    handle(e);
                }
            }
        };
        class B {
            void m() {
                if (x > 0) {
                    // condition 1
                } else if (x < 0) {
                    // condition 2
                }
            }
        };
    """;
        int max = parser.calculateMaximumCyclomaticComplexity(code);
        assertEquals(11, max);
    }

    @Test
    public void testCalculateCyclomaticComplexityForMethod() {
        String method = "void m() { if (x) {} else if (y) {} for (;;) {} switch(n) { case 1: break; case 2: break; } }";
        int complexity = parser.calculateCyclomaticComplexityForMethod(method);
        assertEquals(8, complexity);
    }

    @Test
    public void testCountPatternMatches() {
        String code = "if (x) {} if (y) {} for (;;) {}";
        Pattern pattern = Pattern.compile("if\\s*\\(");
        Matcher matcher = pattern.matcher(code);
        int matches = parser.countPatternMatches(matcher);
        assertEquals(2, matches);
    }

    @Test
    public void testGenerateModelAttributes() {
        String code = "class Sample { int x; void method() { if (x) {} } };";
        Attributes attr = parser.generateModelAttributes(code);
        assertEquals("Sample", attr.getName());
        assertEquals(1, attr.getNumberOfFields());
        assertEquals(1, attr.getNumberOfMethods());
        assertTrue(attr.getLinesOfCode() > 0);
        assertTrue(attr.getMaxCyclomaticComplexity() >= 2); // base + if
    }
}
