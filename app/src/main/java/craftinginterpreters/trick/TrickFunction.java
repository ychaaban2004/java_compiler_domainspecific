package craftinginterpreters.trick;

public class TrickFunction {
    //Environment for when function is declared not called - which is needed for closure
    private final Environment closure;

    TrickFunction(Stmt.Function declaration, Environment closure){
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguements){


        //NOTE: updated environment to consider closure
        Environment environment = new Environment(closure);
    }
}
