import joptsimple.OptionParser;

public class Help {

    private String[] arguments;

    public Help(String[] arguments){
        this.arguments = arguments;
    }

    private void parseArguments(){
    }

    private String generalHelpMessage(){
        return "\nhttpc is a curl-like application but supports HTTP protocol only" +
                "\nUsage: httpc command [arguments]" +
                "\nThe commands are:" +
                "\n\tget\texecutes a HTTP GET request and prints the response." +
                "\n\tpost\texecutes a HTTP POST request and prints the response." +
                "\n\thelp\tprints this screen." +
                "\n\nUse \"httpc help [command]\" for more information about a command";
    }

    private String getHelpMessage(){
        //Using Optionparser to print description of options
        OptionParser getParser = (new GETMethod()).getGETparser();
        return "";
    }

    private String postHelpMessage(){
        //Using Optionparser to print description of options
        OptionParser getParser = (new POSTMethod()).getPOSTparser();
        return "";
    }
}
