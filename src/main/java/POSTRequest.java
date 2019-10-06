/*
    ==============WARNING==============
    This code contains duplicated code with the GETRequest class.
    Refactoring will be needed (it will be done in the next lab)
    Certain methods must be transferred to the Request abstract class
    ===================================

    This class is responsible on handling specific tasks concerning the POST request
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

    private String rawUrl;
    private String host;
    private int port;
    private String status;
    private String location;

    public POSTRequest(String[] args){
        super(args);
        getURL();
    }

    //Sends a POST request to the server
    public void execute() {
        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try {
            URL url = new URL(rawUrl);
            host = url.getHost();
            port = url.getDefaultPort();
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
    private void sendRequest(BufferedWriter requestWriter, String path) throws IOException, Exception {
        Map<String, String> headersMap = headerOption();
        StringBuilder headers = new StringBuilder();

        if(headersMap.containsKey("Content-Length")) {
            for (String key : headersMap.keySet()) {
                headers.append(key).append(": ").append(headersMap.get(key)).append("\r\n");
            }
        }else{
            throw new Exception("Content-Length must be part of the header.");
        }

        String requestHeader = "POST " + path + " HTTP/1.0\r\n" +
                "Host: " + host + ":" + port + "\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64)\r\n" +
                headers +
                "Connection: close\r\n" +
                "\r\n";

        String requestBody = bodyOption();
        requestWriter.write(requestHeader + requestBody);

        requestWriter.flush();
    }

    //Receives the response from the server
    private void receiveResponse(BufferedReader responseReader) throws IOException {
        boolean verboseOption = verboseOption();

        StringBuilder response = new StringBuilder();
        String currentLine;

        //If verbose, print everything
        //Else, ignore the verbose until the body is reached, then print the body
        if(verboseOption){
            status = responseReader.readLine();
            response.append(status).append("\n");
            while (!((currentLine = responseReader.readLine()).equals(""))){
                if(currentLine.toLowerCase().contains("location")){
                    location = currentLine.substring(currentLine.indexOf(":") + 1).replaceAll("\\s+","");
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
                if(currentLine.toLowerCase().contains("location")){
                    location = currentLine.substring(currentLine.indexOf(":") + 1).replaceAll("\\s+","");
                }
            }
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
            }catch (FileNotFoundException e){
                System.out.println(e);
            }catch (IOException e){
                System.out.println(e);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        return body.toString();
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
