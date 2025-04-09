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
        Pattern methodPattern = Pattern.compile(
                "\\b(?:[\\w:\\*&<>]+)\\s+(\\w+)\\s*\\([^)]*\\)\\s*(\\{|;)"
        );
        Matcher matcher = methodPattern.matcher(code);
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

    @Override
    public int calculateMaximumCyclomaticComplexity(String code) {
        int maxComplexity = 0;
        int index = 0;

        while (index < code.length()) {
            // Look for method-like pattern with `(`
            int parenIndex = code.indexOf("(", index);
            if (parenIndex == -1) break;

            // Try to find opening brace after it
            int braceIndex = code.indexOf("{", parenIndex);
            if (braceIndex == -1) break;

            // Now do brace tracking from that point
            int start = braceIndex;
            int braceCount = 0;
            int end = -1;

            for (int i = start; i < code.length(); i++) {
                char c = code.charAt(i);
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                if (braceCount == 0) {
                    end = i + 1;
                    break;
                }
            }

            if (end > start) {
                String methodBody = code.substring(start, end);
                System.out.println("---- METHOD BODY ----");
                System.out.println(methodBody);
                System.out.println("---------------------");

                int complexity = calculateCyclomaticComplexityForMethod(methodBody);
                maxComplexity = Math.max(maxComplexity, complexity);
                index = end;
            } else {
                // Move past the current position to avoid infinite loop
                index = parenIndex + 1;
            }
        }

        return maxComplexity;
    }

    public int calculateCyclomaticComplexityForMethod(String method) {
        int complexity = 1; // Basic complexity starts at 1
        Pattern patternIf = Pattern.compile("if\\s*\\(");
        Pattern patternElseIf = Pattern.compile("else\\s+if\\s*\\(");
        Pattern patternFor = Pattern.compile("for\\s*\\(");
        Pattern patternWhile = Pattern.compile("while\\s*\\(");
        Pattern patternSwitch = Pattern.compile("switch\\s*\\(");
        Pattern patternCase = Pattern.compile("case\\s+");
        Pattern patternCatch = Pattern.compile("catch\\s*\\(");

        Matcher matcherIf = patternIf.matcher(method);
        Matcher matcherElseIf = patternElseIf.matcher(method);
        Matcher matcherFor = patternFor.matcher(method);
        Matcher matcherWhile = patternWhile.matcher(method);
        Matcher matcherSwitch = patternSwitch.matcher(method);
        Matcher matcherCase = patternCase.matcher(method);
        Matcher matcherCatch = patternCatch.matcher(method);

        complexity += countPatternMatches(matcherIf);
        complexity += countPatternMatches(matcherElseIf);
        complexity += countPatternMatches(matcherFor);
        complexity += countPatternMatches(matcherWhile);
        complexity += countPatternMatches(matcherSwitch);
        complexity += countPatternMatches(matcherCase);
        complexity += countPatternMatches(matcherCatch);

        return complexity;
    }

    public int countPatternMatches(Matcher matcher) {
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
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

