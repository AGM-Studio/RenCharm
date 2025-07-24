package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class REIExpressions extends ASTWrapperPsiElement {
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
        return Ternary.getStatement(builder);
    }

    protected static IElementType getPrimaryStatement(PsiBuilder builder) {
        IElementType token = REIFunctionCall.getStatement(builder);
        if (token != null) return token; // RenpyElementTypes.FUNCTION_CALL

        token = REIGroups.getStatement(builder);
        if (token != null) return token; // RenpyElementTypes.GROUP, TUPLE, LIST, SET, DICT;

        token = builder.getTokenType();
        if (token == RenpyTokenTypes.IDENTIFIER || RenpyTokenTypes.LITERAL_VALUES.contains(token)) {
            builder.advanceLexer();
            return token;
        }

        return null;
    }

    public static class Unary extends ASTWrapperPsiElement {
        public Unary(@NotNull ASTNode node) {
            super(node);
        }

        public static IElementType getStatement(PsiBuilder builder) {
            if (RenpyTokenTypes.OPERATOR.isToken(builder, "+", "-")) {
                PsiBuilder.Marker stmt = builder.mark();
                builder.advanceLexer();

                IElementType expr = getStatement(builder);
                if (expr == null) {
                    stmt.rollbackTo();
                    builder.error("Expected expression after unary operator");
                    return null;
                }

                stmt.done(RenpyElementTypes.UNARY);
                return RenpyElementTypes.UNARY;
            }

            if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "not")) {
                PsiBuilder.Marker stmt = builder.mark();
                builder.advanceLexer();
                IElementType expr = getStatement(builder);
                if (expr == null) {
                    stmt.rollbackTo();
                    builder.error("Expected expression after 'not'");
                    return null;
                }
                stmt.done(RenpyElementTypes.UNARY);
                return RenpyElementTypes.UNARY;
            }

            return REIAccess.getStatement(builder);
        }
    }
    public static class Binary extends ASTWrapperPsiElement {
        public Binary(@NotNull ASTNode node) {
            super(node);
        }

        public static IElementType getStatement(PsiBuilder builder, int precedence) {
            PsiBuilder.Marker stmt = builder.mark();

            IElementType left = Unary.getStatement(builder);
            if (left == null) {
                stmt.rollbackTo();
                return null;
            }

            while (true) {
                int opp = getPrecedence(builder.getTokenText());
                if (!isNextOperator(builder) || opp < precedence) break;
                builder.advanceLexer();

                PsiBuilder.Marker rightMarker = builder.mark();
                IElementType right = getStatement(builder, opp + 1);

                if (right == null) {
                    rightMarker.rollbackTo();
                    builder.error("Expected expression after operator");
                    break;
                }

                rightMarker.drop();
                stmt.done(RenpyElementTypes.BINARY);
                stmt = stmt.precede();
            }

            stmt.drop();
            return RenpyElementTypes.BINARY;
        }
        private static boolean isNextOperator(PsiBuilder builder) {
            if (builder.getTokenType() == RenpyTokenTypes.OPERATOR) return true;
            if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "and", "or")) return true;

            return false;
        }

        private static int getPrecedence(String op) {
            if (op == null) return 0;
            return switch (op) {
                case "and", "or" -> 3;
                case "*", "/", "//", "%" -> 2;
                case "+", "-" -> 1;
                default -> 0;
            };
        }

        public static IElementType getStatement(PsiBuilder builder) {
            return getStatement(builder, 0);
        }
    }
    public static class Ternary extends ASTWrapperPsiElement {
        public Ternary(@NotNull ASTNode node) {
            super(node);
        }

        public static @Nullable IElementType getStatement(PsiBuilder builder) {
            PsiBuilder.Marker expr = builder.mark();

            IElementType condition = Binary.getStatement(builder);
            if (condition == null) {
                expr.rollbackTo();
                return null;
            }

            if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "if")) {
                builder.advanceLexer();

                IElementType ifExpr = Binary.getStatement(builder);
                if (ifExpr == null) builder.error("Expected the condition after 'if'");

                if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "else")) {
                    builder.advanceLexer();

                    IElementType elseExpr = Binary.getStatement(builder);
                    if (elseExpr == null) builder.error("Expected an expression after 'else'");
                } else builder.error("Expected 'else' in ternary expression");

                expr.done(RenpyElementTypes.TERNARY);
                return RenpyElementTypes.TERNARY;
            }

            expr.drop();
            return condition;
        }
    }
}
