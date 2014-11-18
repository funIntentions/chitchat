package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Sent to all users in a chat to notify them of chat status changes.
 * 
 * This packet will primarily be sent for things such as chat name changes or
 * to warn users that a chat is about to be deleted. The packet will be sent to
 * all users regardless of whether they're subscribed to updates from that chat.
 * 
 * @author Tim
 */
public class ChatNotifyPacket extends Packet 
{
    private int chatID;
    
    private String chatName;
    
    private int changeFlag;
    
    private int GroupID;
    
    public ChatNotifyPacket(int chat, String name, int flag, int Group)
    {
        super(CHATNOTIFY, 4 + name.length() * 2 + 4 + 4 + 4);
        
        chatID = chat;
        chatName = name;
        changeFlag = flag;
        GroupID = Group;
    }

    public ChatNotifyPacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        
        this.chatID = packetData.getInt();
        this.changeFlag = packetData.getInt();
        int     nameLen  = packetData.getInt();
        byte[]  rawName  = new byte[nameLen];
        packetData.get(rawName);
        chatName = new String(rawName, StandardCharsets.UTF_16LE);
        this.GroupID = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.chatID);
        buf.putInt(this.changeFlag);
        
        // Structure is: {chatID}{changeFlag}{chatNameLength}{chatName}
        buf.putInt(chatName.length() * 2);
        buf.put(chatName.getBytes(StandardCharsets.UTF_16LE));
        buf.putInt(this.GroupID);
        
        buf.rewind();
        
        return buf;
    }
    
    public int getChatID()
    {
        return this.chatID;
    }
    
    public String getChatName()
    {
        return this.chatName;
    }
    
    public int getChangeFlag()
    {
        return this.changeFlag;
    }
    
    public int getGroupID()
    {
        return this.GroupID;
    }
    
    @Override
    public boolean equals(Object o)
    {
         if(!super.equals(o))
            return false;
         
        if(o instanceof ChatNotifyPacket)
        {
            ChatNotifyPacket p = (ChatNotifyPacket)o;
            if(chatID != p.getChatID())
                return false;
            if(!chatName.equals(p.getChatName()))
                return false;
            if(GroupID != p.getGroupID())
                return false;
            return changeFlag == p.getChangeFlag();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.chatID;
        hash = 71 * hash + Objects.hashCode(this.chatName);
        hash = 71 * hash + this.changeFlag;
        hash = 71 * hash + this.GroupID;
        return hash;
    }


}
