package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    private final int GroupID;
    
    /**
     * Creates a client-side ChatListPacket.
     * @param userid THe ID of the user requesting the chat.
     */
    public ChatListPacket(final int userid, final int Groupid)
    {
        this(null, 12, userid, Groupid);
    }
    
    /**
     * Creates a new ChatListPacket.
     * 
     * @param chatRoles  A map of chats to the rolchatListe that the requesting user has.
     * @param roleLength The number of chats in the list. 
     * @param userID     The ID of the user requesting list.
     */
    public ChatListPacket(Map<Chat, Integer> chatRoles, int roleLength, int userID, int GroupID)
    {
        super(CHATLIST, roleLength);
        this.chatList = chatRoles;
        this.userID = userID;
        this.GroupID = GroupID;
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
        this.GroupID = data.getInt();
    }
    
    public Map<Chat, Integer> getChatList()
    {
        return this.chatList;
    }
    
    public int getUserID()
    {
        return userID;
    }
    
    public int getGroupID()
    {
        return GroupID;
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
        buf.putInt(GroupID);
        buf.rewind();
        
        return buf;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof ChatListPacket)
        {
            ChatListPacket p = (ChatListPacket)o;
            
            if(chatList != null && p.getChatList() != null)
            {
                Set<Chat> chatsSet = chatList.keySet();
                Set<Chat> pChatsSet = p.getChatList().keySet();
                if(chatsSet.size() != pChatsSet.size())
                    return false;

                Object[] chats = chatsSet.toArray();
                Object[] pChats= pChatsSet.toArray();

                for(int i = 0; i < chats.length; i++)
                {
                    if(chats[i] instanceof Chat && pChats[i] instanceof Chat)
                    {
                        Chat chat = (Chat)chats[i];
                        Chat pChat = (Chat)pChats[i];

                        if(!Objects.equals(chat.getID(), pChat.getID()))
                            return false;
                        if(!chat.getName().equals(pChat.getName()))
                            return false;
                        if(!Objects.equals(chatList.get(chat), p.getChatList().get(pChat)))
                            return false;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else
            {
                if(chatList == null && p.getChatList() != null)
                    return false;
                if(chatList != null && p.getChatList() == null)
                    return false;
            }
            
            //if(chatList != p.getChatList())
                //return false;
            if(GroupID != p.getGroupID())
                return false;
            return userID == p.getUserID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.chatList);
        hash = 71 * hash + this.userID;
        hash = 71 * hash + this.GroupID;
        return hash;
    }

}
