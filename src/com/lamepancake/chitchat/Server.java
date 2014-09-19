package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.JoinedPacket;
import com.lamepancake.chitchat.packet.LeftPacket;
import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketBuffer;
import com.lamepancake.chitchat.packet.WhoIsInPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * The server that can be run both as a console application or a GUI
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

    /**
     * The socket on which to accept new clients.
     *
     * Note: may be removed later since it is obtainable via the key in the
     * selector set.
     */
    private ServerSocketChannel listenChannel;

    /**
     * A map relating users to sockets.
     */
    private final Map<SelectionKey, User> users;
    
    /**
     * The server will continue until this variable becomes false.
     */
    private boolean keepGoing;
    
    /**
     * The id for the next user.
     */
    private int nextId = 0;

    /**
     * Initialise the server's selector object and listening socket.
     *
     * @param port The port on which to listen for connections.
     */
    public Server(int port) {

        this.listenPort = port;

        try
        {
            this.selector = Selector.open();
            this.listenChannel = ServerSocketChannel.open();
            this.listenChannel.socket().bind(new InetSocketAddress(this.listenPort), BACKLOG);
            this.listenChannel.configureBlocking(false);
            this.listenChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } 
        catch (IOException e)
        {
            System.out.println("Server could not initialise: " + e.getMessage());
        }
        
        this.users = new HashMap<>();
    }

    /**
     * Tells the server to listen for and handles connections and chat messages.
     *
     * This is the main server loop. It waits for the selector find sockets with
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
            
            if (readyChannels == 0) 
            {
                continue;
            }

            keys        = this.selector.selectedKeys();
            keyIterator = keys.iterator();

            while (keyIterator.hasNext())
            {
                SelectionKey currentKey = keyIterator.next();
               
                if (currentKey.isValid() && currentKey.isAcceptable())
                {
                    addClient(currentKey);
                }
                if (currentKey.isValid() && currentKey.isReadable())
                {
                    receivePacket(currentKey);
                }
                if (currentKey.isValid() && currentKey.isWritable())
                {
                    // write data to the buffer and remove OP_WRITE
                }
                keyIterator.remove();
            }
        }

    }

    /**
     * Accepts a connection from a new client and adds them to the list of users.
     *
     * @param key
     */
    private void addClient(SelectionKey key) {
        ServerSocketChannel acceptSocket    = (ServerSocketChannel) key.channel();
        SocketChannel       newClient;
        SelectionKey        clientKey;
          
        try {
            // Set the new client to non-blocking mode and add to the selector
            newClient = acceptSocket.accept();
            newClient.configureBlocking(false);
        } catch (IOException e) {
            System.err.println("Server.addClient: Could not accept client: " + e.getMessage());
            return;
        }
        
        try {
            clientKey = newClient.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e){
            // This nested try...catch is pretty awful, but I can't think of any other way
            System.err.println("Server.addClient: Could not register client in selector: " + e.getMessage());
            try{newClient.close();}catch(IOException i){}
            return;
        }
        
        // Add a new key to the user list
        this.users.put(clientKey, null);

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
        PacketBuffer    packetBuf   = (PacketBuffer)clientKey.attachment();
        Packet          received;
        int             state       = packetBuf.read();
        
        // Remove the user if they've disconnected
        if(state == PacketBuffer.DISCONNECTED)
            remove(clientKey);

        // Read from the stream and check whether we've finished reading
        else if(state == PacketBuffer.FINISHED)
        {
            int type;
            received = packetBuf.getPacket();
            type = received.getType();
            
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
                    sendUserList(clientKey);
                    break;
            }
            packetBuf.clearState();
        }
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
        if(this.users.get(key) != null)
            // The client sent another login packet; ignore it.
            return;

        newUser = new User(loginInfo.getUsername(), loginInfo.getPassword(), User.UNSPEC, this.nextId++);
        this.users.put(key, newUser);
        
        // Send a list of connected clients immediately after login
        sendUserList(key);
        
        announceJoin(key, newUser);
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
        Set<SelectionKey>       userChannels    = this.users.keySet();
        Iterator<SelectionKey>  it;
        User                    client          = this.users.get(key);
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        // Sending a message without logging in? Nope
        if(client == null)
            return;
        // Pretending to be someone else? Also nope
        else if(client.getID() != message.getUserID())
            message.setUserID(client.getID());
        
        it = userChannels.iterator();
        
        while(it.hasNext())
        {
            SelectionKey currentKey = it.next();
            
            if(currentKey.equals(key))
                continue;
            
            try {
                SocketChannel channel = (SocketChannel)currentKey.channel();
                channel.write(message.serialise());              
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
        int id          = this.users.get(sel).getID();
        LeftPacket left = new LeftPacket(id);

        this.users.remove(sel);
        sel.cancel();
        sel.attach(null);

        try{
            sel.channel().close();
        } catch(IOException e) {
            System.err.println("Server.remove: Could not close channel: " + e.getMessage());
        }
        
        userChannels = this.users.keySet();
               
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
     * Creates a WhoIsInPacket and sends it to the requesting client.
     * 
     * @param clientKey The SelectionKey associated with this client.
     */
    private void sendUserList(SelectionKey clientKey)
    {
        ArrayList<User>         userList    = new ArrayList<>(this.users.size());
        Set<SelectionKey>       keys        = this.users.keySet();
        int                     size        = 4; // One extra int for the number of users
        WhoIsInPacket           packet;
        
        for (SelectionKey key : keys)
        {
            User u = this.users.get(key);

            if(u == null)
                continue;

            // Add space for the user's name and three ints (name length, role, id)
            size += u.getName().length() * 2;
            size += 12;
            
            userList.add(u);
        }
        
        packet = new WhoIsInPacket(userList, size);
        try {
            ((SocketChannel)clientKey.channel()).write(packet.serialise());
        } catch (IOException e) {
            System.err.println("Server.sendUserList: Could not send list: " + e.getMessage());
        }
    }
    
    /**
     * Announces to all connected users that a user has joined.
     * 
     * @param key The key associated with the joining user.
     * @param u   A User object containing the user's information.
     */
    private void announceJoin(SelectionKey key, User u)
    {
        Set<SelectionKey> userChannels = this.users.keySet();
        User              client       = this.users.get(key);
        JoinedPacket      join         = new JoinedPacket(u);   
               
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
        for(SelectionKey curKey : userChannels)
        {          
            // Don't announce user joining to themselves
            if(curKey.equals(key))
                continue;
            try {
                SocketChannel channel = (SocketChannel)curKey.channel();
                channel.write(join.serialise());              
            } catch (IOException e) {
                System.err.println("Server.sendMessage: Could not send message: " + e.getMessage());
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
