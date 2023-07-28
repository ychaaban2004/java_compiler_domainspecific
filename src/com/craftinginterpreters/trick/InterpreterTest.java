package com.craftinginterpreters.trick;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

class InterpreterTest {
    private Object binaryInterpreter(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr.Binary expression = (Expr.Binary) parser.parse();
        Interpreter interpreter = new Interpreter();
        return  interpreter.visitBinaryExpr(expression);
    }

    @Test
    public void stringNumberCombine(){
        Object added = binaryInterpreter("\"String \"+4");
        Assertions.assertEquals("String 4",added);
    }

    @Test
    public void charNumberStringCombine(){
        Object added = binaryInterpreter("'a' + 2 + \"string\"");
        Assertions.assertEquals("a2string",added);
    }
}