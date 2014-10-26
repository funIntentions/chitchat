/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shane
 */
public class ChatDAOMySQLImpl extends MySQLDAOBase implements ChatDAO {
    
    private static ChatDAOMySQLImpl inst;
            
    private final PreparedStatement chatByIDStatement;
    private final PreparedStatement chatByNameStatement;
    private final PreparedStatement createChatStatement;
    private final PreparedStatement updateChatStatement;
    private final PreparedStatement deleteChatStatement;
    
    public static void init(String username, String password) throws SQLException
    {
        final String initTable = "CREATE TABLE IF NOT EXISTS `chat` (" +
                                 "  `chatId` smallint(5) unsigned NOT NULL AUTO_INCREMENT," +
                                 "  `name` varchar(30) NOT NULL," +
                                 "  PRIMARY KEY (`chatId`)" +
                                 ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1";
        if(inst != null)
            throw new UnsupportedOperationException("The ChatDAO has already been initialised.");

        inst = new ChatDAOMySQLImpl(username, password, initTable);
    }
    
    public static ChatDAOMySQLImpl getInstance() throws SQLException
    {
        if(inst == null)
            init(DEFAULT_UNAME, DEFAULT_PASS);

        return inst;
    }
    
    private ChatDAOMySQLImpl(final String username, final String password, final String initTable) throws SQLException
    {
        super(username, password, initTable);

        final String chatByID = "SELECT * FROM `chat` WHERE chatId= ?";
        final String chatByName = "SELECT * FROM `chat` WHERE name= ?";
        final String chatUsers = "SELECT * FROM `user` JOIN `chat_user` ON `user`.`userId`=`chat_user`.`userId` AND `chat_user`.`chatId` = ?";
        final String createChat = "INSERT INTO `chat`(`name`) VALUES(?)";
        final String updateChat = "UPDATE `chat` SET `name`= ? WHERE `chatId`= ?";
        final String deleteChat = "DELETE FROM `chat` WHERE `chatId`= ?";
        
        try {
            chatByIDStatement = con.prepareStatement(chatByID);
            chatByNameStatement = con.prepareStatement(chatByName);
            createChatStatement = con.prepareStatement(createChat);
            updateChatStatement = con.prepareStatement(updateChat);
            deleteChatStatement = con.prepareStatement(deleteChat);
            
        } catch(SQLException se) {
            System.err.println("ChatDAOMySQLImpl constructor: could not prepare statements.");
            throw(se);
        }
    }
    
    @Override
    public List<Chat> getAllChats() throws SQLException
    {
        List<Chat> chats;
        int id;
        String name;
        String password;
        
        queryResults = query.executeQuery("SELECT * FROM `chat`;");
        
        chats = new ArrayList<>();
        while(queryResults.next())
        {
            Chat nextChat;
            id = queryResults.getInt("chatId");
            name = queryResults.getString("name");
            
            nextChat = new Chat(name, id);
            chats.add(nextChat);
        }
        
        return chats;
    }
    
    @Override
    public Chat getByID(int id) throws SQLException
    {
        Chat c;
        final int chatID;
        final String name;

        chatByIDStatement.clearParameters();
        chatByIDStatement.setInt(1, id);
        queryResults = chatByIDStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        chatID = queryResults.getInt("chatId");
        name = queryResults.getString("name");
        
        c = new Chat(name, chatID);

        return c;
    }
    
    @Override
    public Chat getByName(String name) throws SQLException
    {
        Chat c;
        final int chatID;
        final String chatName;

        chatByNameStatement.clearParameters();
        chatByNameStatement.setString(1, name);
        queryResults = chatByNameStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        chatID = queryResults.getInt("chatId");
        chatName = queryResults.getString("name");
        
        c = new Chat(chatName, chatID);
        return c;
    }
    
    @Override
    public int create(Chat c) throws SQLException
    {   
        int id = -1;
        createChatStatement.clearParameters();
        createChatStatement.setString(1, c.getName());
        createChatStatement.executeUpdate();
        
        queryResults = query.executeQuery("SELECT MAX(`chatId`) FROM `chat`");
        if(queryResults.next())
            id = queryResults.getInt(1);
        
        return id;
    }
    
    @Override
    public void update(Chat c) throws SQLException
    {   
        updateChatStatement.clearParameters();
        updateChatStatement.setString(1, c.getName());
        updateChatStatement.setInt(2, c.getID());
        updateChatStatement.executeUpdate();
    }
    
    @Override
    public void delete(Chat c) throws SQLException
    {
        deleteChatStatement.clearParameters();
        deleteChatStatement.setInt(1, c.getID());
        deleteChatStatement.executeUpdate();
    }
}
