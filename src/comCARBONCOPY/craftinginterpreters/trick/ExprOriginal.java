package comCARBONCOPY.craftinginterpreters.trick;

public abstract class ExprOriginal {
    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
    }
    static class Binary extends ExprOriginal {
        Binary(ExprOriginal left, TokenOriginal operator, ExprOriginal right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final ExprOriginal left;
        final TokenOriginal operator;
        final ExprOriginal right;
    }
    static class Grouping extends ExprOriginal {
        Grouping(ExprOriginal expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final ExprOriginal expression;
    }
    static class Literal extends ExprOriginal {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }
    static class Unary extends ExprOriginal {
        Unary(TokenOriginal operator, ExprOriginal right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final TokenOriginal operator;
        final ExprOriginal right;
    }

    abstract <R> R accept(Visitor<R> visitor);
}