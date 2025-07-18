package xyz.agmstudio.rencharm.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

public interface RenpyTokenTypes {
    IElementType PRIMARY_KEYWORD    = new RenpyToken("PRIMARY_KEYWORD");
    IElementType FUNCTIONAL_KEYWORD = new RenpyToken("FUNCTIONAL_KEYWORD");
    IElementType STYLE_KEYWORD      = new RenpyToken("STYLE_KEYWORD");
    IElementType CONSTANT_KEYWORD   = new RenpyToken("CONSTANT_KEYWORD");

    IElementType LABEL      = new RenpyToken("LABEL");
    IElementType SCREEN     = new RenpyToken("SCREEN");
    IElementType IDENTIFIER = new RenpyToken("IDENTIFIER");
    IElementType STRING     = new RenpyToken("STRING");
    IElementType NUMBER     = new RenpyToken("NUMBER");
    IElementType OPERATOR   = new RenpyToken("OPERATOR");
    IElementType COMMENT    = new RenpyToken("COMMENT");

    IElementType INDENT     = new RenpyToken("INDENT");
    IElementType NEWLINE    = new RenpyToken("NEWLINE");

    IElementType DOLLAR     = new RenpyToken("DOLLAR");
    IElementType COLON      = new RenpyToken("COLON");
    IElementType DOT        = new RenpyToken("DOT");
    IElementType COMMA      = new RenpyToken("COMMA");
    IElementType LPAREN     = new RenpyToken("LPAREN");
    IElementType RPAREN     = new RenpyToken("RPAREN");
    IElementType LBRACKET   = new RenpyToken("LBRACKET");
    IElementType RBRACKET   = new RenpyToken("RBRACKET");
    IElementType LBRACE     = new RenpyToken("LBRACE");
    IElementType RBRACE     = new RenpyToken("RBRACE");
    IElementType SEMICOLON  = new RenpyToken("SEMICOLON");
    IElementType AT         = new RenpyToken("AT");

    TokenSet LITERAL_VALUES = TokenSet.create(STRING, NUMBER, CONSTANT_KEYWORD);

    IElementType WHITE_SPACE    = TokenType.WHITE_SPACE;
    IElementType BAD_CHARACTER  = TokenType.BAD_CHARACTER;
    class RenpyToken extends IElementType {
        public RenpyToken(@NotNull String debugName) {
            super(debugName, RenpyFileType.Language.INSTANCE);
        }
    }
}
