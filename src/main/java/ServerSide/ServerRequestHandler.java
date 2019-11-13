package ServerSide;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class ServerRequestHandler {
    private String[] args;

    public ServerRequestHandler(String[] args){
        this.args = args;
    }

    public void handle(){
        Server server = new Server(debugOption(), portOption(), filePathOption());
        server.run();
    }

    public boolean debugOption() {
        OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints debugging messages");
        parser.allowsUnrecognizedOptions();
        OptionSet debugOption = parser.parse(args);
        return debugOption.has("v");
    }

    public int portOption() {
        OptionParser parser = new OptionParser();
        OptionSpec<Integer> portSpec = parser.accepts("p", "Specifies the port number that the server" +
                " will listen and server at.\nDefault is 8080.")
                .withRequiredArg()
                .ofType(Integer.class);
        parser.allowsUnrecognizedOptions();
        OptionSet portResponseOption = parser.parse(args);

        Integer portResponse = 8080;

        if(portResponseOption.has("p")){
            portResponse = portResponseOption.valueOf(portSpec);
        }
        return portResponse;
    }

    public String filePathOption() {
        OptionParser parser = new OptionParser();
        OptionSpec<String> filePathSpec = parser.accepts("d", "Specifies the directory that the server " +
                "will use to read/write requested files. Default is the current directory " +
                "when launching the application.")
                .withRequiredArg()
                .ofType(String.class);
        parser.allowsUnrecognizedOptions();
        OptionSet filePathResponseOption = parser.parse(args);

        String filePathResponse = null;
        if(filePathResponseOption.has("d")){
            filePathResponse = "data" + filePathResponseOption.valueOf(filePathSpec).replaceAll("[\"']", "");
        }else{
            filePathResponse = "data/";
        }
        return filePathResponse;
    }
}
