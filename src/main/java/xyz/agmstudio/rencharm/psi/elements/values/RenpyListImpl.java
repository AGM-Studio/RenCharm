package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyListImpl extends ASTWrapperPsiElement {
    public RenpyListImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static @Nullable IElementType getStatement(PsiBuilder builder) {
        if (builder.getTokenType() != RenpyTokenTypes.LBRACKET) return null;
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        while (true) {
            IElementType expr = RenpyExpressionImpl.getStatement(builder);
            if (expr == null || builder.getTokenType() != RenpyTokenTypes.COMMA) break;
            builder.advanceLexer();
        }

        if (builder.getTokenType() == RenpyTokenTypes.RBRACKET) builder.advanceLexer();
        else builder.error("Expected ']' at end of list.");

        stmt.done(RenpyElementTypes.LIST);
        return RenpyElementTypes.LIST;
    }
}
