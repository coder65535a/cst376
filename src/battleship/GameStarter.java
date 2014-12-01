/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author coder65535
 */
public class GameStarter extends Thread{
    static final ArrayDeque<Socket> players = new ArrayDeque();

    
    public void run(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            //Won't happen
        }
        while(players.size() >= 2)
        {
            try {
                new GameManager(players.remove(), players.remove()).start();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
}
