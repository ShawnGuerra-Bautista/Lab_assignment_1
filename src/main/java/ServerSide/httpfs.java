package ServerSide;

public class httpfs
{
    public static void main(String[] args) {

        ServerRequestHandler handler = new ServerRequestHandler(args);
        handler.handle();

    }
}
