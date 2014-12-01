/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author coder65535
 */
public class GameManager extends Thread
{

    Socket p1;
    Socket p2;
    Scanner p1in;
    Scanner p2in;
    OutputStream p1out;
    OutputStream p2out;
    private int[] sunk;

    GameManager(Socket p1, Socket p2) throws IOException
    {
        sunk = new int[3];
        this.p1 = p1;
        this.p2 = p2;
        p1in = new Scanner(p1.getInputStream());
        p2in = new Scanner(p2.getInputStream());
        p1out = p1.getOutputStream();
        p2out = p2.getOutputStream();
//        p1.setSoTimeout(60000);//1 min timeout, before keep-alive message
//        p2.setSoTimeout(60000);//1 min timeout, before keep-alive message
    }

    public void run()
    {
        try {
            p1out.write(("GAME START\r\n"
                         + p2in.nextLine() + "\r\n"
                         + "end\r\n").getBytes());
            p2out.write(("GAME START\r\n"
                         + p1in.nextLine() + "\r\n"
                         + "end\r\n").getBytes());
            int player = 1;
            while (playTurn(player)) {
                player %= 2;
                player++;
            }
        } catch (IOException ex) {
        }
    }

    public boolean playTurn(int player)
    {
        try {
            Scanner playerIn;
            Scanner opponentIn;
            OutputStream playerOut;
            OutputStream opponentOut;
            if (player == 1) {
                playerIn = p1in;
                opponentIn = p2in;
                playerOut = p1out;
                opponentOut = p2out;
            } else {
                playerIn = p2in;
                opponentIn = p1in;
                playerOut = p2out;
                opponentOut = p1out;
            }
            playerOut.write(("START TURN\r\n"
                             + "end\r\n").getBytes());
            boolean cont = true;
            while (cont) {
                while (!playerIn.hasNextLine()); //wait for input

                String in = playerIn.nextLine();
                if (!in.equals("SHOOT") && !in.equals("QUIT")) {
                    playerOut.write(("SYNC ERROR\r\n"
                                     + "end\r\n").getBytes());
                }
                String out = "RECEIVED SHOT\r\n"
                             + playerIn.nextLine() + "\r\n"
                             + playerIn.nextLine() + "\r\n"
                             + "end\r\n";
                opponentOut.write(out.getBytes());
                in = opponentIn.nextLine();
                switch (in)
                {
                    case "HIT":
                        out = "HIT";
                        opponentIn.nextLine();
                        break;
                    case "MISS":
                        out = "MISS";
                        opponentIn.nextLine();
                        break;
                    case "SINK":
                        sunk[player]++;
                        out = "SINK";
                        in = opponentIn.nextLine();
                        while (!in.equals("end"))
                        {
                            out += "\r\n" + in;
                            in = opponentIn.nextLine();
                        }
                        break;
                    case "QUIT":
                        out = "GAME END\r\n"
                        + "disconnect\r\n"
                        + "end\r\n";
                        break;
                    default:
                        opponentOut.write(("UNRECOGNISED COMMAND\r\n").getBytes());
                        continue;
                }
                out += "\r\n"
                + "end\r\n";
                playerOut.write(out.getBytes());
                if (sunk[player] == 5)
                {
                    playerOut.write(("GAME END\r\n"
                            + "win\r\n"
                            + "end\r\n").getBytes());
                    opponentOut.write(("GAME END\r\n"
                            + "lose\r\n"
                            + "end\r\n").getBytes());
                    return false;
                }
                cont = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

}
