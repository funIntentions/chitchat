/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.DAO.ChatDAO;
import com.lamepancake.chitchat.DAO.ChatDAOMySQLImpl;
import com.lamepancake.chitchat.DAO.ChatRoleDAO;
import com.lamepancake.chitchat.DAO.ChatRoleDAOMySQLImpl;
import com.lamepancake.chitchat.DAO.UserDAOMySQLImpl;
import com.lamepancake.chitchat.packet.BootPacket;
import com.lamepancake.chitchat.packet.ChangeRolePacket;
import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dan
 */
public class Chat 
{
    private int chatID;
    private String chatName;
    private ChatRoleDAO crDao;
    private ChatDAO cDao;
    
    /**
     * A map of users and their online status.
     */
    private final Map<User, Boolean> users;
    
    public Chat(String name)
    {
        this.chatName = name;
        this.chatID = -1;
        this.users = new HashMap<>();
    }

    /**
     * Creates a new chat.
     * 
     * @todo Differentiate between client and server chats.
     * @param name
     * @param id 
     */
    public Chat(String name, int id)
    {
        this.chatName = name;
        this.chatID = id;
        this.users = new HashMap<>();
        
        try {
            cDao = ChatDAOMySQLImpl.getInstance();
            crDao = ChatRoleDAOMySQLImpl.getInstance();
        } catch (SQLException se) {
            System.err.println("Chat constructor: could not get database access: " + se.getMessage());
        }
        
    }
    
    public void initUsers(List<User> initialUsers)
    {
        for (User user : initialUsers)
        {
            this.users.put(user, Boolean.FALSE);
        }
    }
    
    public User findUser(String userName)
    {
        Set<User>           userSet;
        userSet = this.users.keySet();
                
        for (User user : userSet)
        {
            if (user.getName().equalsIgnoreCase(userName))
            {
                return user;
            }
        }
        
        return null;
    }
    
