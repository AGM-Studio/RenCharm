package xyz.agmstudio.rencharm.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyParser implements PsiParser {
    @NotNull
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker file = builder.mark();
        while (!builder.eof()) parseStatement(builder);
        file.done(root);
        return builder.getTreeBuilt();
    }

    private void parseStatement(PsiBuilder builder) {
        if (builder.getTokenType() == RenpyTokenTypes.LABEL) {
            PsiBuilder.Marker stmt = builder.mark();
            builder.advanceLexer(); // label
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
                System.out.println(builder.getTokenText());
                builder.advanceLexer(); // label name
            }
            if (builder.getTokenType() == RenpyTokenTypes.COLON) {
                builder.advanceLexer(); // :
            }
            stmt.done(RenpyElementTypes.LABEL_STATEMENT);
        } else {
            builder.advanceLexer(); // skip unknowns
        }
    }
}

