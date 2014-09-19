package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author shane
 */

public class MessagePacket extends Packet {

	private String message;
	
	// constructor
	public MessagePacket(String message) {
            super(Packet.MESSAGE, message.length() * 2);
            this.message = message;
	}

        /**
         * Constructor for data received over the wire.
         * 
         * @param data A ByteBuffer containing data sent over the wire.
         */
        public MessagePacket(ByteBuffer data)
        {
            super(Packet.MESSAGE, data.capacity());
            this.message = new String(data.array(), StandardCharsets.UTF_16LE);
        }
        
        @Override
        public ByteBuffer serialise()
        {
            ByteBuffer buf = super.serialise();
            buf.put(this.message.getBytes(StandardCharsets.UTF_16LE), 0, this.message.length() * 2);
            
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
}

