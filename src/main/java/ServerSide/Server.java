package ServerSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private boolean isDebugMessage;
    private int portNumber;
    private File filePath;

    public Server(boolean isDebugMessage, int portNumber, File filePath){
        this.isDebugMessage = isDebugMessage;
        this.portNumber = portNumber;
        this.filePath = filePath;
    }

    public void run(){

        ServerSocket server = null;
        try {
            server = new ServerSocket(portNumber);
        }catch(IOException e) {
            System.out.println(statusCodes(400, "HTTP/1.0"));
        }

        if(isDebugMessage){
            System.out.println("Starting connection on port: " + portNumber);
        }

        Socket clientSocket;
        while(true){
            try {
                clientSocket = server.accept();
                processRequest(clientSocket);
            }catch(IOException e){
                System.out.println(statusCodes(400, "HTTP/1.0"));
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

            if(isDebugMessage){
                System.out.println("Request content:\n" + request);
            }

            String[] headerAndBody = request.toString().split("\r\n\r\n");
            header = headerAndBody[0];
            if(headerAndBody.length > 1){
                body = headerAndBody[1];
            }

            if(isDebugMessage){
                System.out.println("header:\n" + header);
                System.out.println("Body:\n" + body);
            }

            String requestStatus = executingRequest(header, body);

            if(isDebugMessage){
                System.out.println("Request Status:\n" + requestStatus);
            }

            processResponse(clientSocket, requestStatus, header, body);

            requestReader.close();
        } catch (IOException e) {
            System.out.println(statusCodes(400, "HTTP/1.0"));
        }
    }

    //TODO
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

            responseWriter.write(response);
            responseWriter.flush();
            responseWriter.close();
        }catch(IOException e){
            System.out.println(statusCodes(400, "HTTP/1.0"));
        }
    }

    //TODO
    //Creates the response
    public String responseOutput(){
        return null;
    }

    //List all the files if it is a directory
    public String listFilesInDirectory(){
        File[] listOfFilesArray = filePath.listFiles();
        StringBuilder listOfFiles = new StringBuilder();
        for (File file : listOfFilesArray) {
            listOfFiles.append(file).append("\n");
        }
        return listOfFiles.toString();
    }

    //read the file content
    public String readFile(){
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String currentLine;
            while ((currentLine = fileReader.readLine()) != null) {
                content.append(currentLine);
            }
        } catch (IOException e){
            System.out.println(statusCodes(400, "HTTP/1.0"));
        }
        return content.toString();
    }

    //Write the content/body to the file
    public void writeFile(String content) {
        try {
            PrintWriter fileWriter = new PrintWriter(filePath);
            fileWriter.write(content);
            fileWriter.close();
        } catch(FileNotFoundException e){
            System.out.println(statusCodes(404, "HTTP/1.0"));
        }

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
