public abstract class Method {

    private String[] args;

    public Method(){
        this.args = null;
    }

    public Method(String[] args){
        this.args = args;
    }

    public String[] getArgs(){
        return args;
    }
}
