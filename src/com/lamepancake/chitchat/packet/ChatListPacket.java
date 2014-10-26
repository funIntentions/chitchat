package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private Map<Chat, Integer> chatList;
    private final int userID;
    
    /**
     * Creates a client-side ChatListPacket.
     * @param userid THe ID of the user requesting the chat.
     */
    public ChatListPacket(final int userid)
    {
        this(null, 8, userid);
    }
    
    /**
     * Creates a new ChatListPacket.
     * 
     * @param chatRoles  A map of chats to the role that the requesting user has.
     * @param roleLength The number of chats in the list. 
     * @param userID     The ID of the user requesting list.
     */
    public ChatListPacket(Map<Chat, Integer> chatRoles, int roleLength, int userID)
    {
        super(CHATLIST, roleLength);
        this.chatList = chatRoles;
        this.userID = userID;
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
        int len;
        
        this.userID = data.getInt();
        this.chatList = new HashMap<>();
        len = data.getInt();
        
        if(len > 0)
        {
            for(int i = 0; i < len; i++)
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
    
    public Map<Chat, Integer> getChatList()
    {
        return this.chatList;
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        
        // Structure is: {userID}{numChats}[{namelength}{chatName}{chatID}{role}]
        buf.putInt(this.userID);
        buf.putInt(this.chatList.size());
        for(Chat c : this.chatList.keySet())
        {
            String chatname = c.getName();

            buf.putInt(chatname.length() * 2);
            buf.put(chatname.getBytes(StandardCharsets.UTF_16LE));
            buf.putInt(c.getID());
            buf.putInt(chatList.get(c));
        }
        buf.rewind();
        
        return buf;
    }
}
