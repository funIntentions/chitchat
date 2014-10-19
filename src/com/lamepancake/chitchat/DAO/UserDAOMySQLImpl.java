/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shane
 */
public class UserDAOMySQLImpl extends MySQLDAOBase implements UserDAO {
    
    private static UserDAOMySQLImpl inst;
            
    private final PreparedStatement userByIDStatement;
    private final PreparedStatement userByNameStatement;
    private final PreparedStatement createUser;
    private final PreparedStatement updateUser;

    public static void init(String username, String password) throws SQLException
    {
        final String initTable = "CREATE TABLE IF NOT EXISTS `user` (" +
                                 "  `userId` int(10) unsigned NOT NULL AUTO_INCREMENT," +
                                 "  `username` varchar(30) NOT NULL," +
                                 "  `password` varchar(40) NOT NULL," +
                                 "  PRIMARY KEY (`userId`)" +
                                 ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1";
        
        if(inst != null)
            throw new UnsupportedOperationException("The UserDAO has already been initialised.");

        inst = new UserDAOMySQLImpl(username, password, initTable);
    }
    
    public static UserDAOMySQLImpl getInstance() throws SQLException
    {
        if(inst == null)
            init(DEFAULT_UNAME, DEFAULT_PASS);

        return inst;
    }
    
    private UserDAOMySQLImpl(final String username, final String password, final String initTable) throws SQLException
    {
        super(username, password, initTable);

        final String userByID = "SELECT * FROM `user` WHERE userId= ?";
        final String userByName = "SELECT * FROM `user` WHERE username= ?";
        final String userChats = "SELECT * FROM `chat` JOIN `chat_user` ON `chat`.`chatId`=`chat_user`.`chatId` AND `chat_user`.`userId` = ?";
        final String userCreate = "INSERT INTO `user`(`username`,`password`) VALUES(?, ?)";
        final String userUpdate = "UPDATE `user` SET `password` = ? WHERE `userId` = ?";
        
        try {
            userByIDStatement = con.prepareStatement(userByID);
            userByNameStatement = con.prepareStatement(userByName);
            createUser = con.prepareStatement(userCreate);
            updateUser = con.prepareStatement(userUpdate);
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
        
        if(!queryResults.next())
            return null;
        
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
    public int create(User u) throws SQLException
    {                
        createUser.clearParameters();
        createUser.setString(1, u.getName());
        createUser.setString(2, u.getPassword());
        createUser.executeUpdate();
        queryResults = query.executeQuery("SELECT MAX(`userId`) FROM `user`)");
        return queryResults.getInt(0);
    }
    
    @Override
    public void update(User u) throws SQLException
    {
        updateUser.clearParameters();
        updateUser.setString(1, u.getPassword());
        updateUser.setInt(2, u.getID());
        updateUser.executeUpdate();
    }
}
