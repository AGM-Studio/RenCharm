package xyz.agmstudio.rencharm.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public class RenpyFile extends PsiFileBase {
    public RenpyFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, RenpyFileType.Language.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return RenpyFileType.INSTANCE;
    }
}
