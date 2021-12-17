/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.remote;

import com.client.ui.home;
import com.dbLayer.databaseCon;
import com.mysql.cj.jdbc.PreparedStatementWrapper;
import java.awt.HeadlessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * 
 * @author Sangeeth Nawarathna
 * folowed senior student's youtube video to get and idea about RMI coding
 * https://www.youtube.com/watch?v=eiQq_wQrw84&t=170s
 */
public class RemoteImplement extends UnicastRemoteObject implements RemoteInterface{

    public RemoteImplement() throws RemoteException, NotBoundException{
       super();
    }
    
    Connection conn = databaseCon.databaseConnection();
    
    /**
     * This is the implementation for user signup method. 
     * @param name set name for player class object.
     * @param username set username for player class object.
     * @param password set password for player class object.
     * @throws RemoteException 
     */
    @Override
    public void newPlayer(String name, String username, String password) throws RemoteException {
        
        String res;
        String sql = "INSERT INTO `user`(`name`,`username`, `password`) VALUES (?,?,?)";
        
        try { 
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            
            
            ps.execute();
            res = ("Profile created");
            
        } catch (SQLException ex) {
            System.out.println("SQLEXception "+ex);
            res = "SQL Exception occured";
        }
    }

    
    /**
     * This method is the implementation of user login method.
     * @param name store name for database check
     * @param Password store password for database check
     * @return true if user is available in the database 
     * @throws RemoteException 
     */
    @Override
    public Boolean login(String name, String Password) throws RemoteException {
        Boolean login = false;
        String sql = "SELECT uid,username,password FROM `user` WHERE `username`='"+name+"' AND `password`='"+Password+"'";
        
        PreparedStatement ps;
        ResultSet rs;
        
        try {
            ps =conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            if(rs.next()){
                login = true;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException "+ex);
        }catch(HeadlessException e){
            System.out.println("HeadlessException "+e);
        }
        return login;
        }

  

    /**
     * This method is to store players score in the database.
     * @param score store final score of the player
     * @param uname store username of the player.
     * @throws RemoteException 
     */
    @Override
    public void setScore(int score, String uname) throws RemoteException {
        String res;
        String sql = "update user set score = ? where username ='"+uname+"'";
        
        try { 
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, Integer.toString(score));
            ps.setInt(1, score);
 
            ps.execute();
            res = ("Score Updated!");
            
        } catch (SQLException ex) {
            System.out.println("SQLEXception "+ex);
            res = "SQL Exception occured";
        }
    }

  

  

 
 
    
}
