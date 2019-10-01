import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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


    //Header options to implement (Content-Length; User-Agent; Date)
    protected Map<String, String> headerOption() {
        OptionParser parser = new OptionParser();
        Map<String, String> headersMap = new HashMap<String, String>();

        OptionSpec<String> headerSpec = parser.accepts("h", "Associates headers to HTTP Request with the format 'key:value'")
                .withRequiredArg()
                .ofType(String.class);
        parser.allowsUnrecognizedOptions();
        OptionSet headerOption = parser.parse(args);
        List<String> headerList = headerOption.valuesOf(headerSpec);

        for(String rawHeader: headerList){
            String[] keyAndValues = rawHeader.split(":");
            headersMap.put(keyAndValues[0], keyAndValues[1]);
        }

        return headersMap;
    }

    //Gets the verbose option
    protected boolean verboseOption() {
        OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        parser.allowsUnrecognizedOptions();
        OptionSet verboseOption = parser.parse(args);
        return verboseOption.has("v");
    }

    //Returns Filename of the file with the output
    protected File fileResponseOption() {
        OptionParser parser = new OptionParser();
        parser.accepts("o", "Prints the detail of the response in a file.").withRequiredArg().ofType(String.class);
        parser.allowsUnrecognizedOptions();
        OptionSet fileResponseOption = parser.parse(args);
        File fileResponse = new File((String)fileResponseOption.valueOf("o"));
        return fileResponse;
    }

    protected String[] getArgs() {
        return args;
    }

    protected int getArgsLength(){
        return args.length;
    }
}
