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
public class GrantAccessPacket extends Packet {
    private int userID;
    
    /**
     * Constructs a GrantAccessPacket with the ID of the user being granted access.
     *
     * @param userID The ID of the user being granted access.
     */
    public GrantAccessPacket(int userID)
    {
        super(Packet.GRANTACCESS, 4);
        this.userID = userID;
    }
    
    /**
     * Construct a GrantAccessPacket from a serialised one.
     * 
     * @param data The serialised GrantAccessPacket.
     */
    public GrantAccessPacket(ByteBuffer data)
    {
        super(Packet.GRANTACCESS, 4);
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
     * Gets the user ID of the user being granted access.
     *
     * @return The user ID of the user being granted access.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
}
