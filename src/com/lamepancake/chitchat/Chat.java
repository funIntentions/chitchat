/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dan
 */
public class Chat 
{
    private final int    chatID;
    private String chatName;
    
    /**
     * A map relating sockets to users who are in the chat.
     */
    private final Map<SelectionKey, User> users;
    
    /**
     * A map relating sockets to users who are waiting to enter the chat.
     */
    private final Map<SelectionKey, User> waitingUsers;
    
    public Chat(String name, int id)
    {
        this.chatName = name;
        this.chatID = id;
        this.users = new HashMap<>();
        this.waitingUsers = new HashMap<>();
    }
    
    
    public Map<SelectionKey, User> getConnectedUsers()
    {
        return users;
    }
    
    public Map<SelectionKey, User> getWaitingUsers()
    {
        return waitingUsers;
    }
    
    public String getName()
    {
        return chatName;
    }
    
    public Integer getID()
    {
        return chatID;
    }
}
