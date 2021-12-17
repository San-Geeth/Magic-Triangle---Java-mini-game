/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author Sangeeth Nawarathna
 */
public interface RemoteInterface extends Remote{
    /**
     * Method for user signup. Method implementation is in RemoteImplement.java
     * @param name player's name
     * @param username player's username
     * @param password player's password
     * @throws RemoteException 
     */
    public void newPlayer(String name, String username, String password) throws RemoteException;
    
    
    /**
     * Method for user login. Method implementation is in RemoteImplement.java
     * @param name player's name
     * @param Password player's password
     * @return boolean
     * @throws RemoteException 
     */
    public Boolean login(String name, String Password) throws RemoteException;
    
    
    /**
     * This method is to set player's score in database. Method implementation is in RemoteImplement.java
     * @param score player's score
     * @param uname player's username
     * @throws RemoteException 
     */
    public void  setScore(int score, String uname) throws RemoteException;

}
