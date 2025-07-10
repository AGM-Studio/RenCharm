package xyz.agmstudio.rencharm.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class RenpyPsiElement extends ASTWrapperPsiElement {
    public RenpyPsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
