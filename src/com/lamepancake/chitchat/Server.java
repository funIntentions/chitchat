package com.lamepancake.chitchat;

import static com.lamepancake.chitchat.User.ADMIN;
import static com.lamepancake.chitchat.User.USER;
import com.lamepancake.chitchat.packet.ChatListPacket;
import com.lamepancake.chitchat.packet.GrantAccessPacket;
import com.lamepancake.chitchat.packet.JoinedPacket;
import com.lamepancake.chitchat.packet.LeftPacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketBuffer;
import com.lamepancake.chitchat.packet.WhoIsInPacket;
import com.lamepancake.chitchat.packet.UpdateChatsPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * Coordinates user authentication and transmission of chat messages.
 * 
 * Loosely Based upon the work of Paul-Benoit Larochelle (see link below).
 * @see http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
 */
public class Server {

    /**
     * The number of simultaneous connections that the server can accept.
     */
    public static final int BACKLOG = 5;
    
    /**
     * A selector to multiplex data in the sockets.
     */
    private Selector selector;

    /**
     * The port on which to listen for connections.
     */
    private final int listenPort;

    
    private final Map<SelectionKey, User> lobby;
    
    private final Map<Integer, Chat> chats;
    
    /**
     * The server will continue until this variable becomes false.
     */
    private boolean keepGoing;
    
    /**
     * IDs for previous users that have left.
     */
    private List<Integer> recycledIDs;
    
    /**
     * ChatIDs for chats that have been removed that have left.
     */
    private List<Integer> recycledChatIDs;
    
    /**
     * The id for the next user.
     */
    private int nextId = 0;
    
    /**
     * The id for the next user.
     */
    private int nextChatId = 0;

    /**
     * Initialise the server's selector object and listening socket.
     *
     * @param port The port on which to listen for connections.
     * @throws IOException When the server couldn't be initialised.
     */
    public Server(int port) throws IOException {

        this.listenPort = port;

        try
        {
            ServerSocketChannel listenChannel;
            this.selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(this.listenPort), BACKLOG);
            listenChannel.configureBlocking(false);
            listenChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } 
        catch (IOException e)
        {
            System.out.println("Server could not initialise: " + e.getMessage());
            throw(e);
        }
        
        this.lobby = new HashMap<>();
        this.chats = new HashMap<>();
        this.recycledIDs = new ArrayList<>();
        this.recycledChatIDs = new ArrayList<>();
        
        // temp chat
        
