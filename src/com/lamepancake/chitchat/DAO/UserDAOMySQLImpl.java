/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shane
 */
public class UserDAOMySQLImpl implements UserDAO {
    
    private final PreparedStatement userByIDStatement;
    private final PreparedStatement userByNameStatement;
    private final PreparedStatement userChatsStatement;
    
    private final Connection con;
    private       Statement  query;
    private       ResultSet  queryResults;
    
    
    public UserDAOMySQLImpl(final String username, final String password) throws SQLException
    {
        final String userByID = "SELECT * FROM `user` WHERE userId= ?";
        final String userByName = "SELECT * FROM `user` WHERE username= ?";
        final String userChats = "SELECT * FROM `chat` JOIN `chat_user` ON `chat`.`chatId`=`chat_user`.`chatId` AND `chat_user`.`userId` = ?";
        
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chitchat", username, password);
            con.setAutoCommit(false);
        } catch(SQLException se) {
            System.err.println("UserDAOMySQLImpl constructor: could not get database connnection: " + se.getMessage());
            throw(se);
        }
       
        try {
            query = con.createStatement();
            queryResults = query.executeQuery("USE chitchat");
        } catch (SQLException se) {
            System.err.println("UserDAOMySQLImpl constructor: could not use chitchat database.");
            throw(se);
        }
        
        try {
            userByIDStatement = con.prepareStatement(userByID);
            userByNameStatement = con.prepareStatement(userByName);
            userChatsStatement = con.prepareStatement(userChats);
        } catch(SQLException se) {
            System.err.println("UserDAOMySQLImpl constructor: could not prepare statements.");
            throw(se);
        }
    }
    
    @Override
    public List<User> getAllUsers() throws SQLException
    {
        List<User> users;
        int id;
        String name;
        String password;
        
        queryResults = query.executeQuery("SELECT * FROM `user`;");
        
        users = new ArrayList<>();
        while(queryResults.next())
        {
            User newUser = new User();
            id = queryResults.getInt("userId");
            name = queryResults.getString("username");
            password = queryResults.getString("password");
            
            newUser.setID(id).setName(name).setPassword(password);
            users.add(newUser);
        }
        
        return users;
    }
    
    @Override
    public User getByID(int id) throws SQLException
    {
        User u = new User();
        final int userID;
        final String username;
        final String password;

        userByIDStatement.clearParameters();
        userByIDStatement.setInt(1, id);
        queryResults = userByIDStatement.executeQuery();
        
        userID = queryResults.getInt("userId");
        username = queryResults.getString("username");
        password = queryResults.getString("password");
        
        u.setID(userID).setName(username).setPassword(password);

        return u;
    }
    
    @Override
    public User getByName(String name) throws SQLException
    {
        User u = new User();
        final int userID;
        final String username;
        final String password;

        userByNameStatement.clearParameters();
        userByNameStatement.setString(1, name);
        queryResults = userByNameStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        userID = queryResults.getInt("userId");
        username = queryResults.getString("username");
        password = queryResults.getString("password");
        
        u.setID(userID).setName(username).setPassword(password);
        return u;
    }
    
    @Override
    public List<Chat> getUserChats(User u) throws SQLException
    {
        List<Chat> chatList;
        int chatID;
        String chatName;
        
        userChatsStatement.clearParameters();
        userChatsStatement.setInt(1, u.getID());
        queryResults = userChatsStatement.executeQuery();
        
        chatList = new ArrayList<>();
        
        while(queryResults.next())
        {
            chatID = queryResults.getInt("chatId");
            chatName = queryResults.getString("name");
            chatList.add(new Chat(chatName, chatID));
        }
        return chatList;
    }
    
    @Override
    public void save(User u) throws SQLException
    {
        // Check if the user already exists in the database
        // If not, create a new user with the specified username and password
        // If so, update any values they've requested to change
    }
}
