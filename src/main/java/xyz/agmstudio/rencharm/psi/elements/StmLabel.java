package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class StmLabel extends ASTWrapperPsiElement {
    public static final RenpyElement STATEMENT = new RenpyElement("LABEL_STATEMENT");

    public StmLabel(@NotNull ASTNode node) {
        super(node);
    }

    public String getName() {
        PsiElement id = findChildByType(RenpyTokenTypes.IDENTIFIER);
        return id != null ? id.getText() : null;
    }
}
