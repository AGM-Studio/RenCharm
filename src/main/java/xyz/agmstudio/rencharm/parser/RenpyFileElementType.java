package xyz.agmstudio.rencharm.parser;

import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public class RenpyFileElementType extends IFileElementType {
    public static final RenpyFileElementType INSTANCE = new RenpyFileElementType();

    private RenpyFileElementType() {
        super(RenpyFileType.Language.INSTANCE);
    }

    @Override
    public @NotNull String toString() {
        return "Renpy FILE";
    }
}
