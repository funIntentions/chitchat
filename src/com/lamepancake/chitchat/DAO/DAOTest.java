/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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
        List<Chat> shanesChats;
        User shane;
        UserDAO dao = null;

        try {        
            dao = new UserDAOMySQLImpl(user, password);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(DAOTest.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        }

        allUsers = dao.getAllUsers();
        for(User u : allUsers)
            System.out.println(u);
        
        System.out.println("------------------------");
        
        shane = dao.getByName("Shane");
        System.out.println(shane);
        
        System.out.println("------------------------");
        
        shanesChats = dao.getUserChats(shane);
        for(Chat c : shanesChats)
            System.out.println(c.getName());

        try{
            System.in.read();
        } catch (IOException e) {
            System.out.println("Not having a good day, are we?");
        }
    }
}
