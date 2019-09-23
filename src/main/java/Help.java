public class Help {

    private String[] arguments;

    public Help(String[] arguments) {
        this.arguments = arguments;
    }

    public void executeHelp() {
        if(arguments.length == 1 && arguments[0].equals("help")) {
            System.out.println(generalHelpMessage());
        }else if(arguments.length == 2 && arguments[0].equals("help") && arguments[1].equals("get")) {
            System.out.println(getHelpMessage());
        }else if(arguments.length == 2 && arguments[0].equals("help") && arguments[1].equals("post")) {
            System.out.println(postHelpMessage());
        }else {
            System.out.println("Invalid command.");
        }
    }

    private String generalHelpMessage() {
        return "\nhttpc is a curl-like application but supports HTTP protocol only" +
                "\nUsage: httpc command [arguments]" +
                "\nThe commands are:" +
                "\n\tget\texecutes a HTTP GET request and prints the response." +
                "\n\tpost\texecutes a HTTP POST request and prints the response." +
                "\n\thelp\tprints this screen." +
                "\n\nUse \"httpc help [command]\" for more information about a command.";
    }

    private String getHelpMessage() {
        //Can be replace with parser + handler, but (for now) this works fine
        return "\nUsage: httpc get [-v] [-h key:value] URL" +
                "\n\nGet executes a HTTP GET request for a given URL." +
                "\n\n\t-v\tPrints the details of the response such as protocol, status, and headers." +
                "\n\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";
    }

    private String postHelpMessage() {
        //Can be replace with parser + handler, but (for now) this works fine
        return "\nUsage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL" +
                "\n\nPost executes a HTTP POST request for a given URL with inline data or from file." +
                "\n\n\t-v\tPrints the details of the response such as protocol, status, and headers." +
                "\n\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'." +
                "\n\t-d string\tAssociates an inline data to the body HTTP POST request." +
                "\n\t-f file\tAssociates the content of a file to the body HTTP POST request" +
                "\n\nEither [-d] or [-f] can be used, but not both";
    }
}
