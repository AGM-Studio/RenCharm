package xyz.agmstudio.rencharm.psi;

import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyElementTypes {
    IElementType UNARY      = new RenpyElement("UNARY_EXPRESSION");
    IElementType BINARY     = new RenpyElement("BINARY_EXPRESSION");
    IElementType GROUP      = new RenpyElement("GROUP_EXPRESSION");
    IElementType TUPLE      = new RenpyElement("TUPLE_EXPRESSION");
    IElementType BARE_TUPLE = new RenpyElement("TUPLE_EXPRESSION");
    IElementType LIST       = new RenpyElement("LIST_EXPRESSION");
    IElementType SET        = new RenpyElement("SET_EXPRESSION");

    IElementType FUNCTION_CALL  = new RenpyElement("FUNCTION_CALL_EXPRESSION");
    IElementType ARGUMENT_POSED = new RenpyElement("ARGUMENT_POSED_EXPRESSION");
    IElementType ARGUMENT_NAMED = new RenpyElement("ARGUMENT_NAMED_EXPRESSION");

    IElementType MEMBER_ACCESS = new RenpyElement("MEMBER_ACCESS");
    IElementType SUBSCRIPT_ACCESS = new RenpyElement("SUBSCRIPT_ACCESS");

    IElementType DEFINE_STATEMENT = new RenpyElement("DEFINE_STATEMENT");

    IElementType LABEL_STATEMENT = new RenpyElement("LABEL_STATEMENT");
    IElementType JUMP_STATEMENT = new RenpyElement("JUMP_STATEMENT");

    class RenpyElement extends IElementType {
        public RenpyElement(String statement) {
            super(statement, RenpyFileType.INSTANCE.getLanguage());
        }
    }
}
