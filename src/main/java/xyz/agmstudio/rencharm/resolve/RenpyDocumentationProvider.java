package xyz.agmstudio.rencharm.resolve;

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.intellij.lang.Language;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        if (element instanceof REIVariable variable) return variable.getDoc();
        return null;
    }

    @Override public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        String payload = link.startsWith(DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL) ?
                link.substring(DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL.length()) : link;

        int hash = payload.lastIndexOf('#');
        if (hash > 0) {
            String filePath = payload.substring(0, hash);
            int offset = Integer.parseInt(payload.substring(hash + 1));
            VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (vf != null) {
                PsiFile psiFile = psiManager.findFile(vf);
                if (psiFile != null) return psiFile.findElementAt(offset);
            }
        }
        return null;
    }

    public static @NotNull String highlightCode(@NotNull Project project, @NotNull Language language, @Nullable String codeSnippet) {
        StringBuilder sb = new StringBuilder();
        highlightCode(sb, project, language, codeSnippet);
        return sb.toString();
    }
    public static void highlightCode(StringBuilder buffer, @NotNull Project project, @NotNull Language language, @Nullable String codeSnippet) {
        if (codeSnippet == null) return;
        HtmlSyntaxInfoUtil.appendHighlightedByLexerAndEncodedAsHtmlCodeSnippet(buffer, project, language, codeSnippet, 1.0f);
    }
}
