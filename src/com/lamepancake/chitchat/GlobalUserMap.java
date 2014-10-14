/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author shane
 */
public final class GlobalUserMap {
    private final Map<User, SelectionKey> userMap;
    private static GlobalUserMap instance = null;
    
    /**
     * Private constructor to disallow instantiation from outside of the class.
     */
    private GlobalUserMap()
    {
        userMap = new HashMap<>();
    }
    
    /**
     * Gets a reference to the GlobalUserMap.
     * @return A reference to the GlobalUserMap.
     */
    public static GlobalUserMap getInstance()
    {
        if(instance == null)
            instance = new GlobalUserMap();
        
        return instance;
    }
    
    /**
     * Adds a user/selection key pair to the user map.
     * 
     * @param u The user to add to the map.
     * @param k The selection key to associate with the user.
     */
    public void add(User u, SelectionKey k)
    {
        userMap.put(u, k);
    }
    
    /**
     * Removes a user/selection key pair from the user map.
     * 
     * @param u The user to add to the map.
     */
    public void remove(User u)
    {
        userMap.remove(u);
    }
    
    /**
     * Gets the selection key corresponding to a user.
     * @param u The user for which to get the key.
     * @return The key associated with u.
     */
    public SelectionKey getSelectionKey(User u)
    {
        return userMap.get(u);
    }
}
