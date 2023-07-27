package comCARBONCOPY.craftinginterpreters.trick;

class RuntimeError extends RuntimeException{
    final TokenOriginal token;

    RuntimeError(TokenOriginal token, String message){
        super(message);
        this.token = token;
    }
}