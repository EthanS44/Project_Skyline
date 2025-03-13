package org.Skyline;

import java.util.List;
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
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("//"))
                .count();
    }

    @Override
    public int calculateInheritanceDepth(String code) {
        Pattern pattern = Pattern.compile("class\\s+\\w+\\s*:\\s*(public|private|protected)\\s+\\w+");
        Matcher matcher = pattern.matcher(code);
        int depth = 0;
        while (matcher.find()) {
            depth++;
        }
        return depth;
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
    public List<Attributes> generateModelAttributesList(String code) {return null;}

    @Override
    public Attributes generateModelAttributes(String code) {
        if (isValidClass(code)) {
            Attributes attributes = new Attributes();
            attributes.setName(findClassName(code));
            attributes.setLinesOfCode(countLinesOfCode(code));
            attributes.setNumberOfFields(countFields(code));
            attributes.setNumberOfMethods(countMethods(code));
            attributes.setInheritanceDepth(calculateInheritanceDepth(code));
            return attributes;
        }
        throw new IllegalArgumentException("Not a valid C++ class!");
    }
}

