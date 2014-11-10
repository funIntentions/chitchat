/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import com.lamepancake.chitchat.packet.UpdateChatsPacket;
import java.awt.ComponentOrientation;
import java.awt.Window;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @author shane
 */
public class ClientTest {

    // The mocked client GUI
    ClientGUI     mockedGUI;
    SocketChannel mockedChannel;
    Client        testClient;
    User          fakeUser;
    User          fakeUser2;
    Chat          fakeChat;
    Chat          fakeChat2;
    
    public ClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    

    @Before
    public void setUp() {
        HashMap<Chat, Integer> fakeChats = new HashMap<>(2);
        mockedGUI     = Mockito.mock(ClientGUI.class);
        mockedChannel = Mockito.mock(SocketChannel.class);
        testClient = new Client(mockedGUI, mockedChannel);
        
        fakeUser = new User().setID(0).setName("Shane");
        fakeUser2 = new User().setID(2).setName("Test");
        fakeChat = new Chat("fakeChat", 0);
        fakeChat2 = new Chat("fakeChat2", 1);
        
        fakeChat.initUser(new User().setName(fakeUser.getName()).setID(fakeUser.getID()).setRole(User.ADMIN), true);
        fakeChat.initUser(new User().setName(fakeUser2.getName()).setID(fakeUser2.getID()).setRole(User.USER), true);
        fakeChat2.initUser(new User().setName(fakeUser2.getName()).setID(fakeUser2.getID()).setRole(User.ADMIN), true);
        fakeChat2.initUser(new User().setName(fakeUser.getName()).setID(fakeUser.getID()).setRole(User.USER), true);
        
        fakeChats.put(fakeChat, User.ADMIN);
        fakeChats.put(fakeChat2, User.USER);
        
        // Set the private fields in the client
        try {
            
            Field guiVect = Window.class.getDeclaredField("ownedWindowList");
            Field fakeClientChats = testClient.getClass().getDeclaredField("chatList");
            Field fakeClientUser = testClient.getClass().getDeclaredField("clientUser");
            Field fakeClientWaiting = testClient.getClass().getDeclaredField("waitingOp");
            
            guiVect.setAccessible(true);
            fakeClientChats.setAccessible(true);
            fakeClientUser.setAccessible(true);
            fakeClientWaiting.setAccessible(true);
            
            guiVect.set((Window)mockedGUI, new Vector<WeakReference<Window>>());
            fakeClientChats.set(testClient, fakeChats);
            fakeClientUser.set(testClient, new User().setID(0).setName("Shane"));
            fakeClientWaiting.set(testClient, new HashMap<Integer, Packet>(1));
            
        } catch(NoSuchFieldException | IllegalAccessException n){
            System.out.println("Shit " + n.getMessage());
        }
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class Client.
     */
    @Test
    public void testStart() {

        System.out.println("start");
        Client instance = new Client(mockedGUI, mockedChannel);
        boolean expResult = true;
        boolean result = instance.start();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUser method, of class Client.
     */
    @Test
    public void testGetUser() {

        System.out.println("getUser");
        User expResult = fakeUser;
        User result = testClient.getUser();

        assertEquals(expResult, result);
    }

    /**
     * Test of sendJoinLeave method, of class Client.
     */
    @Test
    public void testSendJoinLeave() {
        ByteBuffer testJLLeave;
        ByteBuffer testJLJoin;
        
        int chatID = 0;

        // Test sending with LEAVE flag
        System.out.println("sendJoinLeave with LEAVE");
        testJLLeave = PacketCreator.createJoinLeave(0, chatID, JoinLeavePacket.LEAVE).serialise();
        testClient.sendJoinLeave(chatID, JoinLeavePacket.LEAVE);
        try { verify(mockedChannel).write(testJLLeave); } catch(IOException i){}
        
        // Test sending with JOIN flag
        System.out.println("sendJoinLeave with JOIN");
        testJLJoin = PacketCreator.createJoinLeave(0, chatID, JoinLeavePacket.JOIN).serialise();
        testClient.sendJoinLeave(chatID, JoinLeavePacket.JOIN);
        try{ verify(mockedChannel).write(testJLJoin); } catch(IOException i){}
        
        // Test sending with bad chat ID
        System.out.println("sendJoinLeave with bad chat");
        testClient.sendJoinLeave(-1, JoinLeavePacket.LEAVE);
        verify(mockedGUI).displayError("Error in leaving chat: chat with ID -1 does not exist.", false);
    }
 
    /**
     * Test of sendLogin method, of class Client.
     */
    @Test
    public void testSendLogin() {
        System.out.println("sendLogin");
        String uname = "Shane";
        String pass = "Spoor";
        Packet expected = PacketCreator.createLogin(uname, pass);
        testClient.sendLogin(uname, pass);
        
        try{verify(mockedChannel).write(expected.serialise()); } catch(IOException i) {}
    }

    /**
     * Test of sendChatList method, of class Client.
     */
    @Test
    public void testSendChatList() {
        System.out.println("sendChatList");
        Packet toSend = PacketCreator.createChatList(fakeUser.getID());
        testClient.sendChatList();
        try{ verify(mockedChannel).write(toSend.serialise()); } catch (IOException i){}
    }

    /**
     * Test of sendRequestAccess method, of class Client.
     */
    @Test
    public void testSendRequestAccess() {
        System.out.println("sendRequestAccess");
        int chatID = 0;
        RequestAccessPacket req = PacketCreator.createRequestAccess(0, chatID);
        testClient.sendRequestAccess(chatID);
        
        try { verify(mockedChannel).write(req.serialise()); } catch (IOException i){}
    }

    /**
     * Test of sendMessageToChat method, of class Client.
     */
    @Test
    public void testSendMessage() {
        System.out.println("sendMessage");
        Packet msg;
        String chatName = fakeChat.getName();
        String message = "Hello";
        
        msg = PacketCreator.createMessage(message, fakeUser.getID(), fakeChat.getID());
        testClient.sendMessage(chatName, message);
        
        try{verify(mockedChannel).write(msg.serialise());} catch(IOException i) {}
        
    }

    /**
     * Test of sendBoot method, of class Client.
     */
    @Test
    public void testSendBoot() {
        System.out.println("sendBoot With Privileges");
        Packet expected = PacketCreator.createBoot(fakeChat, fakeUser2, fakeUser);
        testClient.sendBoot(fakeChat.getName(), fakeUser2.getName());
        
        try{verify(mockedChannel).write(expected.serialise());}catch(IOException i){}
        
        System.out.println("sendBoot Without Privileges");
        testClient.sendBoot(fakeChat2.getName(), fakeUser2.getName());
        verify(mockedGUI).displayError("Insufficient privileges to boot users from this chat.", false);
        
    }

    /**
     * Test of sendChangeRole method, of class Client.
     */
    @Test
    public void testSendChangeRole() {
        System.out.println("sendUserRole");
        Packet expected = PacketCreator.createChangeRole(fakeChat.getID(), fakeUser2.getID(), fakeUser.getID(), User.ADMIN);
        
        testClient.sendChangeRole(fakeChat.getID(), fakeUser2.getName(), User.ADMIN);
        try{verify(mockedChannel).write(expected.serialise());}catch(IOException i){}
    }

    /**
     * Test of sendCreateChat method, of class Client.
     */
    @Test
    public void testSendCreateChat() {
        System.out.println("sendCreateChat");
        
        final String newChatName = "Test2";
        final UpdateChatsPacket expected = PacketCreator.createUpdateChats(newChatName, -1, UpdateChatsPacket.CREATE);
        
        testClient.sendCreateChat(newChatName, -1);
        
        try{verify(mockedChannel).write(expected.serialise());}catch(IOException i){}
    }

    /**
     * Test of sendUpdateChat method, of class Client.
     */
    @Test
    public void testSendUpdateChat() {
        System.out.println("sendUpdateChat");
        int chatid = fakeChat.getID();
        String chatname = "NewChat";
        Packet expected = PacketCreator.createUpdateChats(chatname, chatid, UpdateChatsPacket.UPDATE);
        
        try{verify(mockedChannel).write(expected.serialise());}catch(IOException i){}
    }

    /**
     * Test of sendDeleteChat method, of class Client.
     */
    @Test
    public void testSendDeleteChat() {
        System.out.println("sendUpdateChat");
        int chatid = fakeChat.getID();
        String chatname = "NewChat";
        Packet expected = PacketCreator.createUpdateChats(chatname, chatid, UpdateChatsPacket.DELETE);
        
        try{verify(mockedChannel).write(expected.serialise());}catch(IOException i){}
    }

    /**
     * Test of getUsersAsString method, of class Client.
     */
    @Test
    public void testGetUsersAsString() {
        System.out.println("getUsersAsString");
        String chatName = fakeChat.getName();
        String[] expResult = new String[] {fakeUser.getName() + ", online", fakeUser2.getName() + ", online" };
        String[] result = testClient.getUsersAsString(chatName);
        
        Arrays.sort(result);
        Arrays.sort(expResult);
        assertArrayEquals(expResult, result);
        
    }

    /**
     * Test of getChatName method, of class Client.
     */
    @Test
    public void testGetChatByID() {
        System.out.println("getChatName");
        int chatID = fakeChat.getID();
        Chat result = testClient.getChatByID(chatID);
        
        assertEquals(fakeChat, result);
    }

    /**
     * Test of getChatByName method, of class Client.
     */
    @Test
    public void testGetChatByName() {
        System.out.println("getChatByName");
        String chatName = fakeChat2.getName();
        Chat expResult = fakeChat2;
        Chat result = testClient.getChatByName(chatName);

        assertEquals(expResult, result);
    }
}
