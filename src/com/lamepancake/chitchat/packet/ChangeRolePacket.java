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
    
    private final int role;
    /**
     * The 
     * @param chat
     * @param user
     * @param role 
     */
    public ChangeRolePacket(int chat, int user, int role)
    {
        super(CHANGEROLE, 12);
        this.chatID = chat;
        this.userID = user;
        this.role = role;
    }

    public ChangeRolePacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        
        this.chatID = packetData.getInt();
        this.userID = packetData.getInt();
        this.role = packetData.getInt();
        
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.userID);
        buf.putInt(this.role);
        
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
     * Get user role for the intended user
     * @return the user role
     */
    public int getRole()
    {
        return this.role;
    }
}
