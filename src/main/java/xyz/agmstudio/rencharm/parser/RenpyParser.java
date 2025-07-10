package xyz.agmstudio.rencharm.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class RenpyParser implements PsiParser {
    @NotNull @Override public ASTNode parse(@NotNull IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        while (!builder.eof()) builder.advanceLexer();
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }
}
