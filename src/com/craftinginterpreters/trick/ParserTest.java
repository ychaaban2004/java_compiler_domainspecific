package com.craftinginterpreters.trick;

import com.craftinginterpreters.trick.Expr;
import com.craftinginterpreters.trick.Parser;
import com.craftinginterpreters.trick.Scanner;
import com.craftinginterpreters.trick.Token;
import com.craftinginterpreters.trick.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
class ParserTest {
    private Expr parseExpression(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        return  parser.parse();
    }
    //FOR NOW: We can only test +-/* operations, later we will expand when compiler can handle more
    @Test
    void simpleBinaryTest(){
        Expr expression = parseExpression("1+1/3");
        String astBuild = new ASTprinter().print(expression);
        Assertions.assertEquals("(+ 1.0 (/ 1.0 3.0))",astBuild);
    }

    @Test
    void complexBinaryTest(){
        Expr expression = parseExpression("3.4+4.6/8*(4*3+1)");
        String astBuild = new ASTprinter().print(expression);
        Assertions.assertEquals("(+ 3.4 (* (/ 4.6 8.0) (group (+ (* 4.0 3.0) 1.0))))",astBuild);
    }

}