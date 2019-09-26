/*
        This request should only be responsible of
            >Method
            >Header (Content-length not mandatory)
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GETRequest extends Request {

    private String rawUrl;

    public GETRequest(String[] args){
        super(args);
    }

    public void execute() throws UnknownHostException {
        String url = rawUrl.substring(rawUrl.indexOf("http://"), rawUrl.length()-1);

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(url);
        }catch(UnknownHostException e){
            System.out.println(e);
        }

        Socket serviceSocket = null;
        try {
            serviceSocket = new Socket(inetAddress, 80);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    private String getURL() {
        rawUrl = getArgs()[getArgsLength() - 1];
        return rawUrl;
    }
}
