public class Library {

    private String[] args;

    public Library(String[] args){
        this.args = args;
    }

    public void processArgs(){
        if (args[0].equals("help")) {
            new Help(args);
        }else if(args[0].equals("get") || args[0].equals("post")) {
            new Method(args);
        }else {
            System.out.println("Invalid Command.");
        }
    }
}