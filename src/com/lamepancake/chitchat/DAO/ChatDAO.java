/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.sql.SQLException;

/**
 *
 * @author shane
 */
public interface ChatDAO {
        /**
     * Gets a Chat with the specified ID.
     * 
     * @param id The ID of the chat to get.
     * @return A Chat object containing the information if the chat exists, or null otherwise.
     * @throws SQLException When a database error occurs.
     */
    Chat getByID(int id) throws SQLException;
    
    /**
     * Gets a chat with the specified name.
     * 
     * @param name The name of the chat for which to search.
     * @return A Chat object containing the chat's information or null if the chat doesn't exist.
     * @throws SQLException When a database error occurs.
     */
    Chat getByName(String name) throws SQLException;
    
    /**
     * Writes the chat's details to the database.
     * @param c The Chat to be saved.
     * @throws SQLException When a database error occurs.
     */
    void save(Chat c)throws SQLException;
    
    /**
     * Adds a user to this chat with the specified role.
     * 
     * If the user is already associated with this chat, update their role.
     * @param u The user to be added/updated to the chat.
     * @param role The role to grant the user.
     * @throws SQLException When a database error occurs.
     */
    void addUser(User u, int role) throws SQLException;
    
    /**
     * Dissociates a user from this chat.
     * 
     * @param u The user to be removed from this chat.
     * @throws SQLException When a database error occurs.
     */
    void removeUser(User u) throws SQLException;
}
