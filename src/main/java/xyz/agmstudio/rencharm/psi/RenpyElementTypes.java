package xyz.agmstudio.rencharm.psi;

import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyElementTypes {
    IElementType LABEL_STATEMENT = new RenpyElement("LABEL_STATEMENT");
    IElementType JUMP_STATEMENT = new RenpyElement("JUMP_STATEMENT");
    // add more as needed

    class RenpyElement extends IElementType {
        public RenpyElement(String statement) {
            super(statement, RenpyFileType.INSTANCE.getLanguage());
        }
    }
}
