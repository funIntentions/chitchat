/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

/**
 * Sent to all users in a particular chat when another user's status is updated.
 * 
 * When a user leaves, joins, is promoted or booted, this packet will be sent to
 * all other users listening to the chat for updates. A flag indicates which
 * operation took place.
 * 
 * Note that even users who are waiting to enter the chat (i.e., users with a
 * role of User.WAITING) will receive this packet if they are subscribed to the
 * chat for updates.
 * @author shane
 */
public class UserNotifyPacket extends Packet {
    
    public UserNotifyPacket()
    {
        super(USERNOTIFY, 0);
    }
}
