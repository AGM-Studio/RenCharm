package xyz.agmstudio.rencharm.psi.elements.label;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;
import xyz.agmstudio.rencharm.psi.elements.values.REIFunctionCall;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

/**
 * (IDENTIFIER|STRING)? STRING (ARGUMENTS)?
 */
public class StmDialogue extends ASTWrapperPsiElement {
    public static final RenpyElement STATEMENT = new RenpyElement("DIALOGUE_STATEMENT", StmDialogue.class);

    public static final RenpyElement WHO_VAR = new RenpyElement("WHO_VAR", REIVariable.Referred.class);
    public static final RenpyElement WHO = new RenpyElement("WHO");
    public static final RenpyElement WHAT = new RenpyElement("WHAT");

    public static final RenpyElement DIALOGUE_POSED = new RenpyElement("DIALOGUE_POSED_EXPRESSION", REIFunctionCall.PosedArgument.class);
    public static final RenpyElement DIALOGUE_NAMED = new RenpyElement("DIALOGUE_NAMED_EXPRESSION", REIFunctionCall.NamedArgument.class);

    public StmDialogue(@NotNull ASTNode node) {
        super(node);
    }

    public static PsiBuilder.Marker parse(PsiBuilder builder) {
        PsiBuilder.Marker stmt = builder.mark();
        boolean hasIdentifier = builder.getTokenType() == RenpyTokenTypes.IDENTIFIER;
        if (hasIdentifier) WHO_VAR.mark(builder);
        if (builder.getTokenType() == RenpyTokenTypes.STRING) {
            if (!hasIdentifier && builder.lookAhead(1) == RenpyTokenTypes.STRING)
                WHO.mark(builder);

            PsiBuilder.Marker marker = WHAT.mark(builder);
            boolean hasArguments = REIFunctionCall.parseArguments(builder, DIALOGUE_POSED, DIALOGUE_NAMED);

            RenpyTokenTypes.finishLine(builder, RenpyTokenTypes.SEMICOLON);

            stmt.done(STATEMENT);
            return stmt;
        }

        stmt.rollbackTo();
        return null;
    }
}
