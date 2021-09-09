package com.hit.maestro.fragments;

public class ChatMessage {
    private String message;
    private String sender;
    private int viewType;
    private String image;

    public ChatMessage(String message, String sender, int viewType, String image) {
        this.message = message;
        this.sender = sender;
        this.viewType = viewType;
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
