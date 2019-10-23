package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Create HTTP Server
 *
 */

public class httpfs
{
    static int serverPort = 8080;

    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(serverPort);
        }catch(IOException e) {
            System.out.println(e);
        }

        //Accept requests
        Socket client;
        while(true){
            //Accept Requests
            try {
                client = server.accept();
            }catch(IOException e){
                System.out.println(e);
            }
        }
    }
}
