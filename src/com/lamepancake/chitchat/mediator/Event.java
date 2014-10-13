/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.mediator;

/**
 * Allows the PacketTranslator to notify chats of specific events.
 * @author shane
 */
public class Event {
    public static final int LOGIN = 0;
    public static final int LOGOUT = 1;
    public static final int MESSAGE = 2;
    public static final int JOIN = 3;
    public static final int LEAVE = 4;
    public static final int GRANT = 5;
    public static final int CHATLIST = 6;
    public static final int WHOISIN = 7;
    public static final int WAITINGLIST = 8;
    
    private final int type;
    private final Object arg;
    
    public Event(int eventType, Object eventArg)
    {
        type = eventType;
        arg = eventArg;
    }
    
    public int getType()
    {
        return type;
    }
    
    public Object getArg()
    {
        return arg;
    }
}
