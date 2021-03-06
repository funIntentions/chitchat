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
public class LogoutPacket extends Packet {
    
    private final int userID;
    
    /**
     * Constructs a LogoutPacket with the specified user ID.
     * 
     * @param userID The ID of the user logging out.
     */
    public LogoutPacket(int userID)
    {
        super(LOGOUT, 4);
        this.userID = userID;
    }
    
    /**
     * Reconstructs a LogoutPacket from a serialised one.
     * @param header The packet header.
     * @param data   The packet data (containing the user ID).
     */
    public LogoutPacket(ByteBuffer header, ByteBuffer data)       
    {
        super(header);
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
     * Gets the userID.
     * @return the userID of the user logging out.
     */
    public int getID()
    {
        return userID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof LogoutPacket)
        {
            LogoutPacket p = (LogoutPacket)o;
            return userID == p.getID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.userID;
        return hash;
    }
}
