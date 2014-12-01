/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author mruth, bkeyser
 */
public class Server extends Thread
{

    private static volatile int port = 5000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new GameStarter().start();
        try {
            //open server socket to listen here on 4004
            ServerSocket server = new ServerSocket(4004);
            System.out.println("Server started on port:" + server.getLocalPort());
            //do server loop
            while (true) {
                //wait for client to connect
                Socket client = server.accept();
                //here create a new thread...
                new Server(client).start();
            }

        } catch (Exception e) {
        }
    }

    private final Socket client;

    public Server(Socket client)
    {
        this.client = client;
    }

    public String generateOutput() //change this method to change output.
    {
        //pick a random string from outputs
        return "";
    }

    public void run()
    {

        try {
            Scanner reader = new Scanner(client.getInputStream());
            if (reader.nextLine().equals("FIND GAME")) {
                GameStarter.players.add(client);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
