package craftinginterpreters.trick;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

class InterpreterTest {
    private  static  final Interpreter interpreter = new Interpreter();
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStreamCaptor = new ByteArrayOutputStream();

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
        String expectedOut = "[line 1] Error:Dividing by zero is invalid and will produce infinity as a compensation.";
        Assertions.assertEquals(expectedOut, errStreamCaptor.toString().trim());
    }
}