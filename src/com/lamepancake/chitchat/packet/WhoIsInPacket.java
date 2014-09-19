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
    
    private List<User> users;
    
    /**
     * Construct a WhoIsInPacket to be sent to the server.
     */
    public WhoIsInPacket()
    {
        this(null, 0);
    }
    
    /**
     * Construct a WhoIsIn packet containing a list of users to be sent to
     * the requesting client.
     * 
     * @param users  The list of currently connected users.
     * @param length The length, in bytes, of the packet.
     */
    public WhoIsInPacket(List<User> users, int length)
    {
        super(WHOISIN, length);
        this.users = users;
    }
    
    /**
     * Construct a WhoIsInPacket a serialised WhoIsInPacket.
     * 
     * @param data The serialised data.
     */
    public WhoIsInPacket(ByteBuffer data)
    {
        super(WHOISIN, data.capacity());
        
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
    
    /**
     * Get the list of users contained in this WhoIsInPacket.
     * 
     * @return The list of users in this WhoIsInPacket. 
     */
    public List<User> getUsers()
    {
        return this.users;
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
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
}
