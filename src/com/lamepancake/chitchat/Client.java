package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.*;
import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.*;
import javax.swing.SwingWorker;

/**
 * Transmits chat and other messages to the server.
 * 
 * The Client class only sends messages; receiving the messages of other users is
 * handled by the inner ListenFromServer class in another thread.
 * 
 * Loosely based upon the work of Paul-Benoit Larochelle (see link below).
 * @see http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 */
public class Client {

    /**
     * The socket over which the client sends and receives data.
     */
    private SocketChannel socket;

    /**
     * A GUI, if the client has one.
     */
    private final ClientGUI gui;    
    
    /**
     * The list of chats and the user's role in each.
     */
    private Map<Chat, Integer> chatList;

    /**
     * Whether the user is still connected to the server.
     */
    private volatile boolean connected;
    
    /**
     * Whether the user has logged out or not.
     */
    private volatile boolean logout;

    /**
     * The User object representing this client.
     */
    private final User clientUser;
    
    /**
     * An operation for which we're awaiting confirmation.
     * 
     * This could be OP_LOGIN, OP_CRUD, OP_REQACCESS. No other operations
     * requiring confirmation may be issued while waiting for an operation.
     */
    private final Map<Integer, Packet> waitingOp = new HashMap<>(1);
    
    /**
     * Stores a list of operation ID's and their associated completion callbacks.
     * 
     * When the client issues a request which requires confirmation (a database
     * operation, request for access to a chat, login attempt, etc), it sets a
     * callback function to be run when the corresponding OperationStatusPacket
     * arrives.
     */
    //private final Map<Integer, Runnable> completionCallbacks;

    /**
     * Constructs a new Client with the given server information and 
     * authentication details.
     * 
     * The Client won't try to connect to the server and start accepting input
     * until its start() method is called.
     * 
     * @todo Make the client call open() on the socket rather than the GUI.
     * 
     * @param s   The socket on which to send and receive information.
     * @param gui The GUI for displaying information.
     */
    public Client(ClientGUI gui, SocketChannel s) 
    {
            this.socket = s;
            this.clientUser = new User();
            this.gui = gui;
            
            chatList = new HashMap<>();
            connected = true;
            logout = false;
    }
    
    /**
     * Attempts to connect to the server and begins listening for user input.
     * 
     * @return Whether the connection attempt succeeded.
     */
    public boolean start() 
    {
        // creates the Thread to listen from the server 
        new ListenFromServer().execute();
        return true;
    }

    /**
     * Sends a packet to the server.
     * 
     * @param packet The packet to be sent.
     */
    private void sendPacket(Packet packet) 
    {
        //make sure the user is connected to the server
        if(!connected)
            return;
        
        try 
        {            
            socket.write(packet.serialise());
        } 
        catch(IOException e) 
        {
            gui.displayError(e.getMessage());
        }
    }
    
    /**
     * Tells the server that the user is joining or leaving a chat.
     * 
     * @param chatID  The chat to join or leave.
     * @param joining Whether the user is joining or leaving.
     */
    public void sendJoinLeave(int chatID, int joining)
    {
        final JoinLeavePacket j = PacketCreator.createJoinLeave(clientUser.getID(), chatID, joining);
        waitingOp.clear();
        // Wait for something else?...
        sendPacket(j);
    }
    
    /**
     * Attempts to login with the given credentials.
     * 
     * @param uname The username to use for logging in.
     * @param pass  The password to use for logging in.
     */
    public void sendLogin(final String uname, final String pass)
    {
        final LoginPacket l = PacketCreator.createLogin(uname, pass);
        waitingOp.clear();
        waitingOp.put(OperationStatusPacket.OP_LOGIN, l);
        sendPacket(l);
    }
    
