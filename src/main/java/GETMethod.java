import joptsimple.OptionParser;

public class GETMethod extends Method {

    public OptionParser getGETparser() {
        OptionParser parser = new OptionParser();
        parser.nonOptions("Method GET").ofType(String.class);
        parser.accepts("v", "Prints the detail of the response such as protocol, " +
                "status, and headers.");
        parser.accepts("h", "Associates headers to HTTP Request with the " +
                "format 'key:value'").withRequiredArg();
        parser.nonOptions("URL").ofType(String.class);
        return parser;
    }

}
