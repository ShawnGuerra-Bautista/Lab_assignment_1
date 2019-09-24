import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

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
        OptionParser parser = new OptionParser();
        parser.accepts("h", "Associates headers to HTTP Request with the format 'key:value'")
                .withRequiredArg()
                .ofType(String.class);
        OptionSet headerOption = parser.parse(args);
        Object header = headerOption.valueOf("h");

        //Find out if the Object from headerOption is a String or a List to create the appropriate HashMap
        if(header instanceof String){
            return null;
        }else if(header instanceof List){
            //List<String> headerList = headerOption.valueOf("List");
            return null;
        }else{
            return null;
        }
    }
    private boolean verboseOption(){
        OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        OptionSet verboseOption = parser.parse(args);
        return verboseOption.has("v");
    }
    private File fileResponseOption(){
        OptionParser parser = new OptionParser();
        parser.accepts("o", "Prints the detail of the response in a file.").withRequiredArg().ofType(String.class);
        OptionSet fileResponseOption = parser.parse(args);
        File fileResponse = new File((String)fileResponseOption.valueOf("o"));
        return null;
    }
}
