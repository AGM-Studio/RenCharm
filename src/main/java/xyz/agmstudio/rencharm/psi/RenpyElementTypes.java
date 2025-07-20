package xyz.agmstudio.rencharm.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyElementTypes {
    IElementType UNARY      = new RenpyElement("UNARY_EXPRESSION");
    IElementType BINARY     = new RenpyElement("BINARY_EXPRESSION");
    IElementType GROUP      = new RenpyElement("GROUP_EXPRESSION");
    IElementType TUPLE      = new RenpyElement("TUPLE_EXPRESSION");
    IElementType BARE_TUPLE = new RenpyElement("TUPLE_EXPRESSION");
    IElementType LIST       = new RenpyElement("LIST_EXPRESSION");
    IElementType SET        = new  RenpyElement("SET_EXPRESSION");

    IElementType DEFINE_STATEMENT = new RenpyElement("DEFINE_STATEMENT");

    IElementType LABEL_STATEMENT = new RenpyElement("LABEL_STATEMENT");
    IElementType JUMP_STATEMENT = new RenpyElement("JUMP_STATEMENT");

    class RenpyElement extends IElementType {
        public RenpyElement(String statement) {
            super(statement, RenpyFileType.INSTANCE.getLanguage());
        }
    }

    static boolean isNext(PsiBuilder builder, IElementType type, String... values) {
        if (builder.getTokenType() != type) return false;
        String text = builder.getTokenText();
        if (text == null) return false;
        for (String value: values) if (text.equals(value)) return true;
        return false;
    }
}
