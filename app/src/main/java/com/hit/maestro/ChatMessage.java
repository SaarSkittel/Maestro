package com.hit.maestro;

import java.io.Serializable;
import java.util.Map;

public class ChatMessage  {
    private String message;
    private String sender;
    private long viewType;
    private String image;

    public ChatMessage(){}
    public ChatMessage(Map<String,Object> map){
        this.message = (String)map.get("message");
        this.sender = (String)map.get("sender");
        this.viewType=(long)map.get("viewType");
        this.image = (String)map.get("image");
    }
    public ChatMessage(String message, String sender, String UID, String image) {
        this.message = message;
        if(UID.matches(User.getInstance().getUID())){
            this.sender="Me";
        }
        else this.sender = sender;
        if(UID.matches(User.getInstance().getUID())) {
            this.viewType = 1;
        }
        else this.viewType=2;
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

    public long getViewType() {
        return viewType;
    }

    public void setViewType(long viewType) {
        this.viewType = viewType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
