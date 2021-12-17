/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.remote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import javax.swing.JOptionPane;

/**
 * 
 * @author Sangeeth Nawarathna
 */
public class RemoteServer {
    public static void main(String args[]) throws RemoteException, NotBoundException{
    
        
        RemoteImplement service = new RemoteImplement();


        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RemoteServer", service);
            System.out.println("Server is Now runnig...\n");
        } catch (ExportException ex) {
            JOptionPane.showMessageDialog(null, "Connection error!");
            Registry registry = LocateRegistry.createRegistry(1099);
            registry = LocateRegistry.getRegistry(1099);
            System.out.println("Server is Now runnig...\n");
        }
        
    }
}
