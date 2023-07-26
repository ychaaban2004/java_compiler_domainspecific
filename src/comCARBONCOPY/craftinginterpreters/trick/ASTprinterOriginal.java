package comCARBONCOPY.craftinginterpreters.trick;

// Temporary AST printer for each expression type using the visitor pattern
public class ASTprinterOriginal implements ExprOriginal.Visitor<String>{

    String print(ExprOriginal expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(ExprOriginal.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(ExprOriginal.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(ExprOriginal.Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(ExprOriginal.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, ExprOriginal... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (ExprOriginal expr:exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}