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
 *
 * @author shane
 */
public class WhoIsInPacket extends Packet {
    
    /**
     * See who is in the list of users connected to the chat.
     */
    public static final int CONNECTED = 0;
        
    /**
     * See who is in the list of users waiting to be added to the chat.
     */
    public static final int WAITING = 1;
    
    /**
     * The list of users in the requested list.
     */
    private List<User> users;
    
    /**
     * The list being requested or sent.
     */
    private final int whichList;
    
    /**
     * Id of the chat that's being requested for information.
     */
    private final int chatID;
    
    /**
     * Construct a WhoIsInPacket to be sent to the server.
     * 
     * @param whichList The list of users to see.
     */
    public WhoIsInPacket(int whichList, int id)
    {
        // The 4 is already added in the other constructor, so don't repeat it here
        this(null, 0, whichList, id);
    }
    
    /**
     * Construct a WhoIsIn packet containing a list of users to be sent to
     * the requesting client.
     * 
     * @param users     The list of currently connected users.
     * @param length    The length, in bytes, of the data portion of the packet.
     * @param whichList The list being requested.
     */
    public WhoIsInPacket(List<User> users, int length, int whichList, int id)
    {
        super(WHOISIN, length + 4);
        this.users = users;
        this.whichList = whichList;
        this.chatID = id;
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
        
        this.chatID = data.getInt();
        this.whichList = data.getInt();
        
        // If the data capacity is only enough for whichList, then it doesn't
        // contain any users
        if(this.getLength() != HEADER_SIZE + 4)
        {
            int numUsers = data.getInt();
        
            this.users = new ArrayList<>(numUsers);

            for(int i = 0; i < numUsers; i++)
            {
                int     nameLen  = data.getInt();
                int     role;
                int     id;
                byte[]  rawName  = new byte[nameLen];
                String  username;

                data.get(rawName);
                username = new String(rawName, StandardCharsets.UTF_16LE);
                role = data.getInt();
                id = data.getInt();

                users.add(new User(username, role, id));            
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
     * Returns the type of list being requested/sent.
     * 
     * @return The type of list being requested/sent.
     */
    public int whichList()
    {
        return this.whichList;
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.whichList);

        if(this.users == null)
        {
            buf.rewind();
            return buf;
        }
        
        // Structure is: {numUsers}[{nameLength}{name}{role}{id}]
        buf.putInt(this.users.size());
        for(User u : this.users)
        {
            String username = u.getName();

            buf.putInt(username.length() * 2);
            buf.put(username.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(u.getRole());
            buf.putInt(u.getID());
        }
        
        buf.rewind();
        
        return buf;
    }
    
    public int getChatID()
    {
        return this.chatID;
    }
}
