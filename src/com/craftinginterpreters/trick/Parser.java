package com.craftinginterpreters.trick;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.trick.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    private interface BinaryOperation{
        Expr opApply();
    }
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /*Translation of recursive descent statement parsing rule
     * If we dont match the token with a type of statement it is assumed to be an expressions statement
     * @param: none
     * @return: list of statements - List<Stmt>
     */
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()){
            statements.add(declaration());
        }
        return statements;
    }

    /*Calls for var declaration or statement, and is setup to catch parse errors effectively
     * implementing the panic mode synchronization as an appropriate reset point
     * @param: none
     * @return: Stmt object
     */
    private Stmt declaration(){
        try{
            if(match(VAR)) return varDeclaration();
            return statement();
        } catch (Parser.ParseError error){
            synchronize();
            return null;
        }
    }
    /*declares a variable with an identifier name and a value if provided; otherwise null value
     * @param: none
     * @return: Stmt var object
     */
    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER,"Expect variable name.");

        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name,initializer);
    }

    /*Match each statement to its type and make a Stmt object of it before putting it in the list
     * @param: none
     * @return: Stmt object
     */
    private Stmt statement(){
        if(match(PRINT)) return printStatement();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    /*ensure we have consumed a print statement ending with ; and returning the Stmt object
     * @param: none
     * @return: Stmt object
     */
    private Stmt printStatement(){
        Expr value = expression();
        consume(SEMICOLON,"Expect ';' after value.");
        return new Stmt.Print(value);
    }

    /*ensure we have syntactically consumed an expression and created a Stmt object for it
     * @param: none
     * @return: Stmt object
     */
    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }

        consume(RIGHT_BRACE,"Expect '}' after block.");
        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            //Not thrown as we don need to enter panic mode  if we have not assigned properly, can easily resolve from this.
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    /* Helper method optimizing AST build of binary expressions
    * @param:
    * @return:
    * */
    private Expr binaryOperation(BinaryOperation opApplier, TokenType... operators){
        Expr expr = opApplier.opApply();
        while(match(operators)){
            Token operator = previous();
            Expr right = opApplier.opApply();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr equality(){
        return binaryOperation(this::comparison,BANG_EQUAL,EQUAL_EQUAL);
    }

    private Expr comparison() {
        return binaryOperation(this::term,GREATER,GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expr term() {
        return  binaryOperation(this::factor,MINUS,PLUS);
    }

    private Expr factor() {
        return binaryOperation(this::unary,SLASH,STAR);
    }

    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER, STRING, CHAR)) return new Expr.Literal(previous().literal);
        if(match(IDENTIFIER)){
            return new Expr.Variable(previous());
        }
        if(match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for(TokenType type: types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);

    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current-1);
    }

    private ParseError error(Token token, String message) {
        Trick.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while(!isAtEnd()) {
            if(previous().type == SEMICOLON) return;
            switch(peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}