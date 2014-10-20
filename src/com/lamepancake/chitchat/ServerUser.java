/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.DAO.ChatRoleDAO;
import com.lamepancake.chitchat.DAO.ChatRoleDAOMySQLImpl;
import com.lamepancake.chitchat.DAO.UserDAO;
import com.lamepancake.chitchat.DAO.UserDAOMySQLImpl;
import com.lamepancake.chitchat.packet.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;

/**
 * Represents a user on the server.
 * 
 * The server user holds the state of the remote client user and has additional
 * methods for obtaining and communicating information to the client-side user.
 * 
 * @author shane
 */
public class ServerUser extends User {
    
    private SocketChannel socket;
    private UserDAO uDao;
    private ChatRoleDAO crDao;
    
    public ServerUser(SocketChannel s)
    {
        socket = s;
        
        try {
            uDao = UserDAOMySQLImpl.getInstance();
            crDao = ChatRoleDAOMySQLImpl.getInstance();
        } catch (SQLException se) {
            System.err.println("ServerUser constructor: Could not get database instance: " + se.getMessage());
        }
    }
    
    /**
     * Creates a new, empty ServerUser.
     */
    public ServerUser()
    {
        try {
            uDao = UserDAOMySQLImpl.getInstance();
            crDao = ChatRoleDAOMySQLImpl.getInstance();
        } catch (SQLException se) {
            System.err.println("ServerUser constructor: Could not get database instance: " + se.getMessage());
        }
    }
    
    /**
     * Set the SocketChannel associated with this user.
     * 
     * @param s The SocketChannel to set.
     */
    public void setSocket(SocketChannel s)
    {
        socket = s;
    }
    
    /**
     * Sends a packet to the remote user associated with this user.
     * 
     * @param p The packet to send.
     */
    public void notifyClient(Packet p)
    {
        ByteBuffer serialised = p.serialise();
        try {
            socket.write(serialised);
        } catch (IOException e) {
            System.err.println("ServerUser.notifyClient: could not send packet: " + e.getMessage());
        }
    }
}
