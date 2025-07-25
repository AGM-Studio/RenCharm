package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.values.REIExpressions;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

import java.util.Objects;

public class StmDefine extends ASTWrapperPsiElement implements PsiNamedElement {
    public static final RenpyElement STATEMENT = new RenpyElement("DEFINE_STATEMENT", StmDefine.class);

    public StmDefine(@NotNull ASTNode node) {
        super(node);
    }

    @Override public String getName() {
        PsiElement id = getIdentifier();
        return id != null ? id.getText() : null;
    }

    @Override public PsiElement setName(@NotNull String name) {
        // Optional: implement rename support later
        return this;
    }

    public PsiElement getIdentifier() {
        return findChildByType(REIVariable.ELEMENT);
    }

    public static void parse(PsiBuilder builder) {
        // define IDENTIFIER = VALUE;
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(REIVariable.ELEMENT);
        }
        else builder.error("Expected an identifier to be defined.");
        if (builder.getTokenType() == RenpyTokenTypes.OPERATOR && Objects.equals(builder.getTokenText(), "=")) builder.advanceLexer();
        else builder.error("Expected '=' after the identifier but got '" + builder.getTokenText() + "'.");

        // Values for define
        if (REIExpressions.getBareStatement(builder) == null)
            builder.error("Expected a value or expression but got '" + builder.getTokenText() + "'.");

        // ERROR EVERYTHING except for SEMICOLON till line ends!
        while (builder.getTokenType() != null && builder.getTokenType() != RenpyTokenTypes.NEWLINE) {
            if (builder.getTokenType() != RenpyTokenTypes.SEMICOLON) builder.error("Unexpected value '" + builder.getTokenText() + "'.");
            builder.advanceLexer();
        }

        stmt.done(STATEMENT);
    }
}
