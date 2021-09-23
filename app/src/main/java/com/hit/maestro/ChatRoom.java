package com.hit.maestro;
import com.hit.maestro.ChatMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoom {
    
    public static List<ChatMessage>ParseMap(HashMap<String,HashMap<String, Object>>hashMap){
        List<ChatMessage>chatMessages=new ArrayList<>();
        for(HashMap<String, Object> value : hashMap.values()){
            chatMessages.add(new ChatMessage(value));
        }
        return chatMessages;
    }
}
