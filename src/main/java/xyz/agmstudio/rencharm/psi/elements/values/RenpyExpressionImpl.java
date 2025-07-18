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
        return getStatement(builder, null);
    }
    public static IElementType getStatement(PsiBuilder builder, IElementType skip) {
        PsiBuilder.Marker stmt = builder.mark();
        IElementType token;
        if (skip != RenpyElementTypes.TUPLE && (token = RenpyTupleImpl.getStatement(builder, stmt)) != null) return token;
        if (skip != RenpyElementTypes.LIST  && (token = RenpyListImpl.getStatement(builder, stmt)) != null) return token;

        IElementType currentToken = builder.getTokenType();
        if (currentToken == RenpyTokenTypes.IDENTIFIER || RenpyTokenTypes.LITERAL_VALUES.contains(currentToken)) {
            builder.advanceLexer();
            stmt.drop();
            return currentToken;
        }

        // Failed to parse
        stmt.rollbackTo();
        return null;
    }
}
