package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 *
 * @author Dan
 */
public class GrantAccessPacket extends Packet {
    private final int userID;
    private final int userRole;
    private final int chatID;
    
    /**
     * Constructs a GrantAccessPacket with the ID of the user being granted access.
     *
     * @param userID   The ID of the user being granted access.
     * @param userRole The role of the user being granted access.
     */
    public GrantAccessPacket(final int userID, final int userRole, int chatID)
    {
        super(Packet.GRANTACCESS, 12);
        this.userID = userID;
        this.userRole = userRole;
        this.chatID = chatID;
    }
    
    /**
     * Construct a GrantAccessPacket from a serialised one.
     * 
     * @param header The serialised packet header.
     * @param data   The serialised GrantAccessPacket.
     */
    public GrantAccessPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        this.userID = data.getInt();
        this.userRole = data.getInt();
        this.chatID = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.userRole);
        buf.putInt(this.chatID);
        
        buf.rewind();
        return buf;
    }
    
    /**
     * Gets the user ID of the user being granted access.
     *
     * @return The user ID of the user being granted access.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
    /**
     * Gets the role of the user being granted access.
     * 
     * @return The role of the user being granted access.
     */
    public int getUserRole()
    {
        return this.userRole;
    }
    
    public int getChatID()
    {
        return this.chatID;
    }
}
