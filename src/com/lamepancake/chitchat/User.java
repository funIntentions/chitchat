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
    public User()
    {
    }
    
    /**
     * 
     * @param name
     * @param password 
     */
    public void setCredentials(String name, String password)
    {
        this.name       = name;
        this.password   = password;
        
        // Determine the user's role
    }
    
    
    public String getName()
    {
        return name;
    }
}
