package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

public class REIFunctionCall extends ASTWrapperPsiElement {
    public static final RenpyElement ELEMENT        = new RenpyElement("FUNCTION_CALL_EXPRESSION", REIFunctionCall.class);
    public static final RenpyElement ARGUMENT_POSED = new RenpyElement("ARGUMENT_POSED_EXPRESSION", PosedArgument.class);
    public static final RenpyElement ARGUMENT_NAMED = new RenpyElement("ARGUMENT_NAMED_EXPRESSION", NamedArgument.class);

    public REIFunctionCall(@NotNull ASTNode node) {
        super(node);
    }

    public static IElementType getStatement(PsiBuilder builder) {
        if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) return null;
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        if (builder.getTokenType() != RenpyTokenTypes.LPAREN) {
            stmt.rollbackTo();
            return null; // It's probably an IDENTIFIER
        }
        builder.advanceLexer();

        boolean is_named = false;
        while (builder.getTokenType() != RenpyTokenTypes.RPAREN && builder.getTokenType() != null) {
            PsiBuilder.Marker argument = builder.mark();

            // Try named argument (IDENTIFIER = expr)
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
                builder.advanceLexer();
                if (RenpyTokenTypes.OPERATOR.isToken(builder, "=")) {
                    builder.advanceLexer();
                    IElementType value = REIExpressions.getStatement(builder);
                    if (value == null) builder.error("Expected expression after '='");
                    argument.done(ARGUMENT_NAMED);
                    is_named = true;
                } else {
                    // It's a positional argument, avoid recalling it by calling it now!
                    if (is_named) builder.error("Unexpected positioned argument.");
                    argument.done(ARGUMENT_POSED);
                }
            } else { // Must be an expression then!
                IElementType expr = REIExpressions.getStatement(builder);
                if (expr == null) {
                    builder.error("Expected an expression as n argument");
                    argument.rollbackTo();
                    break;
                } else {
                    if (is_named) builder.error("Unexpected positioned argument.");
                    argument.done(ARGUMENT_POSED);
                }
            }

            if (builder.getTokenType() == RenpyTokenTypes.COMMA) builder.advanceLexer();
            else break;
        }

        if (builder.getTokenType() == RenpyTokenTypes.RPAREN) builder.advanceLexer();
        else builder.error("Expected ')' at end of function call.");

        stmt.done(ELEMENT);
        return ELEMENT;
    }

    public static class PosedArgument extends ASTWrapperPsiElement {
        public PosedArgument(@NotNull ASTNode node) {
            super(node);
        }
    }
    public static class NamedArgument extends ASTWrapperPsiElement {
        public NamedArgument(@NotNull ASTNode node) {
            super(node);
        }
    }
}
