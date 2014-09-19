/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author shane
 */
public class JoinedPacket extends Packet {
    
    private User user;
    
    /**
     * Constructs a JoinedPacket with the new user's name, role and ID.
     * 
     * @param username The new user's name.
     * @param role     The new user's role.
     * @param userID   The new user's ID.
     */
    public JoinedPacket(String username, int role, int userID)
    {
        this(new User(username, role, userID)); 
    } 
    
    /**
     * Constructs a JoinedPacket with the given user's information.
     * 
     * Note that the user must have a valid name, role and ID.
     * @param u The user whose information will go in the packet.
     */
    public JoinedPacket(User u)
    {
        // Space for the username length, the username, the role and the ID
        super(Packet.JOINED, (u.getName().length() * 2) + 12);
        this.user = u;
    }
    
    /**
     * Construct a JoinedPacket from a serialised one.
     * 
     * @param data The serialised JoinedPacket.
     */
    public JoinedPacket(ByteBuffer data)
    {
        super(Packet.JOINED, data.capacity());
        int     nameLen = data.getInt();
        byte[]  rawName = new byte[nameLen];
        data.get(rawName);
        this.user = new User(new String(rawName, StandardCharsets.UTF_16LE), data.getInt(), data.getInt());
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        
        buf.putInt(this.user.getName().length() * 2);
        buf.put(this.user.getName().getBytes(StandardCharsets.UTF_16LE));
        buf.putInt(this.user.getRole());
        buf.putInt(this.user.getID());
        
        buf.rewind();
        return buf;
    }
    
    /**
     * Get the new user's information.
     * @return A User object containing the new user's information.
     */
    public User getUser()
    {
        return this.user;
    }

}
