package com.lamepancake.chitchat;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
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

