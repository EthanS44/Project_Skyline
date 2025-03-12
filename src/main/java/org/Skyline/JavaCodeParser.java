package org.Skyline;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import java.util.List;
public class JavaCodeParser {
    public static boolean isValidClass(String code) {
        try {
            JavaParser parser = new JavaParser();  // Create an instance of JavaParser
            CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
            return cu.findFirst(ClassOrInterfaceDeclaration.class).isPresent();
        } catch (ParseProblemException e) {
            return false;
        }
    }

    public static int countFields(String code) {
        JavaParser parser = new JavaParser();  // Create an instance of JavaParser
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.findAll(com.github.javaparser.ast.body.FieldDeclaration.class).size();
    }

    public static int countMethods(String code) {
        JavaParser parser = new JavaParser();  // Create an instance of JavaParser
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).size();
    }

    // Counts lines of code without counting blanks or spaces
    public static int countLinesOfCode(String code) {
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                .count();
    }

    // Counts all lines includes blanks and comments
    public static int countLinesIncludingComments(String code) {
        // Counting all lines, including empty lines and comments
        return (int) code.lines().count();
    }

    // Don't use this directly, it's just here for the method below.
    public static int calculateCyclomaticComplexityForMethod(String code, MethodDeclaration method) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        int complexity = 1;  // Start with 1 for the method itself

        // Count decision points in the method body
        complexity += method.findAll(IfStmt.class).size();     // if statements
        complexity += method.findAll(ForStmt.class).size();    // for loops
        complexity += method.findAll(WhileStmt.class).size();  // while loops
        complexity += method.findAll(SwitchStmt.class).size(); // switch statements
        complexity += method.findAll(CatchClause.class).size(); // catch blocks (for try-catch)

        return complexity;
    }

    // Method to calculate maximum Cyclomatic Complexity for a class (across all methods)
    public static int calculateMaximumCyclomaticComplexity(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        // Find all methods in the class
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        int maxComplexity = 0;

        // Calculate Cyclomatic Complexity for each method
        for (MethodDeclaration method : methods) {
            int methodComplexity = calculateCyclomaticComplexityForMethod(code, method);
            maxComplexity = Math.max(maxComplexity, methodComplexity);
        }

        return maxComplexity;
    }

    // Method to calculate the average lines of code per method
    public static double calculateAverageLinesPerMethod(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        if (methods.isEmpty()) return 0;

        int totalLines = 0;
        for (MethodDeclaration method : methods) {
            totalLines += method.getRange().map(range -> range.end.line - range.begin.line + 1).orElse(0);
        }
        return (double) totalLines / methods.size();
    }

    // Method to calculate the inheritance depth of the class
    public static int calculateInheritanceDepth(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();
        int depth = 0;

        while (!classDecl.getExtendedTypes().isEmpty()) {
            depth++;

            // Attempt to find the superclass in the same CompilationUnit
            String superClassName = classDecl.getExtendedTypes().get(0).getNameAsString();
            classDecl = cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(c -> c.getNameAsString().equals(superClassName))
                    .findFirst()
                    .orElse(null);

            if (classDecl == null) {
                break; // Avoid infinite loop if superclass isn't found
            }
        }
        return depth;
    }

    // Method to identify the package the class is part of
    public static String identifyPackage(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        return cu.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("No package");
    }

    // Method to calculate class cohesion (a rough measure based on methods using fields)
    public static double calculateClassCohesion(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

        int methodUsingFields = 0;
        for (MethodDeclaration method : methods) {
            for (FieldDeclaration field : fields) {
                if (method.findAll(com.github.javaparser.ast.expr.FieldAccessExpr.class).stream()
                        .anyMatch(expr -> expr.getNameAsString().equals(field.getVariables().get(0).getNameAsString()))) {
                    methodUsingFields++;
                    break;
                }
            }
        }

        return methods.isEmpty() ? 0 : (double) methodUsingFields / methods.size();
    }

    public static int calculateNumberOfImports(String code) {
        return 0; // incomplete
    }

    public static int calculateNumberOfAssociations(String code) {
        return 0; // incomplete
    }

    public static String findClassName(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();

        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow();
        return classDecl.getNameAsString();
    }

    // Must find associations still

    // Method to take class and spit out ModelAttributes
    public static Attributes generateModelAttributes(String code){
        if (isValidClass(code)){

            Attributes attributes = new Attributes();

            attributes.setName(findClassName(code));

            attributes.setLinesOfCode(countLinesIncludingComments(code));

            attributes.setLinesOfCodeNoBlanks(countLinesOfCode(code));

            attributes.setNumberOfFields(countFields(code));

            attributes.setNumberOfMethods(countMethods(code));

            attributes.setMaxCyclomaticComplexity(calculateMaximumCyclomaticComplexity(code));

            attributes.setInheritanceDepth(calculateInheritanceDepth(code));

            attributes.setClassPackage(identifyPackage(code));

            attributes.setAverageLinesPerMethod(calculateAverageLinesPerMethod(code));

            attributes.setNumberOfAssociations(calculateNumberOfAssociations(code));

            attributes.setNumberOfImports(calculateNumberOfImports(code));

            return attributes;
        }

        throw new IllegalArgumentException("Not a valid class!");
    }

}
