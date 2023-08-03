package craftinginterpreters.trick;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/*
 * Types of testing
 * -    != equality
 * -    > comaprison
 * -    - negative term
 * -    * factor
 * -    + addition
 * -    TRUE
 * -    Number
 * -    () brackets
 * -    ( bracket
 * -    Expect Expression (-)
 * -    
 */


class ParserTest {

    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private Expr parseExpression(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        return  parser.parse();
    }

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void cleanUp() {
        System.setErr(standardErr);
    }

    @Test
    public void validEquality() {
        Expr expression = parseExpression("5 != 6");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(!= 5.0 6.0)", astBuilider);
    }

    @Test
    public void validComparison() {
        Expr expression = parseExpression("15 != 5 > 6");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(!= 15.0 (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validTerm() {
        Expr expression = parseExpression("-15 != 5 > 6");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(!= (- 15.0) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validFactor() {
        Expr expression = parseExpression("10 * -15 != 5 > 6");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(!= (* 10.0 (- 15.0)) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validAddition() {
        Expr expression = parseExpression("1 + 10 * -15 != 5 > 6");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(!= (+ 1.0 (* 10.0 (- 15.0))) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validPrimary() {
        Expr expression = parseExpression("!true");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(! true)", astBuilider);
    }

    @Test
    public void validGrouping() {
        Expr expression = parseExpression("10 * (6 + 5)");
        String astBuilider = new ASTprinter().print(expression);
        Assertions.assertEquals("(* 10.0 (group (+ 6.0 5.0)))", astBuilider);
    }

    @Test
    public void invalidGrouping() {
        Expr expression = parseExpression("10 * (6 + 5");
        new ASTprinter().print(expression);
        Assertions.assertEquals("[line 1] Error at end:Expect ')' after expression.", outputStreamCaptor.toString().trim());
    }

    @Test
    public void invalidTerm() {
        Expr expression = parseExpression("-");
        new ASTprinter().print(expression);
        Assertions.assertEquals("[line 1] Error at end:Expect expression.", outputStreamCaptor.toString().trim());
    }

    //FOR NOW: We can only test +-/* operations, later we will expand when compiler can handle more
    @Test
    public void simpleBinaryTest(){
        Expr expression = parseExpression("1+1/3");
        String astBuild = new ASTprinter().print(expression);
        Assertions.assertEquals("(+ 1.0 (/ 1.0 3.0))",astBuild);
    }

    @Test
    public void complexBinaryTest(){
        Expr expression = parseExpression("3.4+4.6/8*(4*3+1)");
        String astBuild = new ASTprinter().print(expression);
        Assertions.assertEquals("(+ 3.4 (* (/ 4.6 8.0) (group (+ (* 4.0 3.0) 1.0))))",astBuild);
    }

}