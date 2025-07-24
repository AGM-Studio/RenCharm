package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

import java.util.concurrent.atomic.AtomicBoolean;

public class REIChained {
    private static ChainResult consume(PsiBuilder builder) {
        ChainResult result = Membership.consume(builder);
        if (result != null) return result;

        return Index.consume(builder);
    }

    public static IElementType getChainedStatement(PsiBuilder builder) {
        PsiBuilder.Marker base = builder.mark();
        IElementType left = REIExpressions.getPrimaryStatement(builder);
        if (left == null) {
            base.rollbackTo();
            return null;
        }

        while (true) {
            ChainResult result = consume(builder);
            if (result != null) {
                if (result.end) break;

                left = result.type;
                base.done(result.type);
                base = base.precede();
            } else break;
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

        public static ChainResult consume(PsiBuilder builder) {
            if (builder.getTokenType() != RenpyTokenTypes.LBRACKET) return null;
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

            return new ChainResult(slice.get() ? RenpyElementTypes.SLICE_ACCESS : RenpyElementTypes.INDEX_ACCESS, false);
        }
    }
    public static class Membership extends ASTWrapperPsiElement {
        public Membership(@NotNull ASTNode node) {
            super(node);
        }

        public static ChainResult consume(PsiBuilder builder) {
            if (builder.getTokenType() != RenpyTokenTypes.DOT) return null;
            builder.advanceLexer();

            IElementType function = REIFunctionCall.getStatement(builder);
            if (function == null) {
                if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) {
                    builder.error("Expected property or method name after '.'");
                    return new ChainResult(null, true);
                }
                builder.advanceLexer();
            }

            return new ChainResult(RenpyElementTypes.MEMBER_ACCESS, false);
        }
    }
}
