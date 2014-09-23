package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 *
 * @author Dan
 */
public class GrantAccessPacket extends Packet {
    private final int userID;
    private final int userRole;
    
    /**
     * Constructs a GrantAccessPacket with the ID of the user being granted access.
     *
     * @param userID   The ID of the user being granted access.
     * @param userRole The role of the user being granted access.
     */
    public GrantAccessPacket(final int userID, final int userRole)
    {
        super(Packet.GRANTACCESS, 8);
        this.userID = userID;
        this.userRole = userRole;
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
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.userRole);
        
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
}
