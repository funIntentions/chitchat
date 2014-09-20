package com.lamepancake.chitchat.packet;
import java.nio.ByteBuffer;

/**
 *
 * @author shane
 */
public abstract class Packet {
    /**
     * Login packet containing username and password.
     */
    public static final int LOGIN    = 0;
    
    /**
     * Logout packet indicating that the user wants to log out.
     */
    public static final int LOGOUT   = 1;
    
    /**
     * Contains chat data.
     */
    public static final int MESSAGE  = 2;
    
    /**
     * Requests or sends a list of users in the chat.
     */
    public static final int WHOISIN  = 3;
    
    /**
     * Sent to all connected clients when a new user joins the chat.
     */
    public static final int JOINED   = 4;
    
    /**
     * Sent to all connected clients when a new user leaves the chat.
     */
    public static final int LEFT     = 5;
    
    /**
     * Sent by an admin to grant a waiting user access to the chat.
     */
    public static final int GRANTACCESS     = 6;
    
    /**
     * The offset from which the inheriting packet classes must interpret data in
     * the buffer.
     */
    public static final int HEADER_SIZE = 8;
    
    private final int type;
    private final int length;
    
    /**
     * Creates a packet of the specified type.
     * 
     * @param packetType The type of packet to be created.
     * @param dataLength The length of data (without header) in this packet. 
     */
    public Packet(int packetType, int dataLength)
    {
        this.length = HEADER_SIZE + dataLength;
        this.type   = packetType;
    }
    
    public Packet(ByteBuffer header)
    {
        this.type   = header.getInt();
        this.length = header.getInt();
    }
    
    /**
     * Allocates a ByteBuffer and adds the packet type and length into it.
     * 
     * @return A ByteBuffer containing the packet type and length.
     */
    public ByteBuffer serialise()
    {
        // Room for the packet type, length, and contents
        ByteBuffer buf = ByteBuffer.allocate(this.length);
        
        buf.putInt(this.type);
        buf.putInt(this.length);        
        return buf;
    }
    
    /**
     * Get the type of this packet.
     * 
     * @return The packet's type.
     */
    public int getType()
    {
        return this.type;
    }
    
    /**
     * Get the total length of this packet.
     * 
     * @return The total length of this packet.
     */
    public int getLength()
    {
        return this.length;
    }
}
