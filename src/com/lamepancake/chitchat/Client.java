package com.lamepancake.chitchat;


import com.lamepancake.chitchat.packet.GrantAccessPacket;
import com.lamepancake.chitchat.packet.JoinedPacket;
import com.lamepancake.chitchat.packet.LeftPacket;
import com.lamepancake.chitchat.packet.LogoutPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.PacketBuffer;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	private SocketChannel socket;

	// if I use a GUI or not
	//private ClientGUI cg;
	
	// the server, the port and the username
	private final String server, username, password;
	private final int    port;
        private int          userID;
        private int          userRole;
        private List<User>   users;
        
        // if the user is in chat or waiting for access
        private boolean      isWaiting;

	/**
	 * Constructor called by console mode
	 * @param server    The server address.
	 * @param port      The port number.
	 * @param username  The username.
         * @param password  The password.
	 */
	public Client(String server, int port, String username, String password) {
		// which calls the common constructor with the GUI set to null
		//this(server, port, username, null);
                this.server = server;
                this.port = port;
                this.username = username;
                this.password = password;
                this.userID   = -1;
                this.isWaiting = true;
                
                users = new ArrayList<>();
	}
	
	/*
	 * To start the dialog
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
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
                try{
                    ByteBuffer login = new LoginPacket(this.username, this.password).serialise();
                    socket.write(login);
                } catch (IOException e) {
                    System.out.println("Failure while sending login packet: " + e.getMessage());
                }
		// success we inform the caller that it worked
		return true;
	}
        
        public boolean waiting()
        {
            return isWaiting;
        }
        
        public void enterChat()
        {
            isWaiting = false;
            System.out.println("Status Update : You've been added to the chat.");
        }

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		//if(cg == null)
			System.out.println(msg);      // println in console mode
		//else
			//cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(Packet msg) {
            try {
                socket.write(msg.serialise());
            } catch(IOException e) {
                System.out.println("Could not send message: " + e.getMessage());
            }
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {

		
		// inform the GUI
		/*if(cg != null)
			cg.connectionFailed();*/
			
	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
    public static void main(String[] args) {
            // default values
            int         portNumber      = 1500;
            String      serverAddress   = "localhost";
            String      username        = "Anonymous";
            String      password        = "Anon";

            // depending of the number of arguments provided we fall through
            switch(args.length) {
                    // > javac Client username portNumber serverAddr
                    case 3:
                            serverAddress = args[2];
                    // > javac Client username portNumber
                    case 2:
                        try {
                                portNumber = Integer.parseInt(args[1]);
                        }
                        catch(Exception e) {
                                System.out.println("Invalid port number.");
                                System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                                return;
                        }
                    // > javac Client username
                    case 1: 
                        username = args[0];
                    // > java Client
                    case 0:
                        break;
                    // invalid number of arguments
                    default:
                        System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
                    return;
            }
            // create the Client object
            Client client = new Client(serverAddress, portNumber, username, password);
            // test if we can start the connection to the Server
            // if it failed nothing we can do
            if(!client.start())
                    return;

            // wait for messages from user
            Scanner scan = new Scanner(System.in);
            // loop forever for message from the user
            while(true) {
                    System.out.print("> ");
                    // read message from user
                    String msg = scan.nextLine();
                    // logout if message is LOGOUT
                    if(msg.equalsIgnoreCase("LOGOUT")) {
                            client.sendMessage(new LogoutPacket());
                            // break to do the disconnect
                            break;
                    }
                    
                    
                    // message WhoIsIn
                    else if(msg.equalsIgnoreCase("WHOISIN")) {
                            System.out.println("[CURRENT USER LIST]");

                            for(User u: client.users)
                            {
                                System.out.print('\t');
                                System.out.println(u);
                            }

                            System.out.println("[END]");
                    }
                    else if (msg.equalsIgnoreCase("ADD"))
                    {
                        client.sendMessage(new GrantAccessPacket(client.userID));
                    }
                    else // default to ordinary message
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
            // done disconnect
            client.disconnect();
    }

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
    class ListenFromServer extends Thread {

        private final PacketBuffer packetBuf;
            
            public ListenFromServer()
            {
                packetBuf = new PacketBuffer(socket);
            }
            
        @Override
        public void run() {
            int     type;
            Packet  p;
            while(true)
            {
                // This should actually never happen since we're in blocking mode
                // But you never know
                if(this.packetBuf.read() != PacketBuffer.FINISHED)
                        continue;

                p = this.packetBuf.getPacket();
                type = p.getType();
                        
                switch(type)
                {
                    case Packet.MESSAGE:
                        displayMessage((MessagePacket)p);
                        break;
                    case Packet.WHOISIN:
                        users = ((WhoIsInPacket)p).getUsers();
                        break;
                    case Packet.JOINED:
                        addUser((JoinedPacket)p);
                        break;
                    case Packet.LEFT:
                        removeUser((LeftPacket)p);
                        break;
                    case Packet.GRANTACCESS:
                        enterChat();
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
            // This is our information
            if(u.getName().equals(""))
            {
                userID = u.getID();
                userRole = u.getRole();
                return;
            }

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
                    display(u + ": " + message.getMessage());
            }
        }
    }
}

