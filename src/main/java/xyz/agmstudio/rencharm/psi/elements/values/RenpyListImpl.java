package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenpyListImpl extends ASTWrapperPsiElement {
    public RenpyListImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static @Nullable IElementType getStatement(PsiBuilder builder) {
        return null;
    }
}
