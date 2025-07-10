package xyz.agmstudio.rencharm.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class RenpyFileType extends LanguageFileType {
    private static final Icon FILE_ICON = IconLoader.getIcon("/icons/rpy.png", RenpyFileType.class);

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
}