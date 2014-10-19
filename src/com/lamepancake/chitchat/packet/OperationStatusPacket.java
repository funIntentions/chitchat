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
     * 1 if success, 0 if failure.
     */
    private final int flag;
    
    /**
     * The ID of the user who initiated the operation.
     */
    private final int userID;
    
    /**
     * The operation which for which this packet contains the status.
     */
    private final int operation;
    
    /**
     * Creates a new OperationStatusPacket with the specified user ID, flag and
     * operation.
     * @param userID    The ID of the user who initiated the operation.
     * @param flag      Flag indicating the success/failure of the operation.
     * @param operation The operation that the user attempted.
     */
    public OperationStatusPacket(int userID, int flag, int operation)
    {
        super(OPERATIONSTATUS, 12);
        this.flag = flag;
        this.operation = operation;
        this.userID = userID;
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
        this.flag = packetData.getInt();
        this.operation = packetData.getInt();
    }
    
    @Override
    public ByteBuffer serialise()
    {
        ByteBuffer buf = super.serialise();
        buf.putInt(userID);
        buf.putInt(this.flag);
        buf.putInt(operation);
        
        buf.rewind();
        
        return buf;
    }
    
    /**
     * Gets the flag to know if it was a success or not.
     * @return the flag to know if it is a success or not.
     */
    public int getStatus()
    {
        return flag;
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
}