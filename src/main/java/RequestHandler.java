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
            GETRequest getRequest = new GETRequest(args);
            getRequest.execute();
        }else if(args[0].equals("post")) {
            POSTRequest postRequest = new POSTRequest(args);
            postRequest.execute();
        }else {
            System.out.println("Invalid Command.");
        }
    }
}
