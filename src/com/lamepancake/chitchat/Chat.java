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
import com.lamepancake.chitchat.packet.OperationStatusPacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import com.lamepancake.chitchat.packet.UserNotifyPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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
    
    /**
     * 
     * @param initialUsers 
     */
    public void initUsers(List<? extends User> initialUsers)
    {
        for (User user : initialUsers)
        {
            this.users.put(user, Boolean.FALSE);
        }
    }
    
    public void addWaitingUserClient(User user, boolean online)
    {
        this.users.put(user, online);
    }
    
    public void initUser(User newUser)
    {
        this.users.put(newUser, Boolean.FALSE);
    }
    
    /**
     * used client side.
     * @param newUser
     * @param online 
     */
    public void initUser(User newUser, boolean online)
    {
        this.users.put(newUser, online);
    }
    
    /**
     * Used on client side.
     * @param userName
     * @return 
     */
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
    
    /**
     * Used on client side. & on Server
     * @param userID
     * @return 
     */
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
     * @param sender   The SelectionKey of the sender.
     * @todo Implement this...
     */
    public void handlePacket(SelectionKey sender, Packet received)
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
                updateState(sender, (JoinLeavePacket)received);
                break;
            case Packet.WHOISIN:
                sendUserList((WhoIsInPacket)received);
                break;
            case Packet.CHANGEROLE:
                promoteUser((ChangeRolePacket)received);
                break;
            case Packet.REQUESTACCESS:
                addWaitingUser(sender, (RequestAccessPacket)received);
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
        String name = null;
        
        // Remove the user from the map
        for(User u: users.keySet())
        {
            if(u.getID() == affectedUserID)
            {
                users.remove(u);
                name = u.getName();
                break;
            }
        }
        
        if (name == null)
        {
            System.err.println("Chat.addWaitingUser: name was null... what?");
            return;
        }

        p = PacketCreator.createUserNotify(name, affectedUserID, this.chatID, User.UNSPEC, Packet.BOOT);
        broadcast(b.getBooterID(), p, false, true);
    }
    
    /**
     * Adds a User to the database with a status of User.WAITING and 
     * @param r 
     */
    private void addWaitingUser(SelectionKey sender, RequestAccessPacket r)
    {
        Packet operationStatus;
        int flag = 1;
        User requester = null; 
        String name;
        
        if (findUser(r.getUserID()) != null)
        {
            System.out.println("Already Waiting.");
            return;
        }
        
        try 
        {
            // Attempt to add the user to the database and add them to the chat's
            // user list
            ChatRoleDAOMySQLImpl.getInstance().addUser(chatID, r.getUserID(), User.WAITING);
            requester = UserDAOMySQLImpl.getInstance().getByID(r.getUserID());
            requester.setRole(User.WAITING);
            this.users.put(requester, Boolean.FALSE);
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.addWaitingUser: SQL exception thrown: " + e.getMessage());
            flag = 0;
        }
              
        operationStatus = PacketCreator.createOperationStatus(r.getUserID(), this.chatID, flag, OperationStatusPacket.OP_REQACCESS);
        // Notify all other users in the chat that a new user is waiting to join if the database op succeeded
        if(flag == 1)
        {
            // Assign the user a socket and send them the packet confirming their new role
            ((ServerUser)requester).setSocket((SocketChannel) sender.channel());
            ((ServerUser)requester).notifyClient(operationStatus);
            
            name = requester.getName();
            Packet p = PacketCreator.createUserNotify(name, r.getUserID(), r.getChatID(), User.WAITING, UserNotifyPacket.WAITING);
            broadcast(r.getUserID(), p, false, true);
        }
        // Don't know why operation failed; user may not exist, send directly over socket
        else
        {
            try {
                SocketChannel s = (SocketChannel)sender.channel();
                s.write(operationStatus.serialise());
            } catch (IOException i) {
                System.err.println("Chat.addWaitingUser: could not send OperationStatusPacket: " + i.getMessage());
            }
        }
    }
    
    /**
     * Promote or demote a user.
     * 
     * Note that the ChatManager will notify the user being promoted/demoted of
     * the change, since it is the only object that knows whether a user is
     * actually connected to the server. The Chat is still responsible for notifying
     * the sender whether the operation succeeded.
     * @param r 
     */
    private void promoteUser(ChangeRolePacket r)
    {
        OperationStatusPacket opStat;
        int flag = OperationStatusPacket.SUCCESS;
        User sender = null;
        User affected = null;
        
        for (User u : users.keySet())
        {
            if(u.getID() == r.getSenderID())
            {
                sender = u;
            }
            else if(u.getID() == r.getUserID())
            {
                affected = u;
            }
            if(sender != null && affected != null)
                break;
        }
        
        // This should *never* happen
        if(sender == null)
            return;
        if(affected != null)
        {
            try 
            {
                // Update the user's role.
                ChatRoleDAOMySQLImpl.getInstance().updateRole(chatID, r.getUserID(), r.getRole());
                
                affected.setRole(r.getRole());
                // If the SQL statement fails, this will never run, so it's safe to do here
                Packet p = PacketCreator.createUserNotify(affected.getName(), r.getUserID(), r.getChatID(), r.getRole(), UserNotifyPacket.PROMOTED);
                broadcast(r.getUserID(), p, false, true);

            } catch (SQLException e) 
            {
                System.err.println("Chat.promoteUser: SQL exception thrown: " + e.getMessage());
                flag = OperationStatusPacket.FAIL;
            }
        }
        else
            flag = OperationStatusPacket.FAIL;
        
        // Send them an OperationStatusPacket no matter what
        opStat = PacketCreator.createOperationStatus(r.getSenderID(), chatID, flag, OperationStatusPacket.OP_CRUD);
        ((ServerUser)sender).notifyClient(opStat);
    }
    
    /**
     * Removes or adds a User to the list of subscribed users and notifies other
     * Users in the chat.
     * @param jl The JoinLeave packet specifying whether the user is joining or
     *           leaving.
     */
    private void updateState(SelectionKey sender, JoinLeavePacket jl)
    {
        System.out.println(jl.getFlag());
        if(jl.getFlag() == JoinLeavePacket.JOIN)
        {
            System.out.println("hello");
            join(sender, jl);
        }
        else
        {
            System.out.println("bye");
            leave(jl);
        }
        
    }
    
    /**
     * Adds the user to the chat and notifies the other users.
     * 
     * If the user is currently waiting to join the chat, 
     * @param sender
     * @param jl 
     */
    private void join(SelectionKey sender, JoinLeavePacket jl)
    {   
        Packet whoisin;
        Packet notify;
        User affected = null;
        
        for(User u : users.keySet())
        {
            if(u.getID() == jl.getUserID())
            {
                affected = u;
                break;
            }
        }
                
        if (affected == null || users.get(affected))
        {
            // Error condition
            System.out.println("Already Joined or Doesn't Exist.");
            return;
        }
        
        // The rest of the chat has already been notified if a user is waiting;
        // only notify people when someone who has a role in the chat joins
        if(affected.getRole() != User.WAITING)
        {
            users.remove(affected);
            users.put(affected, Boolean.TRUE);        
            notify = PacketCreator.createUserNotify(affected.getName(), jl.getUserID(),
                                                    this.chatID, affected.getRole(),
                                                    UserNotifyPacket.JOINED);
            broadcast(jl.getUserID(), notify, false, true);
        }
        
        // Still need to send them a WhoIsIn regardless 
        whoisin = PacketCreator.createWhoIsIn(users, chatID, jl.getUserID());
        ((ServerUser)affected).setSocket((SocketChannel)sender.channel());
        ((ServerUser)affected).notifyClient(whoisin);
        ((ServerUser)affected).notifyClient(jl);
    }
    
    /**
     * Makes a user appear offline in this chat.
     * 
     * Sets the online flag to false and notifies all other users that the user
     * left.
     * @param jl The JoinLeave packet containing the user's information.
     */
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
        
        p = PacketCreator.createUserNotify(affected.getName(), jl.getUserID(), this.chatID, affected.getRole(), UserNotifyPacket.LEFT);
        broadcast(jl.getUserID(), p, false, true);
    }
    
    /**
     * Returns a map of users associated with this chat and their online statuses.
     * 
     * @return A map of users in the chat to their online statuses.
     */
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
         
        broadcast(message.getUserID(), message, false, false);
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
     * @param senderID      The key associated with the sending user.
     * @param p             The packet to be broadcasted.
     * @param sendBack      Whether the user should have the packet sent back to them.
     * @param notifyWaiting Whether to notify users who have not yet been added.
     */
    private void broadcast(int senderID, Packet p, boolean sendBack, boolean notifyWaiting)
    {        
        for(User u : users.keySet())
        {
            Boolean isOnline = users.get(u);
            if (isOnline && u.getRole() != User.WAITING)
            {
                if(!notifyWaiting && u.getRole() == User.WAITING)
                    continue;

                // Don't send the packet back to the snder unless they want that
                if(u.getID() == senderID && !sendBack)
                    continue;
                
                ((ServerUser)u).notifyClient(p);
            }
        }
    }
}