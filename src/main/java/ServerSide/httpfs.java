package ServerSide;

public class httpfs
{
    static int serverPort = 8080;

    public static void main(String[] args) {

        ServerRequestHandler handler = new ServerRequestHandler(args);
        handler.handle();

        /*
            ServerSocket server = null;
            try {
                server = new ServerSocket(serverPort);
            }catch(IOException e) {
                System.out.println(e);
            }

            //Accept requests
            Socket client;
            while(true){
                //Accept Requests
                try {
                    client = server.accept();
                }catch(IOException e){
                    System.out.println(e);
                }
            }
         */
    }
}
