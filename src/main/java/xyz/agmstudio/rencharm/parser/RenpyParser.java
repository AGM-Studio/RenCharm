package xyz.agmstudio.rencharm.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.StmDefine;
import xyz.agmstudio.rencharm.psi.elements.StmLabel;

public class RenpyParser implements PsiParser {
    @NotNull
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker file = builder.mark();
        while (!builder.eof()) parseStatement(builder);
        file.done(root);
        return builder.getTreeBuilt();
    }

    private void parseStatement(PsiBuilder builder) {
        builder.setDebugMode(true);
        if (builder.getTokenType() == RenpyTokenTypes.PRIMARY_KEYWORD) {
            final String key = builder.getTokenText();
            switch (key) {
                case "define":
                    StmDefine.parse(builder);
                    return;
            }
        }
        if (builder.getTokenType() == RenpyTokenTypes.LABEL) {
            PsiBuilder.Marker stmt = builder.mark();
            builder.advanceLexer();

            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
            else builder.error("Expected an identifier for the label.");
            if (builder.getTokenType() == RenpyTokenTypes.COLON) builder.advanceLexer();
            else builder.error("Expected ':' after label.");

            parseIndentedBlock(builder);

            stmt.done(StmLabel.STATEMENT);
        } else {
            builder.advanceLexer(); // skip everything else for now
        }
    }

    private void parseIndentedBlock(PsiBuilder builder) {
        while (builder.getTokenType() == RenpyTokenTypes.NEWLINE) builder.advanceLexer();

        if (builder.getTokenType() == RenpyTokenTypes.INDENT) {
            builder.advanceLexer();
            while (!builder.eof()) {
                if (builder.getTokenType() == RenpyTokenTypes.NEWLINE) {
                    builder.advanceLexer();
                    boolean indent = false;
                    while (builder.getTokenType() == RenpyTokenTypes.INDENT) {
                        builder.advanceLexer();
                        indent = true;
                    }
                    while (builder.getTokenType() == RenpyTokenTypes.WHITE_SPACE) builder.advanceLexer();
                    if (builder.getTokenType() != RenpyTokenTypes.NEWLINE && !indent) break;
                }

                parseStatement(builder);
            }
        }
    }
}
