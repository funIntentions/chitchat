/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.JoinLeavePacket;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.nio.channels.SocketChannel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
        SocketChannel s = Client.parseCmdArgs(new String[] {"localhost", "1500"});
        client = new Client(this, s);
        client.start();
        
        showLoginDialog();
    }

    /**
     * Displays the login dialog and sends the login info to the server.
     */
    private void showLoginDialog()
    {
        final JTextField tfUsername = new JTextField("");
        final JPasswordField tfPassword = new JPasswordField("");
        final Object[] options = {"Username: ", tfUsername, "Password: ", tfPassword};
        final int option = JOptionPane.showConfirmDialog(null, options, "Login", JOptionPane.OK_CANCEL_OPTION);

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

        jPopupMenuChatOptions = new javax.swing.JPopupMenu();
        jMenuItemJoin = new javax.swing.JMenuItem();
        jMenuItemLeave = new javax.swing.JMenuItem();
        jMenuItemRequestAccess = new javax.swing.JMenuItem();
        jPopupMenuUserOptions = new javax.swing.JPopupMenu();
        jMenuItemBootUser = new javax.swing.JMenuItem();
        jMenuChangeRole = new javax.swing.JMenu();
        jMenuItemUserRole = new javax.swing.JMenuItem();
        jMenuItemAdminRole = new javax.swing.JMenuItem();
        PanelMessage = new javax.swing.JPanel();
        PanelMessageScroll = new javax.swing.JScrollPane();
        TextAreaMessage = new javax.swing.JTextArea();
        ButtonSend = new javax.swing.JButton();
        PanelChatLog = new javax.swing.JPanel();
        TabbedPaneChatLog = new javax.swing.JTabbedPane();
        PanelLists = new javax.swing.JPanel();
        TabbedPanelLists = new javax.swing.JTabbedPane();
        ScrollPanelChatLists = new javax.swing.JScrollPane();
        ListChatLists = new javax.swing.JList();
        ScrollPaneUsersLists = new javax.swing.JScrollPane();
        ListUsersLists = new javax.swing.JList();
        MenuBarTop = new javax.swing.JMenuBar();
        MenuFile = new javax.swing.JMenu();
        MenuItemLogout = new javax.swing.JMenuItem();
        MenuChat = new javax.swing.JMenu();
        MenuItemCreateChat = new javax.swing.JMenuItem();
        MenuItemUpdateChat = new javax.swing.JMenuItem();
        MenuItemDeleteChat = new javax.swing.JMenuItem();

        jMenuItemJoin.setText("Join Chat");
        jMenuItemJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemJoinActionPerformed(evt);
            }
        });
        jPopupMenuChatOptions.add(jMenuItemJoin);

        jMenuItemLeave.setText("Leave Chat");
        jMenuItemLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLeaveActionPerformed(evt);
            }
        });
        jPopupMenuChatOptions.add(jMenuItemLeave);

        jMenuItemRequestAccess.setText("Request Access");
        jMenuItemRequestAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRequestAccessActionPerformed(evt);
            }
        });
        jPopupMenuChatOptions.add(jMenuItemRequestAccess);

        jMenuItemBootUser.setText("Boot User");
        jMenuItemBootUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBootUserActionPerformed(evt);
            }
        });
        jPopupMenuUserOptions.add(jMenuItemBootUser);

        jMenuChangeRole.setText("Change Role");

        jMenuItemUserRole.setText("Developer");
        jMenuItemUserRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUserRoleActionPerformed(evt);
            }
        });
        jMenuChangeRole.add(jMenuItemUserRole);

        jMenuItemAdminRole.setText("Scrum Master");
        jMenuItemAdminRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAdminRoleActionPerformed(evt);
            }
        });
        jMenuChangeRole.add(jMenuItemAdminRole);

        jPopupMenuUserOptions.add(jMenuChangeRole);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TextAreaMessage.setColumns(20);
        TextAreaMessage.setLineWrap(true);
        TextAreaMessage.setRows(5);
        PanelMessageScroll.setViewportView(TextAreaMessage);

        ButtonSend.setText("Send");
        ButtonSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ButtonSendMousePressed(evt);
            }
        });

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
        ListChatLists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ListChatListsMousePressed(evt);
            }
        });
        ScrollPanelChatLists.setViewportView(ListChatLists);

        TabbedPanelLists.addTab("Chat", ScrollPanelChatLists);

        ListUsersLists.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ListUsersLists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ListUsersListsMousePressed(evt);
            }
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

        MenuItemLogout.setText("Logout");
        MenuItemLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuItemLogoutMousePressed(evt);
            }
        });
        MenuFile.add(MenuItemLogout);

        MenuBarTop.add(MenuFile);

        MenuChat.setText("Chat");

        MenuItemCreateChat.setText("New Chat");
        MenuItemCreateChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuItemCreateChatMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MenuItemCreateChatMouseClicked(evt);
            }
        });
        MenuChat.add(MenuItemCreateChat);

        MenuItemUpdateChat.setText("Update Chat");
        MenuItemUpdateChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuItemUpdateChatMousePressed(evt);
            }
        });
        MenuChat.add(MenuItemUpdateChat);

        MenuItemDeleteChat.setText("Delete Chat");
        MenuItemDeleteChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuItemDeleteChatMousePressed(evt);
            }
        });
        MenuItemDeleteChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemDeleteChatActionPerformed(evt);
            }
        });
        MenuChat.add(MenuItemDeleteChat);

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

    public void addTab(final String chatname)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea curChatTextArea = new JTextArea();
                curChatTextArea.setEditable(true);
                curChatTextArea.setVisible(true);
                curChatTextArea.setText("Welcome " + client.getUser().getName() + "!\n");

                JScrollPane curChatScrollPane = new JScrollPane(curChatTextArea);
                curChatScrollPane.setName(chatname);
                TabbedPaneChatLog.add(curChatScrollPane);  
            }
        });
    }
    
    public void removeTab(final String chatname)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Component[] panes = TabbedPaneChatLog.getComponents();
                for(int i = 0; i < panes.length; i++)
                {
                    if (TabbedPaneChatLog.getComponent(i) instanceof JScrollPane)
                    {  
                        if(((JScrollPane)TabbedPaneChatLog.getComponent(i)).getName().equals(chatname))
                        {
                            TabbedPaneChatLog.removeTabAt(i);
                        }
                    }
                }
            }
        });
    }
    
    private void MenuItemDeleteChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemDeleteChatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MenuItemDeleteChatActionPerformed

    private void MenuItemUpdateChatMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemUpdateChatMousePressed
        // TODO add your handling code here:
        final JTextField tfChatName = new JTextField("");
        final JTextField tfChatID = new JTextField("");
        Object[] options = {"Chat Name: ", tfChatName ,"Chat ID: ", tfChatID};
        
        int option = JOptionPane.showConfirmDialog(null, options, "Update Chat", JOptionPane.OK_CANCEL_OPTION);
        
        if(option == JOptionPane.OK_OPTION)
        {
            client.sendUpdateChat(Integer.parseInt(tfChatID.getText()), tfChatName.getText());
        } 
        else 
        {
            System.out.println("Login canceled");
        }
        
    }//GEN-LAST:event_MenuItemUpdateChatMousePressed

    private void MenuItemDeleteChatMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemDeleteChatMousePressed
        // TODO add your handling code here:
        final JTextField tfChatID = new JTextField("");
        Object[] options = {"Chat ID: ", tfChatID};
        
        int option = JOptionPane.showConfirmDialog(null, options, "Delete Chat", JOptionPane.OK_CANCEL_OPTION);
        
        if(option == JOptionPane.OK_OPTION)
        {
            client.sendDeleteChat(Integer.parseInt(tfChatID.getText()));
        } 
        else 
        {
            System.out.println("Login canceled");
        }
        
    }//GEN-LAST:event_MenuItemDeleteChatMousePressed

    private void ListChatListsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ListChatListsMousePressed
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) 
        {
            showChatOptionsPopupMenu(evt, PanelChatLog.getWidth());
        }
    }//GEN-LAST:event_ListChatListsMousePressed

    private void ListUsersListsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ListUsersListsMousePressed
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) 
        {
            showUserOptionsPopupMenu(evt, PanelChatLog.getWidth());
        }
    }//GEN-LAST:event_ListUsersListsMousePressed

    private void jMenuItemJoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemJoinActionPerformed
        // TODO add your handling code here:
        if (ListChatLists.getSelectedIndex() != -1) 
        {
            String chatInfo = ListChatLists.getSelectedValue().toString();
            
            String[] info = chatInfo.split(" ");
            
            int chatId = Integer.parseInt(info[0]);
                        
            client.sendJoinLeave(chatId, JoinLeavePacket.JOIN);
        }
    }//GEN-LAST:event_jMenuItemJoinActionPerformed

    private void jMenuItemLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLeaveActionPerformed
        // TODO add your handling code here:
        if (ListChatLists.getSelectedIndex() != -1) 
        {
            String chatInfo = ListChatLists.getSelectedValue().toString();
            
            String[] info = chatInfo.split(" ");
            
            int chatId = Integer.parseInt(info[0]);
                        
            client.sendJoinLeave(chatId, JoinLeavePacket.LEAVE);
        }
    }//GEN-LAST:event_jMenuItemLeaveActionPerformed

    private void jMenuItemBootUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBootUserActionPerformed
        // TODO add your handling code here:
        if (ListUsersLists.getSelectedIndex() != -1)
        {
            String userInfo = ListUsersLists.getSelectedValue().toString();
            
            String[] info = userInfo.split(",");
            
            String userName = info[0];
            
            client.bootUser(userName);
        }
    }//GEN-LAST:event_jMenuItemBootUserActionPerformed

    private void jMenuItemUserRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUserRoleActionPerformed
        // TODO add your handling code here:
        if (ListUsersLists.getSelectedIndex() != -1)
        {
            String userInfo = ListUsersLists.getSelectedValue().toString();
            
            String[] info = userInfo.split(",");
            
            String userName = info[0];
            
            client.changeUserRole(userName, User.USER);
        }
    }//GEN-LAST:event_jMenuItemUserRoleActionPerformed

    private void jMenuItemAdminRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAdminRoleActionPerformed
        // TODO add your handling code here:
        if (ListUsersLists.getSelectedIndex() != -1)
        {
            String userInfo = ListUsersLists.getSelectedValue().toString();
            
            String[] info = userInfo.split(",");
            
            String userName = info[0];
            
            client.changeUserRole(userName, User.ADMIN);
        }
    }//GEN-LAST:event_jMenuItemAdminRoleActionPerformed

    private void jMenuItemRequestAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRequestAccessActionPerformed
        // TODO add your handling code here:
        if (ListChatLists.getSelectedIndex() != -1) 
        {
            String chatInfo = ListChatLists.getSelectedValue().toString();
            
            String[] info = chatInfo.split(" ");
            
            int chatId = Integer.parseInt(info[0]);
            System.out.println(chatId);
            client.sendRequestAccess(chatId);
        }
    }//GEN-LAST:event_jMenuItemRequestAccessActionPerformed

    private void MenuItemLogoutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuItemLogoutMousePressed
        // TODO add your handling code here:
        // currently not set to a chat ID
        //client.sendMessage(0, TextAreaMessage.getText());
        
    }//GEN-LAST:event_MenuItemLogoutMousePressed

    private void ButtonSendMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonSendMousePressed
        // TODO add your handling code here:
        JScrollPane pane = (JScrollPane) TabbedPaneChatLog.getSelectedComponent();
        
        String message = TextAreaMessage.getText();
        
        if(TextAreaMessage.getText() == null || TextAreaMessage.getText().equals(""))
        {
            return;
        }
        
        String chatName = pane.getName();

        User user = client.getUser();

        displayUserMessage(message, user.getName(), chatName);

        client.sendMessageToChat(chatName, message);

        TextAreaMessage.setText("");
    }//GEN-LAST:event_ButtonSendMousePressed

    
    private void showChatOptionsPopupMenu(MouseEvent e, int xOffset) 
    {
        jPopupMenuChatOptions.show(this, xOffset + e.getX(), jPopupMenuChatOptions.getHeight() + e.getY());
    }
    
    private void showUserOptionsPopupMenu(MouseEvent e, int xOffset) 
    {
        jPopupMenuUserOptions.show(this, xOffset + e.getX(), jPopupMenuUserOptions.getHeight() + e.getY());
    }
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
     * Displays an error message.
     * 
     * @param errorStr The string to display in the error message.
     */
    public void displayError(final String errorStr)
    {
        final javax.swing.JFrame thisFrame = this;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(thisFrame, errorStr, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Displays the message and its sender.
     * 
     * @param message The message to display.
     * @param username Name of the user who sent the message.
     * @param chatName The name of the chat to send to
     * @todo Display this in the correct chat tab (will need chat argument).
     * @todo Deal with exceptions properly.
     */
    public void displayUserMessage(final String message, final String username, final String chatName)
    {
        // Not on the GUI thread; make a runnable that invokes this later
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Component[] panes = TabbedPaneChatLog.getComponents();
                for(int i = 0; i < panes.length; i++)
                {
                    if (TabbedPaneChatLog.getComponent(i) instanceof JScrollPane)
                    {                        
                        JViewport viewPort = ((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport();
                        Component[] components = viewPort.getComponents();
                        
                        for(int j = 0; j < components.length; j++)
                        {
                            if(((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j) instanceof JTextArea)
                            {
                                if(((JScrollPane)TabbedPaneChatLog.getComponent(i)).getName().equals(chatName))
                                {
                                    if(((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).getText() == null || ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).getText().equals(""))
                                    {
                                        ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).setText(username + ": " + message + "\n");
                                    }
                                    else
                                    {
                                        ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).append(username + ": " + message + "\n");
                                    }
                                }
                            }
                        }
                    } 
                }
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
     * @param chatName The chat to send to
     */
    public void displayServerMessage(final String message, final String chatName)
    {
        // Not on the GUI thread; make a runnable that invokes this later
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Component[] panes = TabbedPaneChatLog.getComponents();
                for(int i = 0; i < panes.length; i++)
                {
                    if (TabbedPaneChatLog.getComponent(i) instanceof JScrollPane)
                    {                        
                        JViewport viewPort = ((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport();
                        Component[] components = viewPort.getComponents();
                        
                        for(int j = 0; j < components.length; j++)
                        {
                            if(((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j) instanceof JTextArea)
                            {
                                if(((JScrollPane)TabbedPaneChatLog.getComponent(i)).getName().equals(chatName))
                                {
                                    if(((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).getText() == null || ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).getText().equals(""))
                                    {
                                        ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).setText(message + "\n");
                                    }
                                    else
                                    {
                                        ((JTextArea)((JScrollPane)TabbedPaneChatLog.getComponent(i)).getViewport().getComponent(j)).append(message + "\n");
                                    }
                                }
                            }
                        }
                    } 
                }
            }
        });
    }
    
    /**
     * Populates the list of chats.
     * @param chats 
     */
    public void populateChatList(final String[] chats)
    {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run(){
                for(int i = 0; i < chats.length; i++)
                {
                    ((DefaultListModel)ListChatLists.getModel()).addElement(chats[i]);
                }
            }
        });
    }

    /**
     * Populates the list of users.
     * @param users 
     */
    public void populateUserList(final String[] users)
    {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run(){
                for(int i = 0; i < users.length; i++)
                {
                    ((DefaultListModel)ListChatLists.getModel()).addElement(users[i]);
                }
            }
        });
    }
    
    /**
     * 
     * @param user 
     */
    public void addUserToList(final String user)
    {
         SwingUtilities.invokeLater(new Runnable() {    
            @Override
            public void run(){
                ((DefaultListModel)ListUsersLists.getModel()).addElement(user);
            }
        });
    }
    
    /**
     * 
     * @param chat 
     */
    public void addChatToList(final String chat)
    {
         SwingUtilities.invokeLater(new Runnable() {    
            @Override
            public void run(){
                ((DefaultListModel)ListChatLists.getModel()).addElement(chat);
            }
        });
    }
    
    /**
     * 
     * @param chatid
     * @param chat 
     */
    public void updateChatList(final int chatid, final String chat)
    {
        SwingUtilities.invokeLater(new Runnable() {    
            @Override
            public void run(){
                
                for(int i = 0; i < ListChatLists.getModel().getSize(); i++)
                {
                    String[] info = ((String)ListChatLists.getModel().getElementAt(i)).split(" ");
                    int chatId = Integer.parseInt(info[0]);
                    
                    if(chatId == chatid)
                    {
                       ((DefaultListModel)ListChatLists.getModel()).setElementAt(chat, i);
                       break;
                    }
                }  
            }
        });
    }
    
    /**
     * Removes the specified chat from the list.
     * @param toDelete The chat to be deleted.
     */
    public void deleteFromChatList(final int toDelete)
    {
         SwingUtilities.invokeLater(new Runnable() {    
            @Override
            public void run(){
                for(int i = 0; i < ListChatLists.getModel().getSize(); i++)
                {
                    String[] info = ((String)ListChatLists.getModel().getElementAt(i)).split(" ");
                    int chatId = Integer.parseInt(info[0]);
                    
                    if(chatId == toDelete)
                    {
                       ((DefaultListModel)ListChatLists.getModel()).removeElementAt(i);
                       break;
                    }
                }  
            }
        });
    }
    
    /**
     * Tells the GUI whether the login attempt failed or succeeded.
     * 
     * @param userID The user's ID if the login succeeded.
     * @param valid Whether the login was valid.
     */
    public void loginValid(final int userID, final boolean valid)
    {
        // Show the full GUI on success or re-prompt on failure
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                if(valid)
                {
                    initComponents();
                    ListChatLists.setModel(new DefaultListModel());
                    ListUsersLists.setModel(new DefaultListModel());
                    
                    ListChatLists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    ListUsersLists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    
                    // Whenever the selection is changed in the chatLists, repopulate the
                    // user list
                    ListChatLists.addListSelectionListener(new ListSelectionListener() {
                       
                        @Override
                        public void valueChanged(ListSelectionEvent evt)
                        {
                            // If they somehow selected nothing, then don't try to do anything
                            if(((JList)evt.getSource()).getSelectedIndex() == -1)
                                return;
                            
                            final String chatInfo = (String)((JList)evt.getSource()).getSelectedValue();
                            final String[] split = chatInfo.split(" ");
                            final String chatName = split[1].substring(0, split[1].indexOf(','));
                            final String[] usersInChat = client.getUsersAsString(chatName);
                            
                            // If there are no users in the given chat, or the chat couldn't be found, then
                            // get out of there
                            if(usersInChat == null)
                                return;

                            final DefaultListModel userModel = (DefaultListModel)ListUsersLists.getModel();
                            
                            userModel.removeAllElements();
                            
                            for(String s : usersInChat)
                                userModel.addElement(s);
                        }
                    });
                }
                else 
                    showLoginDialog();
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
    private javax.swing.JMenuItem MenuItemDeleteChat;
    private javax.swing.JMenuItem MenuItemLogout;
    private javax.swing.JMenuItem MenuItemUpdateChat;
    private javax.swing.JPanel PanelChatLog;
    private javax.swing.JPanel PanelLists;
    private javax.swing.JPanel PanelMessage;
    private javax.swing.JScrollPane PanelMessageScroll;
    private javax.swing.JScrollPane ScrollPaneUsersLists;
    private javax.swing.JScrollPane ScrollPanelChatLists;
    private javax.swing.JTabbedPane TabbedPaneChatLog;
    private javax.swing.JTabbedPane TabbedPanelLists;
    private javax.swing.JTextArea TextAreaMessage;
    private javax.swing.JMenu jMenuChangeRole;
    private javax.swing.JMenuItem jMenuItemAdminRole;
    private javax.swing.JMenuItem jMenuItemBootUser;
    private javax.swing.JMenuItem jMenuItemJoin;
    private javax.swing.JMenuItem jMenuItemLeave;
    private javax.swing.JMenuItem jMenuItemRequestAccess;
    private javax.swing.JMenuItem jMenuItemUserRole;
    private javax.swing.JPopupMenu jPopupMenuChatOptions;
    private javax.swing.JPopupMenu jPopupMenuUserOptions;
    // End of variables declaration//GEN-END:variables
}
