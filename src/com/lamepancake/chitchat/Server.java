package com.lamepancake.chitchat;

import com.lamepancake.chitchat.packet.Packet;
import com.lamepancake.chitchat.packet.PacketBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

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
        
    /**
     * The server will continue until this variable becomes false.
     */
    private boolean keepGoing;
    
    /**
     * Handles incoming packets.
     */
    private ChatManager chatManager;
    
    
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
        
        chatManager.addClient(key);
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
        {
            //remove(clientKey);TEMPORARY
        }

        // Process the packet if we're finished reading
        else if(state == PacketBuffer.FINISHED)
        {
            Packet  received = packetBuf.getPacket();
            
            chatManager.handlePacket(clientKey, received);
            
            packetBuf.clearState();
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