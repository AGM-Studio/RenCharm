package xyz.agmstudio.rencharm.lang;

import com.intellij.lang.Language;

public final class RenpyLanguage extends Language {
    public static final RenpyLanguage INSTANCE = new RenpyLanguage();

    private RenpyLanguage() {
        super("RenPy");
    }
}