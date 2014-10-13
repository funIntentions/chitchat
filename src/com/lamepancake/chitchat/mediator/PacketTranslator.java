/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.mediator;
import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.GrantAccessPacket;
import com.lamepancake.chitchat.packet.JoinedPacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.LogoutPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.UpdateChatsPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Observable;

/**
 *
 * @author dan
 */
public class PacketTranslator extends Observable 
{
    public PacketTranslator()
    {
        
    }
    
    public void translateReceived(SelectionKey clientKey, Packet packet)
    {
        int type = packet.getType();
        int chatID;
        
        switch(type)
        {
            case Packet.LOGIN:
                LoginPacket loginPacket = (LoginPacket)packet;
                chatID = -1;
                break;
            case Packet.MESSAGE:
                MessagePacket messagePacket = (MessagePacket)packet;
                chatID = messagePacket.getChatID();
                break;
            case Packet.LOGOUT:
                LogoutPacket logoutPacket = (LogoutPacket)packet;
                chatID = logoutPacket.getChatID();
                break;
            case Packet.WHOISIN:
                WhoIsInPacket whoIsInPacket = (WhoIsInPacket)packet;
                chatID = whoIsInPacket.getChatID();
                break;
            case Packet.CHATLIST:
                ChatListPacket chatListPacket = (ChatListPacket)packet;
                chatID = -2;
                break;
            case Packet.JOINED:
                JoinedPacket joinedPacket= (JoinedPacket)packet;
                chatID = joinedPacket.getChatID();
                break;
            case Packet.CHATSUPDATE:
                UpdateChatsPacket updateChatsPacket = (UpdateChatsPacket)packet;
                chatID = updateChatsPacket.getChatID();
                break;
            case Packet.GRANTACCESS:
                GrantAccessPacket grantAccessPacket = (GrantAccessPacket)packet;
                chatID = grantAccessPacket.getChatID();
                break;  
        }
    }
    
    public void dispatchTranslation(Event e)
    {
        int type = e.getType();
        
        switch(type)
        {
            case Event.LOGIN:
                break;
            case Event.LOGOUT:
                break;
            case Event.MESSAGE:
                break;
            case Event.JOIN:
                break;
            case Event.LEAVE:
                break;
            case Event.GRANT:
                break;
            default:
                System.out.println("Unknown event " + type);
        }
    }
    
    //Tests if this object has changed.
    //void notifyObservers()
    //{
    //    
    //}
    
    //If this object has changed, as indicated by the hasChanged method, then notify all of its observers and then call the clearChanged method to indicate that this object has no longer changed.
    //void notifyObservers(Object arg)
    //{
    //    
    //}
}
