package org.Skyline;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class PythonCodeParser extends CodeParser {

    @Override
    public boolean isValidClass(String code) {
        return code.contains("class ");
    }

    @Override
    public int countFields(String code) {
        // Find the __init__ method and extract its body
        Pattern initPattern = Pattern.compile(
                "def\\s+__init__\\s*\\(.*?\\):\\s*\\n((?:[ \\t]+.*\\n*)+)",
                Pattern.DOTALL
        );
        Matcher initMatcher = initPattern.matcher(code);
        if (!initMatcher.find()) {
            return 0; // No __init__ method found
        }

        String initBody = initMatcher.group(1);

        // Count `self.` assignments inside __init__
        Pattern selfFieldPattern = Pattern.compile("self\\.(\\w+)\\s*=.*");
        Matcher selfMatcher = selfFieldPattern.matcher(initBody);
        Set<String> fields = new HashSet<>();

        while (selfMatcher.find()) {
            fields.add(selfMatcher.group(1)); // Collect unique field names
        }

        return fields.size();
    }

    @Override
    public int countMethods(String code) {
        Pattern pattern = Pattern.compile("def\\s+\\w+\\s*\\(.*?\\)\\s*:");
        Matcher matcher = pattern.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public int countLinesOfCode(String code) {
        return (int) code.lines().count();
    }

    @Override
    public int countLinesOfCodeNoBlanks(String code) {
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                .count();
    }

    @Override
    public String findClassName(String code) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No valid class found in the given Python code.");
    }

    @Override
    public Attributes generateModelAttributes(String code) {
        return null;
    }

    @Override
    public int calculateMaximumCyclomaticComplexity(String code) {
        // Split code into methods using regular expressions
        String regex = "(def\\s+(\\w+)\\s*\\(([^)]*)\\):)([\\s\\S]*?)(?=\\ndef\\s+|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        int maxCyclomaticComplexity = 0;

        while (matcher.find()) {
            // Extract method body
            String methodBody = matcher.group(4).trim();
            int cyclomaticComplexity = calculateCyclomaticComplexityForMethod(methodBody);
            System.out.println(methodBody);
            System.out.println("Complexity = " + cyclomaticComplexity);
            maxCyclomaticComplexity = Math.max(maxCyclomaticComplexity, cyclomaticComplexity);
        }

        return maxCyclomaticComplexity;
    }

    private int calculateCyclomaticComplexityForMethod(String methodCode) {
        int complexity = 1;  // Start with a base complexity of 1

        // Match decision points: if, for, while, and, or, elif
        Pattern decisionPointPattern = Pattern.compile("\\b(if|for|while|elif|and|or)\\b");
        Matcher decisionPointMatcher = decisionPointPattern.matcher(methodCode);

        // Count decision points
        while (decisionPointMatcher.find()) {
            complexity++;
        }

        return complexity;
    }

    @Override
    public double calculateAverageLinesPerMethod(String code) {
        int methodCount = countMethods(code);
        if (methodCount == 0) return 0.0;

        int totalLines = countLinesOfCodeNoBlanks(code);
        return (double) totalLines / methodCount;
    }

    public List<Attributes> generateModelAttributesList(String code) {
        List<Attributes> attributesList = new ArrayList<>();
        if (isValidClass(code)) {
            Pattern pattern = Pattern.compile("class\\s+(\\w+)");
            Matcher matcher = pattern.matcher(code);

            // Find all classes in the code
            while (matcher.find()) {
                String className = matcher.group(1);
                String classCode = extractClassCode(code, className);

                Attributes attributes = new Attributes();
                attributes.setName(className);
                attributes.setLinesOfCode(countLinesOfCode(classCode));
                attributes.setLinesOfCodeNoBlanks(countLinesOfCodeNoBlanks(classCode));
                attributes.setNumberOfFields(countFields(classCode));
                attributes.setNumberOfMethods(countMethods(classCode));
                attributes.setMaxCyclomaticComplexity(calculateMaximumCyclomaticComplexity(classCode));
                attributes.setAverageLinesPerMethod(calculateAverageLinesPerMethod(classCode));
                attributesList.add(attributes);
            }
            return attributesList;
        }
        throw new IllegalArgumentException("Not a valid Python class!");
    }

    private static String extractClassCode(String code, String className) {
        String[] lines = code.split("\\r?\\n");
        String classHeader = "class " + className;
        StringBuilder classBody = new StringBuilder();
        boolean inClass = false;
        int indentLevel = -1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (!inClass) {
                if (line.trim().startsWith(classHeader)) {
                    inClass = true;

                    // Look ahead to find the indentation level
                    while (++i < lines.length) {
                        String nextLine = lines[i];
                        if (nextLine.trim().isEmpty()) continue;

                        int leadingSpaces = nextLine.indexOf(nextLine.trim());
                        indentLevel = leadingSpaces;
                        classBody.append(nextLine).append("\n");
                        break;
                    }
                }
            } else {
                if (i >= lines.length) break;
                String nextLine = lines[i];

                if (nextLine.trim().isEmpty()) {
                    classBody.append(nextLine).append("\n");
                    continue;
                }

                int currentIndent = nextLine.indexOf(nextLine.trim());

                if (currentIndent >= indentLevel) {
                    classBody.append(nextLine).append("\n");
                } else {
                    break; // Hit a non-indented line (outside class)
                }
            }
        }

        if (classBody.length() == 0) {
            throw new IllegalArgumentException("Class code not found for " + className);
        }

        return classBody.toString().trim();
    }
}