package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Sent by an admin or user to perform CRUD operations on chats.
 * 
 * For UPDATE and DELETE operations, the sender must be an admin in the chat.
 For CREATE operations, the server will check whether the chat exists and
 attempt to create a new one with the creating user as admin if not.
 
 This packet requires the server to respond with an OperationStatusPacket.
 * @author dan
 */
public class UpdateChatsPacket extends Packet {
    
    public static final int DELETE  = -1;
    public static final int CREATE   = 0;
    public static final int UPDATE    = 1;
    
    private final String chatName;
    private final int chatUpdate;
    private final int chatID;
    
    /**
     * Construct a new UpdateChatsPacket, specifying the type of operation and
     * the chat information.
     * 
     * @param name   The name of the chat to update, create or delete.
     * @param id     The ID of the chat to update, create or delete.
     * @param update The operation to perform (UpdateChatsPacket.DELETE, CREATE, UPDATE).
     */
    public UpdateChatsPacket(String name, int id, int update)
    {
        super(Packet.UPDATECHAT, (name.length() * 2) + 12);
        chatName = name;
        chatID = id;
        chatUpdate = update;
    }
    
    /**
     * Construct a UpdateChatsPacket from a serialised one.
     * 
     * @param header The serialised header.
     * @param data   The serialised JoinedPacket.
     */
    public UpdateChatsPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        int     nameLen = data.getInt();
        byte[]  rawName = new byte[nameLen];
        data.get(rawName);
        chatName = new String(rawName, StandardCharsets.UTF_16LE);
        chatID = data.getInt();
        chatUpdate = data.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        
        buf.putInt(chatName.length() * 2);
        buf.put(chatName.getBytes(StandardCharsets.UTF_16LE));
        buf.putInt(chatID);
        buf.putInt(chatUpdate);
       
        
        buf.rewind();
        return buf;
    }
    
    /**
     * Gets the name of the chat.
     * 
     * @return The name of the chat.
     */
    public String getName()
    {
        return chatName;
    }
    
    /**
     * Gets the ID of the chat.
     * 
     * @return The ID of the chat.
     */
    public int getChatID()
    {
        return chatID;
    }
    
    /**
     * Gets the operation to perform on the chat.
     * @return The operation (UpdateChatsPacket.DELETE, UpdateChatsPacket.UPDATE,
         UpdateChatsPacket.CREATE) to perform on the chat.
     */
    public int getUpdate()
    {
        return chatUpdate;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof UpdateChatsPacket)
        {
            UpdateChatsPacket p = (UpdateChatsPacket)o;
            if(!chatName.equals(p.getName()))
                return false;
            if(chatID != p.getChatID())
                return false;
            return chatUpdate == p.getUpdate();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.chatName);
        hash = 79 * hash + this.chatUpdate;
        hash = 79 * hash + this.chatID;
        return hash;
    }
}
