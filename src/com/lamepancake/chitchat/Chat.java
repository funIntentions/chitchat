/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.DAO.ChatDAO;
import com.lamepancake.chitchat.DAO.ChatDAOMySQLImpl;
import com.lamepancake.chitchat.DAO.ChatRoleDAO;
import com.lamepancake.chitchat.DAO.ChatRoleDAOMySQLImpl;
import com.lamepancake.chitchat.packet.BootPacket;
import com.lamepancake.chitchat.packet.ChangeRolePacket;
import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.sql.SQLException;
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
    private ChatRoleDAO crDao;
    private ChatDAO cDao;
    
    /**
     * A map of users and their online status.
     */
    private final Map<User, Boolean> users;
    
    public Chat(String name)
    {
        this.chatName = name;
        this.chatID = -1;
        this.users = new HashMap<>();
    }

    /**
     * Creates a new chat.
     * 
     * @todo Differentiate between client and server chats.
     * @param name
     * @param id 
     */
    public Chat(String name, int id)
    {
        this.chatName = name;
        this.chatID = id;
        this.users = new HashMap<>();
        
        try {
            cDao = ChatDAOMySQLImpl.getInstance();
            crDao = ChatRoleDAOMySQLImpl.getInstance();
        } catch (SQLException se) {
            System.err.println("Chat constructor: could not get database access: " + se.getMessage());
        }
    }
    
    /**
     * Routes packets to the appropriate function for processing.
     * 
     * @param packet The packet to process.
     * @todo Implement this...
     */
    public void handlePacket(Packet packet)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * 
     * @param b 
     */
    private void bootUser(BootPacket b)
    {
        int affectedUserID = b.getBootedID();
        Packet p;
        
        // Remove the user from the map
        for(User u: users.keySet())
        {
            if(u.getID() == affectedUserID)
            {
                users.remove(u);
                break;
            }
        }

        p = PacketCreator.createUserNotify(affectedUserID, this.chatID, User.UNSPEC, Packet.BOOT);
        broadcast(b.getBooterID(), p, false);
    }
    
    private void promoteUser(ChangeRolePacket r)
    {
        Packet p = PacketCreator.createUserNotify(r.getUserID(), r.getChatID(), r.getRole(), Packet.CHANGEROLE);
        for(User u : users.keySet())
        {
            if(u.getID() == r.getUserID())
            {
                ((ServerUser)u).notifyClient(r);
                break;
            }
        }
        broadcast(r.getUserID(), p, false);
    }
    
    /**
     * Removes or adds a User to the list of subscribed users and notifies other
     * Users in the chat.
     * @param jl The JoinLeave packet specifying whether the user is joining or
     *           leaving.
     */
    private void updateState(JoinLeavePacket jl)
    {
        final Packet p;
        User affected = null;
        
        for(User u : users.keySet())
        {
            if(u.getID() == jl.getUserID())
            {
                affected = u;
                break;
            }
        }
        
        if(affected == null)
            return;
        
        if(jl.getFlag() == JoinLeavePacket.JOIN)
        {
            users.put(affected, Boolean.TRUE);
            ((ServerUser)affected).notifyClient(jl);
        }
        else
            users.put(affected, Boolean.FALSE);
        
        p = PacketCreator.createUserNotify(jl.getUserID(), this.chatID, affected.getRole(), jl.getFlag());
        broadcast(jl.getUserID(), p, false);
    }
    
    public Map<User, Boolean> getConnectedUsers()
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
 
    /**
     * Sends a chat message to all other users in the chat.
     * 
     * @param message The message to be sent.
     * 
     * @todo Handle case where packet doesn't send completely
     */
    private void sendMessage(MessagePacket message)
    {      
        // If they're the only person in the chat, don't bother sending the message
        if(users.size() <= 1)
            return;
         
        broadcast(message.getUserID(), message, false);
    }
    
    /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserList(WhoIsInPacket w)
    {
        User sender = null;
        final Packet p;

        for(User u : users.keySet())
        {
            if(w.getUserID() == u.getID())
            {
                sender = u;
                break;
            }
        }
        
        if(sender == null)
            return;
      
        p = PacketCreator.createWhoIsIn(users, chatID, w.getUserID());
        ((ServerUser)sender).notifyClient(p);
    }
    
    /**
     * Adds a user to the chat so they receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void subscribeUser(User user)
    {
        users.put(user, true);
    }
    
    /**
     * Removes a user from the chat so they don't receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void unsubscribeUser(User user)
    {
        users.put(user, false);
    }
           
    /**
     * Broadcasts the passed in message to all connected users.
     * 
     * @param senderID The key associated with the sending user.
     * @param p        The packet to be broadcasted.
     * @param sendBack Whether the user should have the packet sent back to them.
     */
    private void broadcast(int senderID, Packet p, boolean sendBack)
    {        
        for(User u : users.keySet())
        {
            Boolean isOnline = users.get(u);
            if (isOnline && u.getRole() != User.WAITING)
            {
                // Don't send the packet back to the snder unless they want that
                if(u.getID() == senderID && !sendBack)
                    continue;
                
                ((ServerUser)u).notifyClient(p);
            }
        }
    }
}