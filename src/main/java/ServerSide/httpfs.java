package ServerSide;

public class httpfs
{
    static int serverPort = 8080;

    public static void main(String[] args) {

        ServerRequestHandler handler = new ServerRequestHandler(args);
        handler.handle();
    }
}
