package xyz.agmstudio.rencharm.resolve;

import com.intellij.lang.Language;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.elements.StmDeclaration;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

public class RenpyDocumentationProvider implements DocumentationProvider {
    @Override public @Nullable @Nls String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }
    @Override public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof REIVariable variable) {
            PsiReference ref = variable.getReference();
            PsiElement resolved = ref != null ? ref.resolve() : null;

            if (resolved == null) return null;

            StmDeclaration decl = resolved instanceof StmDeclaration
                    ? (StmDeclaration) resolved
                    : PsiTreeUtil.getParentOfType(resolved, StmDeclaration.class);

            if (decl != null) {
                return DocumentationMarkup.DEFINITION_START +
                        "<b>Variable defined at:</b><br><pre><code>" + highlightCode(resolved.getProject(), resolved.getLanguage(), decl.getText()) + "</code></pre>" +
                        DocumentationMarkup.DEFINITION_END;
            }
        }
        return null;
    }

    public static @NotNull String highlightCode(@NotNull Project project, @NotNull Language language, @Nullable String codeSnippet) {
        if (codeSnippet == null) return "";
        StringBuilder sb = new StringBuilder();
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(sb, project, language, codeSnippet, 1.0f);
        return sb.toString();
    }
}
