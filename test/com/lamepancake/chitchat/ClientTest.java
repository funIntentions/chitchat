/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.JoinLeavePacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.PacketCreator;
import com.lamepancake.chitchat.packet.RequestAccessPacket;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
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
    Client        fakeClient;
    User          fakeUser;
    Chat          fakeChat;
    
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
        mockedGUI     = Mockito.mock(ClientGUI.class);
        mockedChannel = Mockito.mock(SocketChannel.class);
        fakeClient = new Client(mockedGUI, mockedChannel);
        fakeUser = new User().setID(0).setName("Shane");
        fakeChat = new Chat("fakeChat", 0);
        
        // Set the private fields in the client
        try {
            Field fakeClientChats = fakeClient.getClass().getDeclaredField("chatList");
            Field fakeClientUser = fakeClient.getClass().getDeclaredField("clientUser");
            
            fakeClientChats.setAccessible(true);
            fakeClientUser.setAccessible(true);
            
            fakeClientChats.set(fakeClient, new HashMap<Chat, Integer>().put(new Chat("fakeChat", 0), User.ADMIN));
            fakeClientUser.set(fakeClient, new User().setID(0).setName("Shane"));
            
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
        User result = fakeClient.getUser();

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
        fakeClient.sendJoinLeave(chatID, JoinLeavePacket.LEAVE);
        try { verify(mockedChannel).write(testJLLeave); } catch(IOException i){}
        
        // Test sending with JOIN flag
        System.out.println("sendJoinLeave with JOIN");
        testJLJoin = PacketCreator.createJoinLeave(0, chatID, JoinLeavePacket.JOIN).serialise();
        fakeClient.sendJoinLeave(chatID, JoinLeavePacket.JOIN);
        try{ verify(mockedChannel).write(testJLJoin); } catch(IOException i){}
        
        // Test sending with bad chat ID
        System.out.println("sendJoinLeave with bad chat");
        fakeClient.sendJoinLeave(-1, JoinLeavePacket.LEAVE);
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
        fakeClient.sendLogin(uname, pass);
        
        try{ verify(mockedChannel).write(PacketCreator.createLogin(uname, pass).serialise()); } catch(IOException i) {}
    }

    /**
     * Test of sendChatList method, of class Client.
     */
    @Test
    public void testSendChatList() {
        System.out.println("sendChatList");
        Client instance = new Client(mockedGUI, mockedChannel);
        ChatListPacket toSend = PacketCreator.createChatList(0);
        fakeClient.sendChatList();
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
        fakeClient.sendRequestAccess(chatID);
        
        try { verify(mockedChannel).write(req.serialise()); } catch (IOException i){}
    }

    /**
     * Test of sendMessageToChat method, of class Client.
     */
    @Test
    public void testSendMessageToChat() {
        System.out.println("sendMessageToChat");
        MessagePacket msg;
        String chatName = fakeChat.getName();
        String message = "Hello";
        
        msg = PacketCreator.createMessage(message, fakeUser.getID(), fakeChat.getID());
        fakeClient.sendMessageToChat(chatName, message);
        
        try{verify(mockedChannel).write(msg.serialise());} catch(IOException i) {}
        
    }

    /**
     * Test of changeUserRole method, of class Client.
     */
    @Test
    public void testChangeUserRole() {
        System.out.println("changeUserRole");
        String userName = "Shane";
        int role = 0;
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.changeUserRole(userName, role);
    }

    /**
     * Test of bootUser method, of class Client.
     */
    @Test
    public void testBootUser() {
        System.out.println("bootUser");
        String userName = "";
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.bootUser(userName);
    }

    /**
     * Test of sendBoot method, of class Client.
     */
    @Test
    public void testSendBoot() {
        System.out.println("sendBoot");
        Chat chat = null;
        User user = null;
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendBoot(chat, user);
    }

    /**
     * Test of sendChangeRole method, of class Client.
     */
    @Test
    public void testSendChangeRole() {
        System.out.println("sendChangeRole");
        int userid = 0;
        int chatid = 0;
        int role = 0;
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendChangeRole(userid, chatid, role);
    }

    /**
     * Test of sendCreateChat method, of class Client.
     */
    @Test
    public void testSendCreateChat() {
        System.out.println("sendCreateChat");
        String chatname = "";
        int id = 0;
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendCreateChat(chatname, id);
    }

    /**
     * Test of sendUpdateChat method, of class Client.
     */
    @Test
    public void testSendUpdateChat() {
        System.out.println("sendUpdateChat");
        int chatid = 0;
        String chatname = "";
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendUpdateChat(chatid, chatname);
    }

    /**
     * Test of sendDeleteChat method, of class Client.
     */
    @Test
    public void testSendDeleteChat() {
        System.out.println("sendDeleteChat");
        int chatid = 0;
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendDeleteChat(chatid);
    }

    /**
     * Test of sendMessage method, of class Client.
     */
    @Test
    public void testSendMessage() {
        System.out.println("sendMessage");
        int chatID = 0;
        String msg = "";
        Client instance = new Client(mockedGUI, mockedChannel);
        instance.sendMessage(chatID, msg);
    }

    /**
     * Test of getUsersAsString method, of class Client.
     */
    @Test
    public void testGetUsersAsString() {
        System.out.println("getUsersAsString");
        String chatName = "";
        Client instance = new Client(mockedGUI, mockedChannel);
        String[] expResult = null;
        String[] result = instance.getUsersAsString(chatName);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getChatName method, of class Client.
     */
    @Test
    public void testGetChatName() {
        System.out.println("getChatName");
        int chatID = 0;
        String expResult = "fakeChat";
        String result = fakeClient.getChatName(chatID);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChatByName method, of class Client.
     */
    @Test
    public void testGetChatByName() {
        System.out.println("getChatByName");
        String chatName = "";
        Client instance = new Client(mockedGUI, mockedChannel);
        Chat expResult = null;
        Chat result = instance.getChatByName(chatName);
        assertEquals(expResult, result);
    }
}