    public void sendChatList()
    {
        final ChatListPacket cl = PacketCreator.createChatList();
        waitingOp.clear();
        waitingOp.put(OperationStatusPacket.CHATLIST, cl);
        sendPacket(cl);
    }
    /**
     * Send a packet to request access to the given chat.
     * 
     * @param chatID The chat to which we're requesting access.
     */
    public void sendRequestAccess(int chatID)
    {
        final RequestAccessPacket ra = PacketCreator.createRequestAccess(this.clientUser.getID(), chatID);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_REQACCESS, ra);
        sendPacket(ra);
    }
    
    /**
     * If we are an admin in the given chat, sends a boot packet to boot the
     * specified user.
     * @param chat The chat from which to boot a user.
     * @param user The user to boot.
     */
    public void sendBoot(Chat chat, User user)
    {
        if(!restrict(chat.getID(), User.ADMIN))
            return;

        final BootPacket b = PacketCreator.createBoot(chat, user, clientUser);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_CRUD, b);
        sendPacket(b);
    }
    
    /**
     * Tells the server to change the given user's role in the specified chat
     * to a new role.
     * @param userid The User whose role is to be changed.
     * @param chatid The Chat where the new role will be applied.
     * @param role   The role to assign to the User.
     */
    public void sendChangeRole(int userid, int chatid, int role)
    {
        if(!restrict(chatid, User.ADMIN))
            return;

        final ChangeRolePacket cr = PacketCreator.createChangeRole(userid, chatid, role);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_CRUD, cr);
        sendPacket(cr);
    }
    
    /**
     * Tells the server to create a new chat with the given name.
     * 
     * @param chatname The name of the new chat.
     * @param id       Ignored.
     */
    public void sendCreateChat(String chatname, int id)
    {
        final UpdateChatsPacket uc = PacketCreator.createUpdateChats(chatname, id, UpdateChatsPacket.CREATE);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_CRUD, uc);
        sendPacket(uc);
    }
    
    /**
     * Tells the server to change the given chat's name to the new name.
     * 
     * @param chatid   The ID of the chat to change.
     * @param chatname The new name for the chat.
     */
    public void sendUpdateChat(int chatid, String chatname)
    {
        if(!restrict(chatid, User.ADMIN))
            return;

        final UpdateChatsPacket uc = PacketCreator.createUpdateChats(chatname, chatid, UpdateChatsPacket.UPDATE);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_CRUD, uc);
        sendPacket(uc);
    }
    
    /**
     * Tells the server to delete the given chat.
     * 
     * @param chatid The ID of the chat to remove.
     */
    public void sendDeleteChat(int chatid)
    {
        if(!restrict(chatid, User.ADMIN))
            return;

        final UpdateChatsPacket uc = PacketCreator.createUpdateChats("", chatid, UpdateChatsPacket.DELETE);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_CRUD, uc);
        sendPacket(uc);
    }
    
    /**
     * Sends a message to other users in the given chat.
     * 
     * @param chatID The ID of the chat.
     * @param msg    The message to send.
     */
    public void sendMessage(int chatID, String msg)
    {
        sendPacket(PacketCreator.createMessage(msg, this.clientUser.getID(), chatID));
    }
    
    
    /**
     * Determines if the user has the required access rights for a given task.
     * 
     * @param chatID The ID of the chat in which to check the role.
     * @param role   The minimum role that users must have to do the action.
     * @return       Whether the user has the minimum role.
     */
    private boolean restrict(int chatID, int role)
    {
        Chat chat = null;
        Integer roleInChat;
        for(Chat c : chatList.keySet())
        {
            if(c.getID() == chatID)
            {
                chat = c;
                break;
            }
        }
         
        if(chat == null)
            return false;

        roleInChat= chatList.get(chat);
        if(roleInChat == null)
            return false;
        
        return User.restrict(roleInChat, role);
    }
    
    /**
     * Close socket, end listening thread, etc.
     * 
     * @todo Actually implement this...
     */
    private void disconnect() 
    {		
            // inform the GUI
            /*if(cg != null)
                    cg.connectionFailed();*/
    }

    /**
         * Adds a new user to the user list or sets this client's userID.
         * 
         * If the JoinedPacket contains an empty username field, then it designates
         * this user's ID and role.
         * 
         * @param joined The JoinedPacket containing the new user's information.
         */
