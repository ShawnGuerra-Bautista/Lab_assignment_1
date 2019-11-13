package ClientSide;
/*
    This class is responsible of handling the arguments;
    depending on the first argument, a particular option will be chosen.
 */

public class RequestHandler {

    private String[] args;

    public RequestHandler(String[] args){
        this.args = args;
    }

    public void handle(){
        if (args[0].equals("help")) {
            Help help = new Help(args);
            help.executeHelp();
        }else if(args[0].equals("get")) {
            MethodRequest getRequest = new MethodRequest(args, "GET");
            getRequest.execute();
        }else if(args[0].equals("post")) {
            MethodRequest postRequest = new MethodRequest(args, "POST");
            postRequest.execute();
        }else {
            System.out.println("Invalid Command.");
        }
    }
}
