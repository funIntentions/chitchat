/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author shane
 */
public abstract class MySQLDAOBase {
    /**
     * The default username for logging into the database.
     * This will be used if getInstance is called before init.
     */
    public static String DEFAULT_UNAME = "root";

    /**
     * The default username for logging into the database.
     * This will be used if getInstance is called before init.
     */
    public static String DEFAULT_PASS = "";

    protected final Connection con;
    protected       Statement  query;
    protected       ResultSet  queryResults;

    protected MySQLDAOBase(final String username, final String password, final String initTable) throws SQLException
    {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chitchat", username, password);
        } catch(SQLException se) {
            System.err.println("MySQLDAOBase constructor: could not get database connnection: " + se.getMessage());
            throw(se);
        }
       
        try {
            query = con.createStatement();
            queryResults = query.executeQuery("USE chitchat");
        } catch (SQLException se) {
            System.err.println("MySQLDAOBase constructor: could not use chitchat database.");
            throw(se);
        }
        
        try {
            query.execute(initTable);
        } catch (SQLException se) {
            System.err.println("MySQLDAOBase constructor: could not initialise table.");
            throw se;
        }
    }
}
