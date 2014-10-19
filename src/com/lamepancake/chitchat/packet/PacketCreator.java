/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;
import java.util.Map;

/**
 *
 * @author shane
 */
public class PacketCreator {
    private PacketCreator()
    {}
    
    /**
     * Creates a WhoIsInPacket from the given information.
     * 
     * @param chatUsers The users in the chat and their online statuses.
     * @param chatID    The ID of the chat containing the users.
     * @param userID    The ID of the user requesting the packet.
     * @return          The WhoIsInPacket to be transmitted to the client.
     */
    public static WhoIsInPacket createWhoIsIn(Map<User, Boolean> chatUsers, int chatID, int userID)
    {
        // Each boolean in the map is one byte, so set the length to this size initially
        int dataLen = chatUsers.size();
        final WhoIsInPacket ret;

        // Also make space for the chatID, number of users, and userID, which are all ints
        dataLen += 12;
        
        for(User u : chatUsers.keySet())
        {
            // Strings are encoded in UTF_16LE, so make space for the length * 2
            dataLen += u.getName().length() * 2;
            
            // Add space for the user's userID, the username length and the role
            dataLen += 12;
        }
        
        ret = new WhoIsInPacket(chatUsers, dataLen, chatID, userID);
        return ret;
    }
}
