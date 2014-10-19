/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    private List<User> users;
    
    private List<User> onlineUsers;
        
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
        this(null, 0, null, chatID, userID);
    }
    
    /**
     * Construct a WhoIsIn packet containing a list of users to be sent to
     * the requesting client.
     * 
     * @param users  The list of all users for the specified chat.
     * @param length The length, in bytes, of the data portion of the packet.
     * @param onlineUsers The list of all users that are online in the specified chat.
     * @param chatID The ID of the chat containing the user list.
     * @param userID The ID of the user requesting or receiving the list.
     */
    public WhoIsInPacket(List<User> users, int length, List<User> onlineUsers, int chatID, int userID)
    {
        super(WHOISIN, length + (4 * users.size()) + 12);
        this.users = users;
        this.onlineUsers = onlineUsers;
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
            this.users = new ArrayList<>(listLen);
            this.onlineUsers = new ArrayList<>();

            for(int i = 0; i < listLen; i++)
            {
                int     nameLen  = data.getInt();
                int     role;
                int     id;
                int     onlineStatus;
                byte[]  rawName  = new byte[nameLen];
                String  username;
                data.get(rawName);
                username = new String(rawName, StandardCharsets.UTF_16LE);
                role = data.getInt();
                id = data.getInt();
                onlineStatus = data.getInt();
                
                if(onlineStatus == 1)
                {
                    onlineUsers.add(new User().setName(username).setRole(role).setID(id));
                }

                users.add(new User().setName(username).setRole(role).setID(id));            
            }
        }
    }
    
    /**
     * Get the list of users contained in this WhoIsInPacket.
     * 
     * @return The list of users in this WhoIsInPacket. 
     */
    public List<User> getUsers()
    {
        return this.users;
    }
    
    /**
     * Get the list of online users contained in this WhoIsInPacket.
     * 
     * @return The list of online users in this WhoIsInPacket. 
     */
    public List<User> getOnlineUsers()
    {
        return this.onlineUsers;
    }
    
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.userID);

        if(this.users == null)
        {
            buf.rewind();
            return buf;
        }
        
        // Structure is: {numUsers}[{nameLength}{name}{role}{id}{onlineStatus}]
        buf.putInt(this.users.size());
        for(User u : this.users)
        {
            boolean isOnline = false;
            String username = u.getName();

            buf.putInt(username.length() * 2);
            buf.put(username.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(u.getRole());
            buf.putInt(u.getID());
            
            for(User us : this.onlineUsers)
            {
                if(u.getID() == us.getID())
                {
                    buf.putInt(1);
                    isOnline = true;
                    break;
                }
            }
            
            if(!isOnline)
            {
                buf.putInt(0);
            }
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
