package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

public class REIExpressions extends ASTWrapperPsiElement {
    public static final RenpyElement UNARY      = new RenpyElement("UNARY_EXPRESSION", Unary.class);
    public static final RenpyElement BINARY     = new RenpyElement("BINARY_EXPRESSION", Binary.class);
    public static final RenpyElement TERNARY    = new RenpyElement("TERNARY_EXPRESSION", Ternary.class);

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
            stmt.done(REIGroups.BARE_TUPLE);
            return REIGroups.BARE_TUPLE;
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
        if (token == RenpyTokenTypes.IDENTIFIER) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(REIVariable.REFERRED);
            return REIVariable.REFERRED;
        } else if (RenpyTokenTypes.LITERAL_VALUES.contains(token)) {
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
            if (RenpyTokenTypes.OPERATOR.isToken(builder, "+", "-", "~")) {
                PsiBuilder.Marker stmt = builder.mark();
                builder.advanceLexer();

                IElementType expr = getStatement(builder);
                if (expr == null) {
                    stmt.rollbackTo();
                    builder.error("Expected expression after unary operator");
                    return null;
                }

                stmt.done(UNARY);
                return UNARY;
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
                stmt.done(UNARY);
                return UNARY;
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
                stmt.done(BINARY);
                stmt = stmt.precede();
            }

            stmt.drop();
            return BINARY;
        }
        private static boolean isNextOperator(PsiBuilder builder) {
            if (builder.getTokenType() == RenpyTokenTypes.OPERATOR) return true;
            return RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "and", "or", "is", "is not", "in", "not in");
        }

        private static int getPrecedence(String op) {
            if (op == null) return 0;
            return switch (op) {
                case "**" -> 10;
                case "*", "/", "//", "%" -> 9;
                case "+", "-" -> 8;
                case "<<", ">>" -> 7;
                case "&" -> 6;
                case "^" -> 5;
                case "|" -> 4;

                case "<", "<=", ">", ">=", "==", "!=", "is", "is not", "in", "not in" -> 3;

                case "not" -> 2;
                case "and", "or" -> 1;

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

                IElementType ifExpr = getStatement(builder);
                if (ifExpr == null) builder.error("Expected the condition after 'if'");

                if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "else")) {
                    builder.advanceLexer();

                    IElementType elseExpr = getStatement(builder);
                    if (elseExpr == null) builder.error("Expected an expression after 'else'");
                } else builder.error("Expected 'else' in ternary expression");

                expr.done(TERNARY);
                return TERNARY;
            }

            expr.drop();
            return condition;
        }
    }
}