//        private void addUser(JoinedPacket joined)
//        {
//            User u = joined.getUser();
//            display("[ " + u + " has joined the chat ]");
//            users.add(u);
//        }
//        
//        /**
//         * Removes from the list the user who left the chat.
//         * @param left The LeftPacket containing the user's ID.
//         */
//        private void removeUser(LeftPacket left)
//        {
//            for(int i = 0; i < users.size(); i++)
//            {
//                User u = users.get(i);
//                if(u.getID() == left.getUserID())
//                {
//                    display("[ " + u + " has left the chat ]");
//                    users.remove(i);
//                    break;
//                }
//            }
//        }
    
    /**
     * Parses the command arguments and creates a client.
     * 
     * @param args The command line arguments.
     * @return     A new Client object or null if one or more arguments were incorrect.
     */
    public static SocketChannel parseCmdArgs(String[] args)
    {
        int     portNumber  = 1500;
        String  serverName  = "localhost";

        // depending of the number of arguments provided we fall through
        switch(args.length) {
            // username portNumber serverAddr
            case 3:
                    serverName = args[2];
            // username portNumber
            case 2:
                try {
                    portNumber = Integer.parseInt(args[1]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                    return null;
                }

            // default values
            case 0:
                break;
            // invalid number of arguments
            default:
                System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
            return null;
        }
        // create the Client object
        InetSocketAddress addr = new InetSocketAddress(serverName, portNumber);

        SocketChannel socket = null;
        try
        {
            socket = SocketChannel.open(addr);
        } 
        // if it failed not much I can so
        catch(Exception ec) 
        {
//                display("Error connectiong to server:" + ec);
//                return false;
        }
        return socket;
    }
    
    private void processChatList(ChatListPacket p)
    {
        Map<Chat, Integer> chats = p.getChatList();
        Set<Chat> keys = chats.keySet();
        
        String[] chatListList = new String[keys.size()];
        int i = 0;
        
        for(Chat c : keys)
        {
            switch(chats.get(c))
            {
                case User.ADMIN:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Scrum Master";
                case User.USER:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Developer";
                case User.WAITING:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Waiting";    
                case User.UNSPEC:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Unspecified"; 
                default:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Unspecified";    
            }
            i++;
        }
        
        gui.populateChatList(chatListList);
    }

    private void processUserList(WhoIsInPacket p)
    {
        Map<User, Boolean> users = p.getUsers();
        Set<User> keys = users.keySet();
        
        String[] userListList = new String[keys.size()];
        int i = 0;
        
        for(User u : keys)
        {
            if(users.get(u))
            {
                userListList[i] = u.getName() + ", online";
            }
            else
            {
                userListList[i] = u.getName() + ", offline";
            }
            i++;
        }
        gui.populateUserList(userListList);
    }
    
    /**
     * Receives a list of packets from the ListenFromServer thread and processes
     * them.
     * 
     * @param packets The published list of packets from ListenFromServer.
     */
    private synchronized void handlePackets(List<Packet> packets)
    {
        int type;
        for(Packet p: packets)
        {
            type = p.getType();
            switch(type)
            {
                case Packet.MESSAGE:
                    displayMessage((MessagePacket)p);
                    break;
                case Packet.WHOISIN:
                    processUserList((WhoIsInPacket)p);
                    // WhoIsInPacket userList = (WhoIsInPacket)p;
                    break;
                case Packet.CHATLIST:
                    // Call a method for displaying chats
                    processChatList((ChatListPacket)p);
                    break;
                case Packet.OPERATIONSTATUS:
                    operationStatusHandler((OperationStatusPacket)p);
                    break;
                case Packet.JOINLEAVE:
                    // Update the specific chat that we joined or left
                    break;
                case Packet.USERNOTIFY:
                    break;
                case Packet.CHATNOTIFY:
                    break;
                case Packet.BOOT:
                    break;
                case Packet.CHANGEROLE:
                    break;
                default:
                    System.err.println("Invalid packet received.");
                    break;
            }    
        }
    }
    
    /**
     * Displays a chat message in the GUI.
     * 
     * @param m The MessagePacket containing the message to display.
     */
    private void displayMessage(MessagePacket m)
    {
        Chat srcChat = null;
        User srcUser = null;
        
        // Get the source chat
        for(Chat c : chatList.keySet())
        {
            if(c.getID() == m.getChatID())
            {
                srcChat = c;
                break;
            }
        }
        
        if(srcChat == null)
            return;

        for(User u : srcChat.getConnectedUsers().keySet())
        {
            if(u.getID() == m.getUserID())
            {
                srcUser = u;
                break;
            }
        }
        
        if(srcUser == null)
            return;
        
        gui.displayUserMessage(m.getMessage(), srcUser.getName());
    }
    
    /**
     * Handles the success or failure of an issued operation.
     * 
     * @param op The OperationStatusPacket containing the operation result.
     * @todo Split this into reasonably-sized methods...
     */
    private void operationStatusHandler(OperationStatusPacket op)
    {
        int opType = waitingOp.keySet().iterator().next();
        Packet p = waitingOp.get(opType);
        
        // Must be an invalid packet...
        if(op.getOperation() != opType)
            return;
        
        // Welcome to the switch statement from Hell
        switch(opType)
        {
            case OperationStatusPacket.OP_CRUD:
                // Operation succeeded
                if(op.getStatus() == 1)
                {
                    final int waitingPacketType = p.getType();
                    if(waitingPacketType == Packet.UPDATECHAT)
                    {
                        UpdateChatsPacket up = (UpdateChatsPacket)p;
                        final int chatOpType = up.getUpdate();
                        switch(chatOpType)
                        {
                            case UpdateChatsPacket.CREATE:
                                // call an gui.updateChat method
                                break;
                            case UpdateChatsPacket.UPDATE:
                                // call a gui.createChat method
                                break;
                            case UpdateChatsPacket.DELETE:
                                // call a gui.deleteChat method
                                break;
                        }
                    }
                    else if(p.getType() == Packet.BOOT)
                    {
                        // call a gui.removeUser method
                    }
                }
                else
                {
                    // Display an error message. ClientGUI
                }
                break;
            case OperationStatusPacket.OP_LOGIN:
                if(op.getStatus() == 1)
                {
                    // Login succeeded. Call the GUI method
                    gui.loginValid(op.getUserID(), true);
                }
                else
                    gui.loginValid(-1, false);
                break; 
        }
        waitingOp.clear();
    }
    
    /**
     * Receives packets from the server and sends them to the Client for handling.
     * 
     * @todo Properly handle disconnection from the server.
     */
    private class ListenFromServer extends SwingWorker<Void, Packet>
    {
        /**
         * A PacketBuffer to read packets from the server.
         */
        private final PacketBuffer packetBuf;
            
        public ListenFromServer()
        {
            packetBuf = new PacketBuffer(socket);
        }
            
        @Override
        protected Void doInBackground() 
        {
            int     type;
            Packet  p;
            while(connected)
            {
                if(this.packetBuf.read() != PacketBuffer.FINISHED)
                {
                    if(this.packetBuf.getState() == PacketBuffer.DISCONNECTED)
                    {
                        if(!logout)
                        {
                            connected = false;
                            done();
                        }
                        return null;
                    }
                    else
                        continue;
                }
                
                // Get the packet and publish it to the Client for processing
                p = this.packetBuf.getPacket();
                publish(p);
                this.packetBuf.clearState();
            }
            return null;
        }
                
        @Override
        protected void process(List<Packet> packets)
        {
            // Lets the client's code handle any packets that have arrived
            handlePackets(packets);
        }
    }
}

