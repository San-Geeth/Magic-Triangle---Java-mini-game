/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.server.model.Admin;
import com.dbLayer.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * @author Sangeeth Nawarathna
 */
public class connectionMethods {
    
    /**
     * This method is to check whether the user login details are available at the database.
     * @param user1 object of Admin
     * @return -1 if user login details are available at the database.
     */
     public int checkUser(Admin user1) {

        databaseCon con = new databaseCon();
        int retVal = -1;

        String sql = "SELECT * FROM serverAdmin WHERE UserName=? AND Password=?;";

        try {
            PreparedStatement prep = con.databaseConnection().prepareStatement(sql);
            prep.setString(1, user1.getUsername());// set values
            prep.setString(2, user1.getPassword());// set values

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                retVal = 1;// if user is there
            }

        } catch (SQLException ex) {
            Logger.getLogger(databaseCon.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return retVal;
    }
     
     
     /**
      * This method is to generate charts. Player and the Admin are users of this system and admin can view score statistics of players by this method.
      */
     public void quickChart(){
         databaseCon con = new databaseCon();
         
         String setA=null, setB=null,setC=null,setD=null,urll=null;
         BufferedImage img = null;
         
         /**
          * Sql to select players scored between 0 and 100. Result will store in setA as integer.
          */
         String sqlA ="Select count(score) from user where score between 0 and 100";
         try {
                PreparedStatement prep = con.databaseConnection().prepareStatement(sqlA);
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    setA = rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "ERROR:");

            }
         
         
         /**
          * Sql to select players scored between 0 and 100. Result will store in setB as integer.
          */
         String sqlB ="Select count(score) from user where score between 100 and 200";
         try {
                PreparedStatement prep = con.databaseConnection().prepareStatement(sqlB);
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    setB = rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "ERROR:");

            }
         
         
         /**
          * Sql to select players scored between 0 and 100. Result will store in setC as integer.
          */
         String sqlC ="Select count(score) from user where score between 200 and 300";
         try {
                PreparedStatement prep = con.databaseConnection().prepareStatement(sqlC);
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    setC = rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "ERROR:");

            }
        
         
         /**
          * Sql to select players scored between 0 and 100. Result will store in setD as integer.
          */
         String sqlD ="Select count(score) from user where score between 300 and 400";
         try {
                PreparedStatement prep = con.databaseConnection().prepareStatement(sqlD);
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    setD = rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(null, "ERROR:");

            }
         
        try {
                /**
                 * To the urll, system will pass integer values (setA, setB, setC, setD).
                 * Then the API will return rendered chart as an image according to passed values.
                 */
                urll = "https://quickchart.io/chart?bkg=white&c={ type: 'bar', data: { datasets: [ { data: [\'" + setA + "\',\'" + setB + "\', \'" + setC + "\',\'" + setD + "\',], backgroundColor: [ 'rgb(255, 100, 32)', 'rgb(255, 150, 164)', 'rgb(255, 205, 186)', 'rgb(75, 192, 180)', ], label: 'No._of_players_in_score_ranges', }, ], labels: ['50-150', '150-200', '200-250', '>250'], },}";

                urll = urll.replace(" ", "");

                URL url1 = new URL(urll);

                HttpURLConnection urlcon = (HttpURLConnection) url1.openConnection();

                urlcon.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                img = ImageIO.read(urlcon.getInputStream());

                JFrame frame = new JFrame();

                frame.setForeground(Color.WHITE);

                frame.setResizable(false);
                frame.setBackground(Color.WHITE);
                frame.setSize(1000, 850);
                JLabel label = new JLabel(new ImageIcon(img));
                frame.getContentPane().add(label);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Check Connection or try again later!");
                e1.printStackTrace();
            }
     
     }
     
     
}
