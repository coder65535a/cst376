package battleship;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author coder65535
 */
public class ServerIdentifier extends Thread
{

    public void run()
    {
        int id = 0;
        try {
            //need a server...
            DatagramSocket server = new DatagramSocket(7777);
            //buffer necessary
            byte[] buffer = new byte[1024];
            //read packet
            DatagramPacket in = new DatagramPacket(buffer, buffer.length);

            //wait for a packet
            while (true) {
                server.receive(in);
                //get string
                if (new String(in.getData()).trim().equals("FIND GAME SERVER")) {
                    in.setData("GAME SERVER HERE".getBytes());
                    server.send(in);
                }
            }
        } catch (SocketException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
        }

    }
}
