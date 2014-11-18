/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.Group;
import com.lamepancake.chitchat.User;
import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Test of createWhoIsIn method, of class PacketCreator.
     */
    @Test
    public void testCreateWhoIsIn() {
        System.out.println("createWhoIsIn");
        Map<User, Boolean> chatUsers = new HashMap();
        
        User booted = new User();
        booted.setID(1);
        booted.setName("Harold");
        booted.setPassword("No");
        booted.setRole(User.USER);
        
        User booter = new User();
        booter.setID(2);
        booter.setName("Trevor");
        booter.setPassword("Yes");
        booter.setRole(User.ADMIN);
        
        chatUsers.put(booted, false);
        chatUsers.put(booter, true);
                
        int chatID = 54;
        int userID = 32;
        int orgID = 69;
        
        int dataLen = 24 + 12 + 2 + ("Trevor".length() * 2) + ("Harold".length() * 2);
        WhoIsInPacket expResult = new WhoIsInPacket(chatUsers, dataLen, chatID, userID, orgID);
        WhoIsInPacket result = PacketCreator.createWhoIsIn(chatUsers, chatID, userID, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createBoot method, of class PacketCreator.
     */
    @Test
    public void testCreateBoot() {
        System.out.println("createBoot");
        Chat chat = new Chat("Hello");
        chat.setID(4);
        
        User booted = new User();
        booted.setID(1);
        booted.setName("Dan");
        booted.setPassword("No");
        booted.setRole(User.USER);
        
        User booter = new User();
        booter.setID(2);
        booter.setName("Trevor");
        booter.setPassword("Yes");
        booter.setRole(User.ADMIN);
        
        Group o = new Group("Hello", booter);
        
        BootPacket expResult = new BootPacket(chat, booted, booter, o);
        BootPacket result = PacketCreator.createBoot(chat, booted, booter, o);
        assertEquals(expResult, result);
    }

    /**
     * Test of createChangeRole method, of class PacketCreator.
     */
    @Test
    public void testCreateChangeRole() {
        System.out.println("createChangeRole");
        int chat = 6;
        int user = 1;
        int sender = 7;
        int role = User.ADMIN;
        int orgID = 8;
        
        ChangeRolePacket expResult = new ChangeRolePacket(chat, user, sender, role, orgID);
        ChangeRolePacket result = PacketCreator.createChangeRole(chat, user, sender, role, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createChatList method, of class PacketCreator.
     */
    @Test
    public void testCreateChatList_3args() {
        System.out.println("createChatList");
        List<Chat> chats = new ArrayList();
        Map<Integer, Integer> roles = new HashMap();
        
        roles.put(7, User.USER);
        roles.put(3, User.ADMIN);
        
        Chat c1 = new Chat("Shane");
        c1.setID(7);
        Chat c2 = new Chat("Tim");
        c2.setID(3);
        
        chats.add(c1);
        chats.add(c2);
        
        //Map<Chat, Integer> chatRoles = new HashMap();
        //chatRoles.put(new Chat("Shane"), User.USER);
        //chatRoles.put(new Chat("Tim"), User.ADMIN);
       
        //int dataLen = (chats.size() * 8) + 8 + 8 + ("Shane".length() * 2) + ("Tim".length() * 2);
        
        int dataLen = (chats.size() * 8) + 8; // Space for chat IDs, roles, the number of chats, and the user ID
        Map<Chat, Integer> chatList = new HashMap<>();
        
        // Create the map of chats to roles
        for(Chat c : chats)
        {
            Integer r = roles.get(c.getID());
            if(r != null)
            {
                r = roles.get(c.getID());
            }
            else
            {
                r = User.UNSPEC;
            }
            chatList.put(c, r);
            
            dataLen += c.getName().length() * 2; //Chat Name          
            dataLen += 4; //legnth of the name
        }
        
        int userid = 2;
        int orgID = 33;
        
        ChatListPacket expResult = new ChatListPacket(chatList, dataLen, userid, orgID);
        ChatListPacket result = PacketCreator.createChatList(chats, roles, userid, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createChatList method, of class PacketCreator.
     */
    @Test
    public void testCreateChatList_int() {
        System.out.println("createChatList");
        int userID = 9;
        int orgID = 33;
        ChatListPacket expResult = new ChatListPacket(userID, orgID);
        ChatListPacket result = PacketCreator.createChatList(userID, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createChatNotify method, of class PacketCreator.
     */
    @Test
    public void testCreateChatNotify() {
        System.out.println("createChatNotify");
        int chat = 7;
        String name = "Trevor";
        int flag = 0;
        int orgID = 5;
        
        ChatNotifyPacket expResult = new ChatNotifyPacket(chat, name, flag, orgID);
        ChatNotifyPacket result = PacketCreator.createChatNotify(chat, name, flag, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createJoinLeave method, of class PacketCreator.
     */
    @Test
    public void testCreateJoinLeave() {
        System.out.println("createJoinLeave");
        int userid = 69;
        int chatid = 86;
        int flag = JoinLeavePacket.JOIN;
        int orgID = 49;
        
        JoinLeavePacket expResult = new JoinLeavePacket(userid, chatid, flag, orgID);
        JoinLeavePacket result = PacketCreator.createJoinLeave(userid, chatid, flag, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createLogin method, of class PacketCreator.
     */
    @Test
    public void testCreateLogin() {
        System.out.println("createLogin");
        String username = "Trevor";
        String password = "Ware";
        LoginPacket expResult = new LoginPacket(username, password);
        LoginPacket result = PacketCreator.createLogin(username, password);
        assertEquals(expResult, result);
    }

    /**
     * Test of createLogout method, of class PacketCreator.
     */
    @Test
    public void testCreateLogout() {
        System.out.println("createLogout");
        int userID = 7;
        LogoutPacket expResult = new LogoutPacket(userID);
        LogoutPacket result = PacketCreator.createLogout(userID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createMessage method, of class PacketCreator.
     */
    @Test
    public void testCreateMessage() {
        System.out.println("createMessage");
        String message = "This is a test.";
        int userID = 79;
        int chatID = 32;
        int orgID = 43;
        
        MessagePacket expResult = new MessagePacket(message, userID, chatID, orgID);
        MessagePacket result = PacketCreator.createMessage(message, userID, chatID, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createOperationStatus method, of class PacketCreator.
     */
    @Test
    public void testCreateOperationStatus() {
        System.out.println("createOperationStatus");
        int userID = 76;
        int chatID = 45;
        int flag = OperationStatusPacket.OP_LOGIN;
        int operation = 0;
        int orgID = 32;
        
        OperationStatusPacket expResult = new OperationStatusPacket(userID, chatID, flag, operation, orgID);
        OperationStatusPacket result = PacketCreator.createOperationStatus(userID, chatID, flag, operation, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createRequestAccess method, of class PacketCreator.
     */
    @Test
    public void testCreateRequestAccess() {
        System.out.println("createRequestAccess");
        int userid = 54;
        int chatid = 4657687;
        int orgID = 324;
        
        RequestAccessPacket expResult = new RequestAccessPacket(userid, chatid, orgID);
        RequestAccessPacket result = PacketCreator.createRequestAccess(userid, chatid, orgID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createUpdateChats method, of class PacketCreator.
     */
    @Test
    public void testCreateUpdateChats() {
        System.out.println("createUpdateChats");
        String name = "Chats suck guys";
        int id = 435;
        int update = 432;
        int orgID = 321;
        
        UpdateChatsPacket expResult = new UpdateChatsPacket(name, id, update, 321);
        UpdateChatsPacket result = PacketCreator.createUpdateChats(name, id, update, 321);
        assertEquals(expResult, result);
    }

    /**
     * Test of createUserNotify method, of class PacketCreator.
     */
    @Test
    public void testCreateUserNotify() {
        System.out.println("createUserNotify");
        String userName = "Hello";
        int userid = 54;
        int chatid = 573;
        int role = User.WAITING;
        int flag = UserNotifyPacket.BOOTED;
        int orgID = 3245;
        
        UserNotifyPacket expResult = new UserNotifyPacket(userName, userid, chatid, role, flag, orgID);
        UserNotifyPacket result = PacketCreator.createUserNotify(userName, userid, chatid, role, flag, orgID);
        assertEquals(expResult, result);
    }
    
}
