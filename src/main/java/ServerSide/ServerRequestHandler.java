package ServerSide;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;

public class ServerRequestHandler {
    private String[] args;

    public ServerRequestHandler(String[] args){
        this.args = args;
    }

    public void handle(){
        Server server = new Server(debugOption(), portOption(), pathOption());
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

    public File pathOption() {
        OptionParser parser = new OptionParser();
        OptionSpec<String> pathSpec = parser.accepts("d", "Specifies the directory that the server " +
                "will use to read/write requested files. Default is the current directory " +
                "when launching the application.")
                .withRequiredArg()
                .ofType(String.class);
        parser.allowsUnrecognizedOptions();
        OptionSet pathResponseOption = parser.parse(args);

        File pathResponse = null;
        if(pathResponseOption.has("d")){
            pathResponse = new File(pathResponseOption.valueOf(pathSpec).replaceAll("[\"']", ""));
        }
        return pathResponse;
    }
}
