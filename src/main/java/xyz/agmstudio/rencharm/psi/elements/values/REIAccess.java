package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

import java.util.concurrent.atomic.AtomicBoolean;

public class REIAccess {
    public static final RenpyElement MEMBER_ACCESS  = new RenpyElement("MEMBER_ACCESS", Membership.class);
    public static final RenpyElement INDEX_ACCESS   = new RenpyElement("INDEX_ACCESS", Index.class);
    public static final RenpyElement SLICE_ACCESS   = new RenpyElement("SLICE_ACCESS", Slice.class);

    public static IElementType getStatement(PsiBuilder builder) {
        PsiBuilder.Marker base = builder.mark();
        IElementType left = REIExpressions.getPrimaryStatement(builder);
        if (left == null) {
            base.rollbackTo();
            return null;
        }

        while (true) {
            if (builder.getTokenType() == RenpyTokenTypes.DOT) {
                builder.advanceLexer();

                IElementType function = REIFunctionCall.getStatement(builder);
                if (function == null) {
                    if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) {
                        builder.error("Expected property or method name after '.'");
                        break;
                    }
                    builder.advanceLexer();
                }

                left = MEMBER_ACCESS;
                base.done(MEMBER_ACCESS);
                base = base.precede();
                continue;
            }

            if (builder.getTokenType() == RenpyTokenTypes.LBRACKET) {
                builder.advanceLexer();

                IElementType expr = REIExpressions.getStatement(builder);
                if (expr == null && builder.getTokenType() != RenpyTokenTypes.COLON)
                    builder.error("Expected index expression or ':'");

                AtomicBoolean slice = new AtomicBoolean(false);
                if (builder.getTokenType() == RenpyTokenTypes.COLON) {
                    slice.set(true);
                    builder.advanceLexer();
                    REIExpressions.getStatement(builder);
                    if (builder.getTokenType() == RenpyTokenTypes.COLON) {
                        builder.advanceLexer();
                        REIExpressions.getStatement(builder);
                    }
                }

                if (builder.getTokenType() == RenpyTokenTypes.RBRACKET) builder.advanceLexer();
                else builder.error("Expected ']'");

                left = slice.get() ? SLICE_ACCESS : INDEX_ACCESS;
                base.done(slice.get() ? SLICE_ACCESS : INDEX_ACCESS);
                base = base.precede();
                continue;
            }

            break;
        }

        base.drop();
        return left;
    }

    public record ChainResult (IElementType type, boolean end) {
        public boolean isValid() {
            return type != null;
        }
    }

    public static class Index extends ASTWrapperPsiElement {
        public Index(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class Slice extends ASTWrapperPsiElement {
        public Slice(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class Membership extends ASTWrapperPsiElement {
        public Membership(@NotNull ASTNode node) {
            super(node);
        }
    }
}
