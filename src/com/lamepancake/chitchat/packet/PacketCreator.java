/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.util.Map;
import com.lamepancake.chitchat.Chat;
import java.util.List;

/**
 *
 * @author shane
 */
public class PacketCreator {
    private PacketCreator()
    {}
    
    /**
     * Creates a WhoIsInPacket from the given information.
     * 
     * @param chatUsers The users in the chat and their online statuses.
     * @param chatID    The ID of the chat containing the users.
     * @param userID    The ID of the user requesting the packet.
     * @return          The WhoIsInPacket to be transmitted to the client.
     */
    public static WhoIsInPacket createWhoIsIn(Map<User, Boolean> chatUsers, int chatID, int userID)
    {
        // Each boolean in the map is one byte, so set the length to this size initially
        int dataLen = chatUsers.size();
        final WhoIsInPacket ret;

        // Also make space for the chatID, number of users, and userID, which are all ints
        dataLen += 12;
        
        for(User u : chatUsers.keySet())
        {
            // Strings are encoded in UTF_16LE, so make space for the length * 2
            dataLen += u.getName().length() * 2;
            
            // Add space for the user's userID, the username length and the role
            dataLen += 12;
        }
        
        ret = new WhoIsInPacket(chatUsers, dataLen, chatID, userID);
        return ret;
    }
    
    /**
     * Returns a new BootPacket
     * @param chat The chat where the booted user is from.
     * @param booted THe user who is being booted.
     * @return a new BootPacket
     */
    public static BootPacket createBoot(Chat chat, User booted)      
    {
        return new BootPacket(chat, booted);
    }
    
    /**
     * Returns a new ChangeRolePacket.
     * @param chat The id of the chat.
     * @param user The id of the user.
     * @param role The new role of the user.
     * @return a new ChangeRolePacket.
     */
    public static ChangeRolePacket createChangeRole(int chat, int user, int role)      
    {
        return new ChangeRolePacket(chat, user, role);
    }
    
    /**
     * Returns a new ChatListPacket
     * @param chats The list of all of the chats the user subscribes to.
     * @param roles A map of roles that are mapped to the chats the user subscribes to.
     * @param userid The id of the user.
     * @return a new ChatListPacket.
     */
    public static ChatListPacket createChatList(List<Chat> chats, Map<Integer, Integer> roles, int userid)
    {
        int dataLen = chats.size() * 8; // Role, chatid
        final ChatListPacket list;
        
        dataLen += 4; //Number of chats
        for(Chat c : chats)
        {
            dataLen += c.getName().length() * 2; //Chat Name          
            dataLen += 4; //legnth of the name
        }
            
        dataLen += 4; //UserId for who receive it
        
        return new ChatListPacket(chats, dataLen, roles, userid);
    }
    
    /**
     * Returns a new ChatNotifyPacket
     * @param chat THe chat id.
     * @param name The name of the chat,
     * @param flag Determines what operation occurred on the chat.
     * @return a new ChatNotifyPacket
     */
    public static ChatNotifyPacket createChatNotify(int chat, String name, int flag)
    {
        return new ChatNotifyPacket(chat, name, flag);
    }
    
    /**
     * Returns a new JoinLeavePacket.
     * @param userid The user id of the user who wants to leave or join.
     * @param chatid The id of the chat they want to leave or join.
     * @param flag
     * @return a new JoinLeavePacket.
     */
    public static JoinLeavePacket createJoinLeave(int userid, int chatid, int flag)
    {
        return new JoinLeavePacket(userid, chatid, flag);
    }
    
    /**
     * Returns a new LoginPacket.
     * @param username the username of the user wanting to login.
     * @param password The password of the user wanting to login.
     * @return a new LoginPacket.
     */
    public static LoginPacket createLogin(String username, String password)
    {
        return new LoginPacket(username, password);
    }
    
    /**
     * Returns a new MessagePacket.
     * @param message The message to be sent.
     * @param userID the id of the user who sent it.
     * @param chatID the id of the chat it is going to.
     * @return a new MessagePacket.
     */
    public static MessagePacket createMessage(String message, int userID, int chatID)
    {
        return new MessagePacket(message, userID, chatID);
    }
    
    /**
     * Returns a new OperationSuccessPacket.
     * @param userID The id of the user it is being sent to.
     * @param flag A flag to say if the operation was a success or failure.
     * @param operation A flag for what king of operation took place.
     * @return  a new OperationSuccessPacket.
     */
    public static OperationStatusPacket createOperationStatus(int userID, int flag, int operation)
    {
        return new OperationStatusPacket(userID, flag, operation);
    }
    
    /**
     * Returns a new RequestAccessPacket.
     * @param userid The id of the user requesting access to a chat.
     * @param chatid The id of the chat that they are requesting access to.
     * @return a new RequestAccessPacket.
     */
    public static RequestAccessPacket createRequestAccess(int userid, int chatid)
    {
        return new RequestAccessPacket(userid, chatid);
    }
    
    /**
     * Returns a new UpdateChatsPacket.
     * @param name The name of the chat.
     * @param id The id of the chat.
     * @param update A flag to tell what kind of update occurred.
     * @return  a new UpdateChatsPacket.
     */
    public static UpdateChatsPacket createUpdateChats(String name, int id, int update)
    {
        return new UpdateChatsPacket(name, id, update);
    }
    
    /**
     * Returns a new UserNotifyPacket.
     * @param userid The id of the user being notified.
     * @param chatid The id of the chat the user is in.
     * @param role The role of the user.
     * @param flag A flag to know what the notify was for.
     * @return a new UserNotifyPacket.
     */
    public static UserNotifyPacket createUserNotify(int userid, int chatid, int role, int flag)
    {
        return new UserNotifyPacket(userid, chatid, role, flag);
    }
}
