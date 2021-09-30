package com.hit.maestro;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class ChatMessage implements Serializable {
    private String message;
    private String sender;
    //private long viewType;
    private String image;
    private String UID;
    private String time;

    public ChatMessage(){}

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ChatMessage(Map<String,Object> map){
        this.message = (String)map.get("message");
        this.sender = (String)map.get("sender");
        this.UID=(String)map.get("uid");
        this.image = (String)map.get("image");
        this.time=(String)map.get("time");
    }
    public ChatMessage(String message, String sender, String UID, String image) {
        this.message = message;
        this.UID=UID;
        this.sender = sender;
        this.image = image;
        this.time=LocalDateTime.now().toString();
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
