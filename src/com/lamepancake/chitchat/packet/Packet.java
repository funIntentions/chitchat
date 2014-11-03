package com.lamepancake.chitchat.packet;
import java.nio.ByteBuffer;

/**
 * The base class for all packets.
 * 
 * Contains the packet type and the length of the entire packet.
 * 
 * @author shane
 */
public abstract class Packet {
    /**
     * Login packet containing username and password.
     */
    public static final int LOGIN = 0;
    
    /**
     * Contains chat data.
     */
    public static final int MESSAGE = 1;
    
    /**
     * Specifies that the client wants to join or leave a particular chat.
     */
    public static final int JOINLEAVE = 3;
    
    /**
     * Requests or sends list of users in a particular chat..
     */
    public static final int WHOISIN = 4;
  
    /**
     * Requests a list of chats and the user's role within them.
     */
    public static final int CHATLIST = 6;
    
    /**
     * Sent by and admin to boot a user from the chat.
     */
    public static final int BOOT = 7;
    
    /**
     * Sent by an admin to promote or demote a user within the chat.
     */
    public static final int CHANGEROLE = 8;
    
    /**
     * Sent by a user to request access to a particular chat.
     */
    public static final int REQUESTACCESS = 9;

    /**
     * Sent by the chat to all its users to notify them that a user left, joined, was promoted, etc.
     */
    public static final int USERNOTIFY = 10;
    
    /**
     * Sent by the chat to all its users to notify them that the chat state has changed (e.g., the chat was renamed).
     */
    public static final int CHATNOTIFY = 11;
    
    /**
     * Sent by the server in response to certain events that require confirmation (e.g., logging in).
     */
    public static final int OPERATIONSTATUS = 12;
    
    /**
     * Sent from a client to the server to perform CRUD operations on a chat.
     */
    public static final int UPDATECHAT= 13;
    
    /**
     * Sent from a client to the server to indicate that they're leaving all chats.
     */
    public static final int LOGOUT = 14;
    
    /**
     * The header size of a packet.
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
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Packet)
        {
            Packet p = (Packet)o;
            if(type != p.getType())
                return false;
            return length == p.getLength();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.type;
        hash = 31 * hash + this.length;
        return hash;
    }
}
