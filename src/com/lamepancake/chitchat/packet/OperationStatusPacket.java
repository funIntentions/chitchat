/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.packet;

/**
 * Sent to indicate the success or failure of specific operation.
 * 
 * This packet will be sent in response to login and database operations.
 * 
 * @author shane
 */
public class OperationStatusPacket extends Packet
{
    public OperationStatusPacket()
    {
        super(OPERATIONSTATUS, 0);
    }
}