        this.chats.put(1, new Chat("chitchatcat", 1));
    }

    /**
     * Tells the server to listen for and handles connections and chat messages.
     *
     * This is the main server loop. It waits for the selector to find sockets with
     * operations ready and calls the appropriate handler methods.
     * 
     * @throws IOException Thrown on selection set failure.
     */
    public void start() throws IOException {
        Set<SelectionKey>       keys;
        Iterator<SelectionKey>  keyIterator;
        this.keepGoing = true;

        while (keepGoing) {
            int readyChannels = this.selector.select();
            
            if (readyChannels == 0) continue;

            keys        = this.selector.selectedKeys();
            keyIterator = keys.iterator();

            while (keyIterator.hasNext())
            {
                SelectionKey currentKey = keyIterator.next();
               
                if (currentKey.isValid() && currentKey.isAcceptable())
                    addClient(currentKey);

                if (currentKey.isValid() && currentKey.isReadable())
                    receivePacket(currentKey);

                if (currentKey.isValid() && currentKey.isWritable())
                {
                    // write data to the buffer and remove OP_WRITE
                }
                
                // Remove the key from the selected-set
                keyIterator.remove();
            }
        }

    }

    /**
     * Accepts a connection from a new client.
     *
     * @param key The SelectionKey associated with the ServerSocketChannel.
     */
    private void addClient(SelectionKey key) {
        ServerSocketChannel acceptSocket    = (ServerSocketChannel) key.channel();
        SocketChannel       newClient;
        SelectionKey        clientKey;
        
        // Set the new client to non-blocking mode
        try {
            newClient = acceptSocket.accept();
            newClient.configureBlocking(false);
        } catch (IOException e) {
            System.err.println("Server.addClient: Could not accept client: " + e.getMessage());
            return;
        }
        
        // Add the new client to the selector
        try {
            clientKey = newClient.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e){
            // This nested try...catch is pretty awful, but I can't think of any other way
            System.err.println("Server.addClient: Could not register client in selector: " + e.getMessage());
            try{newClient.close();}catch(IOException i){}
            return;
        }

        // Attach a buffer for reading the packets
        clientKey.attach(new PacketBuffer(newClient));
    }

    /**
     * Attempt to receive a packet from the selected socket.
     * 
     * @param clientKey The SelectionKey associated with this client.
     */
    private void receivePacket(SelectionKey clientKey)
    {
        PacketBuffer packetBuf   = (PacketBuffer)clientKey.attachment();
        int          state       = packetBuf.read();
        
        // Remove the user if they've disconnected
        if(state == PacketBuffer.DISCONNECTED)
            remove(clientKey);

        // Process the packet if we're finished reading
        else if(state == PacketBuffer.FINISHED)
        {
            Packet  received = packetBuf.getPacket();
            int     type     = received.getType();
                      
            switch(type)
            {
                case Packet.LOGIN:
                    login(clientKey, (LoginPacket)received);
                    break;
                case Packet.MESSAGE:
                    sendMessage(clientKey, (MessagePacket)received);
                    break;
                case Packet.LOGOUT:
                    remove(clientKey);
                    break;
                case Packet.WHOISIN:
                    sendUserList(clientKey, ((WhoIsInPacket)received).whichList());
                    break;
                case Packet.CHATLIST:
                    sendChatList(clientKey);
                    break;
                case Packet.JOINED:
                    addUserToChat(clientKey, (JoinedPacket)received); 
                    break;
                case Packet.CHATSUPDATE:
                    switch(((UpdateChatsPacket)received).getUpdate())
                    {
                        case UpdateChatsPacket.CREATE:
                            createNewChat(clientKey, (UpdateChatsPacket)received);
                            break;
                        case UpdateChatsPacket.REMOVE:
                            break;
                        case UpdateChatsPacket.UPDATE:
                            updateChat(clientKey, (UpdateChatsPacket)received);
                            break;
                    }
                    break;
                case Packet.GRANTACCESS:
                    Chat chat = this.chats.get(1); // the 1 is just temporary.
                    Map<SelectionKey, User> users = chat.getConnectedUsers();
                    
                    User sender = users.get(clientKey);
                    if(sender != null && sender.getRole() == User.ADMIN)
                    {
                        SelectionKey selected = userCheck(clientKey, (GrantAccessPacket)received);
                        
                        if(selected != null)
                        {
                            User user = users.get(selected);
                            
                            if (((GrantAccessPacket)received).getUserRole() == User.UNSPEC)
                            {
                                removeUserFromChat(selected, (GrantAccessPacket)received);
                            }
                            else if (user == null)
                            {
                                setUserRole(users, selected, (GrantAccessPacket)received);
                                addUserToChat(selected, (GrantAccessPacket)received);
                            }
                            else
                            {
                                updateList(selected);
                                
                                setUserRole(users, selected, (GrantAccessPacket)received);
                                updateUserInChat(selected, (GrantAccessPacket)received);
                            }
                        }
                        
                    }
                    else
                    {
                        System.err.println("Access requirement not met for this command.");
                    }
                    break;
            }
            packetBuf.clearState();
        }
    }
    
    private void createNewChat(SelectionKey key, UpdateChatsPacket chatInfo)
    {
        Chat newChat;
        
        String name = chatInfo.getName();
        int id = getUniqueChatID();
        
        newChat = new Chat(name, id);
        
        this.chats.put(id, newChat);
    }
    
    private void updateChat(SelectionKey key, UpdateChatsPacket chatInfo)
    {
        Chat chat;
        
        String name = chatInfo.getName();
        int id = chatInfo.getID();
        
        chat = chats.get(id);
        chat.setName(name);
    }
    
    private int getUniqueChatID()
    {
        int ID;
        
        if (!recycledChatIDs.isEmpty())
        {
            ID = recycledChatIDs.get(0);
            recycledChatIDs.remove(0);
        }
        else
        {
            ID = nextChatId++;
        }
        
        return ID;
    }
    
    
    private int getUniqueID()
    {
        int ID;
        
        if (!recycledIDs.isEmpty())
        {
            ID = recycledIDs.get(0);
            recycledIDs.remove(0);
        }
        else
        {
            ID = nextId++;
        }
        
        return ID;
    }

    /**
     * Associates the new user with the selection key.
     * 
     * @param key       The SelectionKey with which to associate the user.
     * @param loginInfo The LoginPacket containing the user's information.
     * @todo Add user validation (e.g. check for username/password in DB).
     * @todo Remove user.UNSPEC role.
     */
    private void login(SelectionKey key, LoginPacket loginInfo)
    {
        User newUser;
        int newId = getUniqueID();
        
        //Chat chat = this.chats.get(1); // the 1 is just temporary.
        //Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        // The client sent another login packet; ignore it.
        if(lobby.get(key) != null)
            return;
        
        int role = loginInfo.getUsername().equalsIgnoreCase("Admin") ? ADMIN: User.UNSPEC;
        System.out.println(role);
        newUser = new User(loginInfo.getUsername(), loginInfo.getPassword(), role, newId);
        
        lobby.put(key, newUser);
        
        // Send a list of connected clients immediately after being added to the chat.
        sendUserList(key, WhoIsInPacket.CONNECTED);
    }
    
    /**
     * Check user presence
     * @param key
     * @param userInfo 
     */
    private SelectionKey userCheck(SelectionKey key, GrantAccessPacket userInfo)
    {
        Set<SelectionKey>       userChannels;
        int                     userID       = userInfo.getUserID();
        SelectionKey            sel          = null;
        
        /*if(userChannels.isEmpty()) // if the admin is trying to add users that dont exist
        {
            return;
        }*/
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        
        for(SelectionKey curKey : userChannels)
        {
            User user = users.get(curKey);
            
            if (user.getID() == userID)
            {
                sel = curKey;
                break;
            }
        }
        
        /*if (sel == null) // if the admin is trying to add users that dont exist
        {
            return;
        }  */    
        
        return sel;
    }
    
    /**
     * Sets user role.
     * @param key
     * @param userInfo 
     */
    private void setUserRole(Map<SelectionKey, User> map, SelectionKey key, GrantAccessPacket userInfo)
    {
        User waitingUser = map.get(key);
        waitingUser.setRole(userInfo.getUserRole());
        
        //announceJoin(key, waitingUser);
    }
    
    /**
     * Adds a user to the chat.
     * 
     * @param key       The SelectionKey of the admin who added the user.
     * @param userInfo  Contains the id of the user that's being added.
     * @todo Send the admin's name to the user letting them know who added them.
     */
    private void addUserToChat(SelectionKey key, JoinedPacket userInfo)
    {
        Set<SelectionKey>       userChannels;
        int                     userID       = userInfo.getUser().getID();
        //SelectionKey            sel          = null;
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        // Swap the user from the waiting list to the in chat list.
        User waitingUser = this.lobby.get(key);
        this.lobby.remove(key);
        
        System.out.println(waitingUser.getRole());
        //System.out.println(waitingUser.getRole());
        
        users.put(key, waitingUser);
        
        // inform the user they are now in the chat.
        try {
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(userInfo.serialise());              
        } catch (IOException e) {
            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
        }
        
        // Send a list of connected clients immediately after being added to the chat.
        sendUserList(key, WhoIsInPacket.CONNECTED);
        
        announceJoin(key, waitingUser, 1); /////////////////TEMPORARY
    }
    
    private void addUserToChat(SelectionKey key, GrantAccessPacket userInfo)
    {
        Set<SelectionKey>       userChannels;
        int                     userID       = userInfo.getUserID();
        //SelectionKey            sel          = null;
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        // Swap the user from the waiting list to the in chat list.
        User waitingUser = this.lobby.get(key);
        
        waitingUser.setRole(userInfo.getUserRole());
        
        // inform the user they are now in the chat.
        try {
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(userInfo.serialise());              
        } catch (IOException e) {
            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
        }
        
        // Send a list of connected clients immediately after being added to the chat.
        sendUserList(key, WhoIsInPacket.CONNECTED);
        
        announceJoin(key, waitingUser, 1); /////////////////TEMPORARY
    }
    
    /**
     * Updates a user that's already in the chat.
     * 
     * @param key The SelectionKey of the user being updated.
     * @param userInfo The Packet that contains the updated info.
     */
    private void updateUserInChat(SelectionKey key, GrantAccessPacket userInfo)
    {
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        User chattingUser = users.get(key);
        
        // inform the user they are now in the chat.
        try {
            SocketChannel channel = (SocketChannel)key.channel();
            channel.write(userInfo.serialise());              
        } catch (IOException e) {
            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
        }
        
        // Send a list of connected clients immediately after being added to the chat.
        sendUserList(key, WhoIsInPacket.CONNECTED);
        
        announceJoin(key, chattingUser, 1); //////TEMPORARY
    }
    
    /**
     * Removes a user from the chat.
     * 
     * @param selected The selected user to remove.
     * @param userInfo The packet being sent to them.
     */
    private void removeUserFromChat(SelectionKey selected, GrantAccessPacket userInfo)
    {
        // inform the user they have been booted from the chat.
        try {
            SocketChannel channel = (SocketChannel)selected.channel();
            channel.write(userInfo.serialise());              
        } catch (IOException e) {
            System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
        }
        
        remove(selected);
    }
    
    /**
     * Sends a chat message to all other users in the chat.
     * 
     * @param key     The SelectionKey associated with the sender.
     * @param message The message to be sent.
     * 
     * @todo Handle case where packet doesn't send completely
     */
    private void sendMessage(SelectionKey key, MessagePacket message)
    {
        Set<SelectionKey>       userChannels;
        User                    client;
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        client       = users.get(key);
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        // Sending a message without logging in? Nope
        if(client == null)
            return;
        // Pretending to be someone else? Also nope
        else if(client.getID() != message.getUserID())
            message.setUserID(client.getID());
         
        broadcast(key, message, false);
    }
    
     /**
     * Updates a user from the list, cleans up its socket, and notifies other users.
     * 
     * @param sel The selection key identifying the user.
     */
    private void updateList(SelectionKey sel)
    {
        Set<SelectionKey> userChannels;
        int id;
        LeftPacket left;
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
        id           = users.get(sel).getID();
        
        
        left = new LeftPacket(id);

        for(SelectionKey curKey : userChannels)
        {
            try {
                SocketChannel channel = (SocketChannel)curKey.channel();
                channel.write(left.serialise());              
            } catch (IOException e) {
                System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
            }
        }

    }
    
    /**
     * Removes a user from the list, cleans up its socket, and notifies other users.
     * 
     * @param sel The selection key identifying the user.
     */
    private void remove(SelectionKey sel)
    {
        Set<SelectionKey> userChannels;
        int id;
        LeftPacket left;
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        if (users.containsKey(sel))
        {
            userChannels = users.keySet();
            id           = users.get(sel).getID();
            users.remove(sel);
        }
        else 
        {
            userChannels = this.lobby.keySet();
            id           = this.lobby.get(sel).getID();
            this.lobby.remove(sel);
        }
        
        left = new LeftPacket(id);
        
        recycledIDs.add(id);

        sel.cancel();
        sel.attach(null);

        try{
            sel.channel().close();
        } catch(IOException e) {
            System.err.println("Server.remove: Could not close channel: " + e.getMessage());
        }
               
        // No one left in the chat; no one to notify
        if(userChannels.isEmpty())
            return;
        
        for(SelectionKey curKey : userChannels)
        {
            try {
                SocketChannel channel = (SocketChannel)curKey.channel();
                channel.write(left.serialise());              
            } catch (IOException e) {
                System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a ChatListPacket and sends it to the requesting client.
     * 
     * @param clientKey  The SelectionKey associated with this client.
     */
    private void sendChatList(SelectionKey clientKey)
    {
        ArrayList<Chat>         chatList = new ArrayList<>();
        Set<Integer>            keys;
        ChatListPacket          packet;
        int                     size = 4;
        
        keys = this.chats.keySet();
        for (Integer key : keys)
        {
            Chat c = chats.get(key);

            // Add space for the user's name and three ints (name length, role, id)
            size += c.getName().length() * 2;
            size += 8;
            
            chatList.add(c);
        }
        
        packet = new ChatListPacket(chatList, size);
        try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendChatList: Could not send list: " + e.getMessage());
        }
    }
    
    /**
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     * @param list      The user list to send (WhoIsInPacket.CONNECTED or WhoIsInPacket.WAITING).
     */
    private void sendUserList(SelectionKey clientKey, int list)
    {
        ArrayList<User>             userList;
        Set<SelectionKey>           keys;
        WhoIsInPacket               packet;
        Map<SelectionKey, User>     userMap;
        int                size = 4; // One extra int for the number of users
        
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        if(list == WhoIsInPacket.CONNECTED)
        {
            userList = new ArrayList<>(users.size());
            keys = users.keySet();
            userMap = users;
        }
        else if(list == WhoIsInPacket.WAITING)
        {
            userList = new ArrayList<>(this.lobby.size());
            keys = this.lobby.keySet();
            userMap = this.lobby;
        }
        else
            throw new IllegalArgumentException("list must be 0 (users) or 1 (pending users), was " + list);
        
        for (SelectionKey key : keys)
        {
            User u = userMap.get(key);

            // Add space for the user's name and three ints (name length, role, id)
            size += u.getName().length() * 2;
            size += 12;
            
            userList.add(u);
        }
        
        packet = new WhoIsInPacket(userList, size, list);
        try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendUserList: Could not send list: " + e.getMessage());
        }
    }
    
    /**
     * Announces to all connected users in chat that a user has joined.
     * 
     * @param key The key associated with the joining user.
     * @param u   A User object containing the user's information.
     */
    private void announceJoin(SelectionKey key, User u, int chatID)
    {
        Set<SelectionKey> userChannels;
        JoinedPacket      join         = new JoinedPacket(u, chatID);   
        
        Chat chat = this.chats.get(chatID); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        userChannels = users.keySet();
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        broadcast(key, join, false);
    }
    
    /**
     * Broadcasts the passed in message to all connected users.
     * 
     * @param key The key associated with the sending user.
     * @param p The packet to be broadcasted.
     * @param broadcast True if you want to include the sending user in the broadcast, false otherwise.
     */
    private void broadcast(SelectionKey key, Packet p, Boolean broadcast)
    {
        Chat chat = this.chats.get(1); // the 1 is just temporary.
        Map<SelectionKey, User> users = chat.getConnectedUsers();
        
        Set<SelectionKey> userChannels = users.keySet();
        
        for(SelectionKey curKey : userChannels)
        {
            User user = users.get(curKey);
            
            if (user.getRole() != User.UNSPEC)
            {
                // Don't notify the message sending user what they have sent
                if(curKey.equals(key))
                    if(!broadcast)
                        continue;

                try {
                    SocketChannel channel = (SocketChannel)curKey.channel();
                    channel.write(p.serialise());              
                } catch (IOException e) {
                    System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
                }   
            }
        }
    }
    
    /*
     *  To run as a console application just open a console window and: 
     * > java Server
     * > java Server portNumber
     * If the port number is not specified 1500 is used
     */
    public static void main(String[] args) throws IOException {
        // start server on port 1500 unless a PortNumber is specified 
        int portNumber = 1500;
        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;

        }
        // create a server object and start it
        Server server = new Server(portNumber);
        server.start();
    }
}