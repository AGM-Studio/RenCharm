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
    public static Map<String, StmDefine> collectAllDefines(Project project) {
        Map<String, StmDefine> result = new HashMap<>();
        Collection<VirtualFile> renpyFiles = FilenameIndex.getAllFilesByExt(project, "rpy", GlobalSearchScope.projectScope(project));

        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : renpyFiles) {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile == null) continue;

            Collection<StmDefine> defines = PsiTreeUtil.findChildrenOfType(psiFile, StmDefine.class);
            for (StmDefine def : defines) {
                String name = def.getName();
                if (name != null && !name.isBlank()) {
                    result.put(name, def);
                }
            }
        }

        return result;
    }

    public static StmDefine resolve(Project project, String name) {
        return collectAllDefines(project).get(name);
    }

    public static Collection<String> allDefinedNames(Project project) {
        return collectAllDefines(project).keySet();
    }
}
