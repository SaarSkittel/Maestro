package com.hit.maestro;

import java.io.Serializable;
import java.util.Map;

public class ChatMessage  {
    private String message;
    private String sender;
    private Integer viewType;
    private String image;

    public ChatMessage(){}
    public ChatMessage(Map<String,Object> map){
        this.message = (String)map.get("message");
        this.sender = (String)map.get("sender");
        this.viewType=(Integer)map.get("viewType");
        this.image = (String)map.get("image");
    }
    public ChatMessage(String message, String sender, String UID, String image) {
        this.message = message;
        if(sender.matches(User.getInstance().getFullName())){
            this.sender="Me";
        }
        else this.sender = sender;
        if(UID.matches(User.getInstance().getUID())) {
            this.viewType = 0;
        }
        else this.viewType=1;
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

    public Integer getViewType() {
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
