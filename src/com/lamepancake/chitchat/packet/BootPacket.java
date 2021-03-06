package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;

/**
 * Boots a user from the given chat.
 * 
 * A chat admin can send this packet to completely boot a user from their chat.
 * The database association between the user and the chat is removed such that
 * the user must request access to the chat again if they wish to rejoin.
 * 
 * The client will not allow a user to send this packet if they are not an admin
 * in the given chat. This could be easily circumvented with packet forging, but
 * we decided not to handle that case as this is a demo program.
 * 
 * @author shane
 */
public class BootPacket extends Packet{
        
    private final int bootedID;
    private final int chatID;
    private final int booterID;
    
    /**
     * Construct a new BootPacket to boot the specified user from the chat.
     * 
     * @param chat   The chat from which to boot the user.
     * @param booted The user being booted from the chat.
     */
    public BootPacket(Chat chat, User booted, User booter)
    {
        super(BOOT, HEADER_SIZE + 12);
        this.chatID = chat.getID();
        this.bootedID = booted.getID();
        this.booterID = booter.getID();
    }
     
    /**
     * Construct a new BootPacket to boot the specified user from the chat.
     * 
     * @param chatID   The ID of the chat from which to boot the user.
     * @param bootedID The ID of the user being booted from the chat. 
     */
    public BootPacket(int chatID, int bootedID, int booterID)
    {
        super(BOOT, HEADER_SIZE + 12);
        this.chatID = chatID;
        this.bootedID = bootedID;
        this.booterID = booterID;
    }
    
    /**
     * Reconstruct a BootPacket from data packed into a buffer.
     * 
     * @param header The header containing the packet type and length.
     * @param data   The chatID and userID for the user being booted.
     */
    public BootPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        this.chatID = data.getInt();
        this.bootedID = data.getInt();
        this.booterID = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.bootedID);
        buf.putInt(this.booterID);
        
        buf.rewind();
        return buf;
    }
    
    /**
     * Gets the ID of the chat from which to boot the user.
     * @return The ID of the chat from which to boot the user
     */
    public int getChatID()
    {
        return this.chatID;
    }
    
    /**
     * Gets the ID of the user being booted.
     * @return The ID of user being booted.
     */
    public int getBootedID()
    {
        return this.bootedID;
    }
    
    /**
     * The ID of the person sending the boot packet.
     * @return 
     */
    public int getBooterID()
    {
        return this.booterID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof BootPacket)
        {
            BootPacket boot = (BootPacket)o;
            if(bootedID != boot.getBootedID())
                return false;
            if(booterID != boot.getBooterID())
                return false;
            return chatID == boot.getChatID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.bootedID;
        hash = 67 * hash + this.chatID;
        hash = 67 * hash + this.booterID;
        return hash;
    }
}
