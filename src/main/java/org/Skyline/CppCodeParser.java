package org.Skyline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CppCodeParser extends CodeParser {

    @Override
    public boolean isValidClass(String code) {
        return code.contains("class ");
    }

    @Override
    public int countFields(String code) {
        Pattern pattern = Pattern.compile("\\b(int|double|float|char|string|bool)\\s+\\w+;");
        Matcher matcher = pattern.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public int countMethods(String code) {
        Pattern pattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_<>]*\\s+[a-zA-Z_][a-zA-Z0-9_<>]*\\s*\\(.*?\\)\\s*\\{");
        Matcher matcher = pattern.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public int countLinesOfCode(String code) {
        // Count all lines, including blanks and comments
        return (int) code.lines()
                .count();
    }

    @Override
    public int countLinesOfCodeNoBlanks(String code) {
        // Count non-empty, non-comment lines
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                .count();
    }

    @Override
    public String findClassName(String code) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No valid class found in the given C++ code.");
    }

    private int calculateCyclomaticComplexityForMethod(String code, String method) {
        int complexity = 1; // Basic complexity starts at 1
        Pattern patternIf = Pattern.compile("if\\s*\\(");
        Pattern patternFor = Pattern.compile("for\\s*\\(");
        Pattern patternWhile = Pattern.compile("while\\s*\\(");
        Pattern patternSwitch = Pattern.compile("switch\\s*\\(");
        Pattern patternCatch = Pattern.compile("catch\\s*\\(");

        Matcher matcherIf = patternIf.matcher(method);
        Matcher matcherFor = patternFor.matcher(method);
        Matcher matcherWhile = patternWhile.matcher(method);
        Matcher matcherSwitch = patternSwitch.matcher(method);
        Matcher matcherCatch = patternCatch.matcher(method);

        complexity += countPatternMatches(matcherIf);
        complexity += countPatternMatches(matcherFor);
        complexity += countPatternMatches(matcherWhile);
        complexity += countPatternMatches(matcherSwitch);
        complexity += countPatternMatches(matcherCatch);

        return complexity;
    }

    private int countPatternMatches(Matcher matcher) {
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    public int calculateMaximumCyclomaticComplexity(String code) {
        int maxComplexity = 0;
        Pattern patternMethod = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_<>]*\\s+[a-zA-Z_][a-zA-Z0-9_<>]*\\s*\\(.*?\\)\\s*\\{");
        Matcher methodMatcher = patternMethod.matcher(code);
        while (methodMatcher.find()) {
            String method = code.substring(methodMatcher.start(), methodMatcher.end());
            int methodComplexity = calculateCyclomaticComplexityForMethod(code, method);
            maxComplexity = Math.max(maxComplexity, methodComplexity);
        }
        return maxComplexity;
    }

    @Override
    public double calculateAverageLinesPerMethod(String code) {
        Pattern patternMethod = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_<>]*\\s+[a-zA-Z_][a-zA-Z0-9_<>]*\\s*\\(.*?\\)\\s*\\{");
        Matcher methodMatcher = patternMethod.matcher(code);
        int totalLines = 0;
        int methodCount = 0;

        while (methodMatcher.find()) {
            int methodStart = methodMatcher.start();
            int methodEnd = methodMatcher.end();
            String methodCode = code.substring(methodStart, methodEnd);
            totalLines += methodCode.split("\n").length;
            methodCount++;
        }

        return methodCount == 0 ? 0 : (double) totalLines / methodCount;
    }

    @Override
    public Attributes generateModelAttributes(String code) {
        if (isValidClass(code)) {
            Attributes attributes = new Attributes();
            attributes.setName(findClassName(code));
            attributes.setLinesOfCode(countLinesOfCode(code));
            attributes.setLinesOfCodeNoBlanks(countLinesOfCodeNoBlanks(code));
            attributes.setNumberOfFields(countFields(code));
            attributes.setNumberOfMethods(countMethods(code));
            attributes.setMaxCyclomaticComplexity(calculateMaximumCyclomaticComplexity(code));
            attributes.setAverageLinesPerMethod(calculateAverageLinesPerMethod(code));
            return attributes;
        }
        throw new IllegalArgumentException("Not a valid C++ class!");
    }
}

