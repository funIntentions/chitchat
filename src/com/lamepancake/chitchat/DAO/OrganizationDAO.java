/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.Organization;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author shane
 */
public interface OrganizationDAO {
        /**
     * Gets a Chat with the specified ID.
     * 
     * @param id The ID of the chat to get.
     * @return A Chat object containing the information if the chat exists, or null otherwise.
     * @throws SQLException When a database error occurs.
     */
    Organization getByID(int id) throws SQLException;
    
    /**
     * Gets a chat with the specified name.
     * 
     * @param name The name of the chat for which to search.
     * @return A Chat object containing the chat's information or null if the chat doesn't exist.
     * @throws SQLException When a database error occurs.
     */
    Organization getByName(String name) throws SQLException;
    
    /**
     * Gets a list of all chats.
     * 
     * @return The list of chats.
     * @throws SQLException When a database error occurs.
     */
    List<Organization> getAllOrganizations() throws SQLException;
    
    /**
     * Writes the chat's details to the database.
     * @param o The Chat to be saved.
     * @return The ID of the newly created chat.
     * @throws SQLException When a database error occurs.
     */
    int create(Organization o)throws SQLException;
    
    /**
     * Writes the chat's details to the database.
     * @param o The Chat to be saved.
     * @throws SQLException When a database error occurs.
     */
    void update(Organization o) throws SQLException;
    
    /**
     * Deletes a chat from the database
     * @param o The chat to be deleted.
     * @throws SQLException 
     */
    void delete(Organization o) throws SQLException;
    
    List<Chat> getChats(Organization o) throws SQLException;
}