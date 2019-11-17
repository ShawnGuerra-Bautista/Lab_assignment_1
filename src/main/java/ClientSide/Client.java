package ClientSide;

import UDPPackage.Packet;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Client extends Request{

    private String rawUrl;
    private String host;
    private int port;
    private String location;
    private String method;

    // Router address
    private static final String routerHost = "localhost";
    private static final int routerPort = 3000;

    // Server address
    private static final String serverHost = "localhost";
    private static final int serverPort = 8007;

    // Client Address
    private static final String clientHost = "localhost";
    private static final int clientPort = 41830;

    public Client(String[] args, String method){
        super(args);
        getURL();
        this.method = method;
    }

    //Sends a POST request to the server
    public void execute() {

        //Create datagram socket
        DatagramSocket clientSocket = null;
        SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
        try {
            clientSocket = new DatagramSocket(clientPort);
        }catch(IOException e) {
            System.out.println(e);
        }

        //Gets URL information (Not really needed)
        try {
            //URL info of the server
            URL url = new URL(rawUrl);
            host = url.getHost();
            if((port = url.getPort()) == -1){
                port = url.getDefaultPort();
            }
            location = url.getPath();
        }catch(MalformedURLException e) {
            System.out.println(e);
        }

        //Send requests and receives response
        try {
            String request = request(location);
            sendPackets(clientSocket, routerAddress, request);
            String payload = receivePayload(clientSocket);
            receiveResponse(payload);
        } catch(Exception e){
            System.out.println(e);
        }finally{
            clientSocket.close();
        }
    }

    // Send HTTP request to web server
    private String request(String path) throws Exception {
        Map<String, String> headersMap = headerOption();
        StringBuilder headers = new StringBuilder();

        if(method.equals("POST")) {
            if (headersMap.containsKey("Content-Length")) {
                for (String key : headersMap.keySet()) {
                    headers.append(key).append(": ").append(headersMap.get(key)).append("\r\n");
                }
            }else {
                throw new Exception("Content-Length must be part of the header.");
            }
        }else if(method.equals("GET")){
            for (String key : headersMap.keySet()) {
                headers.append(key).append(": ").append(headersMap.get(key)).append("\r\n");
            }
        }

        String requestHeader = method + " " + path + " HTTP/1.0\r\n" +
               "User-Agent: Mozilla/5.0 (X11; Linux x86_64)\r\n" +
                headers +
               "Connection: close\r\n" +
                "\r\n";

        if(method.equals("POST")){
            String requestBody = bodyOption();
            return (requestHeader + requestBody);
        }else if(method.equals("GET")){
            return requestHeader;
        }else{
            return null;
        }
    }

    private void sendPackets(DatagramSocket clientSocket, SocketAddress routerAddr, String request){
        try {
            InetSocketAddress serverAddr = new InetSocketAddress(serverHost, serverPort);
            Packet requestPacket = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddr.getPort())
                    .setPeerAddress(serverAddr.getAddress())
                    .setPayload(request.getBytes())
                    .create();
            DatagramPacket datagramPacketSend = new DatagramPacket(requestPacket.toBytes(),
                    requestPacket.toBytes().length, routerAddr);
            System.out.println("Sending: " + request);
            clientSocket.send(datagramPacketSend);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private String receivePayload(DatagramSocket clientSocket){
        ByteBuffer receivingBytesBuffer = ByteBuffer
                .allocate(Packet.MAX_LEN)
                .order(ByteOrder.BIG_ENDIAN);
        DatagramPacket receivingDatagramPacket = null;

        String payload = null;
        try {
            //Receive Packet
            receivingBytesBuffer.clear();
            receivingDatagramPacket = new DatagramPacket(receivingBytesBuffer.array(),
                    receivingBytesBuffer.array().length);
            assert clientSocket != null;
            clientSocket.receive(receivingDatagramPacket);
            receivingBytesBuffer.position(receivingDatagramPacket.getLength());

            //Parse the packet
            receivingBytesBuffer.flip();
            Packet receivedPacket = Packet.fromBuffer(receivingBytesBuffer);
            receivingBytesBuffer.flip();

            //Get Payload and process request
            payload = new String(receivedPacket.getPayload(), UTF_8);
        }catch(Exception e){
            System.out.println(e);
        }
        return payload;
    }

    //Receives the response from the server
    private void receiveResponse(String payload) throws IOException {
        boolean verboseOption = verboseOption();

        StringBuilder response = new StringBuilder();

        String header = "";
        String body = "";

        String[] headerAndBody = payload.split("\r\n\r\n");
        header = headerAndBody[0];
        if(headerAndBody.length > 1){
            body = headerAndBody[1];
        }

        //If verbose, print everything
        //Else, ignore the verbose until the body is reached, then print the body
        if(verboseOption){
            response.append(header).append("\r\n\r\n").append(body);
        }else{
            response.append(body);
        }

        //If the file response option (-o) is present, then output the response in that file
        //Else, output the response to the terminal
        File fileOutput = fileResponseOption();
        if(fileOutput != null){
            PrintWriter fileWriter = new PrintWriter(fileResponseOption());
            fileWriter.write(response.toString());
            fileWriter.close();
        }else{
            System.out.println(response);
        }
    }

    private String bodyOption() {
        StringBuilder body = new StringBuilder();

        OptionParser parser = new OptionParser();
        OptionSpec<String> rawBodySpec = parser.accepts("d", "Associates an inline data to the body HTTP POST request.")
                .withRequiredArg()
                .ofType(String.class);
        OptionSpec<String> fileBodySpec = parser.accepts("f", "Associates the content of a file to the body HTTP post request.")
                .availableUnless("d")
                .withRequiredArg()
                .ofType(String.class);
        parser.allowsUnrecognizedOptions();
        OptionSet bodyOption = parser.parse(getArgs());

        if(bodyOption.has("d")) {
            body = new StringBuilder(bodyOption.valueOf(rawBodySpec));
        }else if(bodyOption.has("f")){
            try {
                String fileName = bodyOption.valueOf(fileBodySpec);
                File file = new File(fileName);
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                String currentLine;
                while ((currentLine = fileReader.readLine()) != null) {
                    body.append(currentLine);
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
        return body.toString();
    }

    //Gets the URL of the server that will receive the request
    private void getURL() {
        rawUrl = getArgs()[getArgsLength() - 1].replaceAll("[\"']", "");
    }
}
