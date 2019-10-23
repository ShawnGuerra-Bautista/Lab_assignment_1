package ServerSide;

import java.io.*;
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

    public void processRequest(Socket clientSocket) {

        try {
            BufferedWriter requestWriter = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String httpResponseHeader;

            String httpResponseBody;

            httpResponseHeader = "HTTP/1.0 200 OK\r\n\r\n";

            //clientSocket.getOutputStream().write();
        } catch (IOException e) { }
    }
}
