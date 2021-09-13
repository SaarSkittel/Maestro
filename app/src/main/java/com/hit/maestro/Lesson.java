package com.hit.maestro;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String nameLesson;
    private String chatTitle;
    private String url;

    public Lesson(){}
    public Lesson(String nameLesson, String chatTitle, String url) {
        this.nameLesson = nameLesson;
        this.chatTitle = chatTitle;
        this.url = url;
    }

    public String getNameLesson() {
        return nameLesson;
    }

    public void setNameLesson(String nameLesson) {
        this.nameLesson = nameLesson;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
