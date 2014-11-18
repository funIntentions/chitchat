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
    
    /**
     * The id number of the organization.
     */
    private final int organizationID;
    
    /**
     * The name of the organization.
     */
    private final String organizationName;
    
    /**
     * The founder of the organization.
     */
    private final User founder;
    
    /**
     * List of Chats associated with the organization.
     */
    private final List<Chat> chats;
    
    /**
     * Constructor of an organization
     * @param name The name of the organization.
     * @param u The founder of the organization.
     */
    public Organization(String name, User u)
    {
        this.organizationName = name;
        this.organizationID = -1;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    /**
     * Constructor for the organization.
     * @param name The name of the organization.
     * @param id The unique id of the organization.
     * @param u The founder of the organization.
     */
    public Organization(String name, int id, User u)
    {
        this.organizationName = name;
        this.organizationID = id;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    /**
     * Gets the name of the organization.
     * @return the name of the organization.
     */
    public String getName()
    {
        return organizationName;
    }
    
    /**
     * Gets the id of the organization.
     * @return the id of the organization.
     */
    public int getID()
    {
        return organizationID;
    }
    
    /**
     * Gets the founder of the organization.
     * @return the founder of the organization.
     */
    public User getFounder()
    {
        return founder;
    }
    
    /**
     * Adds a chat to the list of associated chats to that organization.
     * @param c the chat to add to the list.
     */
    public void addChat(Chat c)
    {
       chats.add(c);
    }
    
    /**
     * Deletes a particular chat from the list of chats.
     * @param c the particular chat to delete from the list.
     */
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
    
    /**
     * Updates a particular chat in the list of chats.
     * @param c the chat to update.
     */
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
    
    /**
     * Gets the list of chats.
     * @return the list of chats.
     */
    public List<Chat> getChats()
    {
        return chats;
    }
    
    /**
     * Gets a particular chat.
     * @param chatid the id of the desired chat.
     * @return the chat from the list.
     */
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
