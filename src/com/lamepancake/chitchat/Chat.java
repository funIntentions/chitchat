/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.BootPacket;
import com.lamepancake.chitchat.packet.ChangeRolePacket;
import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private final Map<User, Boolean> users;
    
    public Chat(String name, int id)
    {
        this.chatName = name;
        this.chatID = id;
        this.users = new HashMap<>();
    }
    
    public void bootUser(BootPacket b)
    {
        PacketCreator.createUserNotify();
    }
    
    public void promoteUser(ChangeRolePacket r)
    {
        r.getChatID();
        r.getRole();
        r.getUserID();
        PacketCreator.createUserNotify();
    }
    
    public void updateState(JoinLeavePacket jl)
    {
        PacketCreator.createJoinLeave();
        PacketCreator.createUserNotify();
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
     * Determine if a user is in this chat.
     * @param userID the id of the user.
     * @return true if the user is in the chat, false if they are not.
     */
//    public boolean HasUser(int userID)
//    {
//        Set<SelectionKey>       userChannels;
//                
//        userChannels = users.keySet();
//        
//        for(SelectionKey curKey : userChannels)
//        {
//            User user = users.get(curKey);
//            
//            if (user.getID() == userID)
//            {
//                return true;
//            }
//        }
//        
//        return false;
//    }
//    
    /**
     * Sends a chat message to all other users in the chat.
     * 
     * @param key     The SelectionKey associated with the sender.
     * @param message The message to be sent.
     * 
     * @todo Handle case where packet doesn't send completely
     */
    private void sendMessage(SelectionKey key, MessagePacket message)
    {
        Set<SelectionKey>       userChannels;
        User                    client;
        
        userChannels = users.keySet();
        client       = users.get(key);
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        // Sending a message without logging in? Nope
        if(client == null)
            return;
        // Pretending to be someone else? Also nope
        else if(client.getID() != message.getUserID())
            message.setUserID(client.getID());
         
        broadcast(key, message, false, message.getChatID());
    }
    
    /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserList(WhoIsInPacket w)
    {
        PacketCreator.createWhoIsIn(users, chatID, w.getUserID());
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
     * @param key The key associated with the sending user.
     * @param p The packet to be broadcasted.
     * @param broadcast True if you want to include the sending user in the broadcast, false otherwise.
     */
    private void broadcast(SelectionKey key, Packet p, Boolean broadcast, int chatID)
    {        
        Set<SelectionKey> userChannels = users.keySet();
        
        for(SelectionKey curKey : userChannels)
        {
            User user = users.get(curKey);
            
            if (user.getRole() != User.UNSPEC)
            {
                // Don't notify the message sending user what they have sent
                if(curKey.equals(key))
                    if(!broadcast)
                        continue;

                try {
                    SocketChannel channel = (SocketChannel)curKey.channel();
                    channel.write(p.serialise());              
                } catch (IOException e) {
                    System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
                }   
            }
        }
    }
}