/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shane
 */
public class ChatRoleDAOMySQLImpl extends MySQLDAOBase implements ChatRoleDAO { 
    
    private static ChatRoleDAOMySQLImpl inst;
            
    private final PreparedStatement chatUsersStatement;
    private final PreparedStatement userChatAssocStatement;
    private final PreparedStatement addChatUserStatement;
    private final PreparedStatement updateUserRoleStatement;
    private final PreparedStatement deleteChatUserStatement;
    private final PreparedStatement roleInChatStatement;

    private ChatRoleDAOMySQLImpl(String name, String password, final String initTable) throws SQLException
    {
        super(name, password, initTable);

        final String chatUsers = "SELECT * FROM `user` JOIN `chat_user` ON `user`.`userId`=`chat_user`.`userId` AND `chat_user`.`chatId` = ?";
        final String addUser = "INSERT INTO `chat_user` VALUES(?, ?, ?)";
        final String removeUser = "DELETE  FROM `chat_user` WHERE `userId`= ? AND `chatId`=?";
        final String updateUser = "UPDATE `chat_user` SET `role`=? WHERE `userId`=? AND `chatId`=?";
        final String userChatAssoc = "SELECT `chatId`, `role` FROM `chat_user` WHERE `userId`=?";
        final String roleInChat = "SELECT `role` FROM `chat_user` WHERE `userId`=? AND `chatId`=?";
        
        try {
            chatUsersStatement = con.prepareStatement(chatUsers);
            addChatUserStatement = con.prepareStatement(addUser);
            updateUserRoleStatement = con.prepareStatement(updateUser);
            deleteChatUserStatement = con.prepareStatement(removeUser);
            userChatAssocStatement = con.prepareStatement(userChatAssoc);
            roleInChatStatement = con.prepareStatement(roleInChat);
            
        } catch(SQLException se) {
            System.err.println("ChatDAOMySQLImpl constructor: could not prepare statements.");
            throw(se);
        }
    }   
    
    public static void init(String username, String password) throws SQLException
    {
        final String initTable = "CREATE TABLE IF NOT EXISTS `chat_user` (" +
                                 "  `chatId` smallint(5) unsigned NOT NULL," +
                                 "  `userId` int(10) unsigned NOT NULL," +
                                 "  `role` tinyint(3) unsigned NOT NULL," +
                                 "  PRIMARY KEY (`userId`,`chatId`)," +
                                 "  CONSTRAINT `fk_chat` FOREIGN KEY (`chatId`) REFERENCES `chat` (`chatId`) ON DELETE CASCADE," +
                                 "  CONSTRAINT `fk_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE" +
                                 ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
        if(inst != null)
            throw new UnsupportedOperationException("The ChatRoleDAO has already been initialised.");
        
        inst = new ChatRoleDAOMySQLImpl(username, password, initTable);
    }
    
    public static ChatRoleDAOMySQLImpl getInstance() throws SQLException
    {
        if(inst == null)
            init(DEFAULT_UNAME, DEFAULT_PASS);

        return inst;
    }
    

    @Override
    public void addUser(int chatID, int userID, int role) throws SQLException
    {
        addChatUserStatement.clearParameters();
        addChatUserStatement.setInt(1, userID);
        addChatUserStatement.setInt(2, chatID);
        addChatUserStatement.setInt(3, role);
        addChatUserStatement.executeUpdate();
    }
    
    @Override
    public void updateRole(int chatID, int userID, int role) throws SQLException
    {
        updateUserRoleStatement.clearParameters();
        updateUserRoleStatement.setInt(1, role);
        updateUserRoleStatement.setInt(2, userID);
        updateUserRoleStatement.setInt(3, chatID);
        updateUserRoleStatement.executeUpdate();
    }
    
    @Override
    public void removeUser(int chatID, int userID) throws SQLException
    {
        deleteChatUserStatement.clearParameters();
        deleteChatUserStatement.setInt(1, userID);
        deleteChatUserStatement.setInt(2, chatID);
        deleteChatUserStatement.executeUpdate();
    }
    
    @Override
    public List<User> getUsers(Chat c) throws SQLException
    {
        List<User> userList;
        int userID;
        int role;
        String username;
        
        
        chatUsersStatement.clearParameters();
        chatUsersStatement.setInt(1, c.getID());
        queryResults = chatUsersStatement.executeQuery();
        
        userList = new ArrayList<>();
        
        while(queryResults.next())
        {
            userID = queryResults.getInt("userId");
            username = queryResults.getString("username");
            role = queryResults.getInt("role");
            userList.add(new User().setID(userID).setRole(role).setName(username));
        }
        return userList;
    }
    
    @Override
    public Map<Integer, Integer> getChats(User u) throws SQLException
    {
        Map<Integer, Integer> userRoles = new HashMap<>();
        int chatID;
        int userRole;
        
        userChatAssocStatement.clearParameters();
        userChatAssocStatement.setInt(1, u.getID());
        queryResults = userChatAssocStatement.executeQuery();
        
        while(queryResults.next())
        {
            chatID = queryResults.getInt("chatId");
            userRole = queryResults.getInt("role");
            userRoles.put(chatID, userRole);
        }
        return userRoles;
    }
    
    @Override
    public int getUserRoleInChat(int userID, int chatID) throws SQLException
    {
        int role = User.UNSPEC;
        roleInChatStatement.clearParameters();
        roleInChatStatement.setInt(1, userID);
        roleInChatStatement.setInt(2, chatID);
        queryResults = roleInChatStatement.executeQuery();
        if(queryResults.next())
            role = queryResults.getInt("role");
        
        return role;
    }
        
}
