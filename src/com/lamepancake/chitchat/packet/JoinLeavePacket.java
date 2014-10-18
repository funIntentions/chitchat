/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

/**
 * Sent by a user attempting to join or leave a chat.
 * 
 * A flag in the packet indicates whether the user wants ot join or leave. To
 * "join" or "leave" a chat does not change the user's role in the chat. It only
 * subscribes or unsubscribes to/from a particular chat. On leaving, a user will
 * not receive chat messages or other information about the chat until they
 * rejoin.
 * 
 * If the user does not have a role in chat at all, the client will send a
 * RequestAccessPacket first. 
 * 
 * @author shane
 * @see RequestAccessPacket
 */
public class JoinLeavePacket extends Packet
{
    public JoinLeavePacket()
    {
        super(0, 0);
    }
    
}
