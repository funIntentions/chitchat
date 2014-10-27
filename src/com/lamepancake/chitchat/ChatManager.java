/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.DAO.UserDAOMySQLImpl;
import com.lamepancake.chitchat.packet.BootPacket;
import com.lamepancake.chitchat.packet.ChangeRolePacket;
import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.UpdateChatsPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lamepancake.chitchat.DAO.*;
import com.lamepancake.chitchat.packet.ChatNotifyPacket;
import com.lamepancake.chitchat.packet.LogoutPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.OperationStatusPacket;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author dan
 */
public class ChatManager
{
    /**
     * Contains all users currently logged in.
     */
    private final Map<SelectionKey, ServerUser> lobby;
    
    /**
     * Contains all chats currently available.
     */
    private final Map<Integer, Chat> chats;
        
    /**
     * Initiliases the chat manager and the DAO's.
     * @param uname    The username for the chitchat database.
     * @param password The password for the chitchat database.
     * @throws SQLException If the database could not be initialised.
     */
    public ChatManager(final String uname, final String password) throws SQLException
    {
        UserDAOMySQLImpl.init(uname, password);
        ChatDAOMySQLImpl.init(uname, password);
        ChatRoleDAOMySQLImpl.init(uname, password);
        
        this.lobby = new HashMap<>();
        this.chats = new HashMap<>();
        
        // get stored chats.
        List<Chat> existingChats = ChatDAOMySQLImpl.getInstance().getAllChats();
        
        for (Chat chat : existingChats)
        {
            List<ServerUser> storedUsers = ChatRoleDAOMySQLImpl.getInstance().getUsers(chat);
            
            chat.initUsers(storedUsers);
            
            this.chats.put(chat.getID(), chat);
        }
    }
    
    /**
     * Decides how to handle incoming packets.
     * @param clientKey The client who sent the received packet.
     * @param received The packet that has been received.
     */
    public void handlePacket(SelectionKey clientKey, Packet received)
    {
        int type = received.getType();
        int chatID;
        switch(type)
            {
                case Packet.LOGIN:
                    login(clientKey, (LoginPacket)received);
                    break;
                case Packet.LOGOUT:
                    logoutUser(clientKey, (LogoutPacket)received);
                    break;
                case Packet.CHATLIST:
                    sendListOfChats(clientKey);
                    break;
                case Packet.UPDATECHAT:
                    chatCUD(clientKey, (UpdateChatsPacket)received);
                    break;
                case Packet.BOOT:
                    chatID = ((BootPacket)received).getChatID();
                    PassPacketToChat(clientKey, chatID, received);
                case Packet.REQUESTACCESS:
                    chatID = ((RequestAccessPacket)received).getChatID();
                    if(!PassPacketToChat(clientKey, chatID, received))
                    {
                        sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_REQACCESS);
                    }
                    break;
                case Packet.MESSAGE:
                    chatID = ((MessagePacket)received).getChatID();
                    PassPacketToChat(clientKey, chatID, received);
                    break;
                case Packet.JOINLEAVE:
                    chatID = ((JoinLeavePacket)received).getChatID();
                    PassPacketToChat(clientKey, chatID, received);
                    break;
                case Packet.WHOISIN:
                    chatID = ((WhoIsInPacket)received).getChatID();
                    PassPacketToChat(clientKey, chatID, received);
                    break;
                case Packet.CHANGEROLE:
                    chatID = ((ChangeRolePacket)received).getChatID();
                    PassPacketToChat(clientKey, chatID, received);
                    break;
            }
    }
    
    /**
     * Removes a user from the lobby, logging them out.
     * @param clientKey the client to be logged out.
     * @param packet the packet containing the users ID.
     */
    private void logoutUser(SelectionKey clientKey, LogoutPacket packet)
    {
        this.lobby.remove(clientKey);
    }
    
    /**
     * Sends a list of available chats to a connected client.
     * 
     * @todo Actually use inPacket.
     * 
     * @param clientKey
     */
    private void sendListOfChats(SelectionKey clientKey)
    {
        ChatListPacket outPacket;
        
        ServerUser user = this.lobby.get(clientKey);
        
        List<Chat> chatList;        
        try 
        {
            Map<Integer, Integer> roles = ChatRoleDAOMySQLImpl.getInstance().getChats(user);
            chatList = ChatDAOMySQLImpl.getInstance().getAllChats();
            
            outPacket = PacketCreator.createChatList(chatList, roles, user.getID());
             
            user.notifyClient(outPacket);
             
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.sendListOfChats: SQL exception thrown: " + e.getMessage());
        }
    }
    
    /**
     * Simply passes a packet among for chat to process.
     * @chatID the id of the chat to pass it to.
     * @param received the packet to pass along.
     * @return false if the chat doesn't exist, true if it does.
     */
    private boolean PassPacketToChat(SelectionKey sender, int chatID, Packet received)
    {        
        if (!this.chats.containsKey(chatID))
        {
            return false;
        }
        
        Chat chat = this.chats.get(chatID);
        
        
        chat.handlePacket(sender, received);
        
        return true;
    }
    
    /**
     * TODO: Change this so getRole is relative to chats (will be done in chats).
     * @param clientKey The Client to be checked.
     * @param chatID The id of the chat that the user is being checked for admin status.
     * @param opType The operation the client wants to perform.
     * @return returns true if client is an admin, false otherwise.
     */
    private boolean isAdmin(SelectionKey clientKey, int chatID, int opType)
    {
        ServerUser user = lobby.get(clientKey);
        int userRole;
        
        try 
        {
            userRole = ChatRoleDAOMySQLImpl.getInstance().getUserRoleInChat(chatID, user.getID());

        } catch (SQLException e) 
        {
           System.err.println("ChatManager.verifyAdmin: SQL exception thrown: " + e.getMessage());
           return false;
        }
        
        if (userRole != User.ADMIN)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Tells a ServerUser to notify a client with an operation result.
     * @param clientKey The client it's to be sent to.
     * @param success If the operation was a success or a failure.
     * @param opType The type of operation.
     */
    private void sendOperationResult(SelectionKey clientKey, int success, int opType)
    {
        ServerUser user = lobby.get(clientKey);
        OperationStatusPacket operationStat;
        operationStat = new OperationStatusPacket(user.getID(), success, opType);
        
        user.notifyClient(operationStat);
    }
    
    /**
     * Sends a operation login result when a user login fails.
     * @param clientKey The client it's to be sent to.
     */
    private void sendLoginOperationFailure(SelectionKey clientKey)
    {
        OperationStatusPacket operationStat;
        operationStat = new OperationStatusPacket(-1, OperationStatusPacket.FAIL, OperationStatusPacket.OP_LOGIN);
        
        try 
        {
            SocketChannel channel = (SocketChannel)clientKey.channel();
            channel.write(operationStat.serialise()); 
        } catch (IOException e) {
            System.err.println("ChatManager.sendLoginOperationResult: Could not send message: " + e.getMessage());
        }
    }
    
    /**
     * Handles Create, Update and Destroy (CUD) commands for chats.
     * @param clientKey The client sending the command.
     * @param received The packet containing the chat update info.
     */
    private void chatCUD(SelectionKey clientKey, UpdateChatsPacket received)
    {
        int type = received.getUpdate();

        switch (type)
        {
            case UpdateChatsPacket.CREATE:
                createChat(clientKey, received);
                break;
            case UpdateChatsPacket.UPDATE:
                if (isAdmin(clientKey, received.getChatID(), OperationStatusPacket.OP_CRUD))
                {
                    updateChat(clientKey, received);
                }
                else
                {
                    sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
                }
                break;
            case UpdateChatsPacket.DELETE:
                if (isAdmin(clientKey,received.getChatID(), OperationStatusPacket.OP_CRUD))
                {
                    deleteChat(clientKey, received);
                }
                else
                {
                    sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
                }
                break;
        }
       
    }
    
    /**
     * Deletes a chat, removing it from the database and chat list.
     * @param chatInfo 
     */
    private void deleteChat(SelectionKey clientKey, UpdateChatsPacket chatInfo)
    {
        Chat chat;
        
        if (!this.chats.containsKey(chatInfo.getChatID()))
        {
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
            return;
        }
        
        chat = this.chats.get(chatInfo.getChatID());
        
        
        try 
        {
            ChatDAOMySQLImpl.getInstance().delete(chat);
            this.chats.remove(chat.getID());
            
            sendOperationResult(clientKey, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_CRUD);
            
            notifyUsersOfChatUpdate(chat, UpdateChatsPacket.DELETE);

        } catch (SQLException e) 
        {
            System.err.println("ChatManager.deleteChat: SQL exception thrown: " + e.getMessage());
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
        }
    }
    
    /**
     * Updates a chat, changing it's name.
     * @param chatInfo Contains the new 
     */
    private void updateChat(SelectionKey clientKey, UpdateChatsPacket chatInfo)
    {
        Chat chat;
        
        if (!this.chats.containsKey(chatInfo.getChatID()))
        {
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
            return;
        }
        
        chat = this.chats.get(chatInfo.getChatID());
                
        if (isChatNameTaken(chatInfo.getName()))
        {
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
            return;
        }
        
        try 
        {
            chat.setName(chatInfo.getName());
            ChatDAOMySQLImpl.getInstance().update(chat);
            
            sendOperationResult(clientKey, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_CRUD);
            
            notifyUsersOfChatUpdate(chat, UpdateChatsPacket.UPDATE);
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.updateChat: SQL exception thrown: " + e.getMessage());
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
        }
    }
    
    /**
     * Creates a new chat. Promotes the creator as the chats admin.
     * @param clientKey The chat's creator.
     * @param chatInfo Info for the chat being created.
     */
    private void createChat(SelectionKey clientKey, UpdateChatsPacket chatInfo)
    {
        User user = this.lobby.get(clientKey);
        
        if (isChatNameTaken(chatInfo.getName()))
        {
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
            return;
        }
        
        try 
        {
            // Create chat.
            Chat chat = new Chat(chatInfo.getName());
            chat.setName(chatInfo.getName());
            int chatID = ChatDAOMySQLImpl.getInstance().create(chat);
            this.chats.put(chatID, chat);
            chat.setID(chatID);
            
            //Make user admin for chat.
            ChatRoleDAOMySQLImpl.getInstance().addUser(chatID, user.getID(), User.ADMIN);

            sendOperationResult(clientKey, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_CRUD);
            
            notifyUsersOfChatUpdate(chat, UpdateChatsPacket.CREATE);
            
            chat.initUser(user);
            
            ((ServerUser)user).notifyClient(PacketCreator.createChangeRole(chatID, user.getID(), User.ADMIN));
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.createChat: SQL exception thrown: " + e.getMessage());
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
        }
    }
    
    /**
     * Checks to see if a chat name is already being used.
     * @param name The name to check for.
     * @return true if it's already in use, false otherwise.
     */
    private boolean isChatNameTaken(String name)
    {
        Iterator it = this.chats.entrySet().iterator();
        Chat existingChat;
        
        // check if name is unique
        while (it.hasNext()) 
        {
            Map.Entry pairs = (Map.Entry)it.next();
            existingChat = (Chat)pairs.getValue();
            
            if (existingChat.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Notifies all users that a particular chat has been updated.
     * @param chat The chat that's been updated.
     * @param flag The type of update that happened.
     */
    private void notifyUsersOfChatUpdate(Chat chat, int flag)
    {
        Set<SelectionKey>           keys;
        keys = this.lobby.keySet();
        
        ChatNotifyPacket notify = PacketCreator.createChatNotify(chat.getID(), chat.getName(), flag);
        
        for (SelectionKey key : keys)
        {
            ServerUser user = this.lobby.get(key);
            
            user.notifyClient(notify);
        }
    }
    
    /**
     * Checks to see if a user exists, if so, they'll be logged in and if not they'll be created.
     * 
     * @param key The selection key that gets mapped to a user.
     * @param loginInfo The info that defines a user.
     */    
    private void login(SelectionKey key, LoginPacket loginInfo)
    {
        
        ServerUser user;
        
        try 
        {
            user = UserDAOMySQLImpl.getInstance().getByName(loginInfo.getUsername());

        } catch (SQLException e) 
        {
            System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
            sendLoginOperationFailure(key);
            return;
        }
        
        if(user != null) // user does exist
        {
            if (user.getPassword().equals(loginInfo.getPassword())) // match passwords
            {
                user.setSocket((SocketChannel)key.channel());
                this.lobby.remove(key);
                this.lobby.put(key, user);
                sendOperationResult(key, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_LOGIN); 
                sendListOfChats(key);
            }
            else
            {
                sendLoginOperationFailure(key);
            }
        }
        else // user doesn't exists
        {
            createUser(key, loginInfo);
        }
    }
    
    /**
     * Creates a user and adds them to the lobby.
     * @param key The SelectedKey of the User that's created.
     * @param loginInfo The login info used to create the new user.
     * @return the newly created User.
     */
    private void createUser(SelectionKey key, LoginPacket loginInfo)
    {
        
        ServerUser user = new ServerUser();
        user.setName(loginInfo.getUsername()).setPassword(loginInfo.getPassword());
        user.setSocket((SocketChannel)key.channel());

        try 
        { 
            int id = UserDAOMySQLImpl.getInstance().create(user);
            user.setID(id);
            lobby.put(key, user);
            sendOperationResult(key, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_LOGIN); 
            sendListOfChats(key);
        } catch (SQLException e) {
            System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
            sendLoginOperationFailure(key);
        }
        
    }
    
    /**
     * Adds a selection key of a client to the map prior to login.
     * @param key 
     */
    public void addClient(SelectionKey key)
    {
        lobby.put(key, null);
    }
    
}
