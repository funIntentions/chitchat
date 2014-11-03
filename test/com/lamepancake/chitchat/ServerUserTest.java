/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.Packet;
import java.nio.channels.SocketChannel;
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
public class ServerUserTest {
    
    public ServerUserTest() {
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
     * Test of setSocket method, of class ServerUser.
     */
    @Test
    public void testSetSocket() {
        System.out.println("setSocket");
        SocketChannel s = null;
        ServerUser instance = new ServerUser();
        instance.setSocket(s);
        SocketChannel result = instance.getSocket();
        
        if (result != s)
        {
            fail("Set socket isn't the same as the one returned.");
        }
    }

    /**
     * Test of notifyClient method, of class ServerUser.
     */
    @Test
    public void testNotifyClient() {
        System.out.println("notifyClient");
        Packet p = null;
        ServerUser instance = new ServerUser();
        instance.notifyClient(p);
    }
    
}
