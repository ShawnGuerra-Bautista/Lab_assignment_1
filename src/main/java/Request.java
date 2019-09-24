import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.util.Map;

//All requests (POST/GET) should support
//      1. -o option
//      2. -h option
//      3. -v option
public abstract class Request {

    private String[] args;

    public Request(String[] args){
        this.args = args;
    }

    private Map<String, String> headerOption(){
        //Should return a hashmap
        return null;
    }
    private boolean verboseOption(){
        OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        OptionSet verboseOption = parser.parse(args);
        return verboseOption.has("v");
    }
    private File fileResponseOption(){
        //Should return a File for FileOutputStream
        return null;
    }
}
