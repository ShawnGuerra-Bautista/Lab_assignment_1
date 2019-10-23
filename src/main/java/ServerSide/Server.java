package ServerSide;

import java.io.File;

public class Server {
    private boolean isDebugMessage;
    private int portNumber;
    private File path;

    public Server(boolean isDebugMessage, int portNumber, File path){
        this.isDebugMessage = isDebugMessage;
        this.portNumber = portNumber;
        this.path = path;
    }

    public void run(){
        //Run server
    }

}
