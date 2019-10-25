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
            System.out.println(e);
        }

        if(isDebugMessage){
            System.out.println("Starting connection on port: " + portNumber);
        }

        Socket clientSocket;
        while(true){
            try {
                clientSocket = server.accept();
                processRequest(clientSocket);
                clientSocket.close();
            }catch(IOException e){
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
            while((currentCharacter = requestReader.read()) != -1 && requestReader.ready()){
                request.append((char)currentCharacter);
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
            System.out.println(e);
        }
    }

    //Locates & creates/overwrite files
    public String executingRequest(String header, String body) {

        String requestStatus = "";
        String[] separatedHeader = header.split("\r\n");
        String[] operationInfo = separatedHeader[0].split(" ");

        try {
            if(operationInfo[0].toLowerCase().equals("get")){

                if(!filePath.exists()){
                    requestStatus = statusCodes(404, "HTTP/1.0");
                }

                if(filePath.isDirectory()){
                    requestStatus = listFilesInDirectory();
                    requestStatus = statusCodes(200, "HTTP/1.0");
                }else{
                    requestStatus = readFile();
                    requestStatus = statusCodes(200, "HTTP/1.0");
                }

            }else if(operationInfo[0].toLowerCase().equals("post")){

                if(!filePath.exists()){
                    if(filePath.getParentFile().mkdirs() && filePath.createNewFile()){
                        writeFile(body);
                        requestStatus = statusCodes(201, "HTTP/1.0");
                    }else{
                        requestStatus = statusCodes(403, "HTTP/1.0");
                    }
                }else if(filePath.exists() && !filePath.isDirectory()){
                    writeFile(body);
                    requestStatus = statusCodes(200, "HTTP/1.0");
                }else{
                    requestStatus = statusCodes(403, "HTTP/1.0");
                }

            }else{
                requestStatus = statusCodes(400, "HTTP/1.0");
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return requestStatus;
    }

    //Use the printwriter to output a response
    public void processResponse(Socket clientSocket, String requestStatus, String header, String body){
        try {
            BufferedWriter responseWriter = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            String response = responseOutput(requestStatus, header, body);

            responseWriter.write(response);
            responseWriter.flush();
            responseWriter.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    //Creates the response
    public String responseOutput(String requestStatus, String header, String body){
        if(requestStatus.contains("400") || requestStatus.contains("403") || requestStatus.contains("404")){
            return requestStatus;
        }else{
            StringBuilder response = new StringBuilder();
            response.append(requestStatus).append("\r\n");
            response.append(header).append("\r\n\r\n");
            response.append(body);
            return response.toString();
        }
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
            System.out.println(e);
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
            System.out.println(e);
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
