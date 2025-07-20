package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class REIGroups {
    public static @Nullable IElementType getStatement(PsiBuilder builder) {
        IElementType token;
        if (builder.getTokenType() == RenpyTokenTypes.LPAREN) {
            if ((token = getGroupStatement(builder)) != null) return token;
            else return getSequenceStatement(builder, RenpyElementTypes.TUPLE, RenpyTokenTypes.LPAREN, RenpyTokenTypes.RPAREN, "Expected ')' at the end of tuple");
        }
        if (builder.getTokenType() == RenpyTokenTypes.LBRACKET)
            return getSequenceStatement(builder, RenpyElementTypes.LIST, RenpyTokenTypes.LBRACKET, RenpyTokenTypes.RBRACKET, "Expected ']' at the end of list");
        if (builder.getTokenType() == RenpyTokenTypes.LBRACE) {
            token = getSequenceStatement(builder, RenpyElementTypes.SET, RenpyTokenTypes.LBRACE, RenpyTokenTypes.RBRACE, "Expected '}' at the end of set");
            if (token != null) return token;
            else return null;  // Todo - Dictionaries
        }
        return null;
    }

    private static @Nullable IElementType getGroupStatement(PsiBuilder builder) {
        if (builder.getTokenType() != RenpyTokenTypes.LPAREN) return null;

        PsiBuilder.Marker group = builder.mark();
        builder.advanceLexer();

        IElementType expr = REIExpressions.getStatement(builder);
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
    private static @Nullable IElementType getSequenceStatement(PsiBuilder builder, IElementType type, IElementType start, IElementType end, String missing) {
        if (builder.getTokenType() != start) return null;
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        while (true) {
            IElementType expr = REIExpressions.getStatement(builder);
            if (expr == null || builder.getTokenType() != RenpyTokenTypes.COMMA) break;
            builder.advanceLexer();
        }

        if (builder.getTokenType() == end) builder.advanceLexer();
        else builder.error(missing);

        stmt.done(type);
        return type;
    }

    public static class Group extends ASTWrapperPsiElement {
        public Group(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class List extends ASTWrapperPsiElement {
        public List(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class Tuple extends ASTWrapperPsiElement {
        public Tuple(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class Set extends ASTWrapperPsiElement {
        public Set(@NotNull ASTNode node) {
            super(node);
        }
    }
}
