public class httpc {

    public static void main(String[] args) {

        if (args[0].equals("help")) {
            new Help(args);
        }else if(args[0].equals("get") || args[0].equals("post")) {
            new Method(args);
        }else {
            System.out.println("Invalid Command.");
        }

    }
}
