package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class REIExpressions extends ASTWrapperPsiElement {
    public static class Config {
        public static final Config DEFAULT = new Config(0);
        public static Config create(int precedence) {
            return new Config(precedence);
        }

        final int precedence;
        private Config(int precedence) {
            this.precedence = precedence;
        }
        public Config withPrecedence(int precedence) {
            return new Config(precedence);
        }
    }

    public REIExpressions(@NotNull ASTNode node) {
        super(node);
    }

    public static IElementType getBareStatement(PsiBuilder builder) {
        PsiBuilder.Marker stmt = builder.mark();
        int exprCount = 0;
        boolean hasComma = false;
        IElementType lastExpr = null;

        while (true) {
            IElementType expr = getStatement(builder);
            if (expr == null) break;

            exprCount++;
            lastExpr = expr;

            if (builder.getTokenType() == RenpyTokenTypes.COMMA) {
                hasComma = true;
                builder.advanceLexer();
            } else break;
        }

        if (exprCount >= 2 || (exprCount == 1 && hasComma)) {
            stmt.done(RenpyElementTypes.BARE_TUPLE);
            return RenpyElementTypes.BARE_TUPLE;
        } else if (exprCount == 1) {
            stmt.drop();
            return lastExpr;
        } else {
            stmt.rollbackTo();
            return null;
        }
    }

    public static IElementType getStatement(PsiBuilder builder) {
        return getStatement(builder, Config.DEFAULT);
    }
    public static IElementType getStatement(PsiBuilder builder, Config cfg) {
        PsiBuilder.Marker stmt = builder.mark();

        IElementType left = getUnaryExpression(builder, cfg);
        if (left == null) {
            stmt.rollbackTo();
            return null;
        }

        while (true) {
            int operator = getPrecedence(builder.getTokenText());
            if (builder.getTokenType() != RenpyTokenTypes.OPERATOR || operator < cfg.precedence) break;
            builder.advanceLexer();

            PsiBuilder.Marker mark = builder.mark();
            IElementType right = getStatement(builder, cfg.withPrecedence(operator + 1));
            if (right == null) {
                mark.rollbackTo();
                builder.error("Expected expression after operator");
                break;
            }
            mark.drop();
            stmt.done(RenpyElementTypes.BINARY);
            stmt = stmt.precede();
            left = RenpyElementTypes.BINARY;
        }

        stmt.drop();
        return left;
    }

    private static IElementType getUnaryExpression(PsiBuilder builder, Config cfg) {
        IElementType token = builder.getTokenType();
        String text = builder.getTokenText();
        if (text == null) return null;
        if (RenpyTokenTypes.OPERATOR.isToken(builder, "+", "-") || RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "not")) {
            PsiBuilder.Marker stmt = builder.mark();
            builder.advanceLexer();

            IElementType operand = getStatement(builder, Config.create(3));
            if (operand == null) {
                stmt.rollbackTo();
                builder.error("Expected expression after unary operator");
                return null;
            }

            stmt.done(RenpyElementTypes.UNARY);
            return RenpyElementTypes.UNARY;
        }

        // If not unary operator, fallback to primary expression parsing
        return getPrimaryStatement(builder, cfg);
    }

    private static int getPrecedence(String token) {
        if (token == null) return 0;
        if (token.equals("*") || token.equals("/") || token.equals("//") || token.equals("%")) return 2;
        if (token.equals("+") || token.equals("-")) return 1;
        return 0;
    }

    private static IElementType getPrimaryStatement(PsiBuilder builder, Config cfg) {
        IElementType token = REIFunctionCall.getStatement(builder);
        if (token != null) return token;

        token = REIGroups.getStatement(builder);
        if (token != null) return token;

        token = builder.getTokenType();
        if (token == RenpyTokenTypes.IDENTIFIER || RenpyTokenTypes.LITERAL_VALUES.contains(token)) {
            builder.advanceLexer();
            return token;
        }
        return null;
    }
}
