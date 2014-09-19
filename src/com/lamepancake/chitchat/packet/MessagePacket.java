package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author shane
 */

public class MessagePacket extends Packet {

	private final String message;
        private int          userID;
	
        
        public MessagePacket(String message)
        {
            this(message, -1);
        }

	// constructor
	public MessagePacket(String message, int userID) {
            super(Packet.MESSAGE, 4 + message.length() * 2);
            this.message = message;
            this.userID = userID;
	}

        /**
         * Constructor for data received over the wire.
         * 
         * @param data A ByteBuffer containing data sent over the wire.
         */
        public MessagePacket(ByteBuffer data)
        {
            super(Packet.MESSAGE, data.capacity());
            byte[] rawMessage = new byte[data.capacity() - 4];
            this.userID = data.getInt();
            data.get(rawMessage);
            this.message = new String(rawMessage, StandardCharsets.UTF_16LE);
        }
        
        @Override
        public ByteBuffer serialise()
        {
            ByteBuffer buf = super.serialise();
            buf.putInt(this.userID);
            buf.put(this.message.getBytes(StandardCharsets.UTF_16LE));
            buf.rewind();
            
            return buf;
        }
        
        /**
         * Get the message contained in this packet.
         * @return The message contained in this packet.
         */
	public String getMessage() {
		return message;
	}
        
        /**
         * Returns the ID of the user sending the message.
         * 
         * @return The ID of the user sending the message.
         */
        public int getUserID()
        {
            return this.userID;
        }
        
        /**
         * Set the user ID in case it's not set.
         * 
         * @param newID The new user ID to assign to this message.
         */
        public void setUserID(int newID)
        {
            if(newID < 1)
                throw new IllegalArgumentException("MessagePacket.setUserID: newID must be >= 1, was " + newID);
            
            this.userID = newID;
        }
}

