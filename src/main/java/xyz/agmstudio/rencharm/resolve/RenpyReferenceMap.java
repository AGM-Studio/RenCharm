package xyz.agmstudio.rencharm.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import xyz.agmstudio.rencharm.psi.elements.StmDeclaration;

import java.util.*;

public class RenpyReferenceMap {
    public static Map<String, List<StmDeclaration>> collectAllDefines(Project project) {
        Map<String, List<StmDeclaration>> result = new HashMap<>();
        Collection<VirtualFile> renpyFiles = FilenameIndex.getAllFilesByExt(project, "rpy", GlobalSearchScope.projectScope(project));
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : renpyFiles) {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile == null) continue;

            Collection<StmDeclaration> defines = PsiTreeUtil.findChildrenOfType(psiFile, StmDeclaration.class);
            for (StmDeclaration def : defines) {
                String name = def.getName();
                if (name != null && !name.isBlank()) result.computeIfAbsent(name, k -> new ArrayList<>()).add(def);
            }
        }

        return result;
    }

    public static List<StmDeclaration> resolveAll(Project project, String name) {
        Map<String, List<StmDeclaration>> map = collectAllDefines(project);
        return map.getOrDefault(name, new ArrayList<>());
    }

    public static StmDeclaration resolve(Project project, String name) {
        List<StmDeclaration> list = resolveAll(project, name);
        if (list.isEmpty()) return null;

        return list.getFirst();
    }

    public static Collection<String> allDefinedNames(Project project) {
        return collectAllDefines(project).keySet();
    }
}
