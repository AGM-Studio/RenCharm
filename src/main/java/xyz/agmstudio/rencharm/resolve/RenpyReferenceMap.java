package xyz.agmstudio.rencharm.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import xyz.agmstudio.rencharm.psi.elements.StmDefine;

import java.util.*;

public class RenpyReferenceMap {
    public static Map<String, List<StmDefine>> collectAllDefines(Project project) {
        Map<String, List<StmDefine>> result = new HashMap<>();
        Collection<VirtualFile> renpyFiles = FilenameIndex.getAllFilesByExt(project, "rpy", GlobalSearchScope.projectScope(project));
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : renpyFiles) {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile == null) continue;

            Collection<StmDefine> defines = PsiTreeUtil.findChildrenOfType(psiFile, StmDefine.class);
            for (StmDefine def : defines) {
                String name = def.getName();
                if (name != null && !name.isBlank()) {
                    result.computeIfAbsent(name, k -> new ArrayList<>()).add(def);
                }
            }
        }

        return result;
    }

    public static List<StmDefine> resolveAll(Project project, String name) {
        Map<String, List<StmDefine>> map = collectAllDefines(project);
        return map.getOrDefault(name, List.of());
    }

    public static StmDefine resolve(Project project, String name) {
        List<StmDefine> list = resolveAll(project, name);
        if (list.isEmpty()) return null;

        return list.getFirst();
    }

    public static Collection<String> allDefinedNames(Project project) {
        return collectAllDefines(project).keySet();
    }
}
