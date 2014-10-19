/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shane
 */
public interface ChatRoleDAO {
    
    /**
     * Adds a user to this chat with the specified role.
     * 
     * If the user is already associated with this chat, update their role.
     * @param chatID The ID of the chat to which the user will be added.
     * @param userID The ID of the user to add.
     * @param role   The role to assign to the user.
     * @throws SQLException When a database error occurs.
     */
    void addUser(int chatID, int userID, int role) throws SQLException;
    
    /**
     * Updates a user's role in this chat.
     * 
     * @param chatID The ID of the chat to update.
     * @param userID The ID of the user to update within the chat.
     * @param role   The role to be assigned to the user.
     * @throws SQLException When a database error occurs.
     */
    void updateRole(int chatID, int userID, int role) throws SQLException;
    
    /**
     * Dissociates a user from this chat.
     * 
     * @param chatID The ID of the chat from which to remove the user.
     * @param userID The user to be removed from this chat.
     * @throws SQLException When a database error occurs.
     */
    void removeUser(int chatID, int userID) throws SQLException;
    
    /**
     * Gets a list of users in this chat and the roles they hold.
     * @param c The chat from which to get the list of users.
     * @return The list of users in this chat.
     * @throws SQLException When a database error occurs.
     */
    List<User> getUsers(Chat c) throws SQLException;
    
    /**
     * Gets a list of chats with which a user is associated.
     * @param u The user for whom to get chats.
     * @return A list of chat ID's and the role the user has in the associated chat.
     * @throws SQLException When a database error occurs.
     */
    Map<Integer, Integer> getChats(User u) throws SQLException;
    
    /**
     * Gets the role of the given user in the given chat.
     * @param userID The ID of the user.
     * @param chatID The ID of the chat.
     * @return The role of the user in the given chat.
     */
    int getUserRoleInChat(int userID, int chatID) throws SQLException;
}
