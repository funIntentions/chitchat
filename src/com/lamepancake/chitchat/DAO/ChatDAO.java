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
     * Gets a list of all chats.
     * 
     * @return The list of chats.
     * @throws SQLException When a database error occurs.
     */
    List<Chat> getAllChats() throws SQLException;
    
    /**
     * Writes the chat's details to the database.
     * @param c The Chat to be saved.
     * @throws SQLException When a database error occurs.
     */
    boolean create(Chat c)throws SQLException;
    
    /**
     * Writes the chat's details to the database.
     * @param c The Chat to be saved.
     * @throws SQLException When a database error occurs.
     */
    void update(Chat c) throws SQLException;
    
    /**
     * Deletes a chat from the database
     * @param c
     * @throws SQLException 
     */
    void delete(Chat c) throws SQLException;
}
