package xyz.agmstudio.rencharm.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyTokenTypes {
    RenpyToken PRIMARY_KEYWORD    = new RenpyToken("PRIMARY_KEYWORD");
    RenpyToken FUNCTIONAL_KEYWORD = new RenpyToken("FUNCTIONAL_KEYWORD");
    RenpyToken STYLE_KEYWORD      = new RenpyToken("STYLE_KEYWORD");
    RenpyToken CONSTANT_KEYWORD   = new RenpyToken("CONSTANT_KEYWORD");

    RenpyToken LABEL      = new RenpyToken("LABEL");
    RenpyToken SCREEN     = new RenpyToken("SCREEN");
    RenpyToken IDENTIFIER = new RenpyToken("IDENTIFIER");
    RenpyToken STRING     = new RenpyToken("STRING");
    RenpyToken NUMBER     = new RenpyToken("NUMBER");
    RenpyToken OPERATOR   = new RenpyToken("OPERATOR");
    RenpyToken COMMENT    = new RenpyToken("COMMENT");

    RenpyToken INDENT     = new RenpyToken("INDENT");
    RenpyToken NEWLINE    = new RenpyToken("NEWLINE") {
        @Override public boolean isToken(PsiBuilder builder, String... values) {
            return builder.getTokenType() == null || builder.getTokenType() == this;
        }
    };

    RenpyToken DOLLAR     = new RenpyToken("DOLLAR");
    RenpyToken COLON      = new RenpyToken("COLON");
    RenpyToken DOT        = new RenpyToken("DOT");
    RenpyToken COMMA      = new RenpyToken("COMMA");
    RenpyToken LPAREN     = new RenpyToken("LPAREN");
    RenpyToken RPAREN     = new RenpyToken("RPAREN");
    RenpyToken LBRACKET   = new RenpyToken("LBRACKET");
    RenpyToken RBRACKET   = new RenpyToken("RBRACKET");
    RenpyToken LBRACE     = new RenpyToken("LBRACE");
    RenpyToken RBRACE     = new RenpyToken("RBRACE");
    RenpyToken SEMICOLON  = new RenpyToken("SEMICOLON");
    RenpyToken AT         = new RenpyToken("AT");

    TokenSet LITERAL_VALUES = TokenSet.create(STRING, NUMBER, CONSTANT_KEYWORD);

    IElementType WHITE_SPACE    = TokenType.WHITE_SPACE;
    IElementType BAD_CHARACTER  = TokenType.BAD_CHARACTER;
    class RenpyToken extends IElementType {
        public RenpyToken(@NotNull String debugName) {
            super(debugName, RenpyFileType.Language.INSTANCE);
        }
        public boolean isToken(PsiBuilder builder, String... values) {
            if (builder.getTokenType() != this) return false;
            String text = builder.getTokenText();
            if (text == null) return false;
            for (String value: values) if (text.equals(value)) return true;
            return false;
        }
    }
}
