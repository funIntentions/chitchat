/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tware
 */
public class Organization {
    
    private int organizationID;
    private String organizationName;
    private User founder;
    private List<Chat> chats;
    
    public Organization(String name, User u)
    {
        this.organizationName = name;
        this.organizationID = -1;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    public Organization(String name, int id, User u)
    {
        this.organizationName = name;
        this.organizationID = id;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    public String getName()
    {
        return organizationName;
    }
    
    public int getID()
    {
        return organizationID;
    }
    
    public User getFounder()
    {
        return founder;
    }
    
    public void addChat(Chat c)
    {
       chats.add(c);
    }
    
    public void deleteChat(Chat c)
    {
        for(Chat chat : chats)
        {
            if(chat.getID().equals(c.getID()))
            {
                chats.remove(chat);
                break;
            }
        }
    }
    
    public void updateChat(Chat c)
    {
        for(Chat chat : chats)
        {
            if(chat.getID().equals(c.getID()))
            {
                chat.setID(c.getID());
                chat.setName(c.getName());
                break;
            }
        }
    }
    
    public List<Chat> getChats()
    {
        return chats;
    }
    
    public Chat getChat(int chatid)
    {
        Chat temp = new Chat("default", -1);
        
        for(Chat c : chats)
        {
            if(c.getID() == chatid)
            {
                temp = c;
                break;
            }
        }
        return temp;
    }
}
