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
 * @author Trevor
 */
public interface OrganizationDAO {
        /**
     * Gets a Organization with the specified ID.
     * 
     * @param id The ID of the organization to get.
     * @return A organization object containing the information if the chat exists, or null otherwise.
     * @throws SQLException When a database error occurs.
     */
    Organization getByID(int id) throws SQLException;
    
    /**
     * Gets a Organization with the specified name.
     * 
     * @param name The name of the organization for which to search.
     * @return A organization object containing the organization's information or null if the organization doesn't exist.
     * @throws SQLException When a database error occurs.
     */
    Organization getByName(String name) throws SQLException;
    
    /**
     * Gets a list of all organizations.
     * 
     * @return The list of organization.
     * @throws SQLException When a database error occurs.
     */
    List<Organization> getAllOrganizations() throws SQLException;
    
    /**
     * Writes the Organization's details to the database.
     * @param o The organization to be saved.
     * @return The ID of the newly created organization.
     * @throws SQLException When a database error occurs.
     */
    int create(Organization o)throws SQLException;
    
    /**
     * Writes the Organization's details to the database.
     * @param o The organization to be saved.
     * @throws SQLException When a database error occurs.
     */
    void update(Organization o) throws SQLException;
    
    /**
     * Deletes a Organization from the database
     * @param o The organization to be deleted.
     * @throws SQLException 
     */
    void delete(Organization o) throws SQLException;
    
    /**
     * Gets all of the chats associated with the particular organization.
     * @param o the organization to get the list of chats.
     * @return the list of chats that is associated with the organizations.
     * @throws SQLException 
     */
    List<Chat> getChats(Organization o) throws SQLException;
}