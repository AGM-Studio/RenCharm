package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.resolve.RenpyVariableReference;

public class REIReference extends ASTWrapperPsiElement implements PsiNamedElement {
    public REIReference(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RenpyVariableReference(this);
    }

    @Override public String getName() {
        return getText();
    }

    @Override public PsiElement setName(@NotNull String name) {
        // Optional: Implement for rename
        return this;
    }
}
