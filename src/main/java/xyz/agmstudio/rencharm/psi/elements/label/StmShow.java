package xyz.agmstudio.rencharm.psi.elements.label;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

import java.util.HashSet;

/**
 * show IMAGE (as NAME)? (at TRANSFORM+)? (behind IMAGE)? (onlayer LAYER)? (with TRANSITION+)? ()
 */
public class StmShow extends ASTWrapperPsiElement {
    public static final RenpyElement STATEMENT = new RenpyElement("SHOW_STATEMENT", StmShow.class);

    public static final RenpyElement IMAGE = new RenpyElement("IMAGE");  // TODO KEYWORD CONSUMERS
    public static final RenpyElement AS_ALIAS = new RenpyElement("AS_ALIAS");
    public static final RenpyElement TRANSFORM = new RenpyElement("TRANSFORM");
    public static final RenpyElement BEHIND = new RenpyElement("BEHIND");
    public static final RenpyElement LAYER = new RenpyElement("LAYER");
    public static final RenpyElement TRANSITION = new RenpyElement("TRANSITION");
    public static final RenpyElement ZORDER = new RenpyElement("ZORDER");

    public StmShow(@NotNull ASTNode node) {
        super(node);
    }

    public static PsiBuilder.Marker parse(PsiBuilder builder) {
        if (!RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "show")) return null;

        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        PsiBuilder.Marker image = builder.mark();
        while (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
        if (image.getTextLength() == 0) {
            builder.error("Missing image value");
            image.drop();
        } else image.done(IMAGE);
        HashSet<RenpyElement> optionals = new HashSet<>();

        while (true) {
            boolean result = parseOptional(builder, optionals);
            if (!result) break;
        }

        RenpyTokenTypes.finishLine(builder, RenpyTokenTypes.SEMICOLON);

        if (RenpyTokenTypes.NEWLINE.isToken(builder)) builder.advanceLexer();
        stmt.done(STATEMENT);
        return stmt;
    }

    public static boolean parseOptional(PsiBuilder builder, HashSet<RenpyElement> optionals) {
        if (!optionals.contains(AS_ALIAS) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "as")) {
            builder.advanceLexer();
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) AS_ALIAS.mark(builder);
            else builder.error("Missing identifier for \"as\" parameter");
            return optionals.add(AS_ALIAS);
        }

        if (!optionals.contains(TRANSFORM) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "at")) {
            builder.advanceLexer();
            if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) builder.error("Missing transform identifier");
            else {
                PsiBuilder.Marker mark = builder.mark();
                while (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    if (builder.getTokenType() == RenpyTokenTypes.COMMA) {
                        if (builder.lookAhead(1) == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
                        else builder.error("Unexpected comma");
                    }
                    else if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) builder.error("Missing comma between transforms");
                }

                mark.done(TRANSFORM);
            }
            return optionals.add(TRANSFORM);
        }

        if (!optionals.contains(BEHIND) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "behind")) {
            builder.advanceLexer();
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) BEHIND.mark(builder);
            else builder.error("Missing identifier for \"behind\" parameter");
            return optionals.add(BEHIND);
        }

        if (!optionals.contains(LAYER) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "onlayer")) {
            builder.advanceLexer();
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) LAYER.mark(builder);
            else builder.error("Missing identifier for \"onlayer\" parameter");
            return optionals.add(LAYER);
        }

        if (!optionals.contains(TRANSITION) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "with")) {
            builder.advanceLexer();
            if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) builder.error("Missing transition identifier");
            else {
                PsiBuilder.Marker mark = builder.mark();
                while (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    if (builder.getTokenType() == RenpyTokenTypes.COMMA) {
                        if (builder.lookAhead(1) == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
                        else builder.error("Unexpected comma");
                    }
                    else if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) builder.error("Missing comma between transitions");
                }

                mark.done(TRANSITION);
            }
            return optionals.add(TRANSITION);
        }

        if (!optionals.contains(ZORDER) && RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "zorder")) {
            builder.advanceLexer();
            if (builder.getTokenType() == RenpyTokenTypes.NUMBER) ZORDER.mark(builder);
            else builder.error("Missing zorder parameter");
            return optionals.add(ZORDER);
        }

        return false;
    }
}
