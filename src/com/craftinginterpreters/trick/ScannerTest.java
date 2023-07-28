package com.craftinginterpreters.trick;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.craftinginterpreters.trick.TokenType.*;

class ScannerTest {

    private List<Token> tokenize(String source){
        Scanner scanner = new Scanner(source);
        return scanner.scanTokens();
    }

    @Test
    public void validChar(){
        List<Token> tokens = tokenize("'a'");
        Token actualToken = tokens.get(0);
        Token expectedChar = new Token(CHAR,"'a'","a",1);
        Assertions.assertEquals(expectedChar.toString(),actualToken.toString());
    }
}