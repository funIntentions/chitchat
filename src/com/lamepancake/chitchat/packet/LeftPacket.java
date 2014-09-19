/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 *
 * @author shane
 */
public class LeftPacket extends Packet {
    private int userID;
    
    /**
     * Constructs a LeftPacket with the ID of the leaving user.
     *
     * @param userID The ID of the leaving user.
     */
    public LeftPacket(int userID)
    {
        super(LEFT, 4);
        this.userID = userID;
    }
    
    /**
     * Construct a LeftPacket from a serialised one.
     * 
     * @param data The serialised LeftPacket.
     */
    public LeftPacket(ByteBuffer data)
    {
        super(LEFT, 4);
        this.userID = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        
        buf.rewind();
        return buf;
    }
    
    /**
     * Gets the user ID of the user leaving the chat.
     *
     * @return The user ID of the user leaving the chat.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
}
