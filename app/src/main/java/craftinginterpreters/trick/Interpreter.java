package craftinginterpreters.trick;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{

    private Environment environment = new Environment();

    /*
     * Public API connecting the expression interaction of Interpreter, Expr,
     * and Parser to the character consuming program of Trick
     * @param: expression - of Expr type
     * @return: void
     */
    void interpret(List<Stmt> statements){
        try{
            for (Stmt statement : statements){
                execute(statement);
            }
        } catch (RuntimeError error){
            Trick.runtimeError(error);
        }
    }

    /*
     * Helper function to send statments to acceptor and get visitor pattern running
     * @param: statement object to consume - Stmt
     * @return: none
     */
    private void execute(Stmt stmt){
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment){
        Environment previous = this.environment;
        try{
            this.environment = environment;

            for(Stmt statement : statements){
                execute(statement);
            }
        } finally{
            this.environment = previous;
        }
    }
    private String stringify(Object object){
        if(object == null) return "nil";

        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    /*Takes the subexpression of the other evaluating methods
    and sends that subexpression to be parsed
    @param: expression object/instance
    @return: instance of Expr accept method
     */
    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    /*Determines if any expression is inherently true or false
     * @param: object of analysis
     * @return: true/false
     */
    private boolean isTruthy(Object object){
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        //anything else is automatically deemed true
        return true;
    }

    /*
     * Determines equality and checks for null objects
     * @param: objects to compare
     * @return: equal or not
     */
    private boolean isEqual(Object a, Object b){
        if(a == null && b == null) return true;
        if(a == null) return false;
        //the equal method will satisfy our trick documentation
        return a.equals(b);
    }
    /*
     * Ensures the taken obejcts are actually number (Double) instances
     * @param: operator Token, and operand object(s)
     * @return: none
     */
    private void checkNumberOperand(Token operator, Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Both operands must be a number.");
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    /*implements if else statement abstract interface execution
    * @param: Stmt.If object
    * @return: null*/
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        } else if(stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    /*
     * Implements abstract Stmt interface, by evaluating expression or printing out the statment
     * as appropriate
     * @param: Expression subclass of Stmt object
     * @return: none*/
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        evaluate(stmt.expression);
        return null;
    }

    /*^^ nearly identical ^^*/
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    /*Variable initializer
     * @param: Stmt object of the variable
     * @return: Void - null
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        Object value = null;
        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        if(!environment.values.containsKey(stmt.name.lexeme)){
            environment.define(stmt.name.lexeme, value );
        } else{
         Trick.error(stmt.name.line, "Variable name '" + stmt.name.lexeme +"' has already been defined - cannot be redefined");
        }
        return null;
    }

    /*Ensures we execute while loop body under true evaluation result of condition
    * @param: While object belong to Stmt parent class
    * @return: null*/
    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    /*Similar to variable initializer except no new var is defined, and we must assign
     * @param: Expr of the assign abstract subclass
     * @return: the object value of the var - check documentation on variables about this
     */
    @Override
    public Object visitAssignExpr(Expr.Assign expr){
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    /*Variable retrieval for expressions
     * @param: Expr object of the variable
     * @return: Object value of the variable
     */
    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        return environment.get(expr.name);
    }

    /*evaluates literals by returning the value
     * @param: expression object belonging to literal subclass
     * @return: literal value of the expression
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr){
        Object left = evaluate(expr.left);

        if(expr.operator.type == TokenType.OR){
            if (isTruthy(left)) return left;
        } else{
            if(!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    /*evaluates parentheses grouping by evaluating subexpression
     * @param: expression object belonging to grouping subclass
     * @return: instance of the evaluation of the subexpression
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    /* evaluates unary cases
     * @param: expression object belonging to unary subclass
     * @return: instance of the evaluation of unary operator applied
     * to evaluated subexpression
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
            default: 
        }

        //Unreachable for whatever reason - satisfying method return syntax
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr){
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type){
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL: return !isEqual(left,right);
            case EQUAL_EQUAL: return isEqual(left,right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return(double)left - (double)right;
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }
                if(left instanceof String || right instanceof String){ //allows for things like hello + 4
                    return stringify(left) + stringify(right);
                }
                if(left instanceof Character || right instanceof Character){
                    return stringify(left) + stringify(right);
                }

            throw new RuntimeError(expr.operator, "Operands must be a char/string/number.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if((double)right == 0.0){
                    throw new RuntimeError(expr.operator,"Dividing by zero is not accepted.");
                }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case MODULO:
                checkNumberOperands(expr.operator, left, right);
                return (double)left % (double) right;
            default: 
        }

        //Unreachable as mentioned in visitUnaryExpr
        return null;
    }
}