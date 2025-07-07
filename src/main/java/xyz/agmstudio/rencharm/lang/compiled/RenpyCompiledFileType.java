package xyz.agmstudio.rencharm.lang.compiled;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RenpyCompiledFileType extends LanguageFileType {
    public static final RenpyCompiledFileType INSTANCE = new RenpyCompiledFileType();

    private RenpyCompiledFileType() { super(RenpyCompiledLanguage.INSTANCE); }

    @NotNull @Override public String getName()             { return "Ren'Py compiled"; }
    @NotNull @Override public String getDescription()      { return "Ren'Py .rpyc byte‑code file"; }
    @NotNull @Override public String getDefaultExtension() { return "rpyc"; }
    @Nullable @Override public Icon getIcon()              { return null; }

    @Override public boolean isReadOnly() { return true; }
}