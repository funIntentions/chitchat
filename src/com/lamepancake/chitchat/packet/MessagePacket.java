package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * Contains a message to be sent to other users in the chat.
 * 
 * Specifies the chat to which the message is to be sent and the user sending
 * the message.
 * 
 * @author shane
 */

public class MessagePacket extends Packet {

    private final String message;
    private int          userID;
    private final int    chatID;

    /**
     * Constructs a MessagePacket with the given message and userID.
     * 
     * @param message The message to send.
     * @param userID  The ID of the user sending the message.
     */
    public MessagePacket(String message, int userID, int chatID) {
        super(Packet.MESSAGE, 8 + message.length() * 2);
        this.message = message;
        this.userID = userID;
        this.chatID = chatID;
    }

    /**
     * Construct a MessagePacket from a serialised one.
     * 
     * @param header The serialised header.
     * @param data   A ByteBuffer containing the serialised packet.
     */
    public MessagePacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        byte[] rawMessage = new byte[data.capacity() - 8];
        this.userID = data.getInt();
        this.chatID = data.getInt();
        data.get(rawMessage);
        this.message = new String(rawMessage, StandardCharsets.UTF_16LE);
    }

    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.put(this.message.getBytes(StandardCharsets.UTF_16LE));
        buf.rewind();

        return buf;
    }

    /**
     * Get the message contained in this packet.
     * @return The message contained in this packet.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the ID of the user sending the message.
     * 
     * @return The ID of the user sending the message.
     */
    public int getUserID()
    {
        return this.userID;
    }

    /**
     * Set the user ID in case it's not set.
     * 
     * The user ID must be >= 0. The method will throw an IllegalArgumentException
     * if this rule is not followed.
     *
     * @param newID The new user ID to assign to this message.
     */
    public void setUserID(int newID)
    {
        if(newID < 0)
            throw new IllegalArgumentException("MessagePacket.setUserID: newID must be >= 0, was " + newID);

        this.userID = newID;
    }

    public int getChatID()
    {
        return this.chatID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof MessagePacket)
        {
            MessagePacket p = (MessagePacket)o;
            if(!message.equals(p.getMessage()))
                return false;
            if(chatID != p.getChatID())
                return false;
            return userID == p.getUserID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.message);
        hash = 73 * hash + this.userID;
        hash = 73 * hash + this.chatID;
        return hash;
    }
}

