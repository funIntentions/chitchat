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
     * Asks the server for a list of clients in this chat.
     */
    public static final int WHOISIN  = 3;
    
    /**
     * The offset from which the inheriting packet classes must interpret data in
     * the buffer.
     */
    public static final int BUF_OFFSET     = 8;
    
    private final int type;
    private final int length;
    
    /**
     * Creates a packet of the specified type.
     * 
     * @param packetType    The type of packet to be created.
     * @param packetLength  
     */
    public Packet(int packetType, int packetLength)
    {
        this.length = packetLength;
        this.type   = packetType;
    }
    
    /**
     * Allocates a ByteBuffer and adds the packet type and length into it.
     * 
     * @return A ByteBuffer containing the packet type and length.
     */
    public ByteBuffer serialise()
    {
        // Room for the packet type, length, and contents
        ByteBuffer buf = ByteBuffer.allocate(BUF_OFFSET + this.length);
        
        buf.putInt(this.type);
        buf.putInt(this.length);        
        return buf;
    }
}
