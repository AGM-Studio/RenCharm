package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;

public class REIVariable extends ASTWrapperPsiElement implements PsiNamedElement {
    public static final RenpyElement ELEMENT = new RenpyElement("VARIABLE", REIVariable.class);

    public REIVariable(@NotNull ASTNode node) {
        super(node);
    }

    @Override public @Nullable String getName() {
        return getText();
    }

    @Override public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }
}
