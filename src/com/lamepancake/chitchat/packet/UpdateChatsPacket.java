/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author dan
 */
public class UpdateChatsPacket extends Packet {
    
    public static final int REMOVE  = -1;
    public static final int CREATE   = 0;
    public static final int UPDATE    = 1;
    
    private final String chatName;
    private final int chatUpdate;
    private final int chatID;
    
    public UpdateChatsPacket(String name, int id, int update)
    {
        super(Packet.CHATSUPDATE, (name.length() * 2) + 12);
        chatName = name;
        chatID = id;
        chatUpdate = update;
    }
    
    /**
     * Construct a UpdateChatsPacket from a serialised one.
     * 
     * @param header The serialised header.
     * @param data   The serialised JoinedPacket.
     */
    public UpdateChatsPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        int     nameLen = data.getInt();
        byte[]  rawName = new byte[nameLen];
        data.get(rawName);
        chatName = new String(rawName, StandardCharsets.UTF_16LE);
        chatID = data.getInt();
        chatUpdate = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        
        buf.putInt(chatName.length() * 2);
        buf.put(chatName.getBytes(StandardCharsets.UTF_16LE));
        buf.putInt(chatID);
        buf.putInt(chatUpdate);
       
        
        buf.rewind();
        return buf;
    }
    
    public String getName()
    {
        return chatName;
    }
    
    public int getID()
    {
        return chatID;
    }
    
    public int getUpdate()
    {
        return chatUpdate;
    }
}
