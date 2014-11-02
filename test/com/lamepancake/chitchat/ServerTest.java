/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

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
public class ServerTest {
    
    public ServerTest() {
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
     * Test of start method, of class Server.
     */
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
//        
//        int portNumber = 1500;
//        ChatManager man;
//        man = new ChatManager("chatter", "chitchat");
//        Server server = new Server(portNumber, man);
//        server.start();
    }

    /**
     * Test of main method, of class Server.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = {""};
        Server.main(args);
    }
    
}
