package com.lamepancake.chitchat.packet;

import java.nio.ByteBuffer;

/**
 * Sent to indicate the success or failure of specific operations.
 * 
 * This packet will be sent in response to login and database operations.
 * 
 * @author Trevor
 */
public class OperationStatusPacket extends Packet
{
    /**
     * Packet indicates status of a login attempt.
     */
    public static final int OP_LOGIN = 0;
    
    /**
     * Packet indicates status of a database operation (create, read, update, delete).
     */
    public static final int OP_CRUD = 1;
    
    /**
     * Packet indicates status of a chat access request.
     */
    public static final int OP_REQACCESS = 2;
    
    /**
     * Flag for indicating failure.
     */
    public static final int FAIL = 0;
    
    /**
     * Flag for indicating success.
     */
    public static final int SUCCESS = 1;
    /**
     * 1 if success, 0 if failure.
     */
    private final int flag;
    
    /**
     * The ID of the user who initiated the operation.
     */
    private final int userID;
    
    /**
     * The relevant chat ID, if any, for this operation. 
     */
    private final int chatID;
    
    /**
     * The operation which for which this packet contains the status.
     */
    private final int operation;
    
    /**
     * The ID of the group that the chat belongs to.
     */
    private final int groupID;
    
    /**
     * Creates a new OperationStatusPacket with the specified user ID, flag and
     * operation.
     * @param userID    The ID of the user who initiated the operation.
     * @param flag      Flag indicating the success/failure of the operation.
     * @param operation The operation that the user attempted.
     */
    public OperationStatusPacket(int userID, int chatID, int flag, int operation, int groupID)
    {
        super(OPERATIONSTATUS, 20);
        this.userID = userID;
        this.chatID = chatID;
        this.flag = flag;
        this.operation = operation;
        this.groupID = groupID;
    }

    /**
     * Reconstructs a serialised OperationStatusPacket.
     * @param packetHeader The packet's header.
     * @param packetData   The data about the operation.
     */
    public OperationStatusPacket(ByteBuffer packetHeader, ByteBuffer packetData) 
    {
        super(packetHeader);
        this.userID = packetData.getInt();
        this.chatID = packetData.getInt();
        this.flag = packetData.getInt();
        this.operation = packetData.getInt();
        this.groupID = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(this.userID);
        buf.putInt(this.chatID);
        buf.putInt(this.flag);
        buf.putInt(this.operation);
        buf.putInt(this.groupID);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the flag to know if it was a success or not.
     * @return the flag to know if it is a success or not.
     */
    public int getStatus()
    {
        return this.flag;
    }
    
    /**
     * Gets the ID of the user who initiated the operation.
     * @return The ID of the user who initiated the operation. Note that this
     *         will be invalid for failed login operations.
     */
    public int getUserID()
    {
        return this.userID;
    }
    
    /**
     * Gets the operation for which this packet contains the status.
     * @return The operation for which this packet contains the status.
     */
    public int getOperation()
    {
        return this.operation;
    }
    
    /**
     * Gets the relevant chat ID for this operation.
     * @return The relevant chat ID for this operation.
     */
    public int getChatID()
    {
        return this.chatID;
    }
    
    /**
     * Get the group ID for this chat.
     * @return the id of the group.
     */
    public int getgroupID()
    {
        return this.groupID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(!super.equals(o))
            return false;
        
        if(o instanceof OperationStatusPacket)
        {
            OperationStatusPacket p = (OperationStatusPacket)o;
            if(userID != p.getUserID())
                return false;
            if(chatID != p.getChatID())
                return false;
            if(operation != p.getOperation())
                return false;
            if(groupID != p.getgroupID())
                return false;
            return flag == p.getStatus();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.flag;
        hash = 67 * hash + this.userID;
        hash = 67 * hash + this.chatID;
        hash = 67 * hash + this.operation;
        hash = 67 * hash + this.groupID;
        return hash;
    }
}