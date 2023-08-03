package craftinginterpreters.trick;

// Temporary AST printer for each expression type using the visitor pattern
public class ASTprinter implements Expr.Visitor<String>{

    String print(Expr expr) {
        if(expr != null) return expr.accept(this);
        return null;
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
            if(expr != null) builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}