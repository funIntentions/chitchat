package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.LoginPacket;
import com.lamepancake.chitchat.packet.MessagePacket;
import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
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
     * @throws IOException
     */
    private void addClient(SelectionKey key) throws IOException {
        ServerSocketChannel acceptSocket    = (ServerSocketChannel) key.channel();
        SocketChannel       newClient       = acceptSocket.accept();
        SelectionKey        clientKey;
                
        // Set the new client to non-blocking mode and add to the selector
        newClient.configureBlocking(false);
        clientKey = newClient.register(this.selector, SelectionKey.OP_READ);
        
        // Add a new key-user pair to the user list
        this.users.put(clientKey, null);

        // Attach a buffer for reading the packets
        clientKey.attach(new PacketBuffer(newClient));
    }

    /**
     * 
     * @param key
     * @throws IOException 
     * @todo Deal with the exception.
     */
    private void receivePacket(SelectionKey key) throws IOException 
    {
        PacketBuffer    packetBuf   = (PacketBuffer)key.attachment();
        Packet          received;
        int             state       = packetBuf.read();
        
        if(state == PacketBuffer.DISCONNECTED)
            remove(key);

        // Read from the stream and check whether we've finished reading
        else if(state == PacketBuffer.FINISHED)
        {
            int type;
            received = packetBuf.getPacket();
            type = received.getType();
            
            switch(type)
            {
                case Packet.LOGIN:
                    login(key, (LoginPacket)received);
                    break;
                case Packet.MESSAGE:
                    sendMessage(key, (MessagePacket)received);
                    break;
                case Packet.LOGOUT:
                    remove(key);
                    break;
                case Packet.WHOISIN:
                    // send user list
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
     */
    private void login(SelectionKey key, LoginPacket loginInfo)
    {
        User newUser;
        if(this.users.get(key) != null)
            // The client sent another login packet; ignore it.
            return;

        // Some kind of validation needs to happen here (check password and username)
        // Might need to be in separate method, since reading from a file/database
        // could cause exception to be thrown in the constructor
        newUser = new User(loginInfo.getUsername(), loginInfo.getPassword());
        this.users.put(key, newUser);
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
        
        // If they're the only person in the chat, don't bother sending the message
        if(userChannels.size() <= 1)
            return;
        
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
                System.err.println("sendMessage: Could not send message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Removes a user from the user list, removes its SelectionKey from the Selector's
     * key set, and closes its associated socket.
     * 
     * @param sel The selection key identifying the user.
     */
    private void remove(SelectionKey sel)
    {
        this.users.remove(sel);
        sel.cancel();
        
        try{
            sel.channel().close();
        } catch(IOException e) {
            System.err.println("remove: Error closing channel: " + e.getMessage());
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
