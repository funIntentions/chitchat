/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

import com.lamepancake.chitchat.User;

/**
 *
 * @author shane
 */
public class BootPacket extends Packet{
    
    public BootPacket(User u)
    {
        super(0, 0);
    }
    
}
