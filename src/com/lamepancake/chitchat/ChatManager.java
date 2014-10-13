/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import com.lamepancake.chitchat.mediator.Event;

/**
 *
 * @author shane
 */
public class ChatManager 
{
    public void HandleEvent(Event e)
    {
        int type = e.getType();
        
        switch(type)
        {
            case Event.LOGIN:
                break;
            case Event.LOGOUT:
                break;
            case Event.MESSAGE:
                break;
            case Event.JOIN:
                break;
            case Event.LEAVE:
                break;
            case Event.GRANT:
                break;
            default:
                System.out.println("Unknown event " + type);
        }
    }
}
