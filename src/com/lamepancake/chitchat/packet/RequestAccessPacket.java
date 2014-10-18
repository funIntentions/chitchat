package com.lamepancake.chitchat.packet;

/**
 * Requests access to a given chat.
 * 
 * Users send this packet to join a chat in which they do not yet have a role.
 * If the user is already waiting to enter this chat, a JoinLeavePacket should
 * be sent instead.
 * 
 * @author shane
 */
public class RequestAccessPacket extends Packet
{
    public RequestAccessPacket()
    {
        super(REQUESTACCESS, 0);
    }
}
