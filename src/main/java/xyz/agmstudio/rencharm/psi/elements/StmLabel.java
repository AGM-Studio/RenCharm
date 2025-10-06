package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.label.StmDialogue;
import xyz.agmstudio.rencharm.psi.elements.label.StmShow;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

import java.util.HashSet;

/**
 * label IDENTIFIER:
 *     LABEL_BODY
 */
public class StmLabel extends ASTWrapperPsiElement {
    public static final RenpyElement STATEMENT = new RenpyElement("LABEL_STATEMENT", StmLabel.class);

    private static final HashSet<RenpyElement> ELEMENTS = new HashSet<>();
    static {
        ELEMENTS.add(StmDialogue.STATEMENT);
        ELEMENTS.add(StmShow.STATEMENT);
    }


    public static final RenpyElement RETURN_STATEMENT = new RenpyElement.Singleton("return", ELEMENTS);

    public StmLabel(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        PsiElement id = findChildByType(RenpyTokenTypes.IDENTIFIER);
        return id != null ? id.getText() : null;
    }

    public static PsiBuilder.Marker parse(PsiBuilder builder) {
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(REIVariable.LABEL);
        }
        else builder.error("Expected an identifier to be defined.");

        RenpyTokenTypes.finishLine(builder, RenpyTokenTypes.COLON);

        if (RenpyTokenTypes.NEWLINE.isToken(builder)) builder.advanceLexer();
        while (builder.getTokenType() == RenpyTokenTypes.INDENT) {
            builder.advanceLexer();
            PsiBuilder.Marker body = StmLabel.parseBody(builder);

            if (RenpyTokenTypes.NEWLINE.isToken(builder)) builder.advanceLexer();
        }

        stmt.done(STATEMENT);
        return stmt;
    }

    public static PsiBuilder.Marker parseBody(PsiBuilder builder) {
        for (RenpyElement element: ELEMENTS) {
            PsiBuilder.Marker result = element.tryParse(builder);
            if (result != null) return result;
        }

        if (RenpyTokenTypes.NEWLINE.isToken(builder)) return null;

        PsiBuilder.Marker unknown = builder.mark();
        while (!RenpyTokenTypes.NEWLINE.isToken(builder))
            builder.advanceLexer();

        unknown.error("Unknown statement inside label.");
        return null;
    }
}
