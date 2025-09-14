package xyz.agmstudio.rencharm.psi.elements.values;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.elements.RenpyElement;
import xyz.agmstudio.rencharm.psi.elements.StmDeclaration;
import xyz.agmstudio.rencharm.resolve.RenpyDocumentationProvider;
import xyz.agmstudio.rencharm.resolve.RenpyReferenceMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class REIVariable extends ASTWrapperPsiElement implements PsiNamedElement {
    public static final RenpyElement ELEMENT = new RenpyElement("VARIABLE", REIVariable.class);
    public static final RenpyElement LABEL = new RenpyElement("LABEL_VARIABLE", Label.class);
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

    @Override public Referencer getReference() {
        return new Referencer(this);
    }

    public String getDoc() {
        PsiElement resolved = new Referencer(this).resolve();
        if (resolved == null) return null;

        StmDeclaration declaration = PsiTreeUtil.getParentOfType(resolved, StmDeclaration.class);
        if (declaration == null) return null;

        StringBuilder result = new StringBuilder(DocumentationMarkup.DEFINITION_START);
        result.append("<b>Variable defined at: </b><code>");

        PsiFile containingFile = resolved.getContainingFile();
        VirtualFile vFile = containingFile.getVirtualFile();
        result.append(vFile != null ? vFile.getName() : "<unknown file>");

        Document doc = PsiDocumentManager.getInstance(resolved.getProject()).getDocument(containingFile);
        if (doc != null) {
            int lineNumber = doc.getLineNumber(resolved.getTextOffset()) + 1;
            result.append(lineNumber > 0 ? ":" + lineNumber : "");
        }

        result.append("</code><br><pre><code>");
        RenpyDocumentationProvider.highlightCode(result, resolved.getProject(), resolved.getLanguage(), declaration.getText());
        result.append("</pre></code>");
        result.append(DocumentationMarkup.DEFINITION_END);

        return result.toString();
    }

    public static class Referred extends REIVariable {
        public Referred(@NotNull ASTNode node) {
            super(node);
        }
    }

    public static class Label extends REIVariable {
        public Label(@NotNull ASTNode node) {
            super(node);
        }
    }

    public static class Referencer extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        public Referencer(@NotNull PsiElement element) {
            super(element, TextRange.from(0, element.getTextLength()), true);
        }

        @Override public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
            String name = getElement().getText();
            List<StmDeclaration> declarations = RenpyReferenceMap.resolveAll(getElement().getProject(), name);

            declarations.sort(Comparator.comparing(StmDeclaration::getFileName));

            List<ResolveResult> results = new ArrayList<>();
            for (StmDeclaration declaration : declarations)
                results.add(new PsiElementResolveResult(declaration.getIdentifier(), results.isEmpty()));

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
