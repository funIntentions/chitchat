package com.lamepancake.chitchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.channels.SocketChannel;

/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tfMessage;
        private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort, tfUsername, tfPassword;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
        
        private JTabbedPane chats, lists;
        
        private JButton send;
        
        private SocketChannel socket;
        
        private Client client;
        
	// Constructor connection receiving a socket number
	public ClientGUI(SocketChannel s) {

		super("Chat Client");
                socket = s;
                
                tfUsername = new JTextField("Username");
                tfPassword = new JTextField("Password");
                login = new JButton("Login");
                login.addActionListener(this);
                JPanel loginPage = new JPanel(new GridLayout(7, 1));
                loginPage.add(new JLabel("Username:  "));
                loginPage.add(tfUsername);
                loginPage.add(new JLabel("Password:  "));
                loginPage.add(tfPassword);
//                loginPage.add(new JLabel("Server Address:  "));
//                loginPage.add(tfServer);
                loginPage.add(login);
		add(loginPage, BorderLayout.CENTER);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		//tfServer.requestFocus();
                
                chats = new JTabbedPane();
                lists = new JTabbedPane();
                
                JPanel chatPanel = new JPanel(new GridLayout(3, 1));
                chatPanel.add(chats);
                tfMessage = new JTextField("");
                chatPanel.add(tfMessage);
                
                send = new JButton("Send");
                send.addActionListener(this);
                chatPanel.add(send);
                
                JPanel userPanels = new JPanel(new GridLayout(10, 1));
                JPanel chatPanels = new JPanel(new GridLayout(10, 1));
                lists.addTab("Users", userPanels);
                lists.addTab("Chats", chatPanels);
//                add(chatPanel, BorderLayout.CENTER);
//                add(lists, BorderLayout.EAST);
//                setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setSize(600, 600);
//		setVisible(true);
		                
                
//		// The NorthPanel with:
//		JPanel northPanel = new JPanel(new GridLayout(3,1));
//		// the server name anmd the port number
//		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
//		// the two JTextField with default value for server address and port number
//		tfServer = new JTextField(defaultHost);
//		tfPort = new JTextField("" + defaultPort);
//		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
//
//		serverAndPort.add(new JLabel("Server Address:  "));
//		serverAndPort.add(tfServer);
//		serverAndPort.add(new JLabel("Port Number:  "));
//		serverAndPort.add(tfPort);
//		serverAndPort.add(new JLabel(""));
//		// adds the Server an port field to the GUI
//		northPanel.add(serverAndPort);
//
//		// the Label and the TextField
//		label = new JLabel("Enter your username below", SwingConstants.CENTER);
//		northPanel.add(label);
//		tf = new JTextField("Anonymous");
//		tf.setBackground(Color.WHITE);
//		northPanel.add(tf);
//		add(northPanel, BorderLayout.NORTH);
//
//		// The CenterPanel which is the chat room
//		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
//		JPanel centerPanel = new JPanel(new GridLayout(1,1));
//		centerPanel.add(new JScrollPane(ta));
//		ta.setEditable(false);
//		add(centerPanel, BorderLayout.CENTER);
//
//		// the 3 buttons
//		login = new JButton("Login");
//		login.addActionListener(this);
//		logout = new JButton("Logout");
//		logout.addActionListener(this);
//		logout.setEnabled(false);		// you have to login before being able to logout
//		whoIsIn = new JButton("Who is in");
//		whoIsIn.addActionListener(this);
//		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in
//
//		JPanel southPanel = new JPanel();
//		southPanel.add(login);
//		southPanel.add(logout);
//		southPanel.add(whoIsIn);
//		add(southPanel, BorderLayout.SOUTH);
//
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setSize(600, 600);
//		setVisible(true);
//		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// reset port number and host name as a construction time
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			//guiMediator.receiveMessageFromGUI("LOGOUT");
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			//guiMediator.receiveMessageFromGUI("WHOISIN");				
			return;
		}

		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			//guiMediator.receiveMessageFromGUI("CHAT" + tf.getText());				
			tf.setText("");
			return;
		}
		

		if(o == login) {
			// ok it is a connection request
			String username = tfUsername.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
				return;

                        String password = tfPassword.getText().trim();
                        if(password.length() == 0)
                        {
                            return;
                        }
                        
                        client = new Client(username, password, socket);

                        //guiMediator.receiveMessageFromGUI("PORT" + port);
                        //guiMediator.receiveMessageFromGUI("SERVER" + server);
                        //guiMediator.receiveMessageFromGUI("USERNAME" + username);
//                                                
//			tf.setText("");
//			label.setText("Enter your message below");
//			connected = true;
//			
//			// disable login button
//			login.setEnabled(false);
//			// enable the 2 buttons
//			logout.setEnabled(true);
//			whoIsIn.setEnabled(true);
//			// disable the Server and Port JTextField
//			tfServer.setEditable(false);
//			tfPort.setEditable(false);
//			// Action listener for when the user enter a message
//			tf.addActionListener(this);
		}

	}
        
        public void receiveFromMediator(String input)
        {
            tf.setText(input);
        }
}
