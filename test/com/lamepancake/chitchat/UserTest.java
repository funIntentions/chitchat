/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamepancake.chitchat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan
 */
public class UserTest {
    
    public UserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class User.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        User instance = new User();
        String expResult = "";
        String result = instance.getName();
        
        if (!expResult.equals(result))
        {
            fail("Expected: " + expResult + ", but was: " + result);
        }
    }

    /**
     * Test of getRole method, of class User.
     */
    @Test
    public void testGetRole() {
        System.out.println("getRole");
        User instance = new User();
        int expResult = -1;
        int result = instance.getRole();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPassword method, of class User.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        User instance = new User();
        String expResult = "";
        String result = instance.getPassword();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class User.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        User instance = new User();
        String expResult = "";
        User result = instance.setName(name);
        
        if (!expResult.equals(result.getName()))
        {
            fail("Expected: " + expResult + ", but was: " + result);
        }
    }

    /**
     * Test of setRole method, of class User.
     */
    @Test
    public void testSetRole() {
        System.out.println("setRole");
        int newRole = 0;
        User instance = new User();
        int expResult = 0;
        User result = instance.setRole(newRole);
        assertEquals(expResult, result.getRole());
    }

    /**
     * Test of setPassword method, of class User.
     */
    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        String password = "";
        User instance = new User();
        String expResult = "";
        User result = instance.setPassword(password);
        assertEquals(expResult, result.getPassword());
    }

    /**
     * Test of setID method, of class User.
     */
    @Test
    public void testSetID() {
        System.out.println("setID");
        int id = 0;
        User instance = new User();
        int expResult = 0;
        User result = instance.setID(id);
        assertEquals(expResult, result.getID());
        
    }

    /**
     * Test of getID method, of class User.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        User instance = new User();
        int expResult = -1;
        int result = instance.getID();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class User.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        User instance = new User();
        String expResult = "";
        String result = instance.toString();
        
        if (!expResult.equals(result))
        {
            fail("expected: " + expResult + ", but was: " + result);
        }
    }

    /**
     * Test of restrict method, of class User.
     */
    @Test
    public void testRestrict() {
        System.out.println("restrict");
        int userRole = 1;
        int minRole = 0;
        boolean expResult = false;
        boolean result = User.restrict(userRole, minRole);
        assertEquals(expResult, result);
    }
    
}
