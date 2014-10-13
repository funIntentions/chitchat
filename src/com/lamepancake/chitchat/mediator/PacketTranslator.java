/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.mediator;
import com.lamepancake.chitchat.User;
import com.lamepancake.chitchat.ChatManager;
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
    ChatManager chatManager;
    
    public PacketTranslator()
    {
        chatManager = new ChatManager();
    }
    
    public void translateReceived(SelectionKey clientKey, Packet packet)
    {
        int type = packet.getType();
        Event event = null;
        
        switch(type)
        {
            case Packet.LOGIN:
                LoginPacket loginPacket = (LoginPacket)packet;
                event = new Event(Event.LOGIN, loginPacket);
                break;
            case Packet.MESSAGE:
                MessagePacket messagePacket = (MessagePacket)packet;
                event = new Event(Event.MESSAGE, messagePacket);
                break;
            case Packet.LOGOUT:
                LogoutPacket logoutPacket = (LogoutPacket)packet;
                event = new Event(Event.LOGOUT, logoutPacket);
                break;
            case Packet.WHOISIN:
                WhoIsInPacket whoIsInPacket = (WhoIsInPacket)packet;
                int whichList = whoIsInPacket.whichList();
                if (whichList == WhoIsInPacket.CONNECTED)
                {
                    event = new Event(Event.WHOISIN, whoIsInPacket);
                }
                else if (whichList == WhoIsInPacket.WAITING)
                {
                    event = new Event(Event.WAITINGLIST, whoIsInPacket);
                }
                break;
            case Packet.CHATLIST:
                ChatListPacket chatListPacket = (ChatListPacket)packet;
                event = new Event(Event.CHATLIST, chatListPacket);
                break;
            case Packet.JOINED:
                JoinedPacket joinedPacket= (JoinedPacket)packet;
                event = new Event(Event.JOIN, joinedPacket);
                break;
            case Packet.CHATSUPDATE:
                UpdateChatsPacket updateChatsPacket = (UpdateChatsPacket)packet;
                int updateFlag = updateChatsPacket.getUpdate();
                switch(updateFlag)
                {
                    case UpdateChatsPacket.CREATE:
                        event = new Event(Event.CREATECHAT, updateChatsPacket);
                        break;
                    case UpdateChatsPacket.UPDATE:
                        event = new Event(Event.UPDATECHAT, updateChatsPacket);
                        break;
                    case UpdateChatsPacket.REMOVE:
                        event = new Event(Event.DELETECHAT, updateChatsPacket);
                        break;
                }
                break;
            case Packet.GRANTACCESS:
                GrantAccessPacket grantAccessPacket = (GrantAccessPacket)packet;
                int newRole = grantAccessPacket.getUserRole();
                if (newRole == User.UNSPEC)
                {
                    event = new Event(Event.BOOTUSER, grantAccessPacket);
                }
                else
                {
                    event = new Event(Event.PROMOTEUSER, grantAccessPacket);
                }
                break;
            default:
                return;
        }
        
              
        chatManager.HandleEvent(event);
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
