/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.mediator.Event;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dan
 */
public class Chat 
{
    private final int chatID;
    private String chatName;
    
    /**
     * A map relating sockets to users who are in the chat.
     */
    private final Map<SelectionKey, User> users;
    
    public Chat(String name, int id)
    {
        this.chatName = name;
        this.chatID = id;
        this.users = new HashMap<>();
    }
    
    public Map<SelectionKey, User> getConnectedUsers()
    {
        return users;
    }
    
    public String getName()
    {
        return chatName;
    }
    
    public void setName(String name)
    {
        chatName = name;
    }
    
    public Integer getID()
    {
        return chatID;
    }
    
    public void update(Event e)
    {
        int type = e.getType();
        
        switch(type)
        {
            case Event.LOGIN:
                break;
            case Event.LOGOUT:
                break;
            case Event.MESSAGE:
                break;
            case Event.JOIN:
                break;
            case Event.LEAVE:
                break;
            case Event.GRANT:
                break;
            default:
                System.out.println("Unknown event " + type);
        }
    }
}
