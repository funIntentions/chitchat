/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.Packet;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class ChatTest {
    
    public ChatTest() {
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
     * Test of initUsers method, of class Chat.
     */
    @Test
    public void testInitUsers() {
        System.out.println("initUsers");
        List<? extends User> initialUsers = null;
        Chat instance = new Chat("chitchatcat", -1);
        instance.initUsers(initialUsers);
    }

    /**
     * Test of addWaitingUserClient method, of class Chat.
     */
    @Test
    public void testAddWaitingUserClient() {
        System.out.println("addWaitingUserClient");
        User user = null;
        boolean online = false;
        Chat instance = new Chat("chitchatcat", -1);
        instance.addWaitingUserClient(user, online);
    }

    /**
     * Test of initUser method, of class Chat.
     */
    @Test
    public void testInitUser_User() {
        System.out.println("initUser");
        User newUser = null;
        Chat instance = new Chat("chitchatcat", -1);
        instance.initUser(newUser);
        
    }

    /**
     * Test of initUser method, of class Chat.
     */
    @Test
    public void testInitUser_User_boolean() {
        System.out.println("initUser");
        User newUser = null;
        boolean online = false;
        Chat instance = new Chat("chitchatcat", -1);
        instance.initUser(newUser, online);
        
    }

    /**
     * Test of findUser method, of class Chat.
     */
    @Test
    public void testFindUser_String() {
        System.out.println("findUser");
        String userName = "";
        Chat instance = new Chat("chitchatcat", -1);
        User expResult = null;
        User result = instance.findUser(userName);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of findUser method, of class Chat.
     */
    @Test
    public void testFindUser_int() {
        System.out.println("findUser");
        int userID = 0;
        Chat instance = new Chat("chitchatcat", -1);
        User expResult = null;
        User result = instance.findUser(userID);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of handlePacket method, of class Chat.
     */
    @Test
    public void testHandlePacket() {
        System.out.println("handlePacket");
        SelectionKey sender = null;
        Packet received = null;
        Chat instance = new Chat("chitchatcat", -1);
        instance.handlePacket(sender, received);
        
    }

    /**
     * Test of getConnectedUsers method, of class Chat.
     */
    @Test
    public void testGetConnectedUsers() {
        System.out.println("getConnectedUsers");
        Chat instance = new Chat("chitchatcat", -1);
        Map<User, Boolean> expResult = new HashMap<>();
        Map<User, Boolean> result = instance.getConnectedUsers();
        
        Set<User> exp = expResult.keySet();
        
        for (User user : exp)
        {
            if (!result.containsKey(user))
            {
                fail("result doesn't contain: " + user.getName());
            }
        }
    }

    /**
     * Test of getName method, of class Chat.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String name = "chitchatcat";
        Chat instance = new Chat(name, -1);
        String result = instance.getName();
        assertEquals(name, result);
        
        if (!instance.getName().equals(name))
        {
            fail("expected: " + name + " but was: " + instance.getName());
        }
    }

    /**
     * Test of setName method, of class Chat.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        Chat instance = new Chat("chitchatcat", -1);
        instance.setName(name);
        
        if (!instance.getName().equals(name))
        {
            fail("expected: " + name + " but was: " + instance.getName());
        }
    }

    /**
     * Test of getID method, of class Chat.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        Chat instance = new Chat("chitchatcat", -1);
        Integer expResult = -1;
        Integer result = instance.getID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setID method, of class Chat.
     */
    @Test
    public void testSetID() {
        System.out.println("setID");
        int id = 0;
        Chat instance = new Chat("chitchatcat", -1);
        instance.setID(id);
        
        assertEquals((long)id, (long)instance.getID());
    }
    
}
