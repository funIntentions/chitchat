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
    
    public User getUser()
    {
        return clientUser;
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
            gui.displayError(e.getMessage(), false);
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
        Chat leavingChat = null;
        if(joining == JoinLeavePacket.JOIN && !restrict(chatID, User.USER))
        {
            gui.displayError("You cannot join the chat until an admin promotes you.", false);
            return;
        }
        else if(joining == JoinLeavePacket.LEAVE)
        {
            for(Chat c: chatList.keySet())
            {
                if(c.getID() == chatID)
                {
                    leavingChat = c;
                    break;
                }
            }
            if(leavingChat == null)
            {
                gui.displayError("Error in leaving chat: chat with ID " + chatID + " does not exist.", false);
                return;
            }
            leavingChat.getConnectedUsers().clear();
            gui.populateUserList(leavingChat.getName(), usersListAsStrings(leavingChat.getConnectedUsers()));
        }
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
        clientUser.setName(uname);
        clientUser.setPassword(pass);
        
        final LoginPacket l = PacketCreator.createLogin(uname, pass);
        waitingOp.clear();
        waitingOp.put(OperationStatusPacket.OP_LOGIN, l);
        sendPacket(l);
    }
    
    /**
     * Requests a chat list from the server.
     */
    public void sendChatList()
    {
        final ChatListPacket cl = PacketCreator.createChatList(clientUser.getID());
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
        if(waitingOp.size() > 0)
        {
            gui.displayError("Another operation is already in progress. Please try again later.", false);
            return;
        }
        final RequestAccessPacket ra = PacketCreator.createRequestAccess(this.clientUser.getID(), chatID);
        this.waitingOp.clear();
        this.waitingOp.put(OperationStatusPacket.OP_REQACCESS, ra);
        sendPacket(ra);
    }
    
    /**
     * Sends a message to all other online users in the given chat.
     * @param chatName The name of the to which the message will be sent.
     * @param message  The message to send.
     */
    public void sendMessageToChat(String chatName, String message)
    {
        Set<Chat>           chats;
        chats = this.chatList.keySet();
                
        for (Chat chat : chats)
        {
            if (chat.getName().equalsIgnoreCase(chatName))
            {
                sendMessage(chat.getID(), message);
            }
        }
    }
    
    public void changeUserRole(String userName, int role)
    {
        Set<Chat>           chats;
        chats = this.chatList.keySet();
        User foundUser;
                
        for (Chat chat : chats)
        {
            if ((foundUser = chat.findUser(userName)) != null)
            {
                sendChangeRole(foundUser.getID(), chat.getID(), role);
            }
        }
    }
    
    public void bootUser(String userName)
    {
        Set<Chat>           chats;
        chats = this.chatList.keySet();
        User foundUser;
                
        for (Chat chat : chats)
        {
            if ((foundUser = chat.findUser(userName)) != null)
            {
                sendBoot(chat, foundUser);
            }
        }
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

        final ChangeRolePacket cr = PacketCreator.createChangeRole(chatid, userid, clientUser.getID(), role);
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
     * Gets the list of users as strings in a chat with the given name.
     * 
     * @param chatName The name of the chat from which to get the list of users.
     * @return An array of strings representing the users.
     */
    public String[] getUsersAsString(final String chatName)
    {
        final String[] users;
        Chat selected = null;
        Map<User, Boolean> inChat;

        for(Chat c: chatList.keySet())
        {
            if(c.getName().equals(chatName))
            {
                selected = c;
                break;
            }
        }
        
        if(selected == null)
            return null;
        
        inChat = selected.getConnectedUsers();        
        return usersListAsStrings(inChat);
    }
    
    /**
     * Converts a map of users to online statuses to a list of strings.
     * @param users 
     */
    private String[] usersListAsStrings(final Map<User, Boolean> users)
    {
        final int size = users.size();
        final String[] ret;
        int i = 0;
        
        if(size == 0)
            return null;
        
        ret = new String[users.size()];
        
        for (User u : users.keySet())
        {
            final String status;
            if(u.getRole() == User.WAITING)
                status = "waiting";
            else
                status = users.get(u) ? "online" : "offline";

            ret[i++] = u.getName() + ", " + status;
        }
        
        return ret;
    }
    
    public String getChatName(int chatID)
    {
        for(Chat c : chatList.keySet())
        {
            if(c.getID() == chatID)
            {
                return c.getName();
            }
        }
        
        return null;
    }
    
    public Chat getChatByName(String chatName)
    {
        for(Chat c : chatList.keySet())
        {
            if(c.getName().equalsIgnoreCase(chatName))
            {
                return c;
            }
        }
        
        return null;
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
     * @throws IOException when the socket cannot be created.
     */
    public static SocketChannel parseCmdArgs(String[] args) throws IOException
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
        SocketChannel socket = SocketChannel.open(addr);
        return socket;
    }
    
    /**
     * Processes the list of chats received from the server.
     * @param p The ChatListPacket containing the chats.
     */
    private void processChatList(ChatListPacket p)
    {
        chatList = p.getChatList();
        Set<Chat> keys = chatList.keySet();
        
        String[] chatListList = new String[keys.size()];
        int i = 0;
        
        for(Chat c : keys)
        {
            switch(chatList.get(c))
            {
                case User.ADMIN:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Scrum Master";
                    break;
                case User.USER:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Developer";
                    break;
                case User.WAITING:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Waiting";
                    break;
                case User.UNSPEC:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Unspecified"; 
                    break;
                default:
                    chatListList[i] = c.getID() + " " + c.getName() + ", Invalid role";
            }
            i++;
        }
        
        gui.populateChatList(chatListList);
    }

    /**
     * Processes the WhoIsInPacket for a given chat.
     * @param p The WhoIsInPacket containing the list of users.
     */
    private void processUserList(WhoIsInPacket p)
    {
        Map<User, Boolean> users = p.getUsers();
        Set<User> keys = users.keySet();
        
        String userInfo;
        Chat relevantChat = null;
        
        Set<Chat>           chats;
        chats = this.chatList.keySet();
                
        for (Chat chat : chats)
        {
            if (chat.getID() == p.getChatID())
            {
                relevantChat = chat;
            }
        }
        
        if (relevantChat == null) return; // chat doesn't exist... what?
        
        for(User u : keys)
        {
            
            if (u.getRole() == User.WAITING)
            {
                relevantChat.initUser(u);
            }
            else
            {
                boolean online = users.get(u);
                relevantChat.initUser(u, online);
            }
        }
        
        gui.populateUserList(usersListAsStrings(users));
    }
    
    /**
     * Updates a user's status in a particular chat.
     * @param p 
     */
    private void processUserNotify(UserNotifyPacket p)
    {
        //String userInfo = p.
        final Set<Chat> chats = this.chatList.keySet();
        final Set<User> inChat;
        final int flag = p.getFlag();

        Chat selectedChat = null;
        Map<User, Boolean> list;
        
        for (Chat chat : chats)
        {
            if (chat.getID() == p.getChatID())
            {
                selectedChat = chat;
                break;
            }
        }
        
        if(selectedChat == null)
            return;
        
        list = selectedChat.getConnectedUsers();
        inChat = selectedChat.getConnectedUsers().keySet();
        
        // If the user has just requested access, create them and add them to the list
        if(flag == UserNotifyPacket.WAITING)
        {
            User newUser = new User().setID(p.getUserID()).setName(p.getName()).setRole(User.WAITING);
            list.put(newUser, false);
        }
        // Otherwise, update their status
        else
        {
            for(User u : inChat)
            {
                if(u.getID() == p.getUserID())
                {
                    switch(flag)
                    {
                        case UserNotifyPacket.PROMOTED:
                            u.setRole(p.getUserRole());
                            break;
                        case UserNotifyPacket.JOINED:
                            list.put(u, true);
                            break;
                        case UserNotifyPacket.LEFT:
                            list.put(u, false);
                            break;
                        case UserNotifyPacket.BOOTED:
                            list.remove(u);
                            break;
                    }
                    break;
                }
            }
        }
        // Tell the gui to update the list of users for the given chat
        gui.populateUserList(selectedChat.getName(), usersListAsStrings(list));
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
                    System.out.println("operation");
                    operationStatusHandler((OperationStatusPacket)p);
                    break;
                case Packet.JOINLEAVE:
                    JoinLeavePacket jl = (JoinLeavePacket)p;
                    if (jl.getFlag() == JoinLeavePacket.JOIN)
                    {
                        String name = getChatName(jl.getChatID());
                        if (name != null)
                        {
                            gui.addTab(name);
                        }
                    }
                    else
                    {
                        String name = getChatName(jl.getChatID());
                        if (name != null)
                        {
                            gui.removeTab(name);
                        }
                    }
                    break;
                case Packet.USERNOTIFY:
                    final UserNotifyPacket userNotify = (UserNotifyPacket)p;
                    processUserNotify(userNotify);
                    break;
                case Packet.CHATNOTIFY:
                    switch(((ChatNotifyPacket)p).getChangeFlag())
                    {
                        case UpdateChatsPacket.CREATE:
                            createChat((ChatNotifyPacket)p);
                            break;
                        case UpdateChatsPacket.DELETE:
                            deleteChat((ChatNotifyPacket)p);
                            break;
                        case UpdateChatsPacket.UPDATE:
                            updateChat((ChatNotifyPacket)p);
                            break;
                    }
                    break;
                case Packet.BOOT:
                    getBooted((BootPacket)p);
                    break;
                case Packet.CHANGEROLE:
                    changeRole((ChangeRolePacket)p);
                    break;
                default:
                    System.err.println("Invalid packet received.");
                    break;
            }    
        }
    }
    
    
    private void getBooted(BootPacket bt)
    {
        String name = getChatName(bt.getChatID());
        if (name != null)
        {
            gui.removeTab(name);
            changeRole(bt.getChatID(), User.UNSPEC);
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
        
        gui.displayUserMessage(m.getMessage(), srcUser.getName(), srcChat.getName());
    }
    
    private void changeRole(ChangeRolePacket p)
    {
        Set<Chat> keys = chatList.keySet();
        for(Chat c : keys)
        {
            if(c.getID() == p.getChatID())
            {
                chatList.put(c, p.getRole()); 
                switch(p.getRole())
                {
                    case User.ADMIN:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Scrum Master");
                        break;
                    case User.USER:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Developer");
                        break;
                    case User.WAITING:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Waiting");
                        break;
                    case User.UNSPEC:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Unspecified"); 
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }
    
    private void changeRole(int chatID, int role)
    {
        Set<Chat> keys = chatList.keySet();
        for(Chat c : keys)
        {
            if(c.getID() == chatID)
            {
                chatList.put(c, role); 
                switch(role)
                {
                    case User.ADMIN:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Scrum Master");
                        break;
                    case User.USER:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Developer");
                        break;
                    case User.WAITING:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Waiting");
                        break;
                    case User.UNSPEC:
                        gui.updateChatList(c.getID(), c.getID() + " " + c.getName() + ", Unspecified"); 
                        break;
                    default:
                        break;
                }
                break;
            }
        }
    }
    
    private void updateChat(ChatNotifyPacket p)
    {
        Set<Chat> keys = chatList.keySet();
        Chat updateChat = null;
        String oldChat = "";
        
        for(Chat c : keys)
        {
            if(c.getID() == p.getChatID())
            {
                oldChat = c.getName();
                c.setName(p.getChatName());
                updateChat = c;
                break;
            }
        }
        
        if(updateChat != null)
        {
            switch(chatList.get(updateChat))
            {
                case User.ADMIN:
                    gui.updateChatList(p.getChatID(), p.getChatID() + " " + p.getChatName() + ", Scrum Master");
                    break;
                case User.USER:
                    gui.updateChatList(p.getChatID(), p.getChatID() + " " + p.getChatName() + ", Developer");
                    break;
                case User.WAITING:
                    gui.updateChatList(p.getChatID(), p.getChatID() + " " + p.getChatName() + ", Waiting");
                    break;
                case User.UNSPEC:
                    gui.updateChatList(p.getChatID(), p.getChatID() + " " + p.getChatName() + ", Unspecified"); 
                    break;
                default:
                   break;
            }
        }
    }
    
    
    private void createChat(ChatNotifyPacket p)
    {
        chatList.put(new Chat(p.getChatName(), p.getChatID()), User.UNSPEC);
        String c = p.getChatID() + " " + p.getChatName() + ", Unspecified";
        gui.addChatToList(c);
    }
    
    private void deleteChat(ChatNotifyPacket p)
    {
        Set<Chat> keys = chatList.keySet();
        for(Chat c : keys)
        {
            if(c.getID() == p.getChatID())
            {
                chatList.remove(c);
                break;
            }
        }
        gui.deleteFromChatList(p.getChatID());
    }

    /**
     * Serves some badly-defined function...
     * @param p 
     */
    private void joinLeaveChat(UserNotifyPacket p)
    {
        if(p.getFlag() == 0 && p.getUserID() == clientUser.getID())
        {
            Set<Chat> chatkeys = chatList.keySet();
            for(Chat c : chatkeys)
            {
                if(c.getID() == p.getChatID())
                {
                    gui.addTab(c.getName());
                    break;
                }
            }
        }
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
                if(op.getStatus() == OperationStatusPacket.SUCCESS)
                {
                    final int waitingPacketType = p.getType();
                    if(waitingPacketType == Packet.UPDATECHAT)
                    {
                        handleChatCRUD(op, (UpdateChatsPacket)p);
                    }
                    else if(waitingPacketType == Packet.BOOT)
                    {
                        // call a gui.removeUser method
                    }
                    else if(waitingPacketType == Packet.CHANGEROLE)
                    {
                        User affected = null;
                        Chat selected = null;
                        final ChangeRolePacket cr = (ChangeRolePacket)p;
                        for(Chat c : chatList.keySet())
                        {
                            if(c.getID() == cr.getChatID())
                            {
                                selected = c;
                                break;
                            }
                        }
                        if(selected == null || ((affected = selected.findUser(cr.getUserID())) == null))
                            return;

                        affected.setRole(cr.getRole());
                        gui.populateUserList(selected.getName(), usersListAsStrings(selected.getConnectedUsers()));
                    }
                }
                else
                {
                    gui.displayError("A database operation failed on the server while attempting to " + 
                            (p.getType() == Packet.UPDATECHAT ? "update a chat." : "boot a user."), false);
                }
                break;
            case OperationStatusPacket.OP_LOGIN:
                if(op.getStatus() == 1)
                {
                    // Login succeeded. Call the GUI method
                    clientUser.setID(op.getUserID());
                    gui.loginValid(op.getUserID(), true);
                }
                else
                    gui.loginValid(-1, false);
                break;
            case OperationStatusPacket.OP_REQACCESS:
                Chat chat = null;
                RequestAccessPacket ra = (RequestAccessPacket)p;
                final int id = ra.getChatID();

                for(Chat c: chatList.keySet())
                {
                    if(id == c.getID())
                    {
                        chat = c;
                        break;
                    }
                }
                
                if(chat == null)
                    return;

                if(op.getStatus() == 1)
                {
                    gui.updateChatList(ra.getChatID(), id + " " +  chat.getName() + ", Waiting");
                }
                else 
                {
                    gui.displayError("Request access to chat " + chat.getName() + " failed.", false);
                }
        }
        waitingOp.clear();
    }
    
    /**
     * Notifies the GUI of the success or failure of chat CRUD operations and updates
     * its own list accordingly.
     * @param op The OperationStatusPacket containing the update status.
     * @param up The UpdateChatsPacket containing the original information.
     * @todo Don't hard code the strings quite so much.
     */
    private void handleChatCRUD(OperationStatusPacket op, UpdateChatsPacket up)
    {   
        final int chatOpType = up.getUpdate();
        String chatStr = op.getChatID() + " " + up.getName() + ", ";
        
        if(chatOpType == UpdateChatsPacket.CREATE)
        {
            chatStr += "Unspecified";
            chatList.put(new Chat(up.getName(), op.getChatID()), User.UNSPEC);
            gui.addChatToList(chatStr);
        }
        else
        {
            Set<Chat> keys = chatList.keySet();
            Chat updateChat = null;
            String oldChat = "";

            for(Chat c : keys)
            {
                if(c.getID() == op.getChatID())
                {
                    if(chatOpType == UpdateChatsPacket.UPDATE)
                    {
                        oldChat = c.getName();
                        c.setName(up.getName());
                    }
                    updateChat = c;
                    break;
                }
            }
        
            switch(chatOpType)
            {
                case UpdateChatsPacket.UPDATE:
                    chatStr += "Scrum Master";
                    gui.updateChatList(up.getChatID(), chatStr);
                    gui.updateTab(up.getName(), oldChat);
                    break;
                case UpdateChatsPacket.DELETE:
                    chatStr += "Scrum Master";
                    chatList.remove(updateChat);
                    gui.deleteFromChatList(up.getChatID());
                    break;
            }
        }
        
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
