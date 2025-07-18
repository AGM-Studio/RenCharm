package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyTupleImpl extends ASTWrapperPsiElement {
    public RenpyTupleImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static @Nullable IElementType getStatement(PsiBuilder builder) {
        PsiBuilder.Marker stmt = builder.mark();

        boolean hasParens = builder.getTokenType() == RenpyTokenTypes.LPAREN;
        if (hasParens) builder.advanceLexer();

        int exprCount = 0;
        boolean hasComma = false;

        while (true) {
            IElementType expr = RenpyExpressionImpl.getStatement(builder, hasParens ? null : RenpyElementTypes.TUPLE);
            if (expr == null) break;
            exprCount++;

            if (builder.getTokenType() == RenpyTokenTypes.COMMA) {
                hasComma = true;
                builder.advanceLexer();
            } else break;
        }

        if (hasParens) {
            if (builder.getTokenType() == RenpyTokenTypes.RPAREN) {
                builder.advanceLexer();
                if (exprCount == 1 && !hasComma) {
                    stmt.rollbackTo();
                    return null;
                }
            } else {
                builder.error("Expected ')' at end of tuple.");
                stmt.drop();
                return null;
            }
        } else {
            if (exprCount == 1 && !hasComma) {
                stmt.rollbackTo();
                return null;
            }
        }

        stmt.done(RenpyElementTypes.TUPLE);
        return RenpyElementTypes.TUPLE;
    }
}

