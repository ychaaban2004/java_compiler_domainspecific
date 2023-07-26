package com.craftinginterpreters.trick;

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

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
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
        if(match(NUMBER, STRING)) return new Expr.Literal(previous().literal);
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