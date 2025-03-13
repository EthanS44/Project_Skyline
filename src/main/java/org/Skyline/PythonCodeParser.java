package org.Skyline;

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
        Pattern pattern = Pattern.compile("self\\.\\w+");
        Matcher matcher = pattern.matcher(code);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
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
        return (int) code.lines()
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                .count();
    }

    @Override
    public int calculateInheritanceDepth(String code) {
        Pattern pattern = Pattern.compile("class\\s+\\w+\\s*\\((.*?)\\)");
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
        throw new IllegalArgumentException("No valid class found in the given Python code.");
    }

    @Override
    public Attributes generateModelAttributes(String code) {return null;}

    @Override
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
                attributes.setNumberOfFields(countFields(classCode));
                attributes.setNumberOfMethods(countMethods(classCode));
                attributes.setInheritanceDepth(calculateInheritanceDepth(classCode));

                attributesList.add(attributes);
            }
            return attributesList;
        }
        throw new IllegalArgumentException("Not a valid Python class!");
    }

    private String extractClassCode(String code, String className) {
        // Regex to find the start of the class definition
        Pattern pattern = Pattern.compile("class\\s+" + className + "(\\s*\\(.*?\\))?\\s*:(.*?)\\n(?=\\n|class\\s+|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(2).trim(); // Return the class content
        }
        throw new IllegalArgumentException("Class code not found for " + className);
    }
}