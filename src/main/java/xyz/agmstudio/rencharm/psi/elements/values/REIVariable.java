package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;
import xyz.agmstudio.rencharm.psi.elements.StmDefine;
import xyz.agmstudio.rencharm.resolve.RenpyReferenceMap;

import java.util.ArrayList;
import java.util.List;

public class REIVariable extends ASTWrapperPsiElement implements PsiNamedElement {
    public static final RenpyElement ELEMENT = new RenpyElement("VARIABLE", REIVariable.class);
    public static final RenpyElement REFERRED = new RenpyElement("REFERRED",  Referred.class);

    public REIVariable(@NotNull ASTNode node) {
        super(node);
    }

    @Override public @Nullable String getName() {
        return getText();
    }

    @Override public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override public PsiReference getReference() {
        return new Referencer(this);
    }

    public static class Referred extends REIVariable {
        public Referred(@NotNull ASTNode node) {
            super(node);
        }
    }


    public static class Referencer extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        public Referencer(@NotNull PsiElement element) {
            super(element, TextRange.from(0, element.getTextLength()), true);
        }

        @Override public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
            String name = getElement().getText();
            List<StmDefine> defines = RenpyReferenceMap.resolveAll(getElement().getProject(), name);

            List<ResolveResult> results = new ArrayList<>();
            for (StmDefine define : defines)
                results.add(new PsiElementResolveResult(define.getIdentifier(), results.isEmpty()));

            return results.toArray(new ResolveResult[0]);
        }

        @Override public PsiElement resolve() {
            ResolveResult[] results = multiResolve(false);
            if (results.length == 0) return null;

            return results[0].getElement();
        }

        @Override public Object @NotNull [] getVariants() {
            return RenpyReferenceMap.allDefinedNames(getElement().getProject()).toArray();
        }
    }
}
