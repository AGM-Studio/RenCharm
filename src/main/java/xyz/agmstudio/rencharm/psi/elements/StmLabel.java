package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.values.REIFunctionCall;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

import java.util.HashSet;

/**
 * label IDENTIFIER:
 *     LABEL_BODY
 */
public class StmLabel extends ASTWrapperPsiElement {
    private static final HashSet<RenpyElement> ELEMENTS = new HashSet<>();

    public static final RenpyElement STATEMENT = new RenpyElement("LABEL_STATEMENT", StmLabel.class);
    public static final RenpyElement SAY_STATEMENT = new RenpyElement("SAY_STATEMENT", SayStatement.class, StmLabel.ELEMENTS);

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

        // ERROR EVERYTHING except for SEMICOLON till line ends!
        boolean found = false;
        while (!RenpyTokenTypes.NEWLINE.isToken(builder)) {
            if (builder.getTokenType() != RenpyTokenTypes.COLON || found) builder.error("Unexpected value '" + builder.getTokenText() + "'.");
            else found = true;
            builder.advanceLexer();
        }

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

    /**
     * (IDENTIFIER|STRING)? STRING (ARGUMENTS)?
     */
    public static class SayStatement extends ASTWrapperPsiElement {
        public static final RenpyElement WHO = new RenpyElement("WHO");
        public static final RenpyElement WHAT = new RenpyElement("WHAT");
        
        public static final RenpyElement SAY_POSED = new RenpyElement("SAY_POSED_EXPRESSION", REIFunctionCall.PosedArgument.class);
        public static final RenpyElement SAY_NAMED = new RenpyElement("SAY_NAMED_EXPRESSION", REIFunctionCall.NamedArgument.class);


        public SayStatement(@NotNull ASTNode node) {
            super(node);
        }
        
        private static PsiBuilder.Marker markAs(PsiBuilder builder, RenpyElement element) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(element);
            return marker;
        }

        public static PsiBuilder.Marker parse(PsiBuilder builder) {
            PsiBuilder.Marker stmt = builder.mark();
            boolean hasIdentifier = builder.getTokenType() == RenpyTokenTypes.IDENTIFIER;
            if (hasIdentifier) markAs(builder, WHO);
            if (builder.getTokenType() == RenpyTokenTypes.STRING) {
                if (!hasIdentifier && builder.lookAhead(1) == RenpyTokenTypes.STRING)
                    markAs(builder, WHO);

                PsiBuilder.Marker marker = markAs(builder, WHAT);
                boolean hasArguments = REIFunctionCall.parseArguments(builder, SAY_POSED, SAY_NAMED);

                while (!RenpyTokenTypes.NEWLINE.isToken(builder)) {
                    builder.error("Unexpected value '" + builder.getTokenText() + "' after say statement.");
                    builder.advanceLexer();
                }

                stmt.done(SAY_STATEMENT);
                return stmt;
            }

            stmt.rollbackTo();
            return null;
        }
    }
}
