package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 * Changes a user's role within a chat.
 * 
 * Sent by an admin to change a given user's role in a chat. The admin uses this
 * packet to accept users waiting to join the chat (i.e., their role is
 * User.WAITING) and to promote or demote them within the chat. Once a user has
 * been accepted into the chat, they shouldn't be demoted back to User.WAITING;
 * use a BootPacket instead.
 * 
 * This packet will be sent to the user having their role changed to inform them
 * of the change. All other users in the chat will receive a UserNotifyPacket
 * indicating the change. The database association between this user and the
 * chat will also be updated. Note that users will receive this packet
 * regardless of whether they're subscribed to updates from that chat.
 * 
 * @author Tim
 */
public class ChangeRolePacket extends Packet {
    
    private final int chatID;
    
    private final int userID;
    
    private final int senderID;
    
    private final int role;
    
    private final int GroupID;
    /**
     * The 
     * @param chat
     * @param user
     * @param role 
     * @param Group
     */
    public ChangeRolePacket(int chat, int user, int sender, int role, int Group)
    {
        super(CHANGEROLE, 20);
        this.chatID = chat;
        this.userID = user;
        this.senderID = sender;
        this.role = role;
        this.GroupID = Group;
    }

    public ChangeRolePacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        
        this.chatID = packetData.getInt();
        this.userID = packetData.getInt();
        this.senderID = packetData.getInt();
        this.role = packetData.getInt();
        this.GroupID = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.userID);
        buf.putInt(this.senderID);
        buf.putInt(this.role);
        buf.putInt(this.GroupID);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the chat ID for the list of users.
     * @return The chat ID for the list of users.
     */
    public int getChatID()
    {
        return this.chatID;
    }
    
    /**
     * Gets the user ID of the user requesting/receiving the list.
     * @return The user ID of the user requesting/receiving the list.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
    /**
     * Returns the ID of original sender.
     * 
     * Note that this will be invalid (-1) when the server automatically promotes a
     * chat creator to admin, and possibly in other cases; always check the
     * value before using it.
     * @return 
     */
    public int getSenderID()
    {
        return this.senderID;
    }

    /**
     * Get user role for the intended user
     * @return the user role
     */
    public int getRole()
    {
        return this.role;
    }
    
    /**
     * Get Group ID of the chat passed in
     * @return the Group id
     */
    public int getGroupID()
    {
        return this.GroupID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof ChangeRolePacket)
        {
            ChangeRolePacket p = (ChangeRolePacket)o;
            if(chatID != p.getChatID())
                return false;
            if(userID != p.getUserID())
                return false;
            if(senderID != p.getSenderID())
                return false;
            if(GroupID != p.getGroupID())
                return false;
            return role == p.getRole();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.chatID;
        hash = 29 * hash + this.userID;
        hash = 29 * hash + this.senderID;
        hash = 29 * hash + this.role;
        hash = 29 * hash + this.GroupID;
        return hash;
    }
}
