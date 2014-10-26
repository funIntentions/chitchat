package com.lamepancake.chitchat;

/**
 * Represents a chat user.
 * 
 * <b>IMPORTANT NOTE:</b> The role with the least privileges in a chat is that
 * with the highest number. This rule must be observed when creating new roles
 * as the restrict() method relies on it.
 * 
 * @author shane
 */
public class User {

    public static final int UNSPEC  = Integer.MAX_VALUE;
    public static final int ADMIN   = 0;
    public static final int USER    = 1;
    public static final int WAITING = 2;
    
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
    
        
    /**
     * Gets the user's password, if set.
     * 
     * Note that the password will almost never be set on client-side user
     * objects.
     * 
     * @return The password, if set.
     */
    public String getPassword()
    {
        return this.password;
    }
    
    /**
     * Sets the user's name.
     * 
     * @param name The new name for this user.
     * @return This user.
     */
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
    
    /**
     * Determines whether the user has the minimum required role in a given chat.
     * 
     * @param userRole The user's role in the given chat.
     * @param minRole  The minimum required role for the privilege in the chat.
     * @return 
     */
    public static boolean restrict(final int userRole, final int minRole)
    {
        return (minRole - userRole) >= 0;
    }
}