    public User findUser(int userID)
    {
        Set<User>           userSet;
        userSet = this.users.keySet();
                
        for (User user : userSet)
        {
            if (user.getID() == userID)
            {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Routes packets to the appropriate function for processing.
     * 
     * @param received The packet to process.
     * @todo Implement this...
     */
    public void handlePacket(Packet received)
    {
        int type = received.getType();
        switch(type)
        {
            case Packet.BOOT:
                bootUser((BootPacket)received);
                break;
            case Packet.MESSAGE:
                sendMessage((MessagePacket)received);
                break;
            case Packet.JOINLEAVE:
                updateState((JoinLeavePacket)received);
                break;
            case Packet.WHOISIN:
                sendUserList((WhoIsInPacket)received);
                break;
            case Packet.CHANGEROLE:
                promoteUser((ChangeRolePacket)received);
                break;
            case Packet.REQUESTACCESS:
                addWaitingUser((RequestAccessPacket)received);
                break;
            default:
                throw new UnsupportedOperationException("Unknown packet type: " + type);
        }
    }

    /**
     * 
     * @param b 
     */
    private void bootUser(BootPacket b)
    {
        int affectedUserID = b.getBootedID();
        Packet p;
        
        // Remove the user from the map
        for(User u: users.keySet())
        {
            if(u.getID() == affectedUserID)
            {
                users.remove(u);
                break;
            }
        }

        p = PacketCreator.createUserNotify(affectedUserID, this.chatID, User.UNSPEC, Packet.BOOT);
        broadcast(b.getBooterID(), p, false);
    }
    
    private void addWaitingUser(RequestAccessPacket r)
    {
        Packet p = PacketCreator.createUserNotify(r.getUserID(), r.getChatID(), User.WAITING, Packet.CHANGEROLE);
        
        try 
        {
            //Make user admin for chat.
            ChatRoleDAOMySQLImpl.getInstance().addUser(chatID, r.getUserID(), User.WAITING);
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.createChat: SQL exception thrown: " + e.getMessage());
            return;
        }
        
        for(User u : users.keySet())
        {
            if(u.getID() == r.getUserID())
            {
                ((ServerUser)u).notifyClient(r);
                break;
            }
        }
        broadcast(r.getUserID(), p, false);
    }
    
    private void promoteUser(ChangeRolePacket r)
    {
        Packet p = PacketCreator.createUserNotify(r.getUserID(), r.getChatID(), r.getRole(), Packet.CHANGEROLE);
        
        try 
        {
            //Make user admin for chat.
            ChatRoleDAOMySQLImpl.getInstance().updateRole(chatID, r.getUserID(), r.getRole());
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.createChat: SQL exception thrown: " + e.getMessage());
            return;
        }
        
        for(User u : users.keySet())
        {
            if(u.getID() == r.getUserID())
            {
                ((ServerUser)u).notifyClient(r);
                break;
            }
        }
        broadcast(r.getUserID(), p, false);
    }
    
    /**
     * Removes or adds a User to the list of subscribed users and notifies other
     * Users in the chat.
     * @param jl The JoinLeave packet specifying whether the user is joining or
     *           leaving.
     */
    private void updateState(JoinLeavePacket jl)
    {
        System.out.println(jl.getFlag());
        if(jl.getFlag() == JoinLeavePacket.JOIN)
        {
            System.out.println("hello");
            join(jl);
        }
        else
        {
            System.out.println("bye");
            leave(jl);
        }
        
    }
    
    private void join(JoinLeavePacket jl)
    {
        int currentRole;
        
        User affected = null;
        
        for(User u : users.keySet())
        {
            if(u.getID() == jl.getUserID())
            {
                affected = u;
                break;
            }
        }
                
        if (affected == null)
        {
            try
            {
                currentRole = ChatRoleDAOMySQLImpl.getInstance().getUserRoleInChat(jl.getUserID(), chatID);
                System.out.println(currentRole);
                switch (currentRole)
                {
                    case User.ADMIN: // chat creator
                        System.out.println("admin");
                        affected = UserDAOMySQLImpl.getInstance().getByID(jl.getUserID());
                        users.put(affected, Boolean.TRUE);
                        ((ServerUser)affected).notifyClient(jl);
                        break;
                    case User.UNSPEC: // not in chat?
                        System.out.println("unspec");
                        ChatRoleDAOMySQLImpl.getInstance().addUser(chatID, jl.getUserID(), User.WAITING);
                       break;
                    case User.USER:
                        System.out.println("user");
                        break;
                    case User.WAITING:
                        System.out.println("waiting");
                        break;
                }
            }
            catch (SQLException e) 
            {
                System.err.println("Chat.updateState: SQL exception thrown: " + e.getMessage());
            }
        }
        else
        {
            users.put(affected, Boolean.TRUE);
            ((ServerUser)affected).notifyClient(jl);
        }    
    }
    
    private void leave(JoinLeavePacket jl)
    {
        
        final Packet p;
        User affected = null;
        
        for(User u : users.keySet())
        {
            if(u.getID() == jl.getUserID())
            {
                affected = u;
                break;
            }
        }
        
        if (affected == null) return;
        
        users.put(affected, Boolean.FALSE);
        
        p = PacketCreator.createUserNotify(jl.getUserID(), this.chatID, affected.getRole(), jl.getFlag());
        broadcast(jl.getUserID(), p, false);
    }
    
    public Map<User, Boolean> getConnectedUsers()
    {
        return users;
    }
    
    public String getName()
    {
        return chatName;
    }
    
    public void setName(String name)
    {
        chatName = name;
    }
    
    public Integer getID()
    {
        return chatID;
    }
    
    public void setID(int id)
    {
        chatID = id;
    }
 
    /**
     * Sends a chat message to all other users in the chat.
     * 
     * @param message The message to be sent.
     * 
     * @todo Handle case where packet doesn't send completely
     */
    private void sendMessage(MessagePacket message)
    {      
        // If they're the only person in the chat, don't bother sending the message
        if(users.size() <= 1)
            return;
         
        broadcast(message.getUserID(), message, false);
    }
    
    /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserList(WhoIsInPacket w)
    {
        User sender = null;
        final Packet p;

        for(User u : users.keySet())
        {
            if(w.getUserID() == u.getID())
            {
                sender = u;
                break;
            }
        }
        
        if(sender == null)
            return;
      
        p = PacketCreator.createWhoIsIn(users, chatID, w.getUserID());
        ((ServerUser)sender).notifyClient(p);
    }
    
    /**
     * Adds a user to the chat so they receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void subscribeUser(User user)
    {
        users.put(user, true);
    }
    
    /**
     * Removes a user from the chat so they don't receive messages.
     * @param key The SelectionKey of the user.
     * @param user The user being added.
     */
    private void unsubscribeUser(User user)
    {
        users.put(user, false);
    }
           
    /**
     * Broadcasts the passed in message to all connected users.
     * 
     * @param senderID The key associated with the sending user.
     * @param p        The packet to be broadcasted.
     * @param sendBack Whether the user should have the packet sent back to them.
     */
    private void broadcast(int senderID, Packet p, boolean sendBack)
    {        
        for(User u : users.keySet())
        {
            Boolean isOnline = users.get(u);
            if (isOnline && u.getRole() != User.WAITING)
            {
                // Don't send the packet back to the snder unless they want that
                if(u.getID() == senderID && !sendBack)
                    continue;
                
                ((ServerUser)u).notifyClient(p);
            }
        }
    }
}