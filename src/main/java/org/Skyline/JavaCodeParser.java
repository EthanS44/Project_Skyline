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
    public int countLinesOfCode(String code) {
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                .count();
    }

    public int countLinesIncludingComments(String code) {
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

    @Override
    public int calculateInheritanceDepth(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();
        int depth = 0;
        while (!classDecl.getExtendedTypes().isEmpty()) {
            depth++;
            String superClassName = classDecl.getExtendedTypes().get(0).getNameAsString();
            classDecl = cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(c -> c.getNameAsString().equals(superClassName))
                    .findFirst()
                    .orElse(null);
            if (classDecl == null) {
                break;
            }
        }
        return depth;
    }


    public String identifyPackage(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        return cu.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("No package");
    }

    public double calculateClassCohesion(String code) {
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

    @Override
    public String findClassName(String code) {
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();
        return classDecl.getNameAsString();
    }

    @Override
    public List<Attributes> generateModelAttributesList(String code) {return null;}

    // Must find associations still

    // Method to take class and spit out ModelAttributes
    @Override
    public Attributes generateModelAttributes(String code){
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

            //attributes.setNumberOfAssociations(calculateNumberOfAssociations(code));

            //attributes.setNumberOfImports(calculateNumberOfImports(code));

            return attributes;
        }

        throw new IllegalArgumentException("Not a valid class!");
    }

}
