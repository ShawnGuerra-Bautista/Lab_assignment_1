/*
        This request should only be responsible of
            >Method
            >Header (Content-length not mandatory)
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class GETRequest extends Request {

    private String rawUrl;
    private String host;
    private int port;

    public GETRequest(String[] args){
        super(args);
    }

    public void execute() {

        getURL();

        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            URL url = new URL(rawUrl);
            host = url.getHost();
            port = url.getDefaultPort();

            inetAddress = InetAddress.getByName(host);
            serviceSocket = new Socket(inetAddress, 80);

            BufferedWriter requestWriter = new BufferedWriter(
                    new OutputStreamWriter(serviceSocket.getOutputStream(), "UTF-8"));
            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(serviceSocket.getInputStream()));

            sendRequest(requestWriter, host, port);
            receiveResponse(responseReader);




            requestWriter.close();
            responseReader.close();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // send HTTP request to web server
    public void sendRequest(BufferedWriter requestWriter, String host, int port) throws IOException {
        // write header

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

        String request = "GET / HTTP/1.0\r\n" +
                            "Host: " + host + ":" + port + "\r\n" +
                            "User-Agent: " + userAgent + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n";



        requestWriter.write(request);
        requestWriter.flush();
    }

    public void receiveResponse(BufferedReader responseReader) throws IOException {
        // read response
            // verbose? -> if verbose(true), print everything; if false, print starting from curly bracket {
        StringBuilder stringBuilder = new StringBuilder();
        String data;

        do {
            data = responseReader.readLine();
            stringBuilder.append(data + "\r\n");
        }
        while (data != null);

        String response = stringBuilder.toString();

        System.out.println(response);
    }

    private String getURL() {
        rawUrl = getArgs()[getArgsLength() - 1];
        return rawUrl;
    }
}
