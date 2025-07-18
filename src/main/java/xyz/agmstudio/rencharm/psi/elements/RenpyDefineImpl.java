package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.values.RenpyExpressionImpl;

import java.util.Objects;

public class RenpyDefineImpl extends ASTWrapperPsiElement {
    public RenpyDefineImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        PsiElement id = findChildByType(RenpyTokenTypes.IDENTIFIER);
        return id != null ? id.getText() : null;
    }

    public static void parse(PsiBuilder builder) {
        // define IDENTIFIER = VALUE;
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
        else builder.error("Expected an identifier to be defined.");
        if (builder.getTokenType() == RenpyTokenTypes.OPERATOR && Objects.equals(builder.getTokenText(), "=")) builder.advanceLexer();
        else builder.error("Expected '=' after the identifier but got '" + builder.getTokenText() + "'.");

        // Values for define
        if (RenpyExpressionImpl.getStatement(builder) == null)
            builder.error("Expected a value or expression but got '" + builder.getTokenText() + "'.");

        // ERROR EVERYTHING except for SEMICOLON till line ends!
        while (builder.getTokenType() != null && builder.getTokenType() != RenpyTokenTypes.NEWLINE) {
            if (builder.getTokenType() != RenpyTokenTypes.SEMICOLON) builder.error("Unexpected value '" + builder.getTokenText() + "'.");
            builder.advanceLexer();
        }

        stmt.done(RenpyElementTypes.DEFINE_STATEMENT);
    }
}
