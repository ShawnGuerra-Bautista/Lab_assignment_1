/*
        This request should only be responsible of
            >Method
            >Header (Content-length not mandatory)
 */

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class GETRequest extends Request {

    private String rawUrl;
    private URL url;
    private String host;
    private int port;
    private String status;
    private String location;

    public GETRequest(String[] args){
        super(args);
        getURL();
    }

    public void execute() {
        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            URL url = new URL(rawUrl);
            this.url = url;
            host = url.getHost();
            port = url.getDefaultPort();

            inetAddress = InetAddress.getByName(host);
            serviceSocket = new Socket(inetAddress, port);

            BufferedWriter requestWriter = new BufferedWriter(
                    new OutputStreamWriter(serviceSocket.getOutputStream(), "UTF-8"));
            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(serviceSocket.getInputStream()));

            sendRequest(requestWriter, url.getPath());
            receiveResponse(responseReader);

            while(isRedirectionNeeded()){
                if(location != null){
                    location = null;
                    sendRequest(requestWriter, location);
                    receiveResponse(responseReader);
                }else{
                    break;
                }
            }

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

    // Send HTTP request to web server
    public void sendRequest(BufferedWriter requestWriter, String path) throws IOException {
        Map<String, String> headersMap = headerOption();
        StringBuilder headers = new StringBuilder();
        for(String key: headersMap.keySet()){
            headers.append(key).append(": ").append(headersMap.get(key)).append("\r\n");
        }

        String request = "GET " + path + " HTTP/1.0\r\n" +
                            "Host: " + host + ":" + port + "\r\n" +
                            "User-Agent: Mozilla/5.0 (X11; Linux x86_64)\r\n" +
                            headers +
                            "Connection: close\r\n" +
                            "\r\n";
        requestWriter.write(request);
        requestWriter.flush();
    }

    //Receives the response from the server
    public void receiveResponse(BufferedReader responseReader) throws IOException {
        boolean verboseOption = verboseOption();

        StringBuilder response = new StringBuilder();
        String currentLine;

        //If verbose, print everything
        //Else, ignore the verbose until the body is reached, then print the body
        if(verboseOption){
            status = responseReader.readLine();
            response.append(status).append("\n");
            while (!((currentLine = responseReader.readLine()).equals(""))){
                if(currentLine.toLowerCase().contains("location: ")){
                    location = currentLine.substring(currentLine.indexOf(": ") + 1);
                }
                response.append(currentLine).append("\n");
            }
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
        }else{
            status = responseReader.readLine();
            response.append(status).append("\n");
            while (!((currentLine = responseReader.readLine()).equals(""))){
                if(currentLine.toLowerCase().contains("location: ")){
                    location = currentLine.substring(currentLine.indexOf(": ") + 1);
                }
            }
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
        }
        File fileOutput = fileResponseOption();
        if(fileOutput != null){
            PrintWriter fileWriter = new PrintWriter(fileResponseOption());
            fileWriter.write(response.toString());
            fileWriter.close();
        }else{
            System.out.print(response);
        }
    }

    private boolean isRedirectionNeeded(){
        return status.contains("300") || status.contains("301") || status.contains("302") || status.contains("304");
    }

    private void getURL() {
        rawUrl = getArgs()[getArgsLength() - 1].replaceAll("[\"']", "");
    }
}
