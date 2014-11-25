/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package battleship;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author mruth, bkeyser
 */
public class Server extends Thread {

    private static final String[] outputs = {"HIT","MISS"};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
    
    public Server(Socket client) {
        this.client = client;
    }
    
    public String generateOutput() //change this method to change output.
    {
        //pick a random string from outputs
        return outputs[new Random().nextInt(outputs.length)];
    }
    
    public void run() {
        
       try {
            
            //outwriter
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
            String s = generateOutput();
            out.write(s);
            out.flush();
            
            client.close();
        
            
        
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
}
