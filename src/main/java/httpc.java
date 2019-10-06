/*
    The program will be executed via httpc, thus it is responsible to obtain the arguments of the command line.
 */
public class httpc {

    public static void main(String[] args) {
        RequestHandler handler = new RequestHandler(args);
        handler.handle();
    }
}
