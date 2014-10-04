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
public class LogoutPacket extends Packet{
    
    int chatID;
    
    /**
     * Constructs a new LogoutPacket.
     */
    public LogoutPacket(int chatID)
    {
        super(LOGOUT, 4);
        this.chatID = chatID;
    }
    
    public LogoutPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);       
        this.chatID = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.rewind();
        return buf;
    }
    
    public int getChatID()
    {
        return chatID;
    }
    
}
