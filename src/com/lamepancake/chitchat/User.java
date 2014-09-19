package com.lamepancake.chitchat;

/**
 *
 * @author shane
 */
public class User {
 
    public static final int ADMIN   = 0;
    public static final int USER    = 1;
    
    private String name;
    private String password;
    private int    role;
    
    /*
     * Included for NetBeans' happiness; this constructor does nothing.
     */
    public User(String name, String password)
    {
        this.name       = name;
        this.password   = password;
    }
    
    /**
     * Returns the user's name.
     * 
     * @return The user's name.
     */
    public String getName()
    {
        return this.name;
    }
}
