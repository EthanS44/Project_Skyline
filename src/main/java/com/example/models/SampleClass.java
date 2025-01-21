package com.example.models;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SampleClass {

    // Fields
    private int id;
    private String name;
    private List<String> tags;
    private static final String DEFAULT_NAME = "Unnamed";

    // Constructor
    public SampleClass(int id, String name) {
        this.id = id;
        this.name = name != null ? name : DEFAULT_NAME;
        this.tags = new ArrayList<>();
    }

    // Overloaded Constructor
    public SampleClass() {
        this(0, DEFAULT_NAME);
    }

    // Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        if (tag != null && !tag.isBlank()) {
            this.tags.add(tag);
        }
    }

    public String listTags() {
        return tags.stream().collect(Collectors.joining(", "));
    }

    public int countTags() {
        return tags.size();
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    // Static Utility Method
    public static String getDefaultName() {
        return DEFAULT_NAME;
    }

    // Cyclomatic Complexity Example
    public String categorizeTags() {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            if (tag.length() < 3) {
                sb.append(tag).append(": Short\n");
            } else if (tag.length() < 6) {
                sb.append(tag).append(": Medium\n");
            } else {
                sb.append(tag).append(": Long\n");
            }
        }
        return sb.toString();
    }

    // Inheritance and Associations
    public static class InnerClass {
        private String description;

        public InnerClass(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}