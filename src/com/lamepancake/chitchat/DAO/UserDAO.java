/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

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
     * Creates a new user with the specified info if they don't exist.
     * 
     * @param u The new user to create.
     * @return False if a user with the same username exists.
     * @throws SQLException When a database error occurs.
     */
    boolean create(User u) throws SQLException;
    
    /**
     * Updates a user's information.
     * 
     * @param u The user who needs to be updated.
     * @return False if the user doesn't exist.
     * @throws SQLException When a database error occurs.
     */
    void update(User u) throws SQLException;
}
