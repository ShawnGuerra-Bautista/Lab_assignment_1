import joptsimple.OptionParser;

public class Method {

    private String request;
    private String url;
    private final String HTTP = "HTTP/1.0";

    public Method() {
    }

    public Method(String request, String url) {
        this.request = request;
        this.url = url;
    }

    public String getRequest() {
        return request;
    }

    public String getUrl() {
        return url;
    }

    public String getHTTP(){
        return HTTP;
    }

    public OptionParser getPOSTparser() {
        OptionParser parser = new OptionParser();
        parser.nonOptions("Method POST").ofType(String.class);
        parser.accepts("v", "Prints the detail of the response such as protocol, " +
                "status, and headers.");
        parser.accepts("h", "Associates headers to HTTP Request with the " +
                "format 'key:value'").withRequiredArg();
        parser.accepts("d", "Associates an inline data to the body HTTP POST request.")
                .requiredUnless("f")
                .availableUnless("f");
        parser.accepts("f", "Associates the content of a file to the body HTTP post request.")
                .requiredUnless("d")
                .availableUnless("d");
        parser.nonOptions("URL").ofType(String.class);
        return parser;
    }

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
