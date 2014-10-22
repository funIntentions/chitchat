/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketBuffer {
    /**
     * The PacketBuffer contains no data.
     */
    public static final int NO_DATA = 0;
    
    /**
     * The PacketBuffer contains part of the type and length information for the
     * packet.
     */
    public static final int READING_TYPE = 1;
    
    /**
     * The PacketBuffer contains the packet type and length information and is
     * reading the packet's data.
     */
    public static final int READING_DATA = 2;
    
    /**
     * The PacketBuffer contains the entire packet.
     * 
     * A FINISHED state indicates that getPacket can be safely called.
     */
    public static final int FINISHED = 3;
    
    /**
     * The remote peer closed their socket.
     */
    public static final int DISCONNECTED = 4;
    

    private final ByteBuffer    packetHeader;
    private       ByteBuffer    packetData;
    private final SocketChannel socket;
    
    private int state;
    private Packet packet;

    public PacketBuffer(SocketChannel socket)
    {
        this.packetHeader   = ByteBuffer.allocate(Packet.HEADER_SIZE);
        this.socket         = socket;
	this.state          = NO_DATA;
    }

    /**
     * Reads as much of the packet data as possible.
     * 
     * @return  The PacketBuffer's state.
     */
    public int read()
    {
        switch(this.state)
        {
            // If the socket is disconnected, this PacketBuffer is no longer useful.
            case DISCONNECTED:
                break;
            // NO_DATA only informs the user that reading hasn't started
            // NO_DATA and READING_TYPE should do the same thing
            case NO_DATA:
                this.state = READING_TYPE;
            case READING_TYPE:
                // If the type wasn't read, break; otherwise change state and continue
                if(!readHeader())
                    break;
                
                // Allocate the packetData buffer to the correct size
                this.packetData = ByteBuffer.allocate(this.packetHeader.getInt(4) - Packet.HEADER_SIZE);
                this.state = READING_DATA;

            case READING_DATA:
                // If the data wasn't read, break; otherwise change state and continue
                if(!readData())
                    break;
                createPacket();
                this.state = FINISHED;
                
            case FINISHED:
                // Nothing else to do; just return
                break;
                
        }
        return this.state;
    }
    
    /**
     * Returns the packet contained in this PacketBuffer, if any.
     * 
     * Check either the return value of read or getState to determine whether
     * the 
     * @return 
     */
    public Packet getPacket()
    {
        return this.packet;
    }
    
    /**
     * Get the state of the PacketBuffer.
     * 
     * Reading 
     * @return 
     */
    public int getState()
    {
        return this.state;
    }
    
    /**
     * Reset the PacketBuffer's state to NO_DATA and clear everything.
     */
    public void clearState()
    {
        this.packetHeader.rewind();
        this.packetData = null;
        this.packet     = null;
        this.state      = NO_DATA;
    }
    
    /**
     * Attempts to read the type information.
     * 
     * @return True if all type information has been read; false otherwise.
     */
    private boolean readHeader()
    {
        try {
            if(this.socket.read(this.packetHeader) == -1)
                this.state = DISCONNECTED;
        } catch (IOException e) {
            System.out.println("PacketBuffer.readType: Problem during reading: " + e.getMessage());
        }

        return this.packetHeader.remaining() == 0;
    }
    
    /**
     * Attempts to read the packet's data.
     * 
     * @return True if the packet's data was completely read; false otherwise.
     */
    private boolean readData()
    {
        try {
            if(this.socket.read(this.packetData) == -1)
            {
                this.state = DISCONNECTED;
                return false;
            }
        } catch (IOException e) {
            System.err.println("PacketBuffer.readData: Problem during reading: " + e.getMessage());
            this.state = DISCONNECTED;
            return false;
        }
        
        return this.packetData.remaining() == 0;
    }
   
    /**
     * Creates a packet based on the type information received over the wire.
     */
    private void createPacket()
    {
        int type;
        
        this.packetHeader.rewind();
        this.packetData.rewind();
        type = this.packetHeader.getInt(0);
        
        switch(type)
        {
            case Packet.LOGIN:
                this.packet = new LoginPacket(this.packetHeader, this.packetData);
                break;
            case Packet.BOOT:
                this.packet = new BootPacket(this.packetHeader, this.packetData);
                break;
            case Packet.REQUESTACCESS:
                this.packet = new RequestAccessPacket(this.packetHeader, this.packetData);
                break;
            case Packet.JOINLEAVE:
                this.packet = new JoinLeavePacket(this.packetHeader, this.packetData);
                break;
            case Packet.MESSAGE:
                this.packet = new MessagePacket(this.packetHeader, this.packetData);
                break;
            case Packet.WHOISIN:
                this.packet = new WhoIsInPacket(this.packetHeader, this.packetData);
                break;
            case Packet.CHATLIST:
                this.packet = new ChatListPacket(this.packetHeader, this.packetData);
                break;
            case Packet.USERNOTIFY:
                this.packet = new UserNotifyPacket(this.packetHeader, this.packetData);
                break;    
            case Packet.CHATNOTIFY:
                this.packet = new ChatNotifyPacket(this.packetHeader, this.packetData);
                break;
            case Packet.CHANGEROLE:
                this.packet = new ChangeRolePacket(this.packetHeader, this.packetData);
                break;
            case Packet.UPDATECHAT:
                this.packet = new UpdateChatsPacket(this.packetHeader, this.packetData);
                break;
            case Packet.OPERATIONSTATUS:
                this.packet = new OperationStatusPacket(this.packetHeader, this.packetData);
                break;
            case Packet.LOGOUT:
                this.packet = new LogoutPacket(this.packetHeader, this.packetData);
                break;
            
        }
    }
}
