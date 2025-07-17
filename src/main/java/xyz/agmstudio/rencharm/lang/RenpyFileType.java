package xyz.agmstudio.rencharm.lang;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class RenpyFileType extends LanguageFileType {
    public static final Icon FILE_ICON = IconLoader.getIcon("/icons/rpy.png", RenpyFileType.class);
    public static final RenpyFileType INSTANCE = new RenpyFileType();

    private RenpyFileType() { super(Language.INSTANCE); }

    @Override public @NotNull String getName()              { return "Ren'Py"; }
    @Override public @NotNull String getDescription()       { return "Ren'Py visual‑novel script"; }
    @Override public @NotNull String getDefaultExtension()  { return "rpy"; }
    @Override public @NotNull Icon getIcon()                { return FILE_ICON; }

    public static final class Language extends com.intellij.lang.Language {
        public static final Language INSTANCE = new Language();

        private Language() {
            super("RenPy");
        }
    }

    public static class File extends PsiFileBase {
        public File(@NotNull FileViewProvider provider) {
            super(provider, Language.INSTANCE);
        }

        @Override
        public @NotNull FileType getFileType() {
            return INSTANCE;
        }
    }
}