/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author shane
 */
public class LoginPacket extends Packet {
    
    private final String username;
    private final String password;
    
    /**
     * Creates a LoginPacket with specified username and password.
     * 
     * @param username The username with which to log in.
     * @param password The password with which to log in.
     */
    public LoginPacket(String username, String password)
    {
        // Char is 2 bytes wide, so multiply lengths by 2
        super(Packet.LOGIN, (username.length() * 2) + (password.length() * 2) + 2);
        this.username = username;
        this.password = password;
    }
    
    /**
     * Construct a login packet from data sent over the network.
     * 
     * @param data A ByteBuffer containing the transferred data.
     */
    public LoginPacket(ByteBuffer data)
    {
        super(Packet.LOGIN, data.capacity());
        
        byte[]  rawUsername;
        byte[]  rawPassword;
        byte[]  rawData = data.array();
        int     offset;
        
        // Find the null character and split the string there
        for(offset = 0; offset < data.capacity(); offset += 2)
        {
            if(data.getChar() == (char)0)
                break;
        }

        // Allocate two byte arrays to hold the username and password
        // The "- 2" in the rawPassword array is to compensate for the null character
        rawUsername = new byte[offset];
        rawPassword = new byte[data.capacity() - offset - 2];
        
        System.arraycopy(rawData, 0, rawUsername, 0, rawUsername.length);
        System.arraycopy(rawData, offset + 2, rawPassword, 0, rawPassword.length);
        
        this.username = new String(rawUsername, StandardCharsets.UTF_16LE);
        this.password = new String(rawPassword, StandardCharsets.UTF_16LE);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer  serialised  = super.serialise();
        
            // Write the raw bytes of the username string into the buffer starting at offset
            // Multiply this.username.length by 2 since chars are 2 bytes wide in Java
            serialised.put(this.username.getBytes(StandardCharsets.UTF_16LE), 0, this.username.length() * 2);

            // Add a null character to separate the username and password
            serialised.putChar((char)0);

            // Put the password into the buffer following the same process
            serialised.put(this.password.getBytes(StandardCharsets.UTF_16LE), 0, this.password.length() * 2);
        
        return serialised;
    }
    
    /**
     * Gets the username in this packet.
     * 
     * @return The username contained in this packet.
     */
    public String getUsername()
    {
        return this.username;
    }
    
    /**
     * Gets the password in this packet.
     * 
     * @return String the password contained in this packet.
     */
    public String getPassword()
    {
        return this.password;
    }
}
