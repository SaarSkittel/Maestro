package com.hit.maestro;

import java.io.Serializable;
import java.util.List;

public class Subject implements Serializable {
    private String nameSubject;
    private List<Lesson> lessons;

    public Subject(){}
    public Subject(String nameSubject, List<Lesson> lessons) {
        this.nameSubject = nameSubject;
        this.lessons = lessons;
    }

    public String getNameSubject() {
        return nameSubject;
    }

    public void setNameSubject(String nameSubject) {
        this.nameSubject = nameSubject;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
