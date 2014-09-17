package com.lamepancake.chitchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
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

        try {
            this.selector = Selector.open();
            this.listenChannel = ServerSocketChannel.open();
            this.listenChannel.socket().bind(new InetSocketAddress(this.listenPort), BACKLOG);
            this.listenChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            this.listenChannel.configureBlocking(false);
        } catch (IOException e) {
            System.out.println("Server could not initialise: " + e.getMessage());
        }
    }

    /**
     * Tells the server to listen for and handles connections and chat messages.
     *
     * @throws IOException Thrown on selection set failure.
     */
    public void start() throws IOException {
        Set<SelectionKey>       keys;
        Iterator<SelectionKey>  keyIterator;
        this.keepGoing = true;

        while (keepGoing) {
            int readyChannels = this.selector.select();
            if (readyChannels == 0) {
                continue;
            }

            keys        = this.selector.keys();
            keyIterator = keys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey currentKey = keyIterator.next();
                if (currentKey.isAcceptable()) {
                    addClient(currentKey);
                } else if (currentKey.isReadable()) {
                    readSock(currentKey);
                }
            }
        }

    }

    /**
     * Accepts a connection from a new client and reads their name.
     *
     * @param key
     * @throws IOException
     */
    private void addClient(SelectionKey key) throws IOException {
        ServerSocketChannel acceptSocket = (ServerSocketChannel) key.channel();
        SocketChannel newClient = acceptSocket.accept();
        newClient.register(this.selector, SelectionKey.OP_READ);
    }

    /**
     * 
     * @param key
     * @throws IOException 
     */
    private void readSock(SelectionKey key) throws IOException {
        SocketChannel   readSock    = (SocketChannel) key.channel();
        ByteBuffer      typeAndLen  = ByteBuffer.allocate(Packet.BUF_OFFSET);
        ByteBuffer      data;
        Packet          received;
        int             type;
        int             len;

        if(readSock.read(typeAndLen) != Packet.BUF_OFFSET) {
            // fuck
        }

        type = typeAndLen.getInt();
        len = typeAndLen.getInt();

        data = ByteBuffer.allocate(len);
        readSock.read(data);

        switch (type) {
            case Packet.LOGIN:
                received = new LoginPacket(data);

                break;

            case Packet.LOGOUT:
                // clean up the user
                break;

            case Packet.MESSAGE:
                // send the messsage to the other users
                break;

            case Packet.WHOISIN:
                // send a list of connected clients to the requester
                break;
        }
    }

    /*
     * For the GUI to stop the server
     */
    protected void stop() {
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
    }

    // for a client who logoff using the LOGOUT message
    public void remove(int id) {

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
