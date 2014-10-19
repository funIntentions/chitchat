/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 * Sent by a user attempting to join or leave a chat.
 * 
 * A flag in the packet indicates whether the user wants ot join or leave. To
 * "join" or "leave" a chat does not change the user's role in the chat. It only
 * subscribes or unsubscribes to/from a particular chat. On leaving, a user will
 * not receive chat messages or other information about the chat until they
 * rejoin.
 * 
 * If the user does not have a role in chat at all, the client will send a
 * RequestAccessPacket first. 
 * 
 * @author shane
 * @see RequestAccessPacket
 */
public class JoinLeavePacket extends Packet
{
    private final int userID;
    private final int chatID;
    private final int flag;
    
    public JoinLeavePacket(int userid, int chatid, int flag)
    {
        super(JOINLEAVE, 12);
        this.userID = userid;
        this.chatID = chatid;
        this.flag = flag;
    }

    public JoinLeavePacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        this.userID = packetData.getInt();
        this.chatID = packetData.getInt();
        this.flag = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.putInt(this.flag);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the user ID of the user who wants to join or leave.
     * @return The user ID of the user who wants to join or leave.
     */
    public int getUserID()
    {
        return userID;
    }
    
     /**
     * Gets the chat ID of the chat they want access to.
     * @return The chat ID of the chat they want access to.
     */
    public int getChatID()
    {
        return chatID;
    }
    
     /**
     * Gets the flag to say if they want to join or leave
     * @return The flag to say if they want to join or leave
     */
    public int getFlag()
    {
        return chatID;
    }
}
