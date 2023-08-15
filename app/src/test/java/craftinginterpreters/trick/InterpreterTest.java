package craftinginterpreters.trick;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static craftinginterpreters.trick.TokenType.*;

class InterpreterTest {
    private final Interpreter interpreter = new Interpreter();
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStreamCaptor = new ByteArrayOutputStream();
    private final static boolean pos = true; boolean neg = false;

    private final Map<String,Token> tokenMap = new HashMap<>() {{
       put("and",new Token(AND,"and", null, 1));
       put("or",new Token(OR,"or",null,1));
       put("bang",new Token(BANG,"bang",null,1));
       put("+", new Token(PLUS,"+",null,1));
    }};


    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errStreamCaptor));
    }

    @AfterEach
    public void cleanUp() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }


    private void interpretToConsole(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        interpreter.interpret(statements);
    }

    //GENERAL TESTS BEGIN

    @Test
    public void validVisitVarStmt(){
        Token a_var = new Token(IDENTIFIER,"a",null,1);
        Stmt.Var valInitVar = new Stmt.Var(a_var, new Expr.Binary(
                new Expr.Literal(1.0),
                new Token(PLUS,"+",null,1),
                new Expr.Literal(1.0)));

        interpreter.visitVarStmt(valInitVar);
        interpretToConsole("print a;");
        String expectedOut = "2";
        Assertions.assertEquals(expectedOut,outputStreamCaptor.toString().trim());
    }
    @Test
    public void validVisitIfStmt(){
        interpretToConsole("var a = 1;");
        interpretToConsole("if(a == 1){print 2;}");
        String expectedOut1 = "2";
        Assertions.assertEquals(expectedOut1,outputStreamCaptor.toString().trim());
    }

    @Test
    public void validVisitIfElseStmt(){
        interpretToConsole("var a = 2;");
        interpretToConsole("if(a == 1){print 2;} else{print 4;}");
        String expectedOut1 = "4";
        Assertions.assertEquals(expectedOut1,outputStreamCaptor.toString().trim());
    }

    @Test
    public void validVisitWhileStmt(){
        interpretToConsole("var a = 3; var b = 0;");
        interpretToConsole("while(a != 0){b = b + 1; a = a - 1;}");
        interpretToConsole("print b;");
        String expectedOut = "3";
        Assertions.assertEquals(expectedOut,outputStreamCaptor.toString().trim());
    }
    //series of logical expression tests
    @Test
    //or operation with true left
    public void validOr1() {
        Expr.Logical expression = new Expr.Logical(
                new Expr.Literal(pos),
                tokenMap.get("or"),
                new Expr.Literal(neg)
        );
        boolean result = (boolean) interpreter.visitLogicalExpr(expression);
        Assertions.assertEquals(pos, result);
    }
    @Test
    //or operation with true right
    public void validOr2() {
        Expr.Logical expression = new Expr.Logical(
                new Expr.Literal(neg),
                tokenMap.get("or"),
                new Expr.Literal(pos)
        );
        boolean result = (boolean) interpreter.visitLogicalExpr(expression);
        Assertions.assertEquals(pos, result);
    }
    @Test
    //and operation with false left
    public void validAnd1() {
        Expr.Logical expression = new Expr.Logical(
                new Expr.Literal(neg),
                tokenMap.get("and"),
                new Expr.Literal(pos)
        );
        boolean result = (boolean) interpreter.visitLogicalExpr(expression);
        Assertions.assertEquals(neg, result);
    }
    @Test
    //and operation with false right
    public void validAnd2() {
        Expr.Logical expression = new Expr.Logical(
                new Expr.Literal(pos),
                tokenMap.get("and"),
                new Expr.Literal(neg)
        );
        boolean result = (boolean) interpreter.visitLogicalExpr(expression);
        Assertions.assertEquals(neg, result);
    }
    @Test
    //and operation should eval to true
    public void validAnd3() {
        Expr.Logical expression = new Expr.Logical(
                new Expr.Literal(pos),
                tokenMap.get("and"),
                new Expr.Literal(pos)
        );
        boolean result = (boolean) interpreter.visitLogicalExpr(expression);
        Assertions.assertEquals(pos, result);
    }

    @Test
    public void validVisitUnaryExpr(){
        Expr.Unary expression = new Expr.Unary(tokenMap.get("bang"),new Expr.Literal(pos));
        Object result = interpreter.visitUnaryExpr(expression);
        Assertions.assertEquals(neg,result);
    }

//binary expr: test switch case and runtime error throws
    @Test
    public void binaryExprSwitch(){
        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal(1.0),
                tokenMap.get("+"),
                new Expr.Literal(1.0));
        Object result = interpreter.visitBinaryExpr(expression);
        Assertions.assertEquals(2.0,result);
    }

    @Test
    public void binaryThrow(){
        boolean errorThrown = false;
        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal(1),
                tokenMap.get("+"),
                new Expr.Literal(1)
        );//this is a number but Trick only takes doubles from the parser - no ints being passed from parser allowed
        try {
            interpreter.visitBinaryExpr(expression);
        }catch (RuntimeError error){
            errorThrown = true;
        }
        Assertions.assertEquals(pos,errorThrown);
    }


    //BONUS FEATURES BEGIN
    @Test
    public void invalidVarRedeclaration(){
        Token a_var = new Token(IDENTIFIER,"a",null,1);

        Stmt.Var valInitVar = new Stmt.Var(a_var, new Expr.Literal(1));
        interpreter.visitVarStmt(valInitVar);

        Stmt.Var valReInit = new Stmt.Var(a_var, new Expr.Literal(2));
        interpreter.visitVarStmt(valReInit);

        String expectedOut = "[line 1] Error:Variable name 'a' has already been defined - cannot be redefined";
        Assertions.assertEquals(expectedOut,errStreamCaptor.toString().trim());
    }

    @Test
    public void stringNumberCombine(){
        interpretToConsole("print \"String \"+4;");
        Assertions.assertEquals("String 4",outputStreamCaptor.toString().trim());
    }

    @Test
    public void charNumberStringCombine(){
        interpretToConsole("print 'a' + 2 + \"string\";");
        Assertions.assertEquals("a2string",outputStreamCaptor.toString().trim());
    }

    @Test
    public void divideByZeroError(){
        interpretToConsole("var a = 1/0;");
        String expectedOut = "Dividing by zero is not accepted.\n" +
                "[line 1]";
        Assertions.assertEquals(expectedOut, errStreamCaptor.toString().trim());
    }
}