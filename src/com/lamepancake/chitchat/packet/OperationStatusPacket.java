/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 * Sent to indicate the success or failure of specific operation.
 * 
 * This packet will be sent in response to login and database operations.
 * 
 * @author Trevor
 */
public class OperationStatusPacket extends Packet
{
    /**
     * 1 if success, 0 if failure.
     */
    int flag;
    
    public OperationStatusPacket(int flag)
    {
        super(OPERATIONSTATUS, 4);
        this.flag = flag;
    }

    public OperationStatusPacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        this.flag = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.flag);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the flag to know if it was a success or not.
     * @return the flag to know if it is a success or not.
     */
    public int getFlag()
    {
        return flag;
    }
}