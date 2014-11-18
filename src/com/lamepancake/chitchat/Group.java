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
public class Group {
    
    /**
     * The id number of the group.
     */
    private final int groupID;
    
    /**
     * The name of the group.
     */
    private final String groupName;
    
    /**
     * The founder of the group.
     */
    private final User founder;
    
    /**
     * List of Chats associated with the group.
     */
    private final List<Chat> chats;
    
    /**
     * Constructor of an group
     * @param name The name of the group.
     * @param u The founder of the group.
     */
    public Group(String name, User u)
    {
        this.groupName = name;
        this.groupID = -1;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    /**
     * Constructor for the group.
     * @param name The name of the group.
     * @param id The unique id of the group.
     * @param u The founder of the group.
     */
    public Group(String name, int id, User u)
    {
        this.groupName = name;
        this.groupID = id;
        this.chats = new ArrayList();
        this.founder = u;
    }
    
    /**
     * Gets the name of the group.
     * @return the name of the group.
     */
    public String getName()
    {
        return groupName;
    }
    
    /**
     * Gets the id of the group.
     * @return the id of the group.
     */
    public int getID()
    {
        return groupID;
    }
    
    /**
     * Gets the founder of the group.
     * @return the founder of the group.
     */
    public User getFounder()
    {
        return founder;
    }
    
    /**
     * Adds a chat to the list of associated chats to that group.
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
