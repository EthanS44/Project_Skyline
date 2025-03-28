package org.Skyline;

import jakarta.persistence.*;

@Entity
public class Attributes {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String name;
    private String user;
    private int linesOfCode = 0;
    private int linesOfCodeNoBlanks = 0;
    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    private double averageLinesPerMethod = 0;
    private int maxCyclomaticComplexity = 0;
    private int inheritanceDepth = 0;
    private int numberOfAssociations = 0;
    private int numberOfImports = 0;
    private String classPackage = "";

    /*
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<String> associations = new ArrayList<String>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<String> imports = new ArrayList<String>();
    */

    public Attributes(String user){
        this.user = user;
    }
    public Attributes(){}

    public String getUser() {
        return user;
    }

    public String getName() {return name;}

    public void setName(String name){this.name = name;}

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getLinesOfCodeNoBlanks() {
        return linesOfCodeNoBlanks;
    }

    public void setLinesOfCodeNoBlanks(int linesOfCodeNoBlanks) {
        this.linesOfCodeNoBlanks = linesOfCodeNoBlanks;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int numberOfMethods) {
        this.numberOfMethods = numberOfMethods;
    }

    public int getMaxCyclomaticComplexity() {
        return maxCyclomaticComplexity;
    }

    public void setMaxCyclomaticComplexity(int maxCyclomaticComplexity) {
        this.maxCyclomaticComplexity = maxCyclomaticComplexity;
    }

    public int getInheritanceDepth() {
        return inheritanceDepth;
    }

    public void setInheritanceDepth(int inheritanceDepth) {
        this.inheritanceDepth = inheritanceDepth;
    }

    public int getNumberOfAssociations() {
        return numberOfAssociations;
    }

    public void setNumberOfAssociations(int numberOfAssociations) {
        this.numberOfAssociations = numberOfAssociations;
    }

    public int getNumberOfImports() {
        return numberOfImports;
    }

    public void setNumberOfImports(int numberOfImports) {
        this.numberOfImports = numberOfImports;
    }

    public String getClassPackage() {
        return classPackage;
    }

    public void setClassPackage(String classPackage) {
        this.classPackage = classPackage;
    }

    public double getAverageLinesPerMethod() {
        return averageLinesPerMethod;
    }

    public void setAverageLinesPerMethod(double averageLinesPerMethod) {
        this.averageLinesPerMethod = averageLinesPerMethod;
    }

    @Override
    public String toString() {
        return "Attributes{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user='" + user + '\'' +
                ", linesOfCode=" + linesOfCode +
                ", linesOfCodeNoBlanks=" + linesOfCodeNoBlanks +
                ", numberOfFields=" + numberOfFields +
                ", numberOfMethods=" + numberOfMethods +
                ", averageLinesPerMethod=" + averageLinesPerMethod +
                ", maxCyclomaticComplexity=" + maxCyclomaticComplexity +
                ", inheritanceDepth=" + inheritanceDepth +
                ", numberOfAssociations=" + numberOfAssociations +
                ", numberOfImports=" + numberOfImports +
                ", classPackage='" + classPackage + '\'' +
                '}';
    }
}
