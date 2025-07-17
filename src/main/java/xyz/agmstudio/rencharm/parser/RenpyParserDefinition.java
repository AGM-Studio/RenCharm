package xyz.agmstudio.rencharm.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyFileType;
import xyz.agmstudio.rencharm.lexer.RenpyLexer;
import xyz.agmstudio.rencharm.psi.RenpyElementTypes;
import xyz.agmstudio.rencharm.psi.RenpyPsiElement;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.RenpyLabelImpl;

public class RenpyParserDefinition implements ParserDefinition {
    @Override public @NotNull Lexer createLexer(Project project) {
        return new RenpyLexer();
    }
    @Override public @NotNull PsiParser createParser(Project project) {
        return new RenpyParser();
    }
    @Override public @NotNull IFileElementType getFileNodeType() {
        return RenpyFileElementType.INSTANCE;
    }
    @Override public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(RenpyTokenTypes.WHITE_SPACE);
    }
    @Override public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(RenpyTokenTypes.COMMENT);
    }
    @Override public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(RenpyTokenTypes.STRING);
    }
    @Override public @NotNull PsiElement createElement(ASTNode node) {
        if (node.getElementType() == RenpyElementTypes.LABEL_STATEMENT) return new RenpyLabelImpl(node);
        return new RenpyPsiElement(node);
    }
    @Override public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new RenpyFileType.File(viewProvider);
    }
}
