//All requests (POST/GET) should support
//      1. -o option
//      2. -h option
//      3. -v option
public abstract class Request {

    private String[] args;

    public Request(String[] args){
        this.args = args;
    }

    private String[] headerOption(){
        return null;
    }
    private boolean verboseOption(){
        return false;
    }
    private boolean fileResponseOption(){
        return false;
    }
}
