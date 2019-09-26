/*
        This request should only be responsible of
            >Method
            >Header (Content-length not mandatory)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GETRequest extends Request {

    private String rawUrl;

    public GETRequest(String[] args){
        super(args);
    }

    public void execute() {


        String url = rawUrl.substring(rawUrl.indexOf("http://"), rawUrl.length()-1);

        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            inetAddress = InetAddress.getByName(url);
            serviceSocket = new Socket(inetAddress, 80);

            PrintWriter requestWriter = new PrintWriter(serviceSocket.getOutputStream());
            BufferedReader responseBuffer = new BufferedReader(
                    new InputStreamReader(serviceSocket.getInputStream()));

            // send HTTP request to web server
            requestWriter.println("GET / HTTP/1.0");
            requestWriter.println("Host: " + url + ":" + "80");
            requestWriter.println("Connection: close");
            requestWriter.println();

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private String getURL() {
        rawUrl = getArgs()[getArgsLength() - 1];
        return rawUrl;
    }
}
