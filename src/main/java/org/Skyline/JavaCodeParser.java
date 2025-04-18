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

public class JavaCodeParser extends CodeParser {
    @Override
    public boolean isValidClass(String code) {
        try {
            JavaParser parser = new JavaParser();  // Create an instance of JavaParser
            CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
            return cu.findFirst(ClassOrInterfaceDeclaration.class).isPresent();
        } catch (ParseProblemException e) {
            return false;
        }
    }

    @Override
    public int countFields(String code) {
        JavaParser parser = new JavaParser();  // Create an instance of JavaParser
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.findAll(FieldDeclaration.class).size();
    }

    @Override
    public int countMethods(String code) {
        JavaParser parser = new JavaParser();  // Create an instance of JavaParser
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.findAll(MethodDeclaration.class).size();
    }

    @Override
    public int countLinesOfCodeNoBlanks(String code) {
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                .count();
    }

    @Override
    public int countLinesOfCode(String code) {
        return (int) code.lines().count();
    }

    private int calculateCyclomaticComplexityForMethod(String code, MethodDeclaration method) {
        int complexity = 1;
        complexity += method.findAll(IfStmt.class).size();
        complexity += method.findAll(ForStmt.class).size();
        complexity += method.findAll(WhileStmt.class).size();
        complexity += method.findAll(SwitchStmt.class).size();
        complexity += method.findAll(CatchClause.class).size();
        return complexity;
    }


    public int calculateMaximumCyclomaticComplexity(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        int maxComplexity = 0;
        for (MethodDeclaration method : methods) {
            int methodComplexity = calculateCyclomaticComplexityForMethod(code, method);
            maxComplexity = Math.max(maxComplexity, methodComplexity);
        }
        return maxComplexity;
    }


    public double calculateAverageLinesPerMethod(String code) {
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


    public String identifyPackage(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("No package");
    }

    @Override
    public String findClassName(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();
        return classDecl.getNameAsString();
    }


    // Method to take class and return ModelAttributes
    @Override
    public Attributes generateModelAttributes(String code){
        if (isValidClass(code)){

            Attributes attributes = new Attributes();
            attributes.setName(findClassName(code));
            System.out.println(attributes.getName());
            attributes.setLinesOfCode(countLinesOfCode(code));
            attributes.setLinesOfCodeNoBlanks(countLinesOfCodeNoBlanks(code));
            attributes.setNumberOfFields(countFields(code));
            attributes.setNumberOfMethods(countMethods(code));
            attributes.setMaxCyclomaticComplexity(calculateMaximumCyclomaticComplexity(code));
            attributes.setClassPackage(identifyPackage(code));
            attributes.setAverageLinesPerMethod(calculateAverageLinesPerMethod(code));
            return attributes;
        }
        throw new IllegalArgumentException("Not a valid class!");
    }
}
