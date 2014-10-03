package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.*;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Transmits chat and other messages to the server.
 * 
 * The Client class only sends messages; receiving the messages of other users is
 * handled by the inner ListenFromServer class in another thread.
 * 
 * Loosely based upon the work of Paul-Benoit Larochelle (see link below).
 * @see http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 */
public class Client  {

    /**
     * The socket over which the client sends and receives data.
     */
    private SocketChannel socket;

    /**
     * A GUI, if the client has one.
     */
    //private ClientGUI cg;

    /**
     * The server's host name.
     */
    private final String server;
    
    /**
     * The client's username.
     */
    private final String username;
    
    /**
     * The client's password.
     */
    private final String password;
    
    /**
     * The port with which to connect to the server.
     */
    private final int port;
    
    /**
     * The ID identifying this client within the chat.
     */
    private int userID;
    
    /**
     * This client's role in the chat.
     */
    private int userRole;
    
    /**
     * A list of all users in the chat (including this client).
     */
    private List<User> users;

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
    
    /**
     * Constructs a new Client with the given server information and authentication
     * details.
     * 
     * The Client won't try to connect to the server and start accepting input until
     * its start() method is called.
     * 
     * @param server    The server's host name.
     * @param port      The port number on which to connect.
     * @param username  The client's username.
     * @param password  The client's password.
     */
    public Client(String server, int port, String username, String password) {
            this.server = server;
            this.port = port;
            this.username = username;
            this.password = password;
            this.userID = -1;
            this.isWaiting = true;
            users = new ArrayList<>();
            connected = true;
            logout = false;
    }

