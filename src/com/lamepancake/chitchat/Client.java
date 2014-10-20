package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.*;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
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
    //private ClientGUI cg;
    
    /**
     * The client's username.
     */
    private final String username;
    
    /**
     * The client's password.
     */
    private final String password;
        
    /**
     * The ID identifying this client within the chat.
     */
    private int userID;
    
    /**
     * This client's role in the chat.
     */
    private int userRole;
      
    private Map<Chat, Integer> chatList;

    /**
     * Whether the user is in chat or waiting for access.
     */
    private boolean isWaiting;

    /**
     * Whether the user is still connected to the server.
     */
    private volatile boolean connected;
    
    /**
     * Whether the user has logged out or not.
     */
    private volatile boolean logout;
    
    private int chatID;
    
    private ClientGUI gui;
    
    /**
     * An operation for which we're awaiting confirmation.
     * 
     * This could be OP_LOGIN, OP_CRUD, OP_REQACCESS. No other operations
     * requiring confirmation may be issued while waiting for an operation.
     */
    private final Map<Integer, Packet> waitingOp = new HashMap<>(1);

    /**
     * Constructs a new Client with the given server information and authentication
     * details.
     * 
     * The Client won't try to connect to the server and start accepting input until
     * its start() method is called.
     * 
     * @param s         The socket.
     * @param username  The client's username.
     * @param password  The client's password.
     */
    public Client(String username, String password, SocketChannel s) 
    {
            this.username = username;
            this.socket = s;
            this.password = password;
            this.userID = -1;
            this.isWaiting = true;

            chatList = new HashMap<Chat, Integer>();
            connected = true;
            logout = false;
    }

    /**
     * Sets the GUI for this Client.
     * 
     * This method must be called for the client to functino properly.
     * @param gui The ClientGUI for this Client to use.
     */
    private void setGUI(ClientGUI gui)
    {
        this.gui = gui;
    }
    
    /**
     * Attempts to connect to the server and begins listening for user input.
     * 
     * @return Whether the connection attempt succeeded.
     */
    public boolean start() 
    {

        String msg = "Connection accepted ";
        display(msg);
        msg = "Status Update : Waiting to be added to chat.";
        display(msg);

        // creates the Thread to listen from the server 
        new ListenFromServer().execute();
        return true;
    }
    
    /**
     * Whether the user is waiting to enter the chat.
     * 
     * @return <code>true</code> if the user is waiting, <code>false</code> otherwise.
     */
    public boolean waiting()
    {
        return isWaiting;
    }

    /**
     * Displays a message to the console or the GUI if present.
     */
    private void display(String msg) 
    {
        System.out.println(msg);
        //pass to gui
    }

    /**
     * Sends a packet to the server.
     */
    private void sendPacket(Packet msg) 
    {
        try 
        {
            //make sure the user is connected to the server
            if(!connected)
            {
                return;
            }
            
            socket.write(msg.serialise());
        } 
        catch(IOException e) 
        {
            display("Could not send message: " + e.getMessage());
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
        sendPacket(PacketCreator.createJoinLeave(userID, chatID, joining));
    }
    
    /**
     * Attempts to login with the given credentials.
     * 
     * @param uname The username to use for logging in.
     * @param pass  The password to use for logging in.
     */
    public void sendLogin(final String uname, final String pass)
    {
        final LoginPacket l = PacketCreator.createLogin(username, password);
        waitingOp.clear();
        waitingOp.put(OperationStatusPacket.OP_LOGIN, l);
        sendPacket(l);
    }
    
    /**
     * Send a packet to request access to the given chat.
     * 
     * @param chatID The chat to which we're requesting access.
     */
    public void sendRequestAccess(int chatID)
    {
        sendPacket(PacketCreator.createRequestAccess(userID, chatID));
    }
    
    /**
     * If we are an admin in the given chat, sends a boot packet to boot the
     * specified user.
     * @param chat The chat from which to boot a user.
     * @param user The user to boot.
     */
    public void sendBoot(Chat chat, User user)
    {
        if(userRole == User.ADMIN)
        {
            sendPacket(PacketCreator.createBoot(chat, user, user));
        }
    }
    
    public void sendChangeRole(int userid, int chatid, int role)
    {
        if(userRole == User.ADMIN)
        {
            sendPacket(PacketCreator.createChangeRole(userid, chatid, role));
        }
    }
    
    public void sendMessage(int chatID, String msg)
    {
        sendPacket(PacketCreator.createMessage(msg, userID, chatID));
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
     * The main entry point for a client.
     * 
     * To use the Client in console mode, use one of the following commands:
     * 
     * > java Client
     * > java Client username
     * > java Client username portNumber
     * > java Client username portNumber serverAddress
     * 
     * The default values for username, portNumber and serverAddress are "Anonymous",
     * 1500 and "localhost", repsectively.
     * 
     * @param args The command line arguments as described above.
     */
    public static void main(String[] args)
    {
        String server;
        int port;
        SocketChannel s = parseCmdArgs(args);
        
        Client c = new Client("Shane", "SuperPass", s);
        
        // Set up our wonderful coupling
        ClientGUI clientGUI = new ClientGUI(c);
        c.setGUI(clientGUI);
    }

    /**
     * Parses the command arguments and creates a client.
     * 
     * @param args The command line arguments.
     * @return     A new Client object or null if one or more arguments were incorrect.
     */
    private static SocketChannel parseCmdArgs(String[] args)
    {
        int     portNumber  = 1500;
        String  serverName  = "localhost";
        String  name        = "Anonymous";
        String  pw          = "Anon";

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
            // username
            case 1: 
                name = args[0];
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
    
    public int getUserRole()
    {
        return userRole;
    }

    public int getUserID()
    {
        return userID;
    }

    public String getUserName()
    {
        return username;
    }

    public int getChatID()
    {
        return chatID;
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
                    // Update list of users
                    // WhoIsInPacket userList = (WhoIsInPacket)p;
                    break;
                case Packet.CHATLIST:
                    // Call a method for displaying chats
                    
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
                            case UpdateChatsPacket.REMOVE:
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
                    // Display an error message. Lol
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
     * Receives packets from the server and handles them appropriately.
     * 
     * @todo Make this into a SwingWorker for publishing updates to the GUI. The
     *       client will likely be running on that thread as well, so it can
     *       still go through the client.
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
//                type = p.getType();

                this.packetBuf.clearState();
            }
            return null;
        }
        
        @Override
        protected void process(List<Packet> packets)
        {
            // Lets the Client's code, which is hopefully running on the EDT, process the packets
            handlePackets(packets);
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
    }
}

