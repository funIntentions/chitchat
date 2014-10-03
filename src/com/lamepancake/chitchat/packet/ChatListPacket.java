/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tware
 */
public class ChatListPacket extends Packet
{
    private List<Chat> chats;
    
    public ChatListPacket()
    {
        this(null, 0);
    }
    
    public ChatListPacket(List<Chat> chats, int length)
    {
        super(CHATLIST, length);
        this.chats = chats;
    }
    
    /**
     * Construct a ChatListPacket a serialised ChatListPacket.
     * 
     * @param header The serialised packet header.
     * @param data   The serialised data.
     */
    public ChatListPacket(ByteBuffer header, ByteBuffer data)
    {
        super(header);
        
        // If the data capacity is only enough for whichList, then it doesn't
        // contain any users
        if(this.getLength() != HEADER_SIZE + 4)
        {
            int numChats = data.getInt();
        
            this.chats = new ArrayList<>(numChats);

            for(int i = 0; i < numChats; i++)
            {
                int     nameLen  = data.getInt();
                int     id;
                byte[]  rawName  = new byte[nameLen];
                String  chatName;

                data.get(rawName);
                chatName = new String(rawName, StandardCharsets.UTF_16LE);
                id = data.getInt();

                chats.add(new Chat(chatName, id));            
            }
        }
    }
    
    public List<Chat> GetChats()
    {
        return this.chats;
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();

        if(this.chats == null)
        {
            buf.rewind();
            return buf;
        }
        
        // Structure is: {numUsers}[{nameLength}{name}{role}{id}]
        buf.putInt(this.chats.size());
        for(Chat c : this.chats)
        {
            String chatname = c.getName();

            buf.putInt(chatname.length() * 2);
            buf.put(chatname.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(c.getID());
        }
        
        buf.rewind();
        
        return buf;
    }
}
