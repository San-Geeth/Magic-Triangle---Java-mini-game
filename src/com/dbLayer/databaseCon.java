/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * 
 * @author Sangeeth Nawarathna
 */
public class databaseCon {
    
    
    /**
     * This will create connection between database in XAMPP server.
     * @return connection
     */
    public static Connection databaseConnection(){

        Connection con;
        
        try{
        
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/triangle","root","");
            System.out.println("Connection Successfull...");
            return con;
            
        } 
        catch(ClassNotFoundException e)
        {
            System.out.println("ClassNotFoundException \n"+e);
            return null;
        }
        catch(SQLException e)
        {
            System.out.println("SQLException \n"+e);
            return null;
        }
        catch(NullPointerException ex){
            System.out.println("Null pointer Error \n"+ex);
            return null;
        }
        
    }

  
}
