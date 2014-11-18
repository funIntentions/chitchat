/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.Group;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Trevor
 */
public interface GroupDAO {
        /**
     * Gets a Group with the specified ID.
     * 
     * @param id The ID of the group to get.
     * @return A group object containing the information if the chat exists, or null otherwise.
     * @throws SQLException When a database error occurs.
     */
    Group getByID(int id) throws SQLException;
    
    /**
     * Gets a Group with the specified name.
     * 
     * @param name The name of the group for which to search.
     * @return A group object containing the group's information or null if the group doesn't exist.
     * @throws SQLException When a database error occurs.
     */
    Group getByName(String name) throws SQLException;
    
    /**
     * Gets a list of all groups.
     * 
     * @return The list of group.
     * @throws SQLException When a database error occurs.
     */
    List<Group> getAllGroups() throws SQLException;
    
    /**
     * Writes the Group's details to the database.
     * @param o The group to be saved.
     * @return The ID of the newly created group.
     * @throws SQLException When a database error occurs.
     */
    int create(Group o)throws SQLException;
    
    /**
     * Writes the Group's details to the database.
     * @param o The group to be saved.
     * @throws SQLException When a database error occurs.
     */
    void update(Group o) throws SQLException;
    
    /**
     * Deletes a Group from the database
     * @param o The group to be deleted.
     * @throws SQLException 
     */
    void delete(Group o) throws SQLException;
    
    /**
     * Gets all of the chats associated with the particular group.
     * @param o the group to get the list of chats.
     * @return the list of chats that is associated with the groups.
     * @throws SQLException 
     */
    List<Chat> getChats(Group o) throws SQLException;
}