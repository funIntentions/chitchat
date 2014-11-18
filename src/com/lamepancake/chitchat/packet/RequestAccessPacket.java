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
    private final int groupID;
    //private final int flag;
    //public static final int CHATREQUEST = 0;
    //public static final int GROUPREQUEST = 1;

    public RequestAccessPacket(int userid, int chatid, int Groupid)
    {
        super(REQUESTACCESS, 12);
        this.userID = userid;
        this.chatID = chatid;
        this.groupID = Groupid;
    }

    public RequestAccessPacket(ByteBuffer packetHeader, ByteBuffer packetData)
    {
        super(packetHeader);
        this.userID = packetData.getInt();
        this.chatID = packetData.getInt();
        this.groupID = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.putInt(this.groupID);
        
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
     * Get Group ID of the chat.
     * @return The Group ID.
     */
    public int getgroupID()
    {
        return groupID;
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
            if(groupID != p.getgroupID())
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
        hash = 23 * hash + this.groupID;
        return hash;
    }
}
