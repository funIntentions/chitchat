package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author shane
 */

public class MessagePacket extends Packet {

	public String message;
	
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
            buf.put(this.message.getBytes(StandardCharsets.UTF_16), 0, this.message.length() * 2);
            
            return buf;
        }
        
	String getMessage() {
		return message;
	}
}

