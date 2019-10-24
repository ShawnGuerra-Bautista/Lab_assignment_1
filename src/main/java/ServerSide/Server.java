package ServerSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

    //Reads the request
    public void processRequest(Socket clientSocket) {

        StringBuilder request = new StringBuilder();
        String header = "";
        String body = "";
        try {
            BufferedReader requestReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            int currentCharacter;
            while((currentCharacter = requestReader.read()) != -1){
                request.append(currentCharacter);
            }

            String[] headerAndBody = request.toString().split("\r\n\r\n");
            header = headerAndBody[0];
            if(headerAndBody.length > 1){
                body = headerAndBody[1];
            }

            String requestStatus = executingRequest(header, body);
            processResponse(clientSocket, requestStatus, header, body);

            requestReader.close();
        } catch (IOException e) {

        }
    }

    //Locates & creates/overwrite files
    public String executingRequest(String header, String body){


        String[] separatedHeader = header.split("\r\n");
        String[] operationInfo = separatedHeader[0].split(" ");

        //If GET
            //If directory, then return all files
            //If file, return the content
        //If post
            //Create or overwrite content of files

        return null;
    }

    //Use the printwriter to output a response
    public void processResponse(Socket clientSocket, String requestStatus, String header, String body){
        try {
            BufferedWriter responseWriter = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            String response = responseOutput();

            responseWriter.flush();
            responseWriter.close();
        }catch(IOException e){
            System.out.println(e);
        }

    }

    //Creates the response
    public String responseOutput(){
        return null;
    }

    //List of a ll status codes
    private String statusCodes(int code, String httpVersion){
        String status = "";
        switch(code){
            case 200:
                status = httpVersion + " 200: OK";
                break;
            case 201:
                status = httpVersion + " 201: Created";
                break;
            case 400:
                status = httpVersion + " 400: Bad Request";
                break;
            case 403:
                status = httpVersion + " 403: Forbidden";
                break;
            case 404:
                status = httpVersion + " 404: Not Found";
                break;
        }
        return status;
    }
}
