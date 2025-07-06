package xyz.agmstudio.rencharm.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyLanguage;

public class RenpyTokenTypes extends IElementType {
    public RenpyTokenTypes(@NotNull @NonNls String debugName) {
        super(debugName, RenpyLanguage.INSTANCE);
    }

    public static final RenpyTokenTypes KEYWORD = new RenpyTokenTypes("KEYWORD");
    public static final RenpyTokenTypes COMMENT = new RenpyTokenTypes("COMMENT");
    public static final RenpyTokenTypes STRING = new RenpyTokenTypes("STRING");
    public static final RenpyTokenTypes IDENTIFIER = new RenpyTokenTypes("IDENTIFIER");
    public static final RenpyTokenTypes SYMBOL = new RenpyTokenTypes("SYMBOL");
}
