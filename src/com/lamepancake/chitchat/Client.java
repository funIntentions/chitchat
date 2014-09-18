package com.lamepancake.chitchat;


import com.lamepancake.chitchat.packet.LogoutPacket;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	private Socket socket;

	// if I use a GUI or not
	//private ClientGUI cg;
	
	// the server, the port and the username
	private String server, username, password;
	private int port;

	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	public Client(String server, int port, String username, String password) {
		// which calls the common constructor with the GUI set to null
		//this(server, port, username, null);
                this.server = server;
                this.port = port;
                this.username = username;
                this.password = password;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
                    socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
                try{
                    ByteBuffer login = new LoginPacket(this.username, this.password).serialise();
                    socket.getOutputStream().write(login.array());
                } catch (IOException e) {
                    System.out.println("Shit");
                }
		// success we inform the caller that it worked
		return true;
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
				client.sendMessage(new WhoIsInPacket());				
			}
			else {				// default to ordinary message
				client.sendMessage(new MessagePacket(msg));
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

		public void run() {
                    while(true);
		}
	}
}

