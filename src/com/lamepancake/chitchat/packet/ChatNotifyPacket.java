package com.lamepancake.chitchat.packet;

/**
 * Sent to all users in a chat to notify them of chat status changes.
 * 
 * This packet will primarily be sent for things such as chat name changes or
 * to warn users that a chat is about to be deleted. The packet will be sent to
 * all users regardless of whether they're subscribed to updates from that chat.
 * 
 * @author shane
 */
public class ChatNotifyPacket extends Packet 
{
    public ChatNotifyPacket()
    {
        super(CHATNOTIFY, 0);
    }
}
