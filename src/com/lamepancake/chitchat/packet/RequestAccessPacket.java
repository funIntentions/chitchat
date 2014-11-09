package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 * Requests access to a given chat.
 * 
 * Users send this packet to join a chat in which they do not yet have a role.
 * If the user is already waiting to enter this chat, a JoinLeavePacket should
 * be sent instead.
 * 
 * @author shane
 */
public class RequestAccessPacket extends Packet
{
    private final int userID;
    private final int chatID;
    private final int organizationID;

    public RequestAccessPacket(int userid, int chatid, int organizationid)
    {
        super(REQUESTACCESS, 12);
        this.userID = userid;
        this.chatID = chatid;
        this.organizationID = organizationid;
    }

    public RequestAccessPacket(ByteBuffer packetHeader, ByteBuffer packetData)
    {
        super(packetHeader);
        this.userID = packetData.getInt();
        this.chatID = packetData.getInt();
        this.organizationID = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.putInt(this.organizationID);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the user ID of the user who wants access to a chat.
     * @return The user ID of the user who wants access to a chat.
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
     * Get organization ID of the chat.
     * @return The organization ID.
     */
    public int getOrganizationID()
    {
        return organizationID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof RequestAccessPacket)
        {
            RequestAccessPacket p = (RequestAccessPacket)o;
            if(userID != p.getUserID())
                return false;
            if(organizationID != p.getOrganizationID())
                return false;
            return chatID == p.getChatID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.userID;
        hash = 23 * hash + this.chatID;
        hash = 23 * hash + this.organizationID;
        return hash;
    }
}
