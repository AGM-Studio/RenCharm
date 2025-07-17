package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpyLabelImpl extends ASTWrapperPsiElement {
    public RenpyLabelImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        PsiElement id = findChildByType(RenpyTokenTypes.IDENTIFIER);
        return id != null ? id.getText() : null;
    }
}
