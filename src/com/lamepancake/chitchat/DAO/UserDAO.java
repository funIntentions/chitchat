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
public interface UserDAO {
    /**
     * Gets a list of all users in the database.
     * @return The list of all users in the database.
     * @throws SQLException When a database error occurs.
     */
    List<User> getAllUsers() throws SQLException;
    
    /**
     * Gets a user by their ID.
     * @param id The id for which to search.
     * @return The User with the specified by id, or null if they don't exist.
     * @throws SQLException When a database error occurs.
     */
    User getByID(int id) throws SQLException;
    
    /**
     * Gets a user by their name.
     * @param name The name for which to search.
     * @return The User with the specified name, or null if they don't exist.
     * @throws SQLException When a database error occurs.
     */
    User getByName(String name) throws SQLException;
    
    /**
     * Gets a list of all chats which this user is a part of.
     * @param u The User whose chats to retrieve.
     * @return A list of chats associated with this user.
     * @throws SQLException When a database error occurs.
     */
    List<Chat> getUserChats(User u) throws SQLException;
    
    /**
     * Commits a user's info to the database.
     * @param u
     * @throws SQLException 
     */
    void save(User u) throws SQLException;
}
