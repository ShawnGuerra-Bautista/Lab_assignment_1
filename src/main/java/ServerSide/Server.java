package ServerSide;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private boolean isDebugMessage;
    private int portNumber;
    private File path;

    public Server(boolean isDebugMessage, int portNumber, File path){
        this.isDebugMessage = isDebugMessage;
        this.portNumber = portNumber;
        this.path = path;
    }

    public void run(){
        ServerSocket server = null;
        try {
            server = new ServerSocket(portNumber);
        }catch(IOException e) {
            System.out.println(e);
        }

        System.out.println("Starting connection on port: " + portNumber);

        Socket clientSocket = null;
        while(true){
            try {
                clientSocket = server.accept();
            }catch(IOException e){
                System.out.println(e);
            }
            try {
                processRequest(clientSocket);
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    public void processRequest(Socket clientSocket){

    }
}
