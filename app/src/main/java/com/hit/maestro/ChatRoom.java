package com.hit.maestro;
import com.google.firebase.database.PropertyName;
import com.hit.maestro.ChatMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class ChatRoom implements Serializable {

    private HashMap<String,List<ChatMessage>> messages;

    public HashMap<String, List<ChatMessage>> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, List<ChatMessage>> messages) {
        this.messages = messages;
    }

    public ChatRoom() {
    }
    /*
    public static List<ChatMessage>ParseMap(HashMap<String,HashMap<String, Object>>hashMap){
        List<ChatMessage>chatMessages=new ArrayList<>();
        for(HashMap<String, Object> value : hashMap.values()){
            chatMessages.add(new ChatMessage(value));
        }
        return chatMessages;
    }*/
}
