/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Avci
 */
public class GamePage extends javax.swing.JFrame
{

    public GamePage me;
    public String serverIP;
    public volatile boolean playerTurn;
    public volatile EnemySpace target;
    public boolean gameEnd;

    private static Socket findServer()
    {
        try {
            String toSend = "FIND GAME SERVER";
            //get a byte array
            byte[] buffer = toSend.getBytes();

            InetAddress addy = InetAddress.getByName("255.255.255.255");

            //make a packet...
            DatagramPacket out = new DatagramPacket(buffer, buffer.length, addy, 7777);

            DatagramSocket client = new DatagramSocket();

            client.send(out);
            client.receive(out);
            String in = new String(out.getData());
            //id = Integer.parseInt(in.substring(18, in.length()));
            return new Socket(out.getAddress().toString().substring(1), 4004);

        } catch (UnknownHostException ex) {
            System.out.println(ex);
        } catch (SocketException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Creates new form GamePage
     */
    public GamePage(Socket server) throws IOException
    {
        me = this;
        gameEnd = false;
        int[] hits = {5, 4, 3, 3, 2};
        initComponents();
        PlayerSpace[][] a = new PlayerSpace[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                a[i][j] = new PlayerSpace();
                a[i][j].setBackground(new java.awt.Color(0, 204, 255));
                jPanel1.add(a[i][j]);
                a[i][j].addMouseListener(new java.awt.event.MouseAdapter()
                {
                    public void mouseClicked(java.awt.event.MouseEvent evt)
                    {
                        PlayerSpace space = (PlayerSpace) evt.getComponent();
                        me.player1GameFieldClick(space.x, space.y);
                    }
                });
                a[i][j].x = j;
                a[i][j].y = i;
            }
        }
        EnemySpace[][] e = new EnemySpace[11][11];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                e[i][j] = new EnemySpace();
                e[i][j].setBackground(Color.GREEN);
                jPanel2.add(e[i][j]);
                e[i][j].addMouseListener(new java.awt.event.MouseAdapter()
                {
                    public void mouseClicked(java.awt.event.MouseEvent evt)
                    {
                        EnemySpace space = (EnemySpace) evt.getComponent();
                        me.player2GameFieldClick(space);
                    }
                });
                e[i][j].x = j;
                e[i][j].y = i;
            }
        }
        Color ship1 = new Color(0,0,0);
        Color ship2 = new Color(51,51,51);
        Color ship3 = new Color(102,102,102);
        Color ship4 = new Color(153,153,153);
        Color ship5 = new Color(204,204,204);
        a[2][3].setBackground(ship1);
        a[2][4].setBackground(ship1);
        a[2][5].setBackground(ship1);
        a[2][6].setBackground(ship1);
        a[2][7].setBackground(ship1);
        a[4][5].setBackground(ship2);
        a[5][5].setBackground(ship2);
        a[6][5].setBackground(ship2);
        a[7][5].setBackground(ship2);
        a[7][7].setBackground(ship3);
        a[7][8].setBackground(ship3);
        a[7][9].setBackground(ship3);
        a[1][7].setBackground(ship4);
        a[1][8].setBackground(ship4);
        a[1][9].setBackground(ship4);
        a[10][9].setBackground(ship5);
        a[10][10].setBackground(ship5);
        Scanner serverIn = new Scanner(server.getInputStream());
        OutputStream serverOut = server.getOutputStream();

        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                if (gameEnd || JOptionPane.showConfirmDialog(me,
                                                             "Are you sure to close this window?", "Really Closing?",
                                                             JOptionPane.YES_NO_OPTION,
                                                             JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    try {
                        serverOut.write(("QUIT\r\n"
                                         + "end\r\n").getBytes());
                        System.out.println("write");
                        System.exit(0);
                    } catch (IOException ex) {
                        Logger.getLogger(GamePage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try {
                    me.setVisible(true);
                    System.out.println("aaa");
                    new Thread()
                    {
                        public void run()
                        {
                            while (!serverIn.hasNextLine()); //wait for input
                            System.out.println(serverIn.nextLine());
                            String oppName = serverIn.nextLine();
                            System.out.println(oppName);
                            serverIn.nextLine();

                            while (true) {
                                while (!serverIn.hasNextLine()); //wait for input
                                String in = serverIn.nextLine();

                                if (!(in.equals("GAME END") || in.equals("START TURN") || in.equals("RECEIVED SHOT"))) {
                                    try {
                                        serverOut.write(("SYNC ERROR\r\n"
                                                         + "end\r\n").getBytes());
                                        continue;
                                    } catch (IOException ex) {
                                        Logger.getLogger(GamePage.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                if (in.equals("GAME END")) {
                                    String end = serverIn.nextLine();
                                    if (end.equals("win")) {
                                        jTextArea1.append("YOU WON!\r\n");
                                    } else if (end.equals("loss")) {
                                        jTextArea1.append("You lost...\r\n");
                                    } else {
                                        jTextArea1.append("They quit, so you won!\r\n");
                                    }
                                    gameEnd = true;
                                    while (true);
                                } else if (in.equals("START TURN")) {
                                    serverIn.nextLine();
                                    try {
                                        jTextArea1.append("Your turn!\r\n");
                                        playerTurn = true;
                                        synchronized (me) {
                                            while (target == null) {
                                                try {
                                                    me.wait();
                                                } catch (InterruptedException ex) {
                                                }
                                            }
                                        }
                                        target.setBackground(Color.CYAN);
                                        serverOut.write(("SHOOT\r\n"
                                                         + target.x + "\r\n"
                                                         + target.y + "\r\n"
                                                         + "end\r\n").getBytes());
                                        while (!serverIn.hasNextLine()); //wait for input
                                        in = serverIn.nextLine();
                                        System.out.println(in);
                                        switch (in) {
                                            case "HIT":
                                                target.setBackground(Color.red);
                                                jTextArea1.append("Hit!\r\n");
                                                break;
                                            case "MISS":
                                                target.setBackground(Color.WHITE);
                                                jTextArea1.append("Miss...\r\n");
                                                break;
                                            case "SINK":
                                                target.setBackground(Color.red);
                                                jTextArea1.append("SINK!\r\n");
                                                break;
                                            case "GAME END":
                                                String end = serverIn.nextLine();
                                                if (end.equals("win")) {
                                                    jTextArea1.append("YOU WON!\r\n");
                                                } else if (end.equals("loss")) {
                                                    jTextArea1.append("You lost...\r\n");
                                                } else {
                                                    jTextArea1.append("They quit, so you won!\r\n");
                                                }
                                                gameEnd = true;
                                                while (true);
                                            default:

                                        }
                                        target = null;
                                    } catch (IOException ex) {
                                        Logger.getLogger(GamePage.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    int x = serverIn.nextInt();
                                    serverIn.nextLine();
                                    int y = serverIn.nextInt();
                                    serverIn.nextLine();
                                    PlayerSpace space = a[y][x];
                                    int type = (space.getBackground().getBlue() / 51);
                                    String out = "";
                                    if (type == 5) {
                                        out = "MISS";
                                        space.setBackground(Color.white);
                                        jTextArea1.append("They missed!\r\n");
                                    } else {
                                        space.setBackground(Color.red);
                                        hits[type]--;
                                        if (hits[type] == 0) {
                                            out = "SINK";
                                            jTextArea1.append("They sunk our ship!\r\n");
                                        } else {
                                            out = "HIT";
                                            jTextArea1.append("They hit!\r\n");
                                        }
                                    }
                                    out += "\r\n"
                                           + "end\r\n";
                                    try {
                                        serverOut.write(out.getBytes());
                                    } catch (IOException ex) {
                                        Logger.getLogger(GamePage.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                while (!serverIn.nextLine().equals("end"));

                            }
                        }
                    }.
                            start();
                } catch (Exception ex) {
                }
            }
        }
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Send");

        jPanel1.setBackground(new java.awt.Color(102, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(230, 230));
        jPanel1.setLayout(new java.awt.GridLayout(11, 11, 1, 1));

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setPreferredSize(new java.awt.Dimension(230, 230));
        jPanel2.setVerifyInputWhenFocusTarget(false);
        jPanel2.setLayout(new java.awt.GridLayout(11, 11, 1, 1));

        jLabel1.setText("Player 1");

        jLabel2.setText("Player 2");

        jLabel3.setBackground(new java.awt.Color(102, 102, 255));
        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 255));
        jLabel3.setText("Next");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 255));
        jLabel4.setText("Next");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(86, 86, 86)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(127, 127, 127)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(89, 89, 89)
                                .addComponent(jLabel4))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException
    {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException |
                 javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GamePage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /*
         * Create and display the form
         */
        Socket server = findServer();
        //</editor-fold>
        server.getOutputStream().write(("FIND GAME\r\n"
                                        + "player" + "\r\n").getBytes());
        new GamePage(server);
        /*
         * Create and display the form
         */
    }

    private void player1GameFieldClick(int x, int y)
    {
        jTextArea1.append("" + x + y);
    }

    private void player2GameFieldClick(EnemySpace space)
    {
        synchronized (me) {
            System.out.println("click");
            if (playerTurn) {
                target = space;
                me.notifyAll();
            }
        }
    }

    private class GameSpace extends JPanel
    {

        public int x;
        public int y;
    }

    private class PlayerSpace extends GameSpace
    {

    }

    private class EnemySpace extends GameSpace
    {

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
