package comCARBONCOPY.craftinginterpreters.trick;

public class RuntimeErrorOriginal extends RuntimeException{
    final TokenOriginal token;

    RuntimeErrorOriginal(TokenOriginal token, String message){
        super(message);
        this.token = token;
    }
}