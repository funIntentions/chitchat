package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.User;
import java.nio.ByteBuffer;

/**
 * Changes a user's role within a chat.
 * 
 * Sent by an admin to change a given user's role in a chat. The admin uses this
 * packet to accept users waiting to join the chat (i.e., their role is
 * User.WAITING) and to promote or demote them within the chat. Once a user has
 * been accepted into the chat, they shouldn't be demoted back to User.WAITING;
 * use a BootPacket instead.
 * 
 * This packet will be sent to the user having their role changed to inform them
 * of the change. All other users in the chat will receive a UserNotifyPacket
 * indicating the change. The database association between this user and the
 * chat will also be updated. Note that users will receive this packet
 * regardless of whether they're subscribed to updates from that chat.
 * 
 * @author shane
 */
public class ChangeRolePacket extends Packet {
    
    /**
     * The 
     * @param chat
     * @param user
     * @param role 
     */
    public ChangeRolePacket(Chat chat, User user, int role)
    {
        super(CHANGEROLE, 0);
    }

    public ChangeRolePacket(ByteBuffer packetHeader, ByteBuffer packetData) {
        super(CHANGEROLE, 0);
    }
    
    
}
