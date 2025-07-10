package xyz.agmstudio.rencharm.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RenpyCompiledFileType extends LanguageFileType {
    public static final RenpyCompiledFileType INSTANCE = new RenpyCompiledFileType();

    private RenpyCompiledFileType() { super(Language.INSTANCE); }

    @NotNull @Override public String getName()             { return "Ren'Py compiled"; }
    @NotNull @Override public String getDescription()      { return "Ren'Py .rpyc byte‑code file"; }
    @NotNull @Override public String getDefaultExtension() { return "rpyc"; }
    @Nullable @Override public Icon getIcon()              { return null; }

    @Override public boolean isReadOnly() { return true; }

    public static final class Language extends com.intellij.lang.Language {
        public static final Language INSTANCE = new Language();

        private Language() {
            super("RenPy Compiled");
        }
    }
}