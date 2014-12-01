/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import battleship.Server.Player;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 *
 * @author coder65535
 */
public class GameStarter extends Thread
{

    static final ArrayDeque<Player> players = new ArrayDeque();

    public void run()
    {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                //Won't happen
            }
            while (players.size() >= 2) {
                try {
                    new GameManager(players.remove(), players.remove()).start();
                    System.out.println("start");
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}
