public class httpc {

    public static void main(String[] args) {
        if (args[0].equals("help")) {
            new Help(args);
        }else if(args[0].equals("get")) {
            return;
        }else if(args[0].equals("post")) {
            return;
        }else {
            System.out.println("Invalid Command.");
        }

    }
}
