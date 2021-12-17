/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.ui;

import com.dbLayer.databaseCon;
import com.server.model.player;
import com.server.remote.RemoteInterface;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Sangeeth Nawarathna
 */
public class home extends javax.swing.JFrame {

    com.server.model.player Player;
    Registry regi = null;

    String gameNUm, loggerUser;
    String no;

    //variables for store user inputs as integers and score
    int score = 0, one, two, three, four, five, six, num, side1, side2, side3;

    //variables for count attempts, round and levels. Total 3 attempts, 3 levels and 15 rounds.
    int attempt = 3, level = 1, round = 0;
//    int timeStartLevel1 = 20;

    //variables for counter and calculate final score
    int timeCounter, minCounter, timeReamain, scorePass, finalScore;
    String remainingTime, scoretext;

    //variables for user input
    String inpOne, inpTwo, inpThree, inpFour, inpFive, inpSix;

    static int scoreToEnd = 0;

    /**
     * Creates new form home
     */
    public home() {
        initComponents();

        Player = new player();

        lblScore.setText("");
        txtOne.setHorizontalAlignment(JTextField.CENTER);
        txtTwo.setHorizontalAlignment(JTextField.CENTER);
        txtThree.setHorizontalAlignment(JTextField.CENTER);
        txtFour.setHorizontalAlignment(JTextField.CENTER);
        txtFive.setHorizontalAlignment(JTextField.CENTER);
        txtSix.setHorizontalAlignment(JTextField.CENTER);
        String logger = login.loggingUser();
        lblUserName.setText(logger);
        lblScore.setText("0");
        setTime();
        lblTimer.setText("");
        setMax();
        number();

    }

    /**
     * This method is to count down. Time starts with 5 minutes and 60 seconds.
     * That means player has 6 minutes for whole game. Seconds starts with 60
     * and down to 0. Minutes starts with 5. If seconds reach 00, minutes will
     * reduce by 1 and seconds will set to 60 again. if minutes reach 0, system
     * will redirect to end automatically.
     */
    public void setTime() {
        Timer time = new Timer();
        timeCounter = 60;
        minCounter = 5;
        lblMins.setText(Integer.toString(minCounter));

        TimerTask tsk = new TimerTask() {
            @Override
            public void run() {
                if(timeCounter%2==0){
                lblTimer.setForeground(new java.awt.Color(255, 255, 255));
                }else{
                    lblTimer.setForeground(new java.awt.Color(255, 0, 51));
                }
                timeCounter--;
                lblTimer.setText(Integer.toString(timeCounter));

                if (timeCounter == 0) {
                    minCounter--;
                    timeCounter = 60;
                    lblMins.setText(Integer.toString(minCounter));
                } else if (minCounter == -1) {
                    time.cancel();
//                    new Instructions().setVisible(true);
                    finalStage();
                }
            }

        };
        time.scheduleAtFixedRate(tsk, 1000, 1000);

    }

  

    /**
     * This method will run if user finish the game. Calculate the final score
     * and pass the score to updateScore()
     *
     * @param calScore get the user scored score
     * @param calMins get the remain time
     * @return final score by calScore * calMins, result will store in
     * finalScore
     */
    public int calScore(int calScore, int calMins) {
        int total;
        total = calScore * calMins;
        return total;
    }

    /**
     * This method is call the setScore() method in RMIInterface. finalsScore
     * and the player's username will pass in to the setScore() method.
     */
    public void updateScore() {
        String userName = lblUserName.getText();

        Player.setScore(finalScore);

        try {
            RemoteInterface inter = (RemoteInterface) Naming.lookup("rmi://localhost:1099/RemoteServer");

            inter.setScore(finalScore, userName);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Exception throw/n " + ex);
        }

    }

    
    /**
     * This is to proceed final stage of the game. If player finish the game by complete all the round or because of use all the attempts, this method will run.
     * calScore() and updateScore() methods will run in this method.
     */
    public void finalStage() {
        remainingTime = lblMins.getText();
        scoretext = lblScore.getText();
        timeReamain = Integer.parseInt(remainingTime);
        scorePass = Integer.parseInt(scoretext);
        finalScore = calScore(scorePass, timeReamain);
        scoreToEnd = finalScore;
        updateScore();
        this.setVisible(false);
        new GameOver().setVisible(true);
    }

