package xyz.agmstudio.rencharm.resolve;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

public class RenpyAccessDetector extends ReadWriteAccessDetector {
    @Override public boolean isReadWriteAccessible(@NotNull PsiElement element) {
        return element instanceof REIVariable;
    }

    @Override public boolean isDeclarationWriteAccess(@NotNull PsiElement element) {
        return element instanceof REIVariable;
    }

    @Override public @NotNull Access getReferenceAccess(@NotNull PsiElement element, @NotNull PsiReference reference) {
        if (element instanceof REIVariable.Referred) return Access.Read;
        if (element instanceof REIVariable) return Access.Write;
        return Access.ReadWrite;
    }

    @Override public @NotNull Access getExpressionAccess(@NotNull PsiElement element) {
        if (element instanceof REIVariable.Referred) return Access.Read;
        if (element instanceof REIVariable) return Access.Write;
        return Access.ReadWrite;
    }
}
