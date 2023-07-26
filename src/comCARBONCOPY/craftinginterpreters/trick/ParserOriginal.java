package comCARBONCOPY.craftinginterpreters.trick;

import java.util.List;

import static comCARBONCOPY.craftinginterpreters.trick.TokenTypeOriginal.*;

public class ParserOriginal {

    private static class ParseError extends RuntimeException {}

    private final List<TokenOriginal> tokens;
    private int current = 0;

    public ParserOriginal(List<TokenOriginal> tokens) {
        this.tokens = tokens;
    }

    public ExprOriginal parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }


    private ExprOriginal expression() {
        return equality();
    }


    private ExprOriginal equality() {
        ExprOriginal expr = comparison();
        while(match(BANG_EQUAL, EQUAL_EQUAL)) {
            TokenOriginal operator = previous();
            ExprOriginal right = comparison();
            expr = new ExprOriginal.Binary(expr, operator, right);
        }
        return expr;
    }

    private ExprOriginal comparison() {
        ExprOriginal expr = term();

        while(match(GREATER,GREATER_EQUAL, LESS, LESS_EQUAL)) {
            TokenOriginal operator = previous();
            ExprOriginal right = term();
            expr = new ExprOriginal.Binary(expr, operator, right);
        }
        return expr;
    }

    private ExprOriginal term() {
        ExprOriginal expr = factor();

        while(match(MINUS,PLUS)) {
            TokenOriginal operator = previous();
            ExprOriginal right = factor();
            expr = new ExprOriginal.Binary(expr, operator, right);
        }
        return expr;
    }

    private ExprOriginal factor() {
        ExprOriginal expr = unary();

        while(match(SLASH, STAR)) {
            TokenOriginal operator = previous();
            ExprOriginal right = unary();
            expr = new ExprOriginal.Binary(expr, operator, right);
        }
        return expr;
    }

    private ExprOriginal unary() {
        if(match(BANG, MINUS)) {
            TokenOriginal operator = previous();
            ExprOriginal right = unary();
            return new ExprOriginal.Unary(operator, right);
        }
        return primary();
    }

    private ExprOriginal primary() {
        if(match(FALSE)) return new ExprOriginal.Literal(false);
        if(match(TRUE)) return new ExprOriginal.Literal(true);
        if(match(NIL)) return new ExprOriginal.Literal(null);
        if(match(NUMBER, STRING)) return new ExprOriginal.Literal(previous().literal);
        if(match(LEFT_PAREN)) {
            ExprOriginal expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new ExprOriginal.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenTypeOriginal... types) {
        for(TokenTypeOriginal type: types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private TokenOriginal consume(TokenTypeOriginal type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);

    }

    private boolean check(TokenTypeOriginal type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private TokenOriginal advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private TokenOriginal peek() {
        return tokens.get(current);
    }

    private TokenOriginal previous() {
        return tokens.get(current-1);
    }

    private ParseError error(TokenOriginal token, String message) {
        TrickOriginal.error(token, message);
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