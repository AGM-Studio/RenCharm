package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.RenpyPsiElement;

public class RenpyExpressionImpl extends ASTWrapperPsiElement {
    public static class Config {
        public static final Config EMPTY = new Config();
        public static final Config SKIP_BARE_TUPLE = new Config(true);
        public static Config create(int precedence) {
            return new Config(precedence);
        }

        final int precedence;
        final boolean skipBareTuple;
        private Config() {
            this(0, false);
        }
        private Config(int precedence) {
            this(precedence, false);
        }
        private Config(boolean skipBareTuple) {
            this(0, skipBareTuple);
        }
        private Config(int precedence, boolean skipBareTuple) {
            this.precedence = precedence;
            this.skipBareTuple = skipBareTuple;
        }

        public Config withPrecedence(int precedence) {
            return new Config(precedence, this.skipBareTuple);
        }
        public Config skipBareTuple(boolean skipBareTuple) {
            return new Config(this.precedence, skipBareTuple);
        }
    }

    public RenpyExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static IElementType getStatement(PsiBuilder builder) {
        return getStatement(builder, Config.EMPTY);
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
        if (RenpyElementTypes.isNext(builder, RenpyTokenTypes.OPERATOR, "+", "-")
                || RenpyElementTypes.isNext(builder, RenpyTokenTypes.PRIMARY_KEYWORD, "not")) {
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

    // Parse primary expressions: tuples, lists, identifiers, literals
    private static IElementType getPrimaryStatement(PsiBuilder builder, Config cfg) {
        IElementType token;

        if ((token = RenpyTupleImpl.getStatement(builder, cfg.skipBareTuple)) != null) return token;
        if ((token = RenpyListImpl.getStatement(builder)) != null) return token;

        IElementType currentToken = builder.getTokenType();
        if (currentToken == RenpyTokenTypes.IDENTIFIER || RenpyTokenTypes.LITERAL_VALUES.contains(currentToken)) {
            builder.advanceLexer();
            return currentToken;
        }

        return null;
    }
}
