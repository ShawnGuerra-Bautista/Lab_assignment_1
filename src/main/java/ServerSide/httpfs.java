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

    public static void main(String[] args) throws IOException{

        try( ServerSocket server = new ServerSocket(serverPort) ){
            System.out.println("Server has been instantiated at port " + serverPort);

            while(true){

                try ( Socket client_connection = server.accept() )
                {
                    PrintWriter outbount_client = new PrintWriter(client_connection.getOutputStream(), true);
                    outbount_client.println("Well hello to you too.");
                    client_connection.close();
                }
            }
        }
    }
}
