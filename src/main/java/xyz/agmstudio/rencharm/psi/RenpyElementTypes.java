package xyz.agmstudio.rencharm.psi;

import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyElementTypes {
    IElementType EXPRESSION = new RenpyElement("EXPRESSION");
    IElementType TUPLE      = new RenpyElement("TUPLE_EXPRESSION");
    IElementType LIST       = new RenpyElement("LIST_EXPRESSION");

    IElementType DEFINE_STATEMENT = new RenpyElement("DEFINE_STATEMENT");

    IElementType LABEL_STATEMENT = new RenpyElement("LABEL_STATEMENT");
    IElementType JUMP_STATEMENT = new RenpyElement("JUMP_STATEMENT");

    // add more as needed

    class RenpyElement extends IElementType {
        public RenpyElement(String statement) {
            super(statement, RenpyFileType.INSTANCE.getLanguage());
        }
    }
}
