/*
        This request should be responsible of
            >Method
            >Header (Content-length MANDATORY)
            >Body
                >-d option
                >-f option
                >Could only support one of them only, not both or neither.
 */

import joptsimple.OptionParser;

public class POSTRequest extends Request {

    private String[] args;

    public POSTRequest(String[] args){
        super(args);
    }

    public void execute(){

    }

    public OptionParser getPOSTparser() {
        OptionParser parser = new OptionParser();
        parser.accepts("v", "Prints the detail of the response such as protocol, status, and headers.");
        parser.accepts("h", "Associates headers to HTTP Request with the format 'key:value'")
                .withRequiredArg();
        parser.accepts("d", "Associates an inline data to the body HTTP POST request.")
                .requiredUnless("f")
                .availableUnless("f");
        parser.accepts("f", "Associates the content of a file to the body HTTP post request.")
                .requiredUnless("d")
                .availableUnless("d");
        parser.nonOptions("URL").ofType(String.class);
        return parser;
    }
}
