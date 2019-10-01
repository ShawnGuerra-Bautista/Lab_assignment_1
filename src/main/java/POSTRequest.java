/*
        This request should be responsible of
            >Method
            >Header (Content-length MANDATORY)
            >Body
                >-d option
                >-f option
                >Could only support one of them only, not both or neither.
 */

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class POSTRequest extends Request {

    private String[] args;
    private String rawUrl;
    private String host;
    private int port;

    public POSTRequest(String[] args){
        super(args);
    }

    public void execute() {
        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            URL url = new URL(rawUrl);
            host = url.getHost();
            port = url.getDefaultPort();

            inetAddress = InetAddress.getByName(host);
            serviceSocket = new Socket(inetAddress, port);

            BufferedWriter requestWriter = new BufferedWriter(
                    new OutputStreamWriter(serviceSocket.getOutputStream(), "UTF-8"));
            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(serviceSocket.getInputStream()));

            sendRequest(requestWriter);
            receiveResponse(responseReader);

            requestWriter.close();
            responseReader.close();

        } catch(MalformedURLException e){
            System.out.println(e);
        } catch(IOException e) {
            System.out.println(e);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void sendRequest(BufferedWriter requestWriter) throws IOException {
        Map<String, String> headersMap = headerOption();
        StringBuilder headers = new StringBuilder();
        for(String key: headersMap.keySet()){
            headers.append(key).append(": ").append(headersMap.get(key)).append("\r\n");
        }

        String requestHeader = "POST / HTTP/1.0\r\n" +
                "Host: " + host + ":" + port + "\r\n" +
                headers +
                "Connection: close\r\n" +
                "\r\n";

        String requestBody = bodyOption() + "\r\n" + "\r\n";
        requestWriter.write(requestHeader + requestBody);
        requestWriter.flush();
    }

    private void receiveResponse(BufferedReader responseReader) throws IOException {
        boolean verboseOption = verboseOption();

        StringBuilder response = new StringBuilder();
        String currentLine;

        //If verbose, print everything
        //Else, ignore the verbose until the body is reached, then print the body
        if(verboseOption){
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
        }else{
            while (!(responseReader.readLine().equals("")));
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
        }
        System.out.print(response);
    }

    public String bodyOption() {
        String body = "";
        OptionParser parser = new OptionParser();
        OptionSpec<String> rawBodySpec = parser.accepts("d", "Associates an inline data to the body HTTP POST request.")
                .requiredUnless("f")
                .availableUnless("f")
                .withRequiredArg()
                .ofType(String.class);
        OptionSpec<String> fileBodySpec = parser.accepts("f", "Associates the content of a file to the body HTTP post request.")
                .requiredUnless("d")
                .availableUnless("d")
                .withRequiredArg()
                .ofType(String.class);;
        OptionSet bodyOption = parser.parse(args);

        if(bodyOption.has("d")) {
            body = bodyOption.valueOf(rawBodySpec);
        }else if(bodyOption.has("f")){
            String fileName = bodyOption.valueOf(fileBodySpec);
            //Read the file...
        }

        return null;
    }
}
