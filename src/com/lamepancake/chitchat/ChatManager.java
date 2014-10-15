/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import static com.lamepancake.chitchat.User.ADMIN;
import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.GrantAccessPacket;
import com.lamepancake.chitchat.packet.LeftPacket;
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

/**
 *
 * @author shane
 */
public class ChatManager
{
     /**
     * The id for the next user.
     */
    private int nextId = 0;
    
    
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
     * The id for the next user.
     */
    private int nextChatId = 0;
    
    public ChatManager()
    {
        this.lobby = new HashMap<>();
        this.chats = new HashMap<>();
        this.recycledChatIDs = new ArrayList<>();
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
        LeftPacket left;
        
        Chat chat = this.chats.get(chatID); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        id           = users.get(sel).getID();
        
        
        left = new LeftPacket(id);

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
    
    
    private void createNewChat(SelectionKey key, UpdateChatsPacket chatInfo)
    {
        Chat newChat;
        
        String name = chatInfo.getName();
        int id = getUniqueChatID();
        
        newChat = new Chat(name, id);
        
        this.chats.put(id, newChat);
    }
    
    private void updateChat(UpdateChatsPacket chatInfo)
    {
        Chat chat;
        
        String name = chatInfo.getName();
        int id = chatInfo.getChatID();
        
        chat = chats.get(id);
        chat.setName(name);
    }
    
    private int getUniqueChatID()
    {
        int ID;
        
        if (!recycledChatIDs.isEmpty())
        {
            ID = recycledChatIDs.get(0);
            recycledChatIDs.remove(0);
        }
        else
        {
            ID = nextChatId++;
        }
        
        return ID;
    }
    
    private int getUniqueID()
    {
        int ID;
        
        if (!recycledIDs.isEmpty())
        {
            ID = recycledIDs.get(0);
            recycledIDs.remove(0);
        }
        else
        {
            ID = nextId++;
        }
        
        return ID;
    }
    
    
    /**
     * Sets user role.
     * @param key
     * @param userInfo 
     */
    private void setUserRole(Map<SelectionKey, User> map, SelectionKey key, GrantAccessPacket userInfo)
    {
        User waitingUser = map.get(key);
        waitingUser.setRole(userInfo.getUserRole());
        
        //announceJoin(key, waitingUser);
    }
    
    /**
     * Removes a user from the chat.
     * 
     * @param selected The selected user to remove.
     * @param userInfo The packet being sent to them.
     */
    private void removeUserFromChat(SelectionKey selected, GrantAccessPacket userInfo)
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
        int newId = getUniqueID();
                
        // The client sent another login packet; ignore it.
        if(lobby.get(key) != null)
            return;
        
        int role = loginInfo.getUsername().equalsIgnoreCase("Admin") ? ADMIN: User.UNSPEC;
        System.out.println(role);
        newUser = new User().setName(loginInfo.getUsername()).setPassword(loginInfo.getPassword());        
        lobby.put(key, newUser);
        
        // Send a list of connected clients immediately after being added to the chat.
        //sendUserList(key, WhoIsInPacket.CONNECTED); Needed?
    }
    
    /**
     * Check user presence
     * @param key
     * @param userInfo 
     */
    private SelectionKey userCheck(SelectionKey key, GrantAccessPacket userInfo)
    {
        Set<SelectionKey>       userChannels;
        int                     userID       = userInfo.getUserID();
        SelectionKey            sel          = null;
        
        /*if(userChannels.isEmpty()) // if the admin is trying to add users that dont exist
        {
            return;
        }*/
        
        Chat chat = this.chats.get(userInfo.getChatID()); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        
        for(SelectionKey curKey : userChannels)
        {
            User user = users.get(curKey);
            
            if (user.getID() == userID)
            {
                sel = curKey;
                break;
            }
        }
        
        /*if (sel == null) // if the admin is trying to add users that dont exist
        {
            return;
        }  */    
        
        return sel;
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
