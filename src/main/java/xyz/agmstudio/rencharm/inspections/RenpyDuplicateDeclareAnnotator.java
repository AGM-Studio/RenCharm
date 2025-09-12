package xyz.agmstudio.rencharm.inspections;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.elements.StmDeclaration;
import xyz.agmstudio.rencharm.resolve.RenpyReferenceMap;

import java.util.List;
import java.util.Map;

public class RenpyDuplicateDeclareAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof StmDeclaration.Define declaration)) return;

        String name = declaration.getName();
        if (name == null || name.isBlank()) return;

        Map<String, List<StmDeclaration>> all = RenpyReferenceMap.collectAllDefines(element.getProject());
        List<StmDeclaration> sameNameDefines = all.get(name);

        // Show error if this name is defined more than once
        if (sameNameDefines != null && sameNameDefines.size() > 1) {
            PsiElement id = declaration.getIdentifier();
            if (id != null && declaration.getTextRange().contains(id.getTextRange())) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate define for '" + name + "'")
                        .range(id.getTextRange())
                        .create();
            }
        }
    }
}
