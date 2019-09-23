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

        }else if(args[0].equals("post")) {

        }else {
            System.out.println("Invalid Command.");
        }
    }
}