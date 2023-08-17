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
 * -    Variable (Uninitialised and initialised)
 * -    for statement - NOT TESTS ALSO ERROR TESTING REQUIRED
 * -    if statement
 * -    with and without else statement
 * -    print statement
 * -    while statement - NOT TESTED
 * -    Block statement
 * -    Variable assignment
 * -    "Invalid assignment target"
 * -    Primary variable
 * -    Expect ';' after loop condition
 * -    Expect ')' after for clauses.
 * -    Expect '}' after block.
 * -    Expect '(' after 'while'.
 * -    Expect ')' after while condition.
 * -    Expect '(' after 'if'.
 * -    Expect ')' after if condition.
 * -    "Expect variable name."
 * -    "Expect ';' after variable declaration"
 * -    Parse error
 */


class ParserTest {

    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private List<Stmt> parseExpression(String source){
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
        List<Stmt> statements = parseExpression("5 != 6;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(!= 5.0 6.0)", astBuilider);
    }


    @Test
    public void validComparison() {
        List<Stmt> statements = parseExpression("15 != 5 > 6;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(!= 15.0 (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validTerm() {
        List<Stmt> statements = parseExpression("-15 != 5 > 6;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(!= (- 15.0) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validFactor() {
        List<Stmt> statements = parseExpression("10 * -15 != 5 > 6;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(!= (* 10.0 (- 15.0)) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validAddition() {
        List<Stmt> statements = parseExpression("1 + 10 * -15 != 5 > 6;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(!= (+ 1.0 (* 10.0 (- 15.0))) (> 5.0 6.0))", astBuilider);
    }

    @Test
    public void validPrimary() {
        List<Stmt> statements = parseExpression("!true;");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(! true)", astBuilider);
    }

    @Test
    public void validGrouping() {
        List<Stmt> statements = parseExpression("10 * (6 + 5);");
        String astBuilider = new ASTprinter().print(statements);
        Assertions.assertEquals("(* 10.0 (group (+ 6.0 5.0)))", astBuilider);
    }

    @Test
    public void validVariable() {
        List<Stmt> statements = parseExpression("var hello;");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(hello)",astBuilder);
    }

    @Test
    public void validVariableInitialised() {
        List<Stmt> statements = parseExpression("var hello = 5;");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(hello 5.0)",astBuilder);
    }

    @Test
    public void validIfStatement() {
        List<Stmt> statements = parseExpression("if(5 == 5) hello = 1;");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(IF (== 5.0 5.0) (hello 1.0) )",astBuilder);
    }

    @Test
    public void validIfElseStatement() {
        List<Stmt> statements = parseExpression("if(5 == 5) hello = 1; else hello = 2;");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(IF (== 5.0 5.0) (hello 1.0) (hello 2.0))",astBuilder);
    }

    @Test
    public void validPrint() {
        List<Stmt> statements = parseExpression("print \"Hello world\";");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(PRINT Hello world)",astBuilder);
    }

    @Test
    public void validBlock() {
        List<Stmt> statements = parseExpression("{hello = 1;}");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(BLOCK (hello 1.0))",astBuilder);
    }

    @Test
    public void validMultilineBlock() {
        List<Stmt> statements = parseExpression("{hello = 1; world = 2;}");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(BLOCK (hello 1.0) (world 2.0))",astBuilder);
    }

    @Test
    public void validMultiline() {
        List<Stmt> statements = parseExpression("hello = 1; \n {world = 2;}");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(hello 1.0)(BLOCK (world 2.0))",astBuilder);
    }

    @Test
    public void validVariableAsPrimary() {
        List<Stmt> statements = parseExpression("hello = world;");
        String astBuilder = new ASTprinter().print(statements);
        Assertions.assertEquals("(hello world)",astBuilder);
    }

    // TODO : FIX THESE TESTS
    // @Test
    // public void invalidGrouping() {
    //     List<Stmt> statements = parseExpression("10 * (6 + 5;");
    //     new ASTprinter().print(statements);
    //     Assertions.assertEquals("[line 1] Error at end:Expect ')' after expression.", outputStreamCaptor.toString().trim());
    // }

    // @Test
    // public void invalidTerm() {
    //     List<Stmt> statements = parseExpression("-;");
    //     new ASTprinter().print(statements);
    //     Assertions.assertEquals("[line 1] Error at end:Expect expression.", outputStreamCaptor.toString().trim());
    // }

    //FOR NOW: We can only test +-/* operations, later we will expand when compiler can handle more
    @Test
    public void simpleBinaryTest(){
        List<Stmt> statements = parseExpression("1+1/3;");
        String astBuild = new ASTprinter().print(statements);
        Assertions.assertEquals("(+ 1.0 (/ 1.0 3.0))",astBuild);
    }

    @Test
    public void complexBinaryTest(){
        List<Stmt> statements = parseExpression("3.4+4.6/8*(4*3+1);");
        String astBuild = new ASTprinter().print(statements);
        Assertions.assertEquals("(+ 3.4 (* (/ 4.6 8.0) (group (+ (* 4.0 3.0) 1.0))))",astBuild);
    }

    

    //BONUS FEATURE TESTS
}