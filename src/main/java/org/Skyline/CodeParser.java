package org.Skyline;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CodeParser {
    public abstract boolean isValidClass(String code);
    public abstract int countFields(String code);
    public abstract int countMethods(String code);
    public abstract int countLinesOfCode(String code);
    public abstract int calculateInheritanceDepth(String code);
    public abstract String findClassName(String code);
    public abstract Attributes generateModelAttributes(String code);
    public abstract List<Attributes> generateModelAttributesList(String code);
}