    /**
     * This will select top 5 players from the database table and show in the
     * table in main interface.
     */
    public void setMax() {
        databaseCon con = new databaseCon();
        try {
            String sql = "select name, max(score) from user group by score desc limit 5";
            PreparedStatement prep = con.databaseConnection().prepareStatement(sql);
            ResultSet rs = prep.executeQuery();
            tblPlayers.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException ex) {
            Logger.getLogger(databaseCon.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    /**
     * This will generate random number from the external web API and that
     * number will show in the game. System will pass minimum and maximum value
     * to the APi and, API will return randomly generated number between passed
     * minimum and maximum values.
     */
    public void number() {
        txtOne.requestFocus();
        int min1 = 10, max1 = 99;
        int min2 = 100, max2 = 999;
        int min3 = 1000, max3 = 5000;
        String pr = null;
        URL url;

        if (level == 1) {
            try {
                lblLevel.setText("1");
                url = new URL("https://csrng.net/csrng/csrng.php?min=" + min1 + "&max=" + max1 + "");
                InputStream inputStream = url.openStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                // StandardCharsets.UTF_8.name() > JDK 7
                pr = result.toString("UTF-8");
                gameNUm = result.toString("UTF-8");
                no = gameNUm.substring(48, 50);
                //JOptionPane.showMessageDialog(null,pr);
                System.out.println(result.toString("UTF-8"));

            } catch (Exception e) {
                System.out.println("An Error occur ed: " + e.toString());
                e.printStackTrace();
            }
        } else if (level == 2) {
            try {
                lblLevel.setText("2");
                url = new URL("https://csrng.net/csrng/csrng.php?min=" + min2 + "&max=" + max2 + "");
                InputStream inputStream = url.openStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                // StandardCharsets.UTF_8.name() > JDK 7
                pr = result.toString("UTF-8");
                gameNUm = result.toString("UTF-8");
                no = gameNUm.substring(50, 53);
                //JOptionPane.showMessageDialog(null,pr);
                System.out.println(result.toString("UTF-8"));
                ;
            } catch (Exception e) {
                System.out.println("An Error occur ed: " + e.toString());
                e.printStackTrace();
            }
        } else if (level == 3) {
            try {
                lblLevel.setText("3");
                url = new URL("https://csrng.net/csrng/csrng.php?min=" + min3 + "&max=" + max3 + "");
                InputStream inputStream = url.openStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                // StandardCharsets.UTF_8.name() > JDK 7
                pr = result.toString("UTF-8");
                gameNUm = result.toString("UTF-8");
                no = gameNUm.substring(52, 56);
                //JOptionPane.showMessageDialog(null,pr);
                System.out.println(result.toString("UTF-8"));

            } catch (Exception e) {
                System.out.println("An Error occur ed: " + e.toString());
                e.printStackTrace();
            }

        }

        lblNumber.setText(no);
        //JOptionPane.showMessageDialog(null, gameNUm);
        num = Integer.parseInt(no);
        System.out.println(num);

        //return pr;
    }

    /**
     * This method is to take user inputs and validate the answers of the user.
     */
    public void userInputs() {
        //take user inputs as strings and convert into integers
        inpOne = txtOne.getText();
        one = Integer.parseInt(inpOne.trim());
        System.out.println(one);

        inpTwo = txtTwo.getText();
        two = Integer.parseInt(inpTwo.trim());
        System.out.println(two);

        inpThree = txtThree.getText();
        three = Integer.parseInt(inpThree.trim());
        System.out.println(three);

        inpFour = txtFour.getText();
        four = Integer.parseInt(inpFour);
        System.out.println(four);

        inpFive = txtFive.getText();
        five = Integer.parseInt(inpFive);
        System.out.println(five);

        inpSix = txtSix.getText();
        six = Integer.parseInt(inpSix);
        System.out.println(six);

        //checking the sum of each side and validate
        if ((two != four) && (three != six) && (four != six)) {
            side1 = one + three + six;
            side2 = one + two + four;
            side3 = four + five + six;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHello = new javax.swing.JLabel();
        lblMins = new javax.swing.JLabel();
        lblDot = new javax.swing.JLabel();
        lblTimer = new javax.swing.JLabel();
        tblPlayers = new javax.swing.JTable();
        txtThree = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        txtOne = new javax.swing.JTextField();
        txtFive = new javax.swing.JTextField();
        txtTwo = new javax.swing.JTextField();
        txtSix = new javax.swing.JTextField();
        txtFour = new javax.swing.JTextField();
        lblNumber = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblScore = new javax.swing.JLabel();
        lblLevel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHello.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 1, 18)); // NOI18N
        lblHello.setForeground(new java.awt.Color(255, 0, 51));
        lblHello.setText("Hello");
        getContentPane().add(lblHello, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 20, -1, -1));

        lblMins.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblMins.setForeground(new java.awt.Color(255, 0, 51));
        lblMins.setText("0");
        getContentPane().add(lblMins, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        lblDot.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblDot.setForeground(new java.awt.Color(255, 0, 51));
        lblDot.setText(":");
        getContentPane().add(lblDot, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 9, 10, 50));

        lblTimer.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTimer.setForeground(new java.awt.Color(255, 0, 51));
        lblTimer.setText("00");
        getContentPane().add(lblTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 40, -1));

        tblPlayers.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tblPlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Player", "Score"
            }
        ));
        getContentPane().add(tblPlayers, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 180, 200, 90));

        txtThree.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtThree.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtThreeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtThreeFocusLost(evt);
            }
        });
        txtThree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtThreeKeyPressed(evt);
            }
        });
        getContentPane().add(txtThree, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 240, 120, 40));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Level: ");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 90, -1, -1));

        btnNext.setText("NEXT");
        btnNext.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btnNextMouseMoved(evt);
            }
        });
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        getContentPane().add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 530, 120, 30));

        txtOne.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtOne.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOneFocusLost(evt);
            }
        });
        txtOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOneActionPerformed(evt);
            }
        });
        txtOne.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtOneKeyPressed(evt);
            }
        });
        getContentPane().add(txtOne, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, 120, 40));

        txtFive.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtFive.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFiveFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFiveFocusLost(evt);
            }
        });
        txtFive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFiveKeyPressed(evt);
            }
        });
        getContentPane().add(txtFive, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 450, 120, 40));

        txtTwo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtTwo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTwoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTwoFocusLost(evt);
            }
        });
        txtTwo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTwoKeyPressed(evt);
            }
        });
        getContentPane().add(txtTwo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 240, 120, 40));

        txtSix.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSix.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSixFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSixFocusLost(evt);
            }
        });
        txtSix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSixKeyPressed(evt);
            }
        });
        getContentPane().add(txtSix, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 450, 120, 40));

        txtFour.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtFour.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFourFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFourFocusLost(evt);
            }
        });
        txtFour.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFourKeyPressed(evt);
            }
        });
        getContentPane().add(txtFour, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 450, 120, 40));

        lblNumber.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblNumber.setForeground(new java.awt.Color(255, 0, 51));
        lblNumber.setText("0000");
        getContentPane().add(lblNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 300, 50, 20));

        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newback-01.jpg"))); // NOI18N
        back.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                backMouseMoved(evt);
            }
        });
        getContentPane().add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 600));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Score: ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 50, -1, -1));

        lblScore.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblScore.setText("jLabel2");
        getContentPane().add(lblScore, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 50, -1, -1));

        lblLevel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblLevel.setText("1");
        getContentPane().add(lblLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("Scoreboard");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 130, -1, -1));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 136, 70, 20));

        lblUserName.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 1, 18)); // NOI18N
        lblUserName.setForeground(new java.awt.Color(255, 0, 51));
        lblUserName.setText("jLabel5");
        getContentPane().add(lblUserName, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 20, -1, -1));

        btnExit.setText("Exit");
        btnExit.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btnExitMouseMoved(evt);
            }
        });
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 550, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtOneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOneKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            txtTwo.requestFocus();
        }
    }//GEN-LAST:event_txtOneKeyPressed

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtTwoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTwoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            txtOne.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            txtThree.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            txtFour.requestFocus();
        }
    }//GEN-LAST:event_txtTwoKeyPressed

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtThreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtThreeKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            txtTwo.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            txtOne.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            txtSix.requestFocus();
        }
    }//GEN-LAST:event_txtThreeKeyPressed

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtFourKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFourKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            txtTwo.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            txtFive.requestFocus();
        }
    }//GEN-LAST:event_txtFourKeyPressed

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtFiveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiveKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            txtFour.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            txtSix.requestFocus();
        }
    }//GEN-LAST:event_txtFiveKeyPressed

    /**
     * User can use arrow keys to move around text fields of the games.
     * @param evt Key Event
     */
    private void txtSixKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSixKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            txtFive.requestFocus();
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            txtThree.requestFocus();
        }
    }//GEN-LAST:event_txtSixKeyPressed

    private void txtOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOneActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtOneActionPerformed

    /**
     * This method will run when player click next button after fill all the text fields.
     * This checks whether the validation of the answers in userInput() is the same as the random number generated by the external web API.
     * Attempts and levels are calculated in this method.
     * finalStage() will call if user finish all the attempts or all the rounds.
     * @param evt Action event
     */
    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        userInputs();

        //compare to the given value to the total of the each side. If total of the each side equals to given number, then player can move to next number, else player must enter correct value
        if (round < 15) {
            if ((num == side1) && (num == side2) && (num == side3) && (attempt >= 1)) {
                round++;
                score = score + 5;
                lblScore.setText(String.valueOf(score));
                System.out.println("Round :" + round);
                if (round % 5 == 0) {
                    level++;
                    System.out.println("Level :" + level);

                }

                number();
                txtOne.setText("");
                txtTwo.setText("");
                txtThree.setText("");
                txtFour.setText("");
                txtFive.setText("");
                txtSix.setText("");

            } else {
                
                attempt--;
                JOptionPane.showMessageDialog(null, "Chechk agin! You only have "+ attempt +" attempts left!");
                System.out.println(attempt);
                if (attempt < 1) {

                    finalStage();
                }

            }
        } else if (round == 15) {

            finalStage();
        }


    }//GEN-LAST:event_btnNextActionPerformed

    /**
     * If user click on EXIT button while playing the game, system will prompt warning message.
     * If user clicks yes system will close, else if user clicks no, user can continue the game
     * @param evt Action Event
     */
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        int dialogButton = JOptionPane.showConfirmDialog(null, "Are you sure? Score didn't save yet!", "WARNING", JOptionPane.YES_NO_OPTION);

        if (dialogButton == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnNextMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNextMouseMoved
        // TODO add your handling code here:
        btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNext.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_btnNextMouseMoved

    private void backMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseMoved
        // TODO add your handling code here:
        
        btnNext.setBackground(new java.awt.Color(240,240,240));
        
    }//GEN-LAST:event_backMouseMoved

    private void txtOneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOneFocusGained
        // TODO add your handling code here:
         txtOne.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtOneFocusGained

    private void txtTwoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTwoFocusGained
        // TODO add your handling code here:
        txtTwo.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtTwoFocusGained

    private void txtThreeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtThreeFocusGained
        // TODO add your handling code here:
        txtThree.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtThreeFocusGained

    private void txtFourFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFourFocusGained
        // TODO add your handling code here:
        txtFour.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtFourFocusGained

    private void txtFiveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFiveFocusGained
        // TODO add your handling code here:
        txtFive.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtFiveFocusGained

    private void txtSixFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSixFocusGained
        // TODO add your handling code here:
        txtSix.setBackground(new java.awt.Color(51, 255, 153));
    }//GEN-LAST:event_txtSixFocusGained

    private void txtOneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOneFocusLost
        // TODO add your handling code here:
        txtOne.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtOneFocusLost

    private void txtTwoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTwoFocusLost
        // TODO add your handling code here:
        txtTwo.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtTwoFocusLost

    private void txtThreeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtThreeFocusLost
        // TODO add your handling code here:
        txtThree.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtThreeFocusLost

    private void txtFourFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFourFocusLost
        // TODO add your handling code here:
        txtFour.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtFourFocusLost

    private void txtFiveFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFiveFocusLost
        // TODO add your handling code here:
        txtFive.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtFiveFocusLost

    private void txtSixFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSixFocusLost
        // TODO add your handling code here:
        txtSix.setBackground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_txtSixFocusLost

    private void btnExitMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExitMouseMoved
        // TODO add your handling code here:
        btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       
    }//GEN-LAST:event_btnExitMouseMoved

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
 
    }//GEN-LAST:event_formMouseMoved

    /**
     * This method will pass final score to the next interface.
     * @return finsl score
     */
    public static int scoreToEnd() {

        int finalScorrPass;

        finalScorrPass = scoreToEnd;

        return finalScorrPass;

    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnNext;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblDot;
    private javax.swing.JLabel lblHello;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblMins;
    private javax.swing.JLabel lblNumber;
    private javax.swing.JLabel lblScore;
    private javax.swing.JLabel lblTimer;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JTable tblPlayers;
    private javax.swing.JTextField txtFive;
    private javax.swing.JTextField txtFour;
    private javax.swing.JTextField txtOne;
    private javax.swing.JTextField txtSix;
    private javax.swing.JTextField txtThree;
    private javax.swing.JTextField txtTwo;
    // End of variables declaration//GEN-END:variables
}
