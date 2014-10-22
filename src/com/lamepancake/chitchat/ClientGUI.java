/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import java.nio.channels.SocketChannel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author dan
 */
public class ClientGUI extends javax.swing.JFrame {

    private final Client client;
    /**
     * Creates new form test
     */
    public ClientGUI() {
        final JTextField tfUsername = new JTextField("");
        final JPasswordField tfPassword = new JPasswordField("");
        Object[] options = {"Username: ", tfUsername, "Password: ", tfPassword};
        
        SocketChannel s = Client.parseCmdArgs(new String[] {"localhost", "1500"});
        
        client = new Client(this, s);
        client.start();
        
        int option = JOptionPane.showConfirmDialog(null, options, "Login", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION)
        {
            client.sendLogin(tfUsername.getText(), new String(tfPassword.getPassword()));
        } 
        else 
        {
            System.exit(0);
            System.out.println("Login canceled");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMessage = new javax.swing.JPanel();
        PanelMessageScroll = new javax.swing.JScrollPane();
        TextAreaMessage = new javax.swing.JTextArea();
        ButtonSend = new javax.swing.JButton();
        PanelChatLog = new javax.swing.JPanel();
        TabbedPaneChatLog = new javax.swing.JTabbedPane();
        ScrollPaneChatLog1 = new javax.swing.JScrollPane();
        TextAreaChatLog1 = new javax.swing.JTextArea();
        ScrollPaneChatLog2 = new javax.swing.JScrollPane();
        TextAreaChatLog2 = new javax.swing.JTextArea();
        PanelLists = new javax.swing.JPanel();
        TabbedPanelLists = new javax.swing.JTabbedPane();
        ScrollPanelChatLists = new javax.swing.JScrollPane();
        ListChatLists = new javax.swing.JList();
        ScrollPaneUsersLists = new javax.swing.JScrollPane();
        ListUsersLists = new javax.swing.JList();
        MenuBarTop = new javax.swing.JMenuBar();
        MenuFile = new javax.swing.JMenu();
        MenuChat = new javax.swing.JMenu();
        MenuItemCreateChat = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TextAreaMessage.setColumns(20);
        TextAreaMessage.setLineWrap(true);
        TextAreaMessage.setRows(5);
        PanelMessageScroll.setViewportView(TextAreaMessage);

        ButtonSend.setText("Send");

        javax.swing.GroupLayout PanelMessageLayout = new javax.swing.GroupLayout(PanelMessage);
        PanelMessage.setLayout(PanelMessageLayout);
        PanelMessageLayout.setHorizontalGroup(
            PanelMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMessageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMessageScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonSend)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelMessageLayout.setVerticalGroup(
            PanelMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMessageLayout.createSequentialGroup()
                .addGroup(PanelMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ButtonSend)
                    .addComponent(PanelMessageScroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        TabbedPaneChatLog.setName("Chats"); // NOI18N

        TextAreaChatLog1.setColumns(20);
        TextAreaChatLog1.setRows(5);
        TextAreaChatLog1.setText("Humpfrey: Top of the morning to ya.\na\ndf\na\nsdf\na\nsdf\na\nsd\nf\nasdf\nasdf\n\nasd\nf\nas\ndf\nasdf\na\nsd\nf\nasdf\n\nasd\nf\n\nasdf\na\nsdf\nasdf\nas\ndf\nas\ndf\nasd\nf\nasdf\nas\ndf\nas\nf\n\na\nsdf\nas\ndf\nas\ndf\nasd");
        TextAreaChatLog1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        ScrollPaneChatLog1.setViewportView(TextAreaChatLog1);

        TabbedPaneChatLog.addTab("ChitChatCat", ScrollPaneChatLog1);

        TextAreaChatLog2.setColumns(20);
        TextAreaChatLog2.setRows(5);
        TextAreaChatLog2.setText("Ted: yo dawg\nLightning Storm: what up slaya?");
        ScrollPaneChatLog2.setViewportView(TextAreaChatLog2);

        TabbedPaneChatLog.addTab("ChitChatBat", ScrollPaneChatLog2);

        javax.swing.GroupLayout PanelChatLogLayout = new javax.swing.GroupLayout(PanelChatLog);
        PanelChatLog.setLayout(PanelChatLogLayout);
        PanelChatLogLayout.setHorizontalGroup(
            PanelChatLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedPaneChatLog, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
        );
        PanelChatLogLayout.setVerticalGroup(
            PanelChatLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedPaneChatLog, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        TabbedPaneChatLog.getAccessibleContext().setAccessibleName("Cat");

        PanelLists.setBorder(null);

        TabbedPanelLists.setBorder(null);

        ListChatLists.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ScrollPanelChatLists.setViewportView(ListChatLists);

        TabbedPanelLists.addTab("Chat", ScrollPanelChatLists);

        ListUsersLists.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ScrollPaneUsersLists.setViewportView(ListUsersLists);

        TabbedPanelLists.addTab("Users", ScrollPaneUsersLists);

        javax.swing.GroupLayout PanelListsLayout = new javax.swing.GroupLayout(PanelLists);
        PanelLists.setLayout(PanelListsLayout);
        PanelListsLayout.setHorizontalGroup(
            PanelListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedPanelLists, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
        );
        PanelListsLayout.setVerticalGroup(
            PanelListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabbedPanelLists, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
        );

        TabbedPanelLists.getAccessibleContext().setAccessibleDescription("");

        MenuBarTop.setBackground(new java.awt.Color(56, 19, 39));

        MenuFile.setText("File");
        MenuBarTop.add(MenuFile);

        MenuChat.setText("Chat");

        MenuItemCreateChat.setText("New Chat");
        MenuItemCreateChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuItemCreateChatMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuItemCreateChatMousePressed(evt);
            }
        });
        MenuChat.add(MenuItemCreateChat);

        MenuBarTop.add(MenuChat);

        setJMenuBar(MenuBarTop);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelChatLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelLists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelChatLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelLists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MenuItemCreateChatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemCreateChatMouseClicked
        // TODO add your handling code here:
        System.out.println("New Chat. MouseClicked");
    }//GEN-LAST:event_MenuItemCreateChatMouseClicked

    private void MenuItemCreateChatMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemCreateChatMousePressed
        // TODO add your handling code here:
        final JTextField tfChatName = new JTextField("");
        Object[] options = {"Chat Name: ", tfChatName};
        
        int option = JOptionPane.showConfirmDialog(null, options, "New Chat", JOptionPane.OK_CANCEL_OPTION);
        
        if(option == JOptionPane.OK_OPTION)
        {
            client.sendCreateChat(tfChatName.getText(), -1);
        } 
        else 
        {
            System.out.println("Login canceled");
        }
        
    }//GEN-LAST:event_MenuItemCreateChatMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
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
        // Not on the GUI thread; make a runnable that invokes this later
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Display code
            }
        });
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
        // Not on the GUI thread; make a runnable that invokes this later
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Display code
            }
        });
    }
    
    /**
     * Tells the GUI whether the login attempt c.failed or succeeded.
     * 
     * @param userID The user's ID if the login succeeded.
     * @param valid Whether the login was valid.
     */
    public void loginValid(final int userID, final boolean valid)
    {
        // Queue
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                if(valid)
                {
                    initComponents();
                }
                else 
                {
                    System.out.println("Failed :(");
                }
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonSend;
    private javax.swing.JList ListChatLists;
    private javax.swing.JList ListUsersLists;
    private javax.swing.JMenuBar MenuBarTop;
    private javax.swing.JMenu MenuChat;
    private javax.swing.JMenu MenuFile;
    private javax.swing.JMenuItem MenuItemCreateChat;
    private javax.swing.JPanel PanelChatLog;
    private javax.swing.JPanel PanelLists;
    private javax.swing.JPanel PanelMessage;
    private javax.swing.JScrollPane PanelMessageScroll;
    private javax.swing.JScrollPane ScrollPaneChatLog1;
    private javax.swing.JScrollPane ScrollPaneChatLog2;
    private javax.swing.JScrollPane ScrollPaneUsersLists;
    private javax.swing.JScrollPane ScrollPanelChatLists;
    private javax.swing.JTabbedPane TabbedPaneChatLog;
    private javax.swing.JTabbedPane TabbedPanelLists;
    private javax.swing.JTextArea TextAreaChatLog1;
    private javax.swing.JTextArea TextAreaChatLog2;
    private javax.swing.JTextArea TextAreaMessage;
    // End of variables declaration//GEN-END:variables
}
