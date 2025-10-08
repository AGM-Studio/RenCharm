package xyz.agmstudio.rencharm.psi.elements.label;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

import java.util.ArrayList;
import java.util.List;

/**
 * show IMAGE (as NAME)? (at TRANSFORM+)? (behind IMAGE)? (onlayer LAYER)? (with TRANSITION+)? ()
 */
public class StmShow extends ASTWrapperPsiElement {
    public static final RenpyElement STATEMENT = new RenpyElement("SHOW_STATEMENT", StmShow.class);

    public static final RenpyElement IMAGE = new RenpyElement("IMAGE");
    // The elements!
    public static final RenpyElement ZORDER = new RenpyElement("ZORDER").withTokenConsumer(RenpyTokenTypes.NUMBER);
    public static final RenpyElement BEHIND = new RenpyElement("BEHIND").withMultipleIdentifierConsumer();
    public static final RenpyElement AS_ALIAS = new RenpyElement("AS_ALIAS", "as").withIdentifierConsumer();
    public static final RenpyElement ON_LAYER = new RenpyElement("ON_LAYER", "onlayer").withIdentifierConsumer();
    public static final RenpyElement TRANSFORM = new RenpyElement("TRANSFORM", "with").withMultipleIdentifierConsumer();
    public static final RenpyElement TRANSITION = new RenpyElement("TRANSITION", "at").withMultipleIdentifierConsumer();

    public StmShow(@NotNull ASTNode node) {
        super(node);
    }

    public static PsiBuilder.Marker parse(PsiBuilder builder) {
        if (!RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, "show")) return null;

        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        PsiBuilder.Marker image = builder.mark();
        boolean hasImage = false;
        while (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
            hasImage = true;
        }
        if (hasImage) image.done(IMAGE);
        else {
            builder.error("Missing image value");
            image.drop();
        }

        List<RenpyElement> optionals = new ArrayList<>(List.of(ZORDER, BEHIND, AS_ALIAS, ON_LAYER, TRANSFORM, TRANSITION));
        while (!optionals.isEmpty()) {
            boolean broken = false;
            for (RenpyElement optional: optionals)
                if (optional.parseKeyword(builder)) {
                    optionals.remove(optional);
                    broken = true;
                    break;
                }

            if (!broken) break;
        }

        RenpyTokenTypes.finishLine(builder, RenpyTokenTypes.SEMICOLON);

        if (RenpyTokenTypes.NEWLINE.isToken(builder)) builder.advanceLexer();
        stmt.done(STATEMENT);
        return stmt;
    }
}
