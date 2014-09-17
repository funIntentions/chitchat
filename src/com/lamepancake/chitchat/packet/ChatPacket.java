package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author shane
 */

public class ChatPacket extends Packet {

	public String message;
	
	// constructor
	public ChatPacket(String message) {
            super(Packet.MESSAGE, message.length() * 2);
            this.message = message;
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

