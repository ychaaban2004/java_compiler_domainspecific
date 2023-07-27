package comCARBONCOPY.craftinginterpreters.trick;
/*Token instantiation abstracted in a class */
public class TokenOriginal {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    /*Construct to instantiate token objectts with passed values for instance field instantiation
     * @param: token type - trick's enum object, lexeme name - string, line number - int
     * @return: toke - object
     */
    TokenOriginal(TokenType type, String lexeme, Object literal, int line){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }
    /*Produces word represenation of the token for us to see the interpreter tokenizing source code
     * @param: none
     * @return: token as a message - string
     */
    public String toString(){
        return type + " " + lexeme + " " + literal;
    }
}