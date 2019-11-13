package ClientSide;
/*
    ==============WARNING==============
    This code contains duplicated code with the POSTRequest class.
    Refactoring will be needed (it will be done in the next lab)
    Certain methods must be transferred to the Request abstract class
    ===================================

    This class is responsible on handling specific tasks concerning the GET request
 */
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class GETRequest extends Request {

    private String rawUrl;
    private String host;
    private int port;
    private String status;
    private String location;

    public GETRequest(String[] args){
        super(args);
        getURL();
    }

    //Sends a GET request to the server
    public void execute() {
        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            URL url = new URL(rawUrl);
            host = url.getHost();
            port = url.getPort();
            location = url.getPath();

            //A 'do-while' loop is present in order to cover the case where a redirection is needed
            do {
                inetAddress = InetAddress.getByName(host);
                serviceSocket = new Socket(inetAddress, port);

                BufferedWriter requestWriter = new BufferedWriter(
                        new OutputStreamWriter(serviceSocket.getOutputStream(), "UTF-8"));
                BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(serviceSocket.getInputStream()));

                sendRequest(requestWriter, location);
                location = null;
                receiveResponse(responseReader);
                requestWriter.close();
                responseReader.close();
            }while(isRedirectionNeeded() && location != null);

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
            while ((currentLine = responseReader.readLine()) != null && !(currentLine .equals(""))){
                if(currentLine.toLowerCase().contains("location")){
                    location = currentLine.substring(currentLine.indexOf(":") + 1).replaceAll("\\s+","");
                }
                response.append(currentLine).append("\n");
            }
            response.append("\n");
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
        }else{
            status = responseReader.readLine();
            while ((currentLine = responseReader.readLine()) != null && !(currentLine .equals(""))){
                if(currentLine.toLowerCase().contains("location")){
                    location = currentLine.substring(currentLine.indexOf(":") + 1).replaceAll("\\s+","");
                }
            }
            response.append("\n");
            while ((currentLine = responseReader.readLine()) != null) {
                response.append(currentLine).append("\n");
            }
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

    //Checks if the status of the response is a 3xx for redirection
    private boolean isRedirectionNeeded(){
        return status.contains("300") || status.contains("301") || status.contains("302") || status.contains("304");
    }

    //Gets the URL of the server that will receive the request
    private void getURL() {
        rawUrl = getArgs()[getArgsLength() - 1].replaceAll("[\"']", "");
    }
}
