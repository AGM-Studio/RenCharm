package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyExpressionImpl extends ASTWrapperPsiElement {
    public RenpyExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static IElementType getStatement(PsiBuilder builder) {
        return getStatement(builder, null, 0);
    }
    public static IElementType getStatement(PsiBuilder builder, IElementType skip) {
        return getStatement(builder, skip, 0);
    }
    public static IElementType getStatement(PsiBuilder builder, int precedence) {
        return getStatement(builder, null, precedence);
    }
    public static IElementType getStatement(PsiBuilder builder, IElementType skip, int precedence) {
        PsiBuilder.Marker stmt = builder.mark();

        IElementType left = getPrimaryStatement(builder, skip);
        if (left == null) {
            stmt.rollbackTo();
            return null;
        }

        while (true) {
            int operator = getPrecedence(builder.getTokenText());
            if (builder.getTokenType() != RenpyTokenTypes.OPERATOR || operator < precedence) break;
            builder.advanceLexer();

            PsiBuilder.Marker mark = builder.mark();
            IElementType right = getStatement(builder, skip, operator + 1);
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

    private static int getPrecedence(String token) {
        if (token == null) return 0;
        if (token.equals("*") || token.equals("/") || token.equals("//") || token.equals("%")) return 2;
        if (token.equals("+") || token.equals("-")) return 1;
        return 0;
    }

    // Parse primary expressions: tuples, lists, identifiers, literals
    private static IElementType getPrimaryStatement(PsiBuilder builder, IElementType skip) {
        IElementType token;

        if (skip != RenpyElementTypes.TUPLE && (token = RenpyTupleImpl.getStatement(builder)) != null) return token;
        if (skip != RenpyElementTypes.LIST  && (token = RenpyListImpl.getStatement(builder)) != null) return token;

        IElementType currentToken = builder.getTokenType();
        if (currentToken == RenpyTokenTypes.IDENTIFIER || RenpyTokenTypes.LITERAL_VALUES.contains(currentToken)) {
            builder.advanceLexer();
            return currentToken;
        }

        return null;
    }
}
