package com.craftinginterpreters.trick;

class Interpreter implements Expr.Visitor<Object>{
    /*Public API connecting the expression interaction of Interpreter, Expr,
        and Parser to the character consuming program of Trick
    @param: expression - of Expr type
    @return: void
    */
    void interpret(Expr expression){
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Trick.runtimeError(error);
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

    /*Determines equality and checks for null objects
     * @param: objects to compare
     * @return: equal or not
     */
    private boolean isEqual(Object a, Object b){
        if(a == null && b == null) return true;
        if(a == null) return false;
        //the equal method will satisfy our trick documentation
        return a.equals(b);
    }
    /*Ensures the taken obejcts are actually number (Double) instances
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

    /*evaluates literals by returning the value
     * @param: expression object belonging to literal subclass
     * @return: literal value of the expression
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
    }

    /*evaluates parentheses grouping by evaluating subexpression
     * @param: expression object belonging to grouping subclass
     * @return: instance of the evaluation of the subexpression
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    /*evaluates unary cases
     * @param: expression object belonging to unary subclass
     * @return: instance of the evaluation of unary operator applied
     * to evaluated subexpression
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);

        switch(expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator,right);
                return -(double)right;
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
                if(left instanceof String && right instanceof String){
                    return (String)left + (String)right;
                }

            throw new RuntimeError(expr.operator, "Both operands must be strictly numbers or strictly strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        //Unreachable as mentioned in visitUnaryExpr
        return null;
    }
}