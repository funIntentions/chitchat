/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
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
    
    /**
     * Determine if a user is in this chat.
     * @param userID the id of the user.
     * @return true if the user is in the chat, false if they are not.
     */
    public boolean HasUser(int userID)
    {
        Set<SelectionKey>       userChannels;
                
        userChannels = users.keySet();
        
        for(SelectionKey curKey : userChannels)
        {
            User user = users.get(curKey);
            
            if (user.getID() == userID)
            {
                return true;
            }
        }
        
        return false;
    }
    
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
     * Announces to all connected users in chat that a user has joined.
     * 
     * @param key The key associated with the joining user.
     * @param u   A User object containing the user's information.
     */
    private void announceJoin(SelectionKey key, User u)
    {
        Set<SelectionKey> userChannels;
        //JoinedPacket      join         = new JoinedPacket(u, chatID);
        
        userChannels = users.keySet();
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        //broadcast(key, join, false, chatID);
    }
    
    /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserList(SelectionKey clientKey)
    {
        ArrayList<User>             userList;
        Set<SelectionKey>           keys;
        WhoIsInPacket               packet;
        Map<SelectionKey, User>     userMap;
        int                size = 4; // One extra int for the number of users
                
        userList = new ArrayList<>(users.size());
        keys = users.keySet();
        userMap = users;
        
        for (SelectionKey key : keys)
        {
            User u = userMap.get(key);

            // Add space for the user's name and three ints (name length, role, id)
            size += u.getName().length() * 2;
            size += 12;
            
            userList.add(u);
        }
        
        //packet = new WhoIsInPacket(userList, size, WhoIsInPacket.CONNECTED, chatID);
        /*try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendUserList: Could not send list: " + e.getMessage());
        }*/
    }
    
    
    
    /**
     * Adds a user to the chat so they receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void subscribeUser(SelectionKey key, User user)
    {
        users.put(key, user);
        
        // inform the user they are now in the chat.
//        try {
//            SocketChannel channel = (SocketChannel)key.channel();
//            channel.write(userInfo.serialise());              
//        } catch (IOException e) {
//            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
//        }
        
        // Send a list of connected clients immediately after being added to the chat.
        sendUserList(key);
        
        announceJoin(key, user); /////////////////TEMPORARY
    }
    
    /**
     * Removes a user from the chat so they don't receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void unsubscribeUser(SelectionKey key, User user)
    {
        //users.remove(key, user);
        
        // inform the user they are now in the chat.
//        try {
//            SocketChannel channel = (SocketChannel)key.channel();
//            channel.write(userInfo.serialise());              
//        } catch (IOException e) {
//            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
//        }
        
        // Send a list of connected clients immediately after being added to the chat.
//        sendUserList(key);
//        
//        announceJoin(key, user); /////////////////TEMPORARY
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