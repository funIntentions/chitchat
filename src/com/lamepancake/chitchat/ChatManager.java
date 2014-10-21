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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lamepancake.chitchat.DAO.*;
import com.lamepancake.chitchat.packet.ChatNotifyPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.OperationStatusPacket;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author dan
 */
public class ChatManager
{
    
    private final Map<SelectionKey, ServerUser> lobby;
    
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
    }
    
    public void handlePacket(SelectionKey clientKey, Packet received)
    {
        int type = received.getType();
        int chatID = -1;
        
        switch(type)
            {
                case Packet.LOGIN:
                    login(clientKey, (LoginPacket)received);
                    break;
                case Packet.CHATLIST:
                    sendListOfChats(clientKey, (ChatListPacket)received);
                    break;
                case Packet.UPDATECHAT:
                    chatCUD(clientKey, (UpdateChatsPacket)received);
                    break;
                case Packet.BOOT:
                    chatID = ((BootPacket)received).getChatID();
                    PassPacketToChat(chatID, received);
                case Packet.REQUESTACCESS:
                    chatID = ((RequestAccessPacket)received).getChatID();
                    if(!PassPacketToChat(chatID, received))
                    {
                        sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_REQACCESS);
                    }
                    break;
                case Packet.MESSAGE:
                    chatID = ((MessagePacket)received).getChatID();
                    PassPacketToChat(chatID, received);
                    break;
                case Packet.JOINLEAVE:
                    chatID = ((JoinLeavePacket)received).getChatID();
                    PassPacketToChat(chatID, received);
                    break;
                case Packet.WHOISIN:
                    chatID = ((WhoIsInPacket)received).getChatID();
                    PassPacketToChat(chatID, received);
                    break;
                case Packet.CHANGEROLE:
                    chatID = ((ChangeRolePacket)received).getChatID();
                    PassPacketToChat(chatID, received);
                    break;
            }
    }
    
    /**
     * Sends a list of available chats to a connected client.
     * @param clientKey
     * @param inPacket 
     */
    private void sendListOfChats(SelectionKey clientKey, ChatListPacket inPacket)
    {
        ChatListPacket outPacket;
        
        ServerUser user = this.lobby.get(clientKey);
        
        List<Chat> chatList = new ArrayList<>();
        
        Iterator it = this.chats.entrySet().iterator();
        
        while (it.hasNext()) 
        {
            Map.Entry pairs = (Map.Entry)it.next();
            chatList.add((Chat)pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        try 
        {
            Map<Integer, Integer> roles = ChatRoleDAOMySQLImpl.getInstance().getChats(user);

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
    private boolean PassPacketToChat(int chatID, Packet received)
    {        
        if (!this.chats.containsKey(chatID))
        {
            return false;
        }
        
        Chat chat = this.chats.get(chatID);
        
        
        chat.handlePacket(received);
        
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
        int userRole = User.UNSPEC;
        
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
    
    private void sendOperationResult(SelectionKey clientKey, int success, int opType)
    {
        ServerUser user = lobby.get(clientKey);
        OperationStatusPacket operationStat;
        operationStat = new OperationStatusPacket(user.getID(), success, opType);
        
        user.notifyClient(operationStat);
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
            case UpdateChatsPacket.REMOVE:
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
            
            notifyUsersOfChatUpdate(chat, UpdateChatsPacket.REMOVE);

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
            
            //Make user admin for chat.
            ChatRoleDAOMySQLImpl.getInstance().updateRole(chatID, user.getID(), User.ADMIN);
            
            sendOperationResult(clientKey, OperationStatusPacket.SUCCESS, OperationStatusPacket.OP_CRUD);
            
            notifyUsersOfChatUpdate(chat, UpdateChatsPacket.CREATE);
            
        } catch (SQLException e) 
        {
            System.err.println("ChatManager.createChat: SQL exception thrown: " + e.getMessage());
            sendOperationResult(clientKey, OperationStatusPacket.FAIL, OperationStatusPacket.OP_CRUD);
        }
    }
    
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
    
    
    private void notifyUsersOfChatUpdate(Chat chat, int flag)
    {
        Set<SelectionKey>           keys;
        keys = lobby.keySet();
        
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
        OperationStatusPacket operationStat;
        
        if(lobby.get(key) != null) // user does exist
        {
            try 
            {
                user = UserDAOMySQLImpl.getInstance().getByName(loginInfo.getUsername());
                
                if (user.getPassword().equals(loginInfo.getPassword())) // match passwords
                {
                    user.setSocket((SocketChannel)key.channel());
                    lobby.put(key, user);
                    operationStat = new OperationStatusPacket(user.getID(), 
                                                              OperationStatusPacket.SUCCESS,
                                                              OperationStatusPacket.OP_LOGIN);
                }
                else
                {
                    operationStat = new OperationStatusPacket(0,
                                                              OperationStatusPacket.FAIL,
                                                              OperationStatusPacket.OP_LOGIN);
                }
                
            } catch (SQLException e) 
            {
                System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
                operationStat = new OperationStatusPacket(0,
                                                          OperationStatusPacket.FAIL,
                                                          OperationStatusPacket.OP_LOGIN);
            }
        }
        else // user doesn't exists
        {
            try 
            { 
                user = createUser(key, loginInfo);
                UserDAOMySQLImpl.getInstance().create(user);
                operationStat = new OperationStatusPacket(user.getID(),
                                                          OperationStatusPacket.SUCCESS,
                                                          OperationStatusPacket.OP_LOGIN);
                
            } catch (SQLException e) {
                System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
                operationStat = new OperationStatusPacket(0, 
                                                          OperationStatusPacket.FAIL,
                                                          OperationStatusPacket.OP_LOGIN);
            }
        }
        
        try 
        {
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(operationStat.serialise()); 
        } catch (IOException e) {
            System.err.println("ChatManager.login: Could not send message: " + e.getMessage());
        }
    }
    
    /**
     * Creates a user and adds them to the lobby.
     * @param key The SelectedKey of the User that's created.
     * @param loginInfo The login info used to create the new user.
     * @return the newly created User.
     */
    private ServerUser createUser(SelectionKey key, LoginPacket loginInfo)
    {
        ServerUser user = new ServerUser();
        user.setName(loginInfo.getUsername()).setPassword(loginInfo.getPassword());
        
        user.setSocket((SocketChannel)key.channel());
        
        lobby.put(key, user);
        
        return user;
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
