/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Sent to request or transmit a list of all users in a given chat.
 * 
 * The WhoIsInPacket will be sent to a client upon joining a chat. It contains a
 * list of all users, online or offline, who have access to the chat.
 * 
 * The user will send a packet with only the chat ID and their user ID to the
 * server, and the server will construct a list of all users and their roles in
 * the chat if the chat exists.
 * 
 * Unless it is explicitly requested later, the WhoIsInPacket will not be sent
 * to the user after their initial join. All other updates in regards to users'
 * statuses in the chat will be handled by the UserNotifyPacket. Note also that
 * the server will send this packet unsolicited when the user joins, so clients
 * must be prepared to handle this.
 * 
 * @author shane
 */
public class WhoIsInPacket extends Packet {
    
    /**
     * The list of users in the requested list.
     */
    //private List<User> users;
    
    //private List<User> onlineUsers;
    
    private Map<User, Boolean> users;
    
    /**
     * ID of the chat that's being requested for information.
     */
    private final int chatID;
    
    /**
     * ID of the user requesting or receiving the chat list.
     */
    private final int userID;
    
    /**
     * Construct a WhoIsInPacket to be sent to the server.
     * 
     * @param chatID The ID of the chat from which to get the list of users.
     * @param userID The user requesting the list.
     */
    public WhoIsInPacket(int chatID, int userID)
    {
        // The 4 is already added in the other constructor, so don't repeat it here
        this(null, 0, chatID, userID);
    }
    
    /**
     * Construct a WhoIsIn packet containing a list of users to be sent to
     * the requesting client.
     * 
     * @param users  The list of all users for the specified chat.
     * @param length The length, in bytes, of the data portion of the packet.
     * @param chatID The ID of the chat containing the user list.
     * @param userID The ID of the user requesting or receiving the list.
     */
    public WhoIsInPacket(Map<User, Boolean> users, int length, int chatID, int userID)
    {
        super(WHOISIN, length + 12);
        this.users = users;
        this.chatID = chatID;
        this.userID = userID;
    }
    
    /**
     * Construct a WhoIsInPacket a serialised WhoIsInPacket.
     * 
     * @param header The serialised packet header.
     * @param data   The serialised data.
     */
    public WhoIsInPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        int listLen;
        
        this.chatID = data.getInt();
        this.userID = data.getInt();
        
        listLen = data.getInt();
        
        if(listLen > 0)
        {
            this.users = new HashMap<>(listLen);

            for(int i = 0; i < listLen; i++)
            {
                int     nameLen  = data.getInt();
                int     role;
                int     id;
                byte    onlineStatus;
                byte[]  rawName  = new byte[nameLen];
                String  username;
                data.get(rawName);
                username = new String(rawName, StandardCharsets.UTF_16LE);
                role = data.getInt();
                id = data.getInt();
                onlineStatus = data.get();
                
                if(onlineStatus == 1)
                {
                    users.put(new User().setName(username).setRole(role).setID(id), true); 
                }
                else
                {
                    users.put(new User().setName(username).setRole(role).setID(id), false);  
                }
            }
        }
    }
    
    /**
     * Get the list of users contained in this WhoIsInPacket.
     * 
     * @return The list of users in this WhoIsInPacket. 
     */
    public Map<User, Boolean> getUsers()
    {
        return this.users;
    }
       
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.userID);

        if(this.users == null)
        {
            buf.putInt(0);
            buf.rewind();
            return buf;
        }
        
        // Structure is: {numUsers}[{nameLength}{name}{role}{id}{onlineStatus}]
        buf.putInt(this.users.size());
        Set<User> keys = users.keySet();
        
        for(User u : keys)
        {
            String username = u.getName();

            buf.putInt(username.length() * 2);
            buf.put(username.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(u.getRole());
            buf.putInt(u.getID());
            Byte b;
            if(users.get(u))
            {
                b = 1;
            }
            else
            {
                b = 0;
            }
            buf.put(b);
        }
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the chat ID for the list of users.
     * @return The chat ID for the list of users.
     */
    public int getChatID()
    {
        return this.chatID;
    }
    
    /**
     * Gets the user ID of the user requesting/receiving the list.
     * @return The user ID of the user requesting/receiving the list.
     */
    public int getUserID()
    {
        return this.userID;
    }
}
