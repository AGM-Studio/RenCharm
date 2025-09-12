package xyz.agmstudio.rencharm.inspections;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.elements.StmDefine;
import xyz.agmstudio.rencharm.resolve.RenpyReferenceMap;

import java.util.List;
import java.util.Map;

public class RenpyDuplicateDefineAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof StmDefine define)) return;

        String name = define.getName();
        if (name == null || name.isBlank()) return;

        Map<String, List<StmDefine>> allDefines = RenpyReferenceMap.collectAllDefines(element.getProject());
        List<StmDefine> sameNameDefines = allDefines.get(name);

        // Show error if this name is defined more than once
        if (sameNameDefines != null && sameNameDefines.size() > 1) {
            PsiElement id = define.getIdentifier();
            if (id != null && define.getTextRange().contains(id.getTextRange())) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate define for '" + name + "'")
                        .range(id.getTextRange())
                        .create();
            }
        }
    }
}
