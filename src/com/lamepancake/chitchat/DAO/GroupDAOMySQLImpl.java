/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.Group;
import com.lamepancake.chitchat.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tware
 */
public class GroupDAOMySQLImpl extends MySQLDAOBase implements GroupDAO {

    private static GroupDAOMySQLImpl inst;
            
    private final PreparedStatement GroupByIDStatement;
    private final PreparedStatement GroupByNameStatement;
    private final PreparedStatement createGroupStatement;
    private final PreparedStatement updateGroupStatement;
    private final PreparedStatement deleteGroupStatement;
    private final PreparedStatement GroupChatsStatement;
    private final PreparedStatement founderStatement;
    
    public static void init(String username, String password) throws SQLException
    {
        final String initTable = "CREATE TABLE IF NOT EXISTS `Group` (" +
                                 "  `GroupId` smallint(5) unsigned NOT NULL AUTO_INCREMENT," +
                                 "  `name` varchar(30) NOT NULL," +
                                 "  `userId` int(10) unsigned NOT NULL," +
                                 "  PRIMARY KEY (`GroupId`)" +
                                 "  CONSTRAINT `fk_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE" +
                                 ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1";
        if(inst != null)
            throw new UnsupportedOperationException("The OrgaizationDAO has already been initialised.");

        inst = new GroupDAOMySQLImpl(username, password, initTable);
    }
    
    public static GroupDAOMySQLImpl getInstance() throws SQLException
    {
        if(inst == null)
            init(DEFAULT_UNAME, DEFAULT_PASS);

        return inst;
    }
    
    private GroupDAOMySQLImpl(final String username, final String password, final String initTable) throws SQLException
    {
        super(username, password, initTable);

        final String orgByID = "SELECT * FROM `Group` WHERE GroupId= ?";
        final String orgByName = "SELECT * FROM `Group` WHERE name= ?";
        final String orgChats = "SELECT * FROM `chat` JOIN `Group` ON `chat`.`GroupId`=`Group`.`GroupId` AND `Group`.`GrouptId` = ?";
        final String createOrg = "INSERT INTO `Group`(`name`) VALUES(?)";
        final String updateOrg = "UPDATE `Group` SET `name`= ? WHERE `GroupId`= ?";
        final String deleteOrg = "DELETE FROM `Group` WHERE `GroupId`= ?";
        final String founder = "SELECT * FROM `user` JOIN `Group` ON `user`.`userId`=`Group`.`userId` AND `Group`.`GrouptId` = ?";
        
        try {
            GroupByIDStatement = con.prepareStatement(orgByID);
            GroupByNameStatement = con.prepareStatement(orgByName);
            createGroupStatement = con.prepareStatement(createOrg);
            updateGroupStatement = con.prepareStatement(updateOrg);
            deleteGroupStatement = con.prepareStatement(deleteOrg);
            GroupChatsStatement = con.prepareStatement(orgChats);
            founderStatement = con.prepareStatement(founder);
            
        } catch(SQLException se) {
            System.err.println("ChatDAOMySQLImpl constructor: could not prepare statements.");
            throw(se);
        }
    }
    
    @Override
    public Group getByID(int id) throws SQLException {
        Group o;
        final int GroupID;
        final String name;
        final User founder = new User();

        GroupByIDStatement.clearParameters();
        GroupByIDStatement.setInt(1, id);
        queryResults = GroupByIDStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        GroupID = queryResults.getInt("GroupId");
        name = queryResults.getString("name");
        
        founderStatement.clearParameters();
        founderStatement.setInt(1, id);
        queryResults = founderStatement.executeQuery();
        
        founder.setID(queryResults.getInt("userId"));
        founder.setName(queryResults.getString("username"));
        founder.setPassword(queryResults.getString("password"));
        
        
        o = new Group(name, GroupID, founder);

        return o;
    }

    @Override
    public Group getByName(String name) throws SQLException {
        Group o;
        final int orgID;
        final String orgName;
        final User founder = new User();

        GroupByNameStatement.clearParameters();
        GroupByNameStatement.setString(1, name);
        queryResults = GroupByNameStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        orgID = queryResults.getInt("GroupId");
        orgName = queryResults.getString("name");
        
        founderStatement.clearParameters();
        founderStatement.setInt(1, orgID);
        queryResults = founderStatement.executeQuery();
        
        founder.setID(queryResults.getInt("userId"));
        founder.setName(queryResults.getString("username"));
        founder.setPassword(queryResults.getString("password"));
        
        o = new Group(orgName, orgID, founder);
        return o;
    }

    @Override
    public List<Group> getAllGroups() throws SQLException {
        List<Group> orgs;
        int id;
        String name;
        String password;
        
        queryResults = query.executeQuery("SELECT * FROM `Group`;");
        
        orgs = new ArrayList<>();
        while(queryResults.next())
        {
            Group nextOrg;
            id = queryResults.getInt("GroupId");
            name = queryResults.getString("name");
            
            User founder = new User();
            
            founderStatement.clearParameters();
            founderStatement.setInt(1, id);
            queryResults = founderStatement.executeQuery();

            founder.setID(queryResults.getInt("userId"));
            founder.setName(queryResults.getString("username"));
            founder.setPassword(queryResults.getString("password"));
            
            nextOrg = new Group(name, id, founder);
            orgs.add(nextOrg);
        }
        
        return orgs;
    }

    @Override
    public int create(Group o) throws SQLException {
        int id = -1;
        createGroupStatement.clearParameters();
        createGroupStatement.setString(1, o.getName());
        createGroupStatement.executeUpdate();
        
        queryResults = query.executeQuery("SELECT MAX(`GroupId`) FROM `Group`");
        if(queryResults.next())
            id = queryResults.getInt(1);
        
        return id;
    }

    @Override
    public void update(Group o) throws SQLException {
        updateGroupStatement.clearParameters();
        updateGroupStatement.setString(1, o.getName());
        updateGroupStatement.setInt(2, o.getID());
        updateGroupStatement.executeUpdate();
    }

    @Override
    public void delete(Group o) throws SQLException {
        deleteGroupStatement.clearParameters();
        deleteGroupStatement.setInt(1, o.getID());
        deleteGroupStatement.executeUpdate();
    }
    
    @Override
    public List<Chat> getChats(Group o) throws SQLException {
        List<Chat> chatList;
        Chat temp;
        int chatID;
        String name;
        
        
        GroupChatsStatement.clearParameters();
        GroupChatsStatement.setInt(1, o.getID());
        queryResults = GroupChatsStatement.executeQuery();
        
        chatList = new ArrayList<>();
        
        while(queryResults.next())
        {
            chatID = queryResults.getInt("chatId");
            name = queryResults.getString("name");
            temp = new Chat(name, chatID);
            chatList.add(temp);
        }
        return chatList;
    }
    
}