    /**
     * Attempts to connect to the server and begins listening for user input.
     * 
     * @return Whether the connection attempt succeeded.
     */
    public boolean start() {
        // try to connect to the server
        InetSocketAddress addr = new InetSocketAddress(server, port);

        try {
            socket = SocketChannel.open(addr);
        } 
        // if it failed not much I can so
        catch(Exception ec) {
                display("Error connectiong to server:" + ec);
                return false;
        }

        String msg = "Connection accepted " + addr.toString();
        display(msg);
        msg = "Status Update : Waiting to be added to chat.";
        display(msg);

        // creates the Thread to listen from the server 
        new ListenFromServer().start();

        try{
            ByteBuffer login = new LoginPacket(this.username, this.password).serialise();
            socket.write(login);
        } catch (IOException e) {
            System.out.println("Failure while sending login packet: " + e.getMessage());
            return false;
        }

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
     * Allows the user to send messages and notifies them that they've been accepted.
     */
    private void updateStatus(GrantAccessPacket packet)
    {
        int role = packet.getUserRole();
        
        userRole = role;
        switch(role)
        {
            case User.ADMIN:
            case User.USER:
            {
                if (isWaiting)
                {
                    System.out.println("Status Update : You've been added to the chat.");
                    isWaiting = false;
                }
               
                System.out.println("Status Update : An admin has updated your role to " + (role == User.ADMIN ? "Scrum Master." : "Developer."));
                userID = packet.getUserID();
                break;
            }
            case User.UNSPEC:
            {
                System.out.println("Status Update : An admin has booted you from the chat.");
                break;
            }
        }
    }

    /**
     * Displays a message to the console or the GUI if present.
     */
    private void display(String msg) {
            //if(cg == null)
                    System.out.println(msg); // println in console mode
            //else
                    //cg.append(msg + "\n"); // append to the ClientGUI JTextArea (or whatever)
    }

    /**
     * Sends a packet to the server.
     */
    private void sendMessage(Packet msg) {
        try {
            
            //make sure the user is connected to the server
            if(!connected)
            {
                return;
            }
            
            socket.write(msg.serialise());
        } catch(IOException e) {
            System.out.println("Could not send message: " + e.getMessage());
        }
    }
    
    /**
     * Close socket, end listening thread, etc.
     * 
     * @todo Actually implement this...
     */
    private void disconnect() {		
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
    public static void main(String[] args) {

        Client client = parseCmdArgs(args);
        
        // test if we can start the connection to the Server
        // if it failed nothing we can do
        if(!client.start())
                return;

        // wait for messages from user
        Scanner scan = new Scanner(System.in);
        
        // loop forever for message from the user
        while(true) {
            
            if(!client.connected)
            {
                //Attempt to create a new connections
                System.out.println("Status Update : Attempting to reconnect.");
                client = parseCmdArgs(args);
                
                if(!client.start())
                {
                    System.out.println("Status Update : Unable to reconnect. Quiting program.");
                    break;
                }
            }
            
            System.out.print(">> ");
            
            // read message from user
            String msg = scan.nextLine();
            
            String[] splitmsg = msg.split("\\s+");
            
            if ((client.userRole == User.ADMIN) &&
                    (splitmsg.length > 1) && 
                    splitmsg[0].equalsIgnoreCase("ADD"))
            {
                String recievingUserID = splitmsg[1];
                int id;
                int role;
                
                try {
                    id = Integer.parseInt(recievingUserID);
                    
                    role = (splitmsg.length > 2) ? Integer.parseInt(splitmsg[2]) : User.USER;
                    
                } catch(Exception e) {
                    System.out.println("Not a valid user ID: " + recievingUserID);
                    continue;
                }
                
                client.sendMessage(new GrantAccessPacket(id, role));
            }
            else if((client.userRole == User.ADMIN) &&
                    (splitmsg.length > 1) && 
                    splitmsg[0].equalsIgnoreCase("BOOT"))
            {
                String recievingUserID = splitmsg[1];
                int id;
                
                try {
                    id = Integer.parseInt(recievingUserID);
                    
                } catch(Exception e) {
                    System.out.println("Not a valid user ID: " + recievingUserID);
                    continue;
                }
                
                client.sendMessage(new GrantAccessPacket(id, User.UNSPEC));
            }
            else if((client.userRole == User.ADMIN) &&
                    (splitmsg.length > 1) && 
                    splitmsg[0].equalsIgnoreCase("UPDATECHAT"))
            {
                String updateCommand = splitmsg[1];
                String name;
                int id;
                int update;
                
                try {
                    if (updateCommand.equalsIgnoreCase("CREATE"))
                    {
                        name = splitmsg[2];
                        id = -1;
                        update = UpdateChatsPacket.CREATE;
                    }
                    else if (updateCommand.equalsIgnoreCase("UPDATE"))
                    {
                        id = Integer.parseInt(splitmsg[2]);
                        name = splitmsg[3];
                        update = UpdateChatsPacket.UPDATE;
                    }
                    else if (updateCommand.equalsIgnoreCase("REMOVE"))
                    {
                        id = Integer.parseInt(splitmsg[2]);
                        name = "";
                        update = UpdateChatsPacket.REMOVE;
                    }
                    else
                    {
                        System.out.println("Chat update command: " + updateCommand + " is not valid.");
                        continue;
                    }
                    
                } catch(Exception e) {
                    System.out.println("Not a valid chat update.");
                    continue;
                }
                
                client.sendMessage(new UpdateChatsPacket(name, id, update));
            }
            else if((splitmsg.length > 1) && 
                    splitmsg[0].equalsIgnoreCase("JOIN"))
            {
                String recievingChatID = splitmsg[1];
                int id;
                
                try {
                    id = Integer.parseInt(recievingChatID);
                    
                } catch(Exception e) {
                    System.out.println("Not a valid chat ID: " + recievingChatID);
                    continue;
                }
                System.out.println(client.userRole);
                client.sendMessage(new JoinedPacket(client.username, client.userRole, client.userID, id));
                //System.out.println(client.userRole);
            }
            else if(msg.equalsIgnoreCase("LOGOUT")) {
                System.out.println("Logging out.");
                client.logout = true;
                client.sendMessage(new LogoutPacket());
                break;
            }
            else if(msg.equalsIgnoreCase("WHOISIN")) {     
                /* 
                 * WHOISIN doesn't actually send anything to the server; it just
                 * displays all users in the list. 
                 */
                System.out.println("[CURRENT USER LIST]");

                for(User u: client.users)
                {
                    System.out.print('\t');
                    System.out.println(u + " ID: " + u.getID());
                }

                System.out.println("[END]");
            }
            else if(msg.equalsIgnoreCase("WAITINGLIST"))
            {
                client.sendMessage(new WhoIsInPacket(WhoIsInPacket.WAITING));
            }
            else if(msg.equalsIgnoreCase("CHATLIST"))
            {
                client.sendMessage(new ChatListPacket());
            }
            else
            {
                if (!client.waiting())
                {
                    client.sendMessage(new MessagePacket(msg, client.userID));
                }
                else
                {
                    System.out.println("You'll need to be in the chat before you can send messages. ;)");
                }       
            }  
            
        }
        // We're done here; disconnect
        client.disconnect();
    }

    /**
     * Parses the command arguments and creates a client.
     * 
     * @param args The command line arguments.
     * @return     A new Client object or null if one or more arguments were incorrect.
     */
    private static Client parseCmdArgs(String[] args)
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
        return new Client(serverName, portNumber, name, pw);
    }
    
    /**
     * Receives packets from the server and handles them appropriately.
     * 
     * Loosely Based upon the work of Paul-Benoit Larochelle (see link below).
     * @see http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
     */
    private class ListenFromServer extends Thread {

        /**
         * A PacketBuffer to read packets from the server.
         */
        private final PacketBuffer packetBuf;
            
        public ListenFromServer()
        {
            packetBuf = new PacketBuffer(socket);
        }
            
        @Override
        public void run() {
            int     type;
            Packet  p;
            while(connected)
            {
                // This should actually never happen since we're in blocking mode
                // But you never know
                if(this.packetBuf.read() != PacketBuffer.FINISHED)
                {
                    if(this.packetBuf.getState() == PacketBuffer.DISCONNECTED)
                    {
                        if(!logout)
                        {
                            System.out.println("Status Update : Disconnected from chat. Press Enter to continue.");
                            connected = false;
                        }
                        return;
                    }
                    else
                    {
                        continue;
                    }
                }
                
                p = this.packetBuf.getPacket();
                type = p.getType();
                        
                switch(type)
                {
                    case Packet.MESSAGE:
                        displayMessage((MessagePacket)p);
                        break;
                    case Packet.WHOISIN:
                        WhoIsInPacket userList = (WhoIsInPacket)p;
                        if(userList.whichList() == WhoIsInPacket.WAITING)
                        {
                            System.out.println("[WAITING USERS]");
                            List<User> waitingUsers = userList.getUsers();
                            for(User u : waitingUsers)
                            {
                                System.out.print('\t');
                                System.out.println("Name: " + u.getName() + "; ID: " + u.getID());
                            }
                            System.out.println("[END]");
                        }
                        else
                            users = userList.getUsers();
                        break;
                    case Packet.JOINED:
                        addUser((JoinedPacket)p);
                        break;
                    case Packet.LEFT:
                        removeUser((LeftPacket)p);
                        break;
                    case Packet.GRANTACCESS:
                        updateStatus((GrantAccessPacket)p);
                        break;
                    case Packet.CHATLIST:
                        ChatListPacket chatList = (ChatListPacket)p;
                        System.out.println("[OPEN CHATS]");
                        List<Chat> openChats = chatList.getChats();
                        for(Chat c : openChats)
                        {
                            System.out.print('\t');
                            System.out.println("Name: " + c.getName() + "; ID: " + c.getID());
                        }
                        System.out.println("[END]");
                        break;
                }
                this.packetBuf.clearState();
            }
        }
        
        /**
         * Adds a new user to the user list or sets this client's userID.
         * 
         * If the JoinedPacket contains an empty username field, then it designates
         * this user's ID and role.
         * 
         * @param joined The JoinedPacket containing the new user's information.
         */
        private void addUser(JoinedPacket joined)
        {
            User u = joined.getUser();
            display("[ " + u + " has joined the chat ]");
            users.add(u);
        }
        
        /**
         * Removes from the list the user who left the chat.
         * @param left The LeftPacket containing the user's ID.
         */
        private void removeUser(LeftPacket left)
        {
            for(int i = 0; i < users.size(); i++)
            {
                User u = users.get(i);
                if(u.getID() == left.getUserID())
                {
                    display("[ " + u + " has left the chat ]");
                    users.remove(i);
                    break;
                }
            }
        }
        
        /**
         * Displays the message, its sender and its sender's role.
         * 
         * @param message A MessagePacket containing the message.
         */
        private void displayMessage(MessagePacket message)
        {
            for(User u : users)
            {
                if(u.getID() == message.getUserID())
                {
                    display(u + ": " + message.getMessage());
                    break;
                }
            }
        }
    }
}
