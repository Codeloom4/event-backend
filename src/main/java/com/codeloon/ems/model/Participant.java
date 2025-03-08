package com.codeloon.ems.model;

public class Participant {
    private String name;
    private int age;
    private String jobRole;

    public Participant(String name, int age, String jobRole) {
        this.name = name;
        this.age = age;
        this.jobRole = jobRole;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
}