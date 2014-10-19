/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import static com.lamepancake.chitchat.User.ADMIN;
import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.UpdateChatsPacket;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lamepancake.chitchat.DAO.*;
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
    
    public void handlePacket(Packet p)
    {
        int type = p.getType();
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
        
        packet = new WhoIsInPacket(userList, size, list, 1); // one is temp?
        try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendUserList: Could not send list: " + e.getMessage());
        }
    }
   
    private void createNewChat(SelectionKey key, UpdateChatsPacket chatInfo)
    {
        Chat newChat;
        
        String name = chatInfo.getName();        
        newChat = new Chat(name, id);
        
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
     * Associates the new user with the selection key.
     * 
     * @param key       The SelectionKey with which to associate the user.
     * @param loginInfo The LoginPacket containing the user's information.
     * @todo Add user validation (e.g. check for username/password in DB).
     * @todo Remove user.UNSPEC role.
     */
    private void login(SelectionKey key, LoginPacket loginInfo)
    {
        User newUser;

        if(lobby.get(key) != null)
        {
            //OperationStatusPacket loginstatus
            return;
        }
        // Check to make sure that the User isn't already logged in
        // If they are
        //      Send OperationStatusPacket with OP_LOGIN and 0
        //
        // Get user info from database
        // If it exists
        //      Check that the username/password match
        //      If not, send OperationStatusPacket with OP_LOGIN and 0
        //      Else
        //          Set User object's socket to SelectionKey.channel();
        //          Add User object to the map
        //          Send OperationStatusPacket with OP_LOGIN, user's ID, and 1
        // Else
        //      Attempt to create a new User in the database with specified username and pw
        //      If it succeeds
        //          Set User object's socket to SelectionKey.channel();
        //          Add User object to the map
        //          Send OperationStatusPacket with OP_LOGIN, user's ID, and 1
        //      Else
        //          Send OperationStatusPacke with OP_LOGIN and 0
        // 
        //

        newUser = new User().setName(loginInfo.getUsername()).setPassword(loginInfo.getPassword());        
        lobby.put(key, newUser);
        
        // Send a list of connected clients immediately after being added to the chat.
        //sendUserList(key, WhoIsInPacket.CONNECTED); Needed?
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
        LeftPacket left;
        
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
        
        left = new LeftPacket(id);
        
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
