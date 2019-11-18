package ServerSide;

import UDPPackage.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    private boolean isDebugMessage;
    private int portNumber;
    private String filePathName;

    // Router address
    private static final String routerHost = "localhost";
    private static final int routerPort = 3000;

    // Server address
    private static final String serverHost = "localhost";
    private static final int serverPort = 8007;

    // Client Address
    private static final String clientHost = "localhost";
    private static final int clientPort = 41830;

    public Server(boolean isDebugMessage, int portNumber, String filePathName){
        this.isDebugMessage = isDebugMessage;
        this.portNumber = portNumber;
        this.filePathName = filePathName;
    }

    //Listen and serve
    public void run(){

        DatagramSocket serverSocket = null;
        SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
        ByteBuffer receivingBytesBuffer = ByteBuffer
                .allocate(Packet.MAX_LEN)
                .order(ByteOrder.BIG_ENDIAN);
        try {
            serverSocket = new DatagramSocket(serverPort);
        }catch(IOException e) {
            System.out.println(e);
        }

        if(isDebugMessage){
            System.out.println("Starting connection on port: " + portNumber);
        }

        DatagramPacket receivingDatagramPacket = null;
        try {
            while(true) {

                //Handshake
                threeWayHandshake(serverSocket, routerAddress);

                //Receiving the packet
                receivingBytesBuffer.clear();
                receivingDatagramPacket = new DatagramPacket(receivingBytesBuffer.array(),
                        receivingBytesBuffer.array().length);
                serverSocket.receive(receivingDatagramPacket);

                //Must specify the real length of packet
                receivingBytesBuffer.position(receivingDatagramPacket.getLength());

                //Parse the packet
                receivingBytesBuffer.flip();
                Packet receivedPacket = Packet.fromBuffer(receivingBytesBuffer);
                receivingBytesBuffer.flip();

                //Get Payload and process request
                String payload = new String(receivedPacket.getPayload(), UTF_8);
                String response = processRequest(payload);

                //To send a packet to the client just do this: (This echoes request)
                Packet responsePacket = receivedPacket.toBuilder()
                        .setPayload(response.getBytes())
                        .create();

                //Sending Datagram packets should have the Address and port number of receiver
                DatagramPacket sendingDatagramPacket = new DatagramPacket(responsePacket.toBytes(),
                        responsePacket.toBytes().length, routerAddress);
                serverSocket.send(sendingDatagramPacket);
            }
        }catch(Exception e){
            System.out.println(e);
        }finally {
            serverSocket.close();
        }
    }

    //Reads the request
    public String processRequest(String payload) {

        String header = "";
        String body = "";
        if(isDebugMessage){
            System.out.println("Request content:\n" + payload);
        }

        String[] headerAndBody = payload.split("\r\n\r\n");
        header = headerAndBody[0];
        if(headerAndBody.length > 1){
            body = headerAndBody[1];
        }

        if(isDebugMessage){
            System.out.println("Header:\n" + header);
            System.out.println("Body:\n" + body);
        }

        String[] requestStatus = executingRequest(header, body);

        if(isDebugMessage){
            System.out.println("Request Status:\n" + requestStatus[0]);
            System.out.println("Request result:\n" + requestStatus[1]);
        }

        if(requestStatus[1] != null){
            body = requestStatus[1];
        }

        return processResponse(requestStatus[0], header, body);

    }

    //Locates & creates/overwrite files
    public String[] executingRequest(String header, String body) {

        //Changed from 3 to 2
        String[] requestStatus = new String[2];
        requestStatus[1] = null;
        String[] separatedHeader = header.split("\r\n");

        // 1 = path; 0 = post/get; 2 = version
        String[] operationInfo = separatedHeader[0].split(" ");

        File filePath = new File(filePathName + operationInfo[1]);

        if(filePath.toString().contains("/..")){
            requestStatus[0] = statusCodes(403, "HTTP/1.0");
            return requestStatus;
        }

        try {

            if(isDebugMessage){
                System.out.println("Accessing filepath: " + filePath.toString());
            }

            if(operationInfo[0].toLowerCase().equals("get")){

                if(!filePath.exists()){
                    requestStatus[0] = statusCodes(404, "HTTP/1.0");
                    return requestStatus;
                }

                if(filePath.isDirectory()){
                    requestStatus[1] = "List of Files:\n" + listFilesInDirectory(filePath);
                    requestStatus[0] = statusCodes(200, "HTTP/1.0");
                }else{
                    if(filePath.canRead()) {
                        requestStatus[1] = "Content of file:\n" + readFile(filePath);
                        requestStatus[0] = statusCodes(200, "HTTP/1.0");
                    }else{
                        requestStatus[0] = statusCodes(404, "HTTP/1.0");
                        return requestStatus;
                    }
                }

            }else if(operationInfo[0].toLowerCase().equals("post")){

                if(!filePath.exists()){
                    if(!filePath.getParentFile().exists() && filePath.getParentFile().mkdirs() && filePath.createNewFile()){
                        writeFile(filePath, body);
                        requestStatus[0] = statusCodes(201, "HTTP/1.0");
                    }else if(filePath.getParentFile().exists() && filePath.createNewFile()){
                        writeFile(filePath, body);
                        requestStatus[0] = statusCodes(201, "HTTP/1.0");
                    }else{
                        requestStatus[0] = statusCodes(403, "HTTP/1.0");
                    }
                }else if(filePath.exists() && !filePath.isDirectory() && !filePath.canWrite()){
                    writeFile(filePath, body);
                    requestStatus[0] = statusCodes(200, "HTTP/1.0");
                }else{
                    requestStatus[0] = statusCodes(403, "HTTP/1.0");
                }

            }else{
                requestStatus[0] = statusCodes(400, "HTTP/1.0");
            }
        } catch(Exception e){
            System.out.println(e);
        }

        return requestStatus;
    }

    //Generates response
    public String processResponse(String requestStatus, String header, String body){
        String response = responseOutput(requestStatus, header, body);
        return response;
    }

    //Creates the response
    public String responseOutput(String requestStatus, String header, String body){

        if(requestStatus.contains("400") || requestStatus.contains("403") || requestStatus.contains("404")){
            return requestStatus;
        }else{
            header = header.substring(header.indexOf('\n')+1);
            StringBuilder response = new StringBuilder();
            response.append(requestStatus).append("\r\n");
            response.append(header).append("\r\n\r\n");
            response.append(body);

            if(isDebugMessage){
                System.out.println("Response: " + response);
            }

            return response.toString();
        }
    }

    // 3-way handshake
    /*
        1. Receive SYN (seq = x)
        2. Send SYN_ACK (seq = x+1)
        3. Receive ACK (seq=y+1)
     */
    private void threeWayHandshake(DatagramSocket serverSocket, SocketAddress routerAddress) throws Exception{

        String synAck = "I acknowledge";

        DatagramPacket receiveAckPacket = null;
        DatagramPacket receiveSynPacket = null;
        ByteBuffer receivingBytesBuffer = ByteBuffer
                .allocate(Packet.MAX_LEN)
                .order(ByteOrder.BIG_ENDIAN);

        //================================RECEIVE=================================

        //Receive SYN
        receivingBytesBuffer.clear();
        receiveSynPacket = new DatagramPacket(receivingBytesBuffer.array(),
                receivingBytesBuffer.array().length);
        serverSocket.receive(receiveSynPacket);

        //Must specify the real length of the packet from the DatagramPacket
        receivingBytesBuffer.position(receiveSynPacket.getLength());

        //Parse the packet
        receivingBytesBuffer.flip();
        Packet synPacket = Packet.fromBuffer(receivingBytesBuffer);
        receivingBytesBuffer.flip();

        if(synPacket.getType() != Packet.SYN){
            throw new Exception("Not a syn from client");
        }

        System.out.println(new String(synPacket.getPayload(), UTF_8));

        //=============================SEND=================================

        // Create Packet(s) to send to the server
        Packet synAckPacket = synPacket.toBuilder()
                .setType(Packet.SYN_ACK)
                .setSequenceNumber(synPacket.getSequenceNumber() + 1L)
                .setPayload(synAck.getBytes())
                .create();

        // Send the packets in byte[] through the client socket
        DatagramPacket synAckDatagramPacket = new DatagramPacket(synAckPacket.toBytes(),
                synAckPacket.toBytes().length, routerAddress);
        serverSocket.send(synAckDatagramPacket);

        //=============================RECEIVE=================================

        //New bytesBuffer
        receivingBytesBuffer = ByteBuffer
                .allocate(Packet.MAX_LEN)
                .order(ByteOrder.BIG_ENDIAN);

        receivingBytesBuffer.clear();
        receiveAckPacket = new DatagramPacket(receivingBytesBuffer.array(),
                receivingBytesBuffer.array().length);
        serverSocket.receive(receiveAckPacket);

        //Must specify the real length of the packet from the DatagramPacket
        receivingBytesBuffer.position(receiveAckPacket.getLength());

        //Parse the packet
        receivingBytesBuffer.flip();
        Packet ackPacket = Packet.fromBuffer(receivingBytesBuffer);
        receivingBytesBuffer.flip();

        if(ackPacket.getType() != Packet.ACK){
            throw new Exception("Not an ack from client");
        }

        System.out.println(new String(ackPacket.getPayload(), UTF_8));
    }

    //List all the files if it is a directory
    public String listFilesInDirectory(File filePath){
        File[] listOfFilesArray = filePath.listFiles();
        StringBuilder listOfFiles = new StringBuilder();
        for (File file : listOfFilesArray) {
            listOfFiles.append(file.isDirectory() ? "Directory: " : "File: ").append(file.getName()).append("\n");
        }
        return listOfFiles.toString();
    }

    //read the file content
    public String readFile(File filePath){
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String currentLine;
            while ((currentLine = fileReader.readLine()) != null) {
                content.append(currentLine);
            }
            fileReader.close();
        } catch (IOException e){
            System.out.println(e);
        }
        return content.toString();
    }

    //Write the content/body to the file
    public void writeFile(File filePath, String content) {
        try {
            PrintWriter fileWriter = new PrintWriter(filePath);
            fileWriter.write(content);
            fileWriter.close();
        } catch(FileNotFoundException e){
            System.out.println(e);
        }

    }

    //List of status codes
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
