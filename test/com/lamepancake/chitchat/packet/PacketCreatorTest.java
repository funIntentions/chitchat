/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author shane
 */
public class PacketCreatorTest {
    
    public PacketCreatorTest() {
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
     * Test of createWhoIsIn method, of class PacketCreator.
     */
    @Test
    public void testCreateWhoIsIn() {
        System.out.println("createWhoIsIn");
        Map<User, Boolean> chatUsers = null;
        int chatID = 0;
        int userID = 0;
        WhoIsInPacket expResult = null;
        WhoIsInPacket result = PacketCreator.createWhoIsIn(chatUsers, chatID, userID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createBoot method, of class PacketCreator.
     */
    @Test
    public void testCreateBoot() {
        System.out.println("createBoot");
        Chat chat = null;
        User booted = null;
        User booter = null;
        BootPacket expResult = null;
        BootPacket result = PacketCreator.createBoot(chat, booted, booter);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createChangeRole method, of class PacketCreator.
     */
    @Test
    public void testCreateChangeRole() {
        System.out.println("createChangeRole");
        int chat = 0;
        int user = 0;
        int sender = 0;
        int role = 0;
        ChangeRolePacket expResult = null;
        ChangeRolePacket result = PacketCreator.createChangeRole(chat, user, sender, role);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createChatList method, of class PacketCreator.
     */
    @Test
    public void testCreateChatList_3args() {
        System.out.println("createChatList");
        List<Chat> chats = null;
        Map<Integer, Integer> roles = null;
        int userid = 0;
        ChatListPacket expResult = null;
        ChatListPacket result = PacketCreator.createChatList(chats, roles, userid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createChatList method, of class PacketCreator.
     */
    @Test
    public void testCreateChatList_int() {
        System.out.println("createChatList");
        int userID = 0;
        ChatListPacket expResult = null;
        ChatListPacket result = PacketCreator.createChatList(userID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createChatNotify method, of class PacketCreator.
     */
    @Test
    public void testCreateChatNotify() {
        System.out.println("createChatNotify");
        int chat = 0;
        String name = "";
        int flag = 0;
        ChatNotifyPacket expResult = null;
        ChatNotifyPacket result = PacketCreator.createChatNotify(chat, name, flag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createJoinLeave method, of class PacketCreator.
     */
    @Test
    public void testCreateJoinLeave() {
        System.out.println("createJoinLeave");
        int userid = 0;
        int chatid = 0;
        int flag = 0;
        JoinLeavePacket expResult = null;
        JoinLeavePacket result = PacketCreator.createJoinLeave(userid, chatid, flag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createLogin method, of class PacketCreator.
     */
    @Test
    public void testCreateLogin() {
        System.out.println("createLogin");
        String username = "";
        String password = "";
        LoginPacket expResult = null;
        LoginPacket result = PacketCreator.createLogin(username, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createLogout method, of class PacketCreator.
     */
    @Test
    public void testCreateLogout() {
        System.out.println("createLogout");
        int userID = 0;
        LogoutPacket expResult = null;
        LogoutPacket result = PacketCreator.createLogout(userID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMessage method, of class PacketCreator.
     */
    @Test
    public void testCreateMessage() {
        System.out.println("createMessage");
        String message = "";
        int userID = 0;
        int chatID = 0;
        MessagePacket expResult = null;
        MessagePacket result = PacketCreator.createMessage(message, userID, chatID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createOperationStatus method, of class PacketCreator.
     */
    @Test
    public void testCreateOperationStatus() {
        System.out.println("createOperationStatus");
        int userID = 0;
        int chatID = 0;
        int flag = 0;
        int operation = 0;
        OperationStatusPacket expResult = null;
        OperationStatusPacket result = PacketCreator.createOperationStatus(userID, chatID, flag, operation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createRequestAccess method, of class PacketCreator.
     */
    @Test
    public void testCreateRequestAccess() {
        System.out.println("createRequestAccess");
        int userid = 0;
        int chatid = 0;
        RequestAccessPacket expResult = null;
        RequestAccessPacket result = PacketCreator.createRequestAccess(userid, chatid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createUpdateChats method, of class PacketCreator.
     */
    @Test
    public void testCreateUpdateChats() {
        System.out.println("createUpdateChats");
        String name = "";
        int id = 0;
        int update = 0;
        UpdateChatsPacket expResult = null;
        UpdateChatsPacket result = PacketCreator.createUpdateChats(name, id, update);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createUserNotify method, of class PacketCreator.
     */
    @Test
    public void testCreateUserNotify() {
        System.out.println("createUserNotify");
        String userName = "";
        int userid = 0;
        int chatid = 0;
        int role = 0;
        int flag = 0;
        UserNotifyPacket expResult = null;
        UserNotifyPacket result = PacketCreator.createUserNotify(userName, userid, chatid, role, flag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
