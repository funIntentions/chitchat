/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.mediator;
import com.lamepancake.chitchat.Client;
import com.lamepancake.chitchat.packet.*;
import com.lamepancake.chitchat.User;
import com.lamepancake.chitchat.Chat;
import com.lamepancake.chitchat.ClientGUI;
import java.util.*;

/**
 *
 * @author tware
 */
public class GUIMediator {
    
    Client client;
    ClientGUI gui;
    
    public GUIMediator(Client c)
    {
        client = c;
        gui = new ClientGUI(this);
    }
    
    public void receiveMessageFromGUI(String msg)
    {
        String[] splitmsg = msg.split("\\s+");
        
        if ((client.getUserRole() == User.ADMIN) && (splitmsg.length > 1) &&  splitmsg[0].equalsIgnoreCase("ADD"))
        {
            String recievingUserID = splitmsg[1];
            int id;
            int role;

            try
            {
                id = Integer.parseInt(recievingUserID);

                role = (splitmsg.length > 2) ? Integer.parseInt(splitmsg[2]) : User.USER;

            } 
            catch(Exception e)
            {
                gui.receiveFromMediator("Not a valid user ID: " + recievingUserID);
            }

            client.receiveFromMediator(new GrantAccessPacket(id, role, client.getChatID()));
        }
        else if((client.getUserRole() == User.ADMIN) && (splitmsg.length > 1) && splitmsg[0].equalsIgnoreCase("BOOT"))
        {
            String recievingUserID = splitmsg[1];
            int id;

            try 
            {
                id = Integer.parseInt(recievingUserID);

            } 
            catch(Exception e)
            {
                gui.receiveFromMediator("Not a valid user ID: " + recievingUserID);
            }

            client.receiveFromMediator(new GrantAccessPacket(id, User.UNSPEC, client.getChatID()));
        }
        else if((client.getUserRole() == User.ADMIN) && (splitmsg.length > 1) && splitmsg[0].equalsIgnoreCase("UPDATECHAT"))
        {
            String updateCommand = splitmsg[1];
            String name;
            int id;
            int update;

            try 
            {
                if (updateCommand.equalsIgnoreCase("CREATE"))
                {
                    name = splitmsg[2];
                    id = -1;
                    update = UpdateChatsPacket.CREATE;
                }
                else if (updateCommand.equalsIgnoreCase("UPDATE"))
                {
                    id = Integer.parseInt(splitmsg[2]);
                    name = splitmsg[3];
                    update = UpdateChatsPacket.UPDATE;
                }
                else if (updateCommand.equalsIgnoreCase("REMOVE"))
                {
                    id = Integer.parseInt(splitmsg[2]);
                    name = "";
                    update = UpdateChatsPacket.REMOVE;
                }
                else
                {
                    gui.receiveFromMediator("Chat update command: " + updateCommand + " is not valid.");
                }
                
                client.receiveFromMediator(new UpdateChatsPacket(name, id, update));
            } 
            catch(Exception e) 
            {
                gui.receiveFromMediator("Not a valid chat update.");
            }
        }
        else if((splitmsg.length > 1) && splitmsg[0].equalsIgnoreCase("JOIN"))
        {
            String recievingChatID = splitmsg[1];
            int id;

            try 
            {
                id = Integer.parseInt(recievingChatID);

            } 
            catch(Exception e) 
            {
                gui.receiveFromMediator("Not a valid chat ID: " + recievingChatID);
            }
            
            client.receiveFromMediator(new JoinedPacket(client.getUserName(), client.getUserRole(), client.getUserID(), id));
        }
        else if(msg.equalsIgnoreCase("LOGOUT")) 
        {
            gui.receiveFromMediator("Logging out.");
            client.receiveFromMediator(new LogoutPacket(client.getChatID()));
        }
        else if(msg.equalsIgnoreCase("WHOISIN")) 
        {     
            client.receiveFromMediator(new WhoIsInPacket(WhoIsInPacket.WHOISIN));
        }
        else if(msg.equalsIgnoreCase("WAITINGLIST"))
        {
            client.receiveFromMediator(new WhoIsInPacket(WhoIsInPacket.WAITING));
        }
        else if(msg.equalsIgnoreCase("CHATLIST"))
        {
            client.receiveFromMediator(new ChatListPacket());
        }
        else if((splitmsg.length > 1) && splitmsg[0].equalsIgnoreCase("CHAT"))
        {
            if (!client.waiting())
            {
                String message = "";
                for(int i = 1; i < splitmsg.length; i++)
                {
                    message += splitmsg[i];
                }
                
                client.receiveFromMediator(new MessagePacket(message, client.getUserID(), client.getChatID()));
            }
            else
            {
                gui.receiveFromMediator("You'll need to be in the chat before you can send messages. ;)");
            }       
        }     
    }
    
    public void receivePacketFromClient(Packet p)
    {
        int type = p.getType();
        
        switch(type)
        {
            case Packet.MESSAGE:
                gui.receiveFromMediator(((MessagePacket)p).getMessage());
                break;
            case Packet.WHOISIN:
                WhoIsInPacket userList = (WhoIsInPacket)p;
                String users = "";
                if(userList.whichList() == WhoIsInPacket.WAITING)
                {
                    users += "[WAITING USERS]\n";
                    List<User> waitingUsers = userList.getUsers();
                    for(User u : waitingUsers)
                    {
                        users += '\t';
                        users += "Name: " + u.getName() + "; ID: " + u.getID() + "\n";
                    }
                    users += "[END]";
                }
                else
                {
                    users += "[USERS]\n";
                    for(User u : userList.getUsers())
                    {
                        users += '\t';
                        users += "Name: " + u.getName() + "; ID: " + u.getID() + "\n";
                    }
                    users += "[END]";
                }
                gui.receiveFromMediator(users);
                break;
            case Packet.JOINED:
                gui.receiveFromMediator("[ " + ((JoinedPacket)p).getUser().getName() + " has joined the chat ]");
                break;
            case Packet.LEFT:
                gui.receiveFromMediator("[ " + ((LeftPacket)p).getUserID()+ " has left the chat ]");
                break;
            case Packet.GRANTACCESS:
                int userRole = ((GrantAccessPacket)p).getUserRole();
                switch(userRole)
                {
                    case User.ADMIN:
                    case User.USER:
                    {
                        if (client.waiting())
                        {
                            gui.receiveFromMediator("Status Update : You've been added to the chat.");
                        }

                        gui.receiveFromMediator("Status Update : An admin has updated your role to " + (userRole == User.ADMIN ? "Scrum Master." : "Developer."));
                        break;
                    }
                    case User.UNSPEC:
                    {
                        gui.receiveFromMediator("Status Update : An admin has booted you from the chat.");
                        break;
                    }
                }
                break;
            case Packet.CHATLIST:
                ChatListPacket chatList = (ChatListPacket)p;
                String chats = "";
                
                chats += "[OPEN CHATS]";
                for(Chat c : chatList.getChats())
                {
                    chats += '\t';
                    chats += "Name: " + c.getName() + "; ID: " + c.getID();
                }
                chats += "[END]";
                gui.receiveFromMediator(chats);
                break;
        }
    }
    
}
