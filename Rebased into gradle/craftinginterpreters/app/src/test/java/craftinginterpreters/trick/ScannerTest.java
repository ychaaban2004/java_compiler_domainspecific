package craftinginterpreters.trick;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


import static craftinginterpreters.trick.TokenType.*;

//Not sure how to implement error checking for System.err outputs

/*
 * Types of tests needed
 *  -   valid null token
 *  -   valid char
 *  -   non terminating char
 *  -   invalid char (string)
 *  -   invalid last char is '
 *  -   invalid last char is the char without ' ending
 *  -   valid string
 *  -   non terminating string
 *  -   valid multiline string
 *  -   valid number
 *  -   Valid decimal
 *  -   valid keyword
 *  -   valid consecutive keywords
 *  -   valid comment
 *  -   valid multiline comment
 *  -   valid error reporting
 */

class ScannerTest {

    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void cleanUp() {
        System.setErr(standardErr);
    }

    private List<Token> tokenize(String source){
        Scanner scanner = new Scanner(source);
        return scanner.scanTokens();
    }

    @Test
    public void EOFToken() {
        List<Token> tokens = tokenize("");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(EOF,"",null,1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validChar(){
        List<Token> tokens = tokenize("'A'");
        Token actualToken = tokens.get(0);
        Token expectedChar = new Token(CHAR,"'A'","A",1);
        Assertions.assertEquals(expectedChar.toString(),actualToken.toString());
    }

    @Test
    public void emptyChar() {
        tokenize("''");
        String expected  = "[line 1] Error:No expression given to char";
        Assertions.assertEquals(expected,outputStreamCaptor.toString().trim());
    }

    @Test
    public void nonTerminatingChar() {
        tokenize("'A");
        String expected  = "[line 1] Error:Unterminated char";
        Assertions.assertEquals(expected,outputStreamCaptor.toString().trim());
    }

    @Test
    public void tooLongChar() {
        tokenize("'String'");
        String expected  = "[line 1] Error:Invalid char";
        Assertions.assertEquals(expected,outputStreamCaptor.toString().trim());
    }

    @Test
    public void EOFEmptyChar() {
        tokenize("'");
        String expected  = "[line 1] Error:Unterminated char";
        Assertions.assertEquals(expected,outputStreamCaptor.toString().trim());
    }

    @Test
    public void validString() {
        List<Token> tokens = tokenize("\"String\"");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(STRING,"\"String\"","String",1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validMultiLineString() {
        List<Token> tokens = tokenize("\"Line 1 \n Line 2\"");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(STRING, "\"Line 1 \n Line 2\"", "Line 1 \n Line 2", 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    } 

    @Test
    public void nonTerminatingString() {
        tokenize("\"String");
        String expected  = "[line 1] Error:Unterminated string.";
        Assertions.assertEquals(expected,outputStreamCaptor.toString().trim());
    } 

    @Test
    public void validNumber() {
        List<Token> tokens = tokenize("1234");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(NUMBER, "1234", 1234.0, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validDouble() {
        List<Token> tokens = tokenize("1234.5678");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(NUMBER, "1234.5678", 1234.5678, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validKeyword() {
        List<Token> tokens = tokenize("+");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(PLUS, "+", null, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validConsecutiveKeyword() {
        List<Token> tokens = tokenize("!=");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(BANG_EQUAL, "!=", null, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validSlash() {
        List<Token> tokens = tokenize("12/5");
        Token actualToken = tokens.get(1);
        Token expectedToken = new Token(SLASH, "/", null, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validComment() {
        List<Token> tokens = tokenize("//Comment");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(EOF, "", null, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void validMultilineComment() {
        List<Token> tokens = tokenize("/* Line 1 \n* Line 2 \n*/");
        Token actualToken = tokens.get(0);
        Token expectedToken = new Token(EOF, "", null, 1);
        Assertions.assertEquals(expectedToken.toString(), actualToken.toString());
    }

    @Test
    public void complexTest() {
        List<Token> tokens = tokenize("'A';; \"String\" \n String5678 \n 1234.5678 != 12/5 \n  // Single line Comment \n /* \n * Multiline comment \n */");
        List<Token> expectedTokens = Arrays.asList(
            new Token(CHAR, "'A'", "A",1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(SEMICOLON, ";", null, 1),
            new Token(STRING, "\"String\"", "String", 1),
            new Token(IDENTIFIER, "String5678",null,2),
            new Token(NUMBER, "1234.5678", 1234.5678,3),
            new Token(BANG_EQUAL, "!=", null, 3),
            new Token(NUMBER, "12", 12.0, 3),
            new Token(SLASH, "/", null, 3),
            new Token(NUMBER, "5", 5.0, 3),
            new Token(EOF, "", null, 4));
        Assertions.assertEquals(expectedTokens.size(), tokens.size());
        int start = 0;
        for(Token t: tokens) {
            Assertions.assertEquals(expectedTokens.get(start++).toString(), t.toString());
        }
    }
}