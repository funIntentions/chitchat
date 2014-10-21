package com.lamepancake.chitchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;

/**
 * A Swing GUI for the Client.
 * 
 * @todo Ensure that all display methods run on the EDT (use reflection).
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

    private Client client;
    
    private JOptionPane loginPage;
    
    private JPanel chatPanel;

    // Constructor connection receiving a socket number
    public ClientGUI(Client c) {

        super("Chat Client");
        
        client = c;
        
        tfUsername = new JTextField("Username");
        tfPassword = new JTextField("Password");
        Object[] options = {"Username: ", tfUsername, "Password: ", tfPassword};
        
        c.start();
        
        int option = JOptionPane.showConfirmDialog(null, options, "Login", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION)
        {
            client.sendLogin(tfUsername.getText(), tfPassword.getText());
        } 
        else 
        {
            System.out.println("Login canceled");
        }
        
        login = new JButton("Login");
        login.addActionListener(this);
        loginPage = new JOptionPane(new GridLayout(7, 1));
        loginPage.add(new JLabel("Username:  "));
        loginPage.add(tfUsername);
        loginPage.add(new JLabel("Password:  "));
        loginPage.add(tfPassword);
//                loginPage.add(new JLabel("Server Address:  "));
//                loginPage.add(tfServer);
        loginPage.add(login);
//        add(loginPage, BorderLayout.CENTER);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setSize(600, 600);
//        setVisible(true);
		//tfServer.requestFocus();

        chats = new JTabbedPane();
        lists = new JTabbedPane();

        chatPanel = new JPanel(new GridLayout(3, 1));
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
                add(chatPanel, BorderLayout.CENTER);
                add(lists, BorderLayout.EAST);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
    }

    // called by the Client to append text in the TextArea 
    void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
    }
    
    /**
     * Tells the GUI whether the login attempt c.failed or succeeded.
     * 
     * @param userID The user's ID if the login succeeded.
     * @param valid Whether the login was valid.
     */
    public void loginValid(final int userID, final boolean valid)
    {
        if(SwingUtilities.isEventDispatchThread())
        {
            if(valid)
            {
                System.out.println("Hello! Login succeeded.");
            }
            else 
            {
                System.out.println("Failed :(");
            }
        }
        else 
        {
            // Not on the GUI thread; make a runnable that invokes this later
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    try {
                        Method m = ClientGUI.class.getMethod("loginValid", int.class, boolean.class);
                        m.invoke(this, userID, valid);
                    }
                    catch( Exception e ) {
                        throw new RuntimeException( e );
                    }
                }
            });
        }
    }
    
    public void createChat()
    {
        
    }
    
    /**
     * Resets the buttons on the GUI if the connection fails.
     */
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
        if (o == logout) {
            //guiMediator.receiveMessageFromGUI("LOGOUT");
            return;
        }
        // if it the who is in button
        if (o == whoIsIn) {
            //guiMediator.receiveMessageFromGUI("WHOISIN");				
            return;
        }

        // ok it is coming from the JTextField
        if (connected) {
			// just have to send the message
            //guiMediator.receiveMessageFromGUI("CHAT" + tf.getText());				
            tf.setText("");
            return;
        }

        if (o == login) {
            // ok it is a connection request
            String username = tfUsername.getText().trim();
            // empty username ignore it
            if (username.length() == 0) {
                return;
            }

            String password = tfPassword.getText().trim();
            if (password.length() == 0) {
                return;
            }

            //client = new Client(this);
            this.add(chatPanel);


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

    /**
     * Displays the message and its sender.
     * 
     * @param message The message to display.
     * @param username Name of the user who sent the message.
     * @todo Display this in the correct chat tab (will need chat argument).
     * @todo Deal with exceptions properly.
     */
    public void displayUserMessage(final String message, final String username)
    {
        if(SwingUtilities.isEventDispatchThread())
        {
            // Run the display code here
        }
        else 
        {
            // Not on the GUI thread; make a runnable that invokes this later
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    try {
                        Method m = ClientGUI.class.getMethod("displayUserMessage", String.class, String.class);
                        m.invoke(this, message, username);
                    }
                    catch( Exception e ) {
                        throw new RuntimeException( e );
                    }
                }
            });
        }
        // Probably going to end up putting append() code in here
    }
    
    /**
     * Displays a message from the Server.
     * 
     * If the thread code isn't on the EDT, run it later in a runnable.
     * 
     * @todo Deal with exceptions properly.
     * @param message The message to display.
     */
    public void displayServerMessage(final String message)
    {
        // Display a message without a username, possibly as a popup
        if(SwingUtilities.isEventDispatchThread())
        {
            // Run the display code here
        }
        else 
        {
            // Not on the GUI thread; make a runnable that invokes this later
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    try {
                        Method m = ClientGUI.class.getMethod("displayServerMessage", String.class);
                        m.invoke(this, message);
                    }
                    catch( Exception e ) {
                        throw new RuntimeException( e );
                    }
                }
            });
        }
    }

}
