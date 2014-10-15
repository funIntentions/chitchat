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
     * Constructs an empty User.
     * 
     * Note that you'll have to use the set methods (can be chained) to construct
     * the object.
     */
    public User()
    {}
       
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
    
    public User setName(String name)
    {
        this.name = name;
        return this;
    }
    
    /**
     * 
     * @param newRole 
     * @return
     */
    public User setRole(int newRole)
    {
        if(newRole > USER)
                throw new IllegalArgumentException("role must be -1 <= role <= 1, was " + role);
        
        this.role = newRole;
        return this;
    }
    
    /**
     * Sets the user's password.
     * @param password The new password to set.
     * @return This User.
     */
    public User setPassword(String password)
    {
        this.password = password;
        return this;
    }
    
    /**
     * Sets the user's ID.
     * @param id The new ID to assign to the User.
     * @return 
     */
    public User setID(int id)
    {
        this.id = id;
        return this;
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
                roleString = "[scrum master]";
                break;
            case USER:
                roleString = "[developer]";
                break;
            case UNSPEC:
                roleString = "[scoundrel]";
                break;
            default:
                roleString = null;
        }
        
        return this.name + (roleString == null ? "" : " " + roleString);
    }
}
