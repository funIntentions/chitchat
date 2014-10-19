/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shane
 */
public class DAOTest {

    public static void main(String[] args) throws Throwable{

        final String user = "chatter";
        final String password = "chitchat";
        
        List<User> allUsers;
        Map<Integer, Integer> shanesChats;
        User shane;
        Chat hellochat;

        UserDAO uDao = null;
        ChatDAO cDao = null;
        ChatRoleDAO crDao = null;

        try {
            UserDAOMySQLImpl.init(user, password);
            ChatDAOMySQLImpl.init(user, password);
            ChatRoleDAOMySQLImpl.init(user, password);

            uDao = UserDAOMySQLImpl.getInstance();
            cDao = ChatDAOMySQLImpl.getInstance();
            crDao = ChatRoleDAOMySQLImpl.getInstance();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DAOTest.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        }

        System.out.println("------------Created the tables-------------");
        try{
            System.in.read();
        } catch (IOException e) {
            System.out.println("Not having a good day, are we?");
        }
        
        uDao.create(new User().setName("Shane").setPassword("silly"));
        shane = uDao.getByName("Shane");
        shane.setPassword("hello");
        uDao.update(shane);

        System.out.println("------------Created a user-------------");
        try{
            System.in.read();
        } catch (IOException e) {
            System.out.println("Not having a good day, are we?");
        }
        
        cDao.create(new Chat("hellochat", 0));
        hellochat = cDao.getByName("hellochat");
        cDao.create(new Chat("otherchat", 0));
        cDao.create(new Chat("anotherchat", 0));
        System.out.println(cDao.create(new Chat("hellochat", 0)));
        
        
        System.out.println("------------Created a chat-------------");
        try{
            System.in.read();
        } catch (IOException e) {
            System.out.println("Not having a good day, are we?");
        }
        
        crDao.addUser(hellochat.getID(), shane.getID(), User.USER);
        shanesChats = crDao.getChats(shane);
        for(Integer key : shanesChats.keySet())
            System.out.println("Chat name: " + cDao.getByID(key).getName() + ", role: " + shanesChats.get(key));
        
        System.out.println("------------Got chat info-------------");
        try{
            System.in.read();
        } catch (IOException e) {
            System.out.println("Not having a good day, are we?");
        }
    }
}
