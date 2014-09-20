package com.lamepancake.chitchat;

/**
 *
 * @author shane
 */
public class User {
 
    public static final int UNSPEC  = -1;
    public static final int ADMIN   = 0;
    public static final int USER    = 1;
    
    private String name;
    private String password;
    private int    role;
    private int    id;
    
    /**
     * Constructs a new user with the specified name, ID and role.
     * 
     * @param name   The user's name.
     * @param userID The user's ID.
     * @param role   The user's role.
     */
    public User(String name, int role, int userID)
    {
        this(name, null, role, userID);
    }
    
    /**
     * Constructs a user with the specified name, password and ID.
     *
     * @param name      The user's name.
     * @param password  The user's password.
     * @param userID    The user's ID within the chat.
     */
    public User(String name, String password, int userID)
    {
        this(name, password, -1, userID);
    }
    
    public User(String name, String password, int role, int userID)
    {
        this.name       = name;
        this.password   = password;
        this.id         = userID;
        
        //validating admin role to username
        if(name.compareToIgnoreCase("Admin") == 0)
        {
            this.role   = 0;
        }
        else
        {
            this.role   = role;
        }
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
    
    /**
     * Gets the user's role.
     * 
     * @return The user's role.
     */
    public int getRole()
    {
        return this.role;
    }
    
    /**
     * Gets the user's ID.
     * 
     * @return The user's ID.
     */
    public int getID()
    {
        return this.id;
    }
    
    @Override
    public String toString()
    {
        String roleString;
        switch(this.role)
        {
            case ADMIN:
                roleString = "[admin]";
                break;
            case USER:
                roleString = "[user]";
                break;
            case UNSPEC:
            default:
                roleString = null;
        }
        
        return this.name + (roleString == null ? "" : " " + roleString);
    }
}
