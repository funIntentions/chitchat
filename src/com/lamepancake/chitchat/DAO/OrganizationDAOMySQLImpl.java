/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.Organization;
import com.lamepancake.chitchat.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tware
 */
public class OrganizationDAOMySQLImpl extends MySQLDAOBase implements OrganizationDAO {

    private static OrganizationDAOMySQLImpl inst;
            
    private final PreparedStatement organizationByIDStatement;
    private final PreparedStatement organizationByNameStatement;
    private final PreparedStatement createOrganizationStatement;
    private final PreparedStatement updateOrganizationStatement;
    private final PreparedStatement deleteOrganizationStatement;
    private final PreparedStatement organizationChatsStatement;
    private final PreparedStatement founderStatement;
    
    public static void init(String username, String password) throws SQLException
    {
        final String initTable = "CREATE TABLE IF NOT EXISTS `organization` (" +
                                 "  `organizationId` smallint(5) unsigned NOT NULL AUTO_INCREMENT," +
                                 "  `name` varchar(30) NOT NULL," +
                                 "  `userId` int(10) unsigned NOT NULL," +
                                 "  PRIMARY KEY (`organizationId`)" +
                                 "  CONSTRAINT `fk_user` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE" +
                                 ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1";
        if(inst != null)
            throw new UnsupportedOperationException("The OrgaizationDAO has already been initialised.");

        inst = new OrganizationDAOMySQLImpl(username, password, initTable);
    }
    
    public static OrganizationDAOMySQLImpl getInstance() throws SQLException
    {
        if(inst == null)
            init(DEFAULT_UNAME, DEFAULT_PASS);

        return inst;
    }
    
    private OrganizationDAOMySQLImpl(final String username, final String password, final String initTable) throws SQLException
    {
        super(username, password, initTable);

        final String orgByID = "SELECT * FROM `organization` WHERE organizationId= ?";
        final String orgByName = "SELECT * FROM `organization` WHERE name= ?";
        final String orgChats = "SELECT * FROM `chat` JOIN `organization` ON `chat`.`organizationId`=`organization`.`organizationId` AND `organization`.`organizationtId` = ?";
        final String createOrg = "INSERT INTO `organization`(`name`) VALUES(?)";
        final String updateOrg = "UPDATE `organization` SET `name`= ? WHERE `organizationId`= ?";
        final String deleteOrg = "DELETE FROM `organization` WHERE `organizationId`= ?";
        final String founder = "SELECT * FROM `user` JOIN `organization` ON `user`.`userId`=`organization`.`userId` AND `organization`.`organizationtId` = ?";
        
        try {
            organizationByIDStatement = con.prepareStatement(orgByID);
            organizationByNameStatement = con.prepareStatement(orgByName);
            createOrganizationStatement = con.prepareStatement(createOrg);
            updateOrganizationStatement = con.prepareStatement(updateOrg);
            deleteOrganizationStatement = con.prepareStatement(deleteOrg);
            organizationChatsStatement = con.prepareStatement(orgChats);
            founderStatement = con.prepareStatement(founder);
            
        } catch(SQLException se) {
            System.err.println("ChatDAOMySQLImpl constructor: could not prepare statements.");
            throw(se);
        }
    }
    
    @Override
    public Organization getByID(int id) throws SQLException {
        Organization o;
        final int organizationID;
        final String name;
        final User founder = new User();

        organizationByIDStatement.clearParameters();
        organizationByIDStatement.setInt(1, id);
        queryResults = organizationByIDStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        organizationID = queryResults.getInt("organizationId");
        name = queryResults.getString("name");
        
        founderStatement.clearParameters();
        founderStatement.setInt(1, id);
        queryResults = founderStatement.executeQuery();
        
        founder.setID(queryResults.getInt("userId"));
        founder.setName(queryResults.getString("username"));
        founder.setPassword(queryResults.getString("password"));
        
        
        o = new Organization(name, organizationID, founder);

        return o;
    }

    @Override
    public Organization getByName(String name) throws SQLException {
        Organization o;
        final int orgID;
        final String orgName;
        final User founder = new User();

        organizationByNameStatement.clearParameters();
        organizationByNameStatement.setString(1, name);
        queryResults = organizationByNameStatement.executeQuery();
        
        if(!queryResults.next())
            return null;
        
        orgID = queryResults.getInt("organizationId");
        orgName = queryResults.getString("name");
        
        founderStatement.clearParameters();
        founderStatement.setInt(1, orgID);
        queryResults = founderStatement.executeQuery();
        
        founder.setID(queryResults.getInt("userId"));
        founder.setName(queryResults.getString("username"));
        founder.setPassword(queryResults.getString("password"));
        
        o = new Organization(orgName, orgID, founder);
        return o;
    }

    @Override
    public List<Organization> getAllOrganizations() throws SQLException {
        List<Organization> orgs;
        int id;
        String name;
        String password;
        
        queryResults = query.executeQuery("SELECT * FROM `organization`;");
        
        orgs = new ArrayList<>();
        while(queryResults.next())
        {
            Organization nextOrg;
            id = queryResults.getInt("organizationId");
            name = queryResults.getString("name");
            
            User founder = new User();
            
            founderStatement.clearParameters();
            founderStatement.setInt(1, id);
            queryResults = founderStatement.executeQuery();

            founder.setID(queryResults.getInt("userId"));
            founder.setName(queryResults.getString("username"));
            founder.setPassword(queryResults.getString("password"));
            
            nextOrg = new Organization(name, id, founder);
            orgs.add(nextOrg);
        }
        
        return orgs;
    }

    @Override
    public int create(Organization o) throws SQLException {
        int id = -1;
        createOrganizationStatement.clearParameters();
        createOrganizationStatement.setString(1, o.getName());
        createOrganizationStatement.executeUpdate();
        
        queryResults = query.executeQuery("SELECT MAX(`organizationId`) FROM `organization`");
        if(queryResults.next())
            id = queryResults.getInt(1);
        
        return id;
    }

    @Override
    public void update(Organization o) throws SQLException {
        updateOrganizationStatement.clearParameters();
        updateOrganizationStatement.setString(1, o.getName());
        updateOrganizationStatement.setInt(2, o.getID());
        updateOrganizationStatement.executeUpdate();
    }

    @Override
    public void delete(Organization o) throws SQLException {
        deleteOrganizationStatement.clearParameters();
        deleteOrganizationStatement.setInt(1, o.getID());
        deleteOrganizationStatement.executeUpdate();
    }
    
    @Override
    public List<Chat> getChats(Organization o) throws SQLException {
        List<Chat> chatList;
        Chat temp;
        int chatID;
        String name;
        
        
        organizationChatsStatement.clearParameters();
        organizationChatsStatement.setInt(1, o.getID());
        queryResults = organizationChatsStatement.executeQuery();
        
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
