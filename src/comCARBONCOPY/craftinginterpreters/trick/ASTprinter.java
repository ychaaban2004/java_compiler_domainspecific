package comCARBONCOPY.craftinginterpreters.trick;

import static comCARBONCOPY.craftinginterpreters.trick.Expr.*;

import comCARBONCOPY.craftinginterpreters.trick.Expr.Binary;
import comCARBONCOPY.craftinginterpreters.trick.Expr.Grouping;
import comCARBONCOPY.craftinginterpreters.trick.Expr.Literal;
import comCARBONCOPY.craftinginterpreters.trick.Expr.Unary;

// Temporary AST printer for each expression type using the visitor pattern
public class ASTprinter implements Expr.Visitor<String>{

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr:exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}