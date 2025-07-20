package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyGroupImpl extends ASTWrapperPsiElement {
    public RenpyGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static @Nullable IElementType getStatement(PsiBuilder builder) {
        if (builder.getTokenType() != RenpyTokenTypes.LPAREN) return null;

        PsiBuilder.Marker group = builder.mark();
        builder.advanceLexer();

        IElementType expr = RenpyExpressionImpl.getStatement(builder);
        if (builder.getTokenType() == RenpyTokenTypes.COMMA) { // It's a tuple!
            group.rollbackTo();
            return null;
        }

        if (builder.getTokenType() == RenpyTokenTypes.RPAREN) {
            if (expr == null) builder.error("Expected an expression inside group");
            builder.advanceLexer();
        } else builder.error("Expected ')'");

        group.done(RenpyElementTypes.GROUP);
        return RenpyElementTypes.GROUP;
    }
}
