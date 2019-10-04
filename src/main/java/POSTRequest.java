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

    private String rawUrl;
    private URL url;
    private String host;
    private int port;
    private String status;
    private String location;

    public POSTRequest(String[] args){
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

    private boolean isRedirectionNeeded(){
        return status.contains("300") || status.contains("301") || status.contains("302") || status.contains("304");
    }
    private void getURL() {
        rawUrl = getArgs()[getArgsLength() - 1].replaceAll("[\"']", "");
    }
}
