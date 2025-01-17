package com.example.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a person with basic attributes and behaviors.
 */
public class Person {
    // Fields
    private String name;
    private int age;
    private List<String> hobbies;

    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        this.hobbies = new ArrayList<>();
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for age
    public int getAge() {
        return age;
    }

    // Setter for age
    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    // Add a hobby
    public void addHobby(String hobby) {
        if (hobby != null && !hobby.isEmpty()) {
            hobbies.add(hobby);
        }
    }

    // Get hobbies
    public List<String> getHobbies() {
        return hobbies;
    }

    // Method to display person's details
    public void printDetails() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Hobbies: " + String.join(", ", hobbies));
    }

    // Nested class
    public static class Address {
        private String street;
        private String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return street + ", " + city;
        }
    }
}
