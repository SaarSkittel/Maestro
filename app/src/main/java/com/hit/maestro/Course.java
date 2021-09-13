package com.hit.maestro;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    String name;
    String lecturer;
    String image;
    String description;
    List<Subject> subjects;

    public Course() {

    }

    public Course(String name, String lecturer, String image, String description, List<Subject> subjects) {
        this.name = name;
        this.lecturer = lecturer;
        this.image = image;
        this.description = description;
        this.subjects = subjects;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
