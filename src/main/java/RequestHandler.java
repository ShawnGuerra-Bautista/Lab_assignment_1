public class RequestHandler {

    private String[] args;

    public RequestHandler(String[] args){
        this.args = args;
    }

    public void processArgs(){
        if (args[0].equals("help")) {
            Help help = new Help(args);
            help.executeHelp();
        }else if(args[0].equals("get") || args[0].equals("post")) {

            //new Method(args);
        }else {
            System.out.println("Invalid Command.");
        }
    }
}