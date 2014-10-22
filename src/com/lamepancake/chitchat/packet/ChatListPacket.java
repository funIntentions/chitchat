package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sent to request or transmit a list of all available chats.
 * 
 * The server will send this packet to a user unsolicited upon logging in. After
 * this, the user must explicitly request a chat list to receive updates on
 * available chats.
 * 
 * The packet contains the list of all available chats upon login and the role
 * that the user holds in each of them.
 * 
 * @author tware
 */
public class ChatListPacket extends Packet
{
    private List<Chat> chats;
    private Map<Integer, Integer> roles;
    private Map<Chat, Integer> chatList;
    
    public ChatListPacket()
    {
        this(null, 0, null, 0);
    }
    
    public ChatListPacket(List<Chat> chats, int chatLength, Map<Integer, Integer> role, int roleLength)
    {
        super(CHATLIST, chatLength);
        this.chats = chats;
        this.roles = role;
        
        for(Chat c : chats)
        {
            Integer r = roles.get(c.getID());
            if(r != null)
            {
                r = roles.get(c.getID());
            }
            else
            {
                r = User.UNSPEC;
            }
            chatList.put(c, r);
        }
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
        if(this.getLength() != HEADER_SIZE)
        {
            int numChats = data.getInt();

            for(int i = 0; i < numChats; i++)
            {
                int     nameLen  = data.getInt();
                int     id;
                int     role;
                byte[]  rawName  = new byte[nameLen];
                String  chatName;

                data.get(rawName);
                chatName = new String(rawName, StandardCharsets.UTF_16LE);
                id = data.getInt();
                role = data.getInt();

                chatList.put(new Chat(chatName, id), role);            
            }
        }
    }
    
    public List<Chat> getChats()
    {
        return this.chats;
    }
    
    public Map<Integer, Integer> getRoles()
    {
        return this.roles;
    }
    
    public Map<Chat, Integer> getChatList()
    {
        return this.chatList;
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
        
        // Structure is: {numChats}{chatName}{chatID}{userID}{role}
        buf.putInt(this.chats.size());
        for(Chat c : this.chats)
        {
            String chatname = c.getName();

            buf.putInt(chatname.length() * 2);
            buf.put(chatname.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(c.getID());
            buf.putInt(roles.get(c.getID()));
        }
        buf.rewind();
        
        return buf;
    }
}
