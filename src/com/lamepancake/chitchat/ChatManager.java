/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.DAO.UserDAOMySQLImpl;
import static com.lamepancake.chitchat.User.ADMIN;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lamepancake.chitchat.DAO.*;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.OperationStatusPacket;
import java.sql.SQLException;

/**
 *
 * @author shane
 */
public class ChatManager
{
    
    private final Map<SelectionKey, User> lobby;
    
    private final Map<Integer, Chat> chats;
    
    /**
     * The server will continue until this variable becomes false.
     */
    private boolean keepGoing;
    
    /**
     * ChatIDs for chats that have been removed that have left.
     */
    private List<Integer> recycledChatIDs;
    
    /**
     * IDs for previous users that have left.
     */
    private List<Integer> recycledIDs;
    
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
        
        switch(type)
            {
                case Packet.LOGIN:
                    login(clientKey, (LoginPacket)received);
                    break;
                case Packet.MESSAGE:
                    //sendMessage(clientKey, (MessagePacket)received);
                    break;
                case Packet.JOINLEAVE:
                    //remove(clientKey, ((LogoutPacket)received).getChatID());
                    //or
                    //addUserToChat(clientKey, (JoinedPacket)received); 
                    break;
                case Packet.WHOISIN:
                    //sendUserList(clientKey, ((WhoIsInPacket)received).whichList());
                    break;
                case Packet.CHATLIST:
                    sendChatList(clientKey);
                    break;
                case Packet.UPDATECHAT:
//                    switch(((UpdateChatsPacket)received).getUpdate())
//                    {
//                        case UpdateChatsPacket.CREATE:
//                            createNewChat(clientKey, (UpdateChatsPacket)received);
//                            break;
//                        case UpdateChatsPacket.REMOVE:
//                            break;
//                        case UpdateChatsPacket.UPDATE:
//                            updateChat(clientKey, (UpdateChatsPacket)received);
//                            break;
//                    }
                    break;
                case Packet.CHANGEROLE:
                    break;
                case Packet.REQUESTACCESS:
                    break;
//                case Packet.GRANTACCESS:
//                    Chat chat = this.chats.get(((GrantAccessPacket)received).getChatID()); 
//                    Map<SelectionKey, User> users = chat.getConnectedUsers();
//                    
//                    User sender = users.get(clientKey);
//                    if(sender != null && sender.getRole() == User.ADMIN)
//                    {
//                        SelectionKey selected = userCheck(clientKey, (GrantAccessPacket)received);
//                        
//                        if(selected != null)
//                        {
//                            User user = users.get(selected);
//                            
//                            if (((GrantAccessPacket)received).getUserRole() == User.UNSPEC)
//                            {
//                                removeUserFromChat(selected, (GrantAccessPacket)received);
//                            }
//                            else if (user == null)
//                            {
//                                setUserRole(users, selected, (GrantAccessPacket)received);
//                                addUserToChat(selected, (GrantAccessPacket)received);
//                            }
//                            else
//                            {
//                                updateList(selected, ((GrantAccessPacket)received).getChatID());
//                                
//                                setUserRole(users, selected, (GrantAccessPacket)received);
//                                updateUserInChat(selected, (GrantAccessPacket)received);
//                            }
//                        }
//                        
//                    }
//                    else
//                    {
//                        System.err.println("Access requirement not met for this command.");
//                    }
//                    break;
            }
    }
     /**
     * Updates a user from the list, cleans up its socket, and notifies other users.
     * 
     * @param sel The selection key identifying the user.
     */
    private void updateList(SelectionKey sel, int chatID)
    {
        Set<SelectionKey> userChannels;
        int id;
        
        Chat chat = this.chats.get(chatID); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        id           = users.get(sel).getID();
    }
    
        /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserLobbyList(Map<SelectionKey, User> lobby, SelectionKey clientKey, int list)
    {
        ArrayList<User>             userList;
        Set<SelectionKey>           keys;
        WhoIsInPacket               packet;
        Map<SelectionKey, User>     userMap;
        int                size = 4; // One extra int for the number of users
                
        userList = new ArrayList<>(lobby.size());
        keys = lobby.keySet();
        userMap = lobby;
        
        
        for (SelectionKey key : keys)
        {
            User u = userMap.get(key);

            // Add space for the user's name and three ints (name length, role, id)
            size += u.getName().length() * 2;
            size += 12;
            
            userList.add(u);
        }
        
//        packet = new WhoIsInPacket(userList, size, list, 1); // one is temp?
//        try {
//            ((SocketChannel)clientKey.channel()).write(packet.serialise());
//        } catch (IOException e) {
//            System.err.println("Server.sendUserList: Could not send list: " + e.getMessage());
//        }
    }
   
    private void createNewChat(SelectionKey key, UpdateChatsPacket chatInfo)
    {
        Chat newChat;
        
        String name = chatInfo.getName();        
        //newChat = new Chat(name, id);
        
        //this.chats.put(id, newChat);
    }
    
    private void updateChat(UpdateChatsPacket chatInfo)
    {
        Chat chat;
        
        String name = chatInfo.getName();
        int id = chatInfo.getChatID();
        
        chat = chats.get(id);
        chat.setName(name);
    }
        
    /**
     * Sets user role.
     * @param key
     * @param userInfo 
     */
    private void setUserRole(Map<SelectionKey, User> map, SelectionKey key, ChangeRolePacket userInfo)
    {
        User waitingUser = map.get(key);
        waitingUser.setRole(userInfo.getRole());
        
        //announceJoin(key, waitingUser);
    }
    
    /**
     * Removes a user from the chat.
     * 
     * @param selected The selected user to remove.
     * @param userInfo The packet being sent to them.
     */
    private void bootUserFromChat(SelectionKey selected, BootPacket userInfo)
    {
        // inform the user they have been booted from the chat.
        try {
            SocketChannel channel = (SocketChannel)selected.channel();
            channel.write(userInfo.serialise());              
        } catch (IOException e) {
            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
        }
        
        remove(selected, userInfo.getChatID());
    }
    
    /**
     * Checks to see if a user exists, if so, they'll be logged in and if not they'll be created.
     * 
     * @param key The selection key that gets mapped to a user.
     * @param loginInfo The info that defines a user.
     */    
    private void login(SelectionKey key, LoginPacket loginInfo)
    {
        
        User user = null;
        OperationStatusPacket operationStat;
        
        if(lobby.get(key) != null) // user does exist
        {
            try 
            {
                user = UserDAOMySQLImpl.getInstance().getByName(loginInfo.getUsername());
                
                if (user.getPassword().equalsIgnoreCase(loginInfo.getPassword())) // match passwords
                {
                    user.setSocket((SocketChannel)key.channel());
                    lobby.put(key, user);
                    
                    operationStat = new OperationStatusPacket(user.getID(), OperationStatusPacket.OP_LOGIN, 1);
                }
                else
                {
                    operationStat = new OperationStatusPacket(0, OperationStatusPacket.OP_LOGIN, 0);
                }
                
            } catch (SQLException e) 
            {
                System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
                
                operationStat = new OperationStatusPacket(0, OperationStatusPacket.OP_LOGIN, 0);
            }
        }
        else // user doesn't exists
        {
            try 
            {
                user = new User().setName(loginInfo.getUsername()).setPassword(loginInfo.getPassword());
                
                UserDAOMySQLImpl.getInstance().create(user);
                
                user.setSocket((SocketChannel)key.channel());
                
                lobby.put(key, user);
                    
                operationStat = new OperationStatusPacket(user.getID(), OperationStatusPacket.OP_LOGIN, 1);
                
            } catch (SQLException e) 
            {
                System.err.println("ChatManager.login: SQL exception thrown: " + e.getMessage());
                
                operationStat = new OperationStatusPacket(0, OperationStatusPacket.OP_LOGIN, 0);
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
     * Adds a selection key of a client to the map prior to login.
     * @param key 
     */
    public void addClient(SelectionKey key)
    {
        lobby.put(key, null);
    }
    
    /**
     * Removes a user from the list, cleans up its socket, and notifies other users.
     * 
     * @param sel The selection key identifying the user.
     */
    private void remove(SelectionKey sel, int chatID)
    {
        Set<SelectionKey> userChannels;
        int id;
        JoinLeavePacket left;
        
        Chat chat = this.chats.get(chatID); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        if (users.containsKey(sel))
        {
            userChannels = users.keySet();
            id           = users.get(sel).getID();
            users.remove(sel);
        }
        else 
        {
            userChannels = this.lobby.keySet();
            id           = this.lobby.get(sel).getID();
            this.lobby.remove(sel);
        }
        
        left = new JoinLeavePacket(id, chatID, JoinLeavePacket.LEAVE);
        
        recycledIDs.add(id);

        sel.cancel();
        sel.attach(null);

        try{
            sel.channel().close();
        } catch(IOException e) {
            System.err.println("Server.remove: Could not close channel: " + e.getMessage());
        }
               
        // No one left in the chat; no one to notify
        if(userChannels.isEmpty())
            return;
        
        for(SelectionKey curKey : userChannels)
        {
            try {
                SocketChannel channel = (SocketChannel)curKey.channel();
                channel.write(left.serialise());              
            } catch (IOException e) {
                System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a ChatListPacket and sends it to the requesting client.
     * 
     * @param clientKey  The SelectionKey associated with this client.
     */
    private void sendChatList(SelectionKey clientKey)
    {
        ArrayList<Chat>         chatList = new ArrayList<>();
        Set<Integer>            keys;
        ChatListPacket          packet;
        int                     size = 4;
        
        keys = this.chats.keySet();
        for (Integer key : keys)
        {
            Chat c = chats.get(key);

            // Add space for the user's name and three ints (name length, role, id)
            size += c.getName().length() * 2;
            size += 8;
            
            chatList.add(c);
        }
        
        packet = new ChatListPacket(chatList, size);
        try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendChatList: Could not send list: " + e.getMessage());
        }
    }
       
}
