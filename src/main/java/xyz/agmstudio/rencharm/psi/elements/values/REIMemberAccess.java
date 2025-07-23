package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class REIMemberAccess extends ASTWrapperPsiElement {
    public REIMemberAccess(@NotNull ASTNode node) {
        super(node);
    }

    public static IElementType getStatement(PsiBuilder builder) {
        PsiBuilder.Marker base = builder.mark();
        IElementType left = REIExpressions.getPrimaryStatement(builder);
        if (left == null) {
            base.drop();
            return null;
        }

        while (builder.getTokenType() == RenpyTokenTypes.DOT) {
            builder.advanceLexer();
            IElementType function = REIFunctionCall.getStatement(builder);
            if (function == null) {
                if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) {
                    builder.error("Expected property or method name after '.'");
                    break;
                }
                builder.advanceLexer();
            }

            base.done(RenpyElementTypes.MEMBER_ACCESS);
            base = base.precede();
        }

        base.drop(); // if no further chaining
        return RenpyElementTypes.MEMBER_ACCESS;
    }
}
