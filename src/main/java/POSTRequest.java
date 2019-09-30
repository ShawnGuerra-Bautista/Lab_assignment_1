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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class POSTRequest extends Request {

    private String[] args;

    public POSTRequest(String[] args){
        super(args);
    }

    public void execute(){
        InetAddress inetAddress = null;
        Socket serviceSocket = null;

        try{
            BufferedWriter requestWriter =  new BufferedWriter(new OutputStreamWriter(serviceSocket.getOutputStream(), "UTF-8"));
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));

            sendRequest(requestWriter);
            receiveResponse(responseReader);

            requestWriter.close();
            responseReader.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    private void sendRequest(BufferedWriter requestWriter) throws IOException {
        //Create BufferedWriter
        //Create a String and convert it into UTF-8 + getBytes
            //Write Headers
            //Write Body
        //flush
        requestWriter.flush();
    }

    private void receiveResponse(BufferedReader responseReader) throws IOException {
        //Read response
            //Check if verbose is demanded
                //if
    }

    private String bodyOption() {
        //parse the command to check whether it's a file or body
        //read the file/body
        //return the string
        return null;
    }

    public OptionParser getPOSTparser() {
        OptionParser parser = new OptionParser();
        parser.accepts("d", "Associates an inline data to the body HTTP POST request.")
                .requiredUnless("f")
                .availableUnless("f");
        parser.accepts("f", "Associates the content of a file to the body HTTP post request.")
                .requiredUnless("d")
                .availableUnless("d");
        parser.nonOptions("URL").ofType(String.class);
        return parser;
    }
}
