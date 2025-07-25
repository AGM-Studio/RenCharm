package xyz.agmstudio.rencharm.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.StmDefine;

public class RenpyVariableReference extends PsiReferenceBase<PsiElement> {

    public RenpyVariableReference(@NotNull PsiElement element) {
        super(element, TextRange.from(0, element.getTextLength()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        String name = getElement().getText();
        StmDefine resolved = RenpyReferenceMap.resolve(getElement().getProject(), name);
        if (resolved == null) return null;
        return resolved.getIdentifier();
    }

    @Override
    public Object @NotNull [] getVariants() {
        return RenpyReferenceMap.allDefinedNames(getElement().getProject()).toArray();
    }
}
