/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import java.nio.ByteBuffer;

/**
 *
 * @author shane
 */
public class LogoutPacket extends Packet{
    
    public LogoutPacket()
    {
        super(LOGOUT, 0);
    }
    
    @Override
    public ByteBuffer serialise()
    {
        return super.serialise();
    }
    
}
