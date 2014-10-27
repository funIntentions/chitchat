/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Sent to all users in a particular chat when another user's status is updated.
 * 
 * When a user leaves, joins, is promoted or booted, this packet will be sent to
 * all other users listening to the chat for updates. A flag indicates which
 * operation took place.
 * 
 * Note that even users who are waiting to enter the chat (i.e., users with a
 * role of User.WAITING) will receive this packet if they are subscribed to the
 * chat for updates.
 * @author shane
 */
public class UserNotifyPacket extends Packet
{
    /**
     * The user joined the chat (came online).
     */
    public static final int JOINED = 0;
    
    /**
     * The user left the chat (went online).
     */
    public static final int LEFT = 1;
    
    /**
     * The user was promoted or demoted.
     */
    public static final int PROMOTED = 2;
    
    /**
     * The user was booted from the chat (no longer has a role).
     */
    public static final int BOOTED = 3;
    
    /**
     * The user was booted from the chat (no longer has a role).
     */
    public static final int WAITING = 4;

    /**
     * The id of the chat that this will be sent to.
     */
    private final int chatID;
    
    /**
     * The id of the user that has been changed.
     */
    private final int userID;
    
    /**
     * The role of the user.
     */
    private final int userRole;
    
    /**
     * The name of the user.
    */
    private final String userName;
    
    
    /**
     * Indicates whether the user joined(0), left(1), or promoted(2).
     */
    private final int flag;

    
    public UserNotifyPacket(String userName, int userid, int chatid, int role, int flag)
    {
        super(USERNOTIFY, 16 + (userName.length() * 2) + 4);
        this.userID = userid;
        this.chatID = chatid;
        this.userRole = role;
        this.flag = flag;
        this.userName = userName;
    }

    public UserNotifyPacket(ByteBuffer packetHeader, ByteBuffer packetData)
    {
        super(packetHeader);
        this.userID = packetData.getInt();
        this.chatID = packetData.getInt();
        this.userRole = packetData.getInt();
        this.flag = packetData.getInt();
        
        int     nameLen  = packetData.getInt();
        byte[]  rawName  = new byte[nameLen];
        packetData.get(rawName);
        this.userName = new String(rawName, StandardCharsets.UTF_16LE);
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.putInt(this.userRole);
        buf.putInt(this.flag);
        
        buf.putInt(this.userName.length() * 2);
        buf.put(this.userName.getBytes(StandardCharsets.UTF_16LE));
        
        buf.rewind();
        
        return buf;
    }
    
     /**
     * Gets the user ID of the user who has been updated.
     * @return The user ID of the user who has been updated.
     */
    public int getUserID()
    {
        return userID;
    }
    
     /**
     * Gets the chat ID of the chat that this packet is being sent to.
     * @return The chat ID of the chat that this packet is being sent to.
     */
    public int getChatID()
    {
        return chatID;
    }
    
     /**
     * Gets the role of the user who has joined, left or promoted.
     * @return The role of the user who has joined, left or promoted.
     */
    public int getUserRole()
    {
        return userRole;
    }
    
    /**
     * Gets the flag of the packet to determine if this is a join, left or promoted packet.
     * @return The flag of the packet to determine if this is a join, left or promoted packet.
     */
    public int getFlag()
    {
        return flag;
    }
    
    /**
     * Gets the name of the user being change.
     * @return The name of the user
     */
    public String getName()
    {
        return userName;
    }
}
