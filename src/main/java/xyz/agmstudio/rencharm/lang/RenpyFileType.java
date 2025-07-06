package xyz.agmstudio.rencharm.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.RenCharmIcons;

import javax.swing.*;

public final class RenpyFileType extends LanguageFileType {
    public static final RenpyFileType INSTANCE = new RenpyFileType();

    private RenpyFileType() { super(RenpyLanguage.INSTANCE); }

    @Override public @NotNull String getName()              { return "Ren'Py"; }
    @Override public @NotNull String getDescription()       { return "Ren'Py visual‑novel script"; }
    @Override public @NotNull String getDefaultExtension()  { return "rpy"; }
    @Override public @NotNull Icon getIcon()                { return RenCharmIcons.FILE; }
}