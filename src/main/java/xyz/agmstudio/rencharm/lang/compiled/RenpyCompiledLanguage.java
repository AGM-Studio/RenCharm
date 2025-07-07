package xyz.agmstudio.rencharm.lang.compiled;

import com.intellij.lang.Language;

public final class RenpyCompiledLanguage extends Language {
    public static final RenpyCompiledLanguage INSTANCE = new RenpyCompiledLanguage();

    private RenpyCompiledLanguage() {
        super("RenPy Compiled");
    }
}