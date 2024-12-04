package org.Skyline;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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

    // Must add inheritance depth
    // Average lines per method
    // Identify package
    // Identify associations
    // Class cohesion?

    // Method to take class and spit out ModelAttributes: (TO DO)
    public static ModelAttributes generateModelAttributes(String code){
        return new ModelAttributes();
    }

}
