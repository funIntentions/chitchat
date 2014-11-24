/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.Packet;
import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan
 */
public class ChatManagerTest {
    
    public ChatManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of handlePacket method, of class ChatManager.
     */
    @Test
    public void testHandlePacket() {
        System.out.println("handlePacket");
        SelectionKey clientKey = null;
        Packet received = null;
        try
        {
            ChatManager instance = new ChatManager("test", "test");
            instance.handlePacket(clientKey, received);
        }
        catch (SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    /**
     * Test of addClient method, of class ChatManager.
     */
//    @Test
//    public void testAddClient() {
//        System.out.println("addClient");
//        SelectionKey key = null;
//        
//        try
//        {
//            ChatManager instance = new ChatManager("test", "test");
//            instance.addClient(key);
//        }
//        catch (SQLException e)
//        {
//            System.out.println("SQLException: " + e.getMessage());
//        }
//    }
    
}
