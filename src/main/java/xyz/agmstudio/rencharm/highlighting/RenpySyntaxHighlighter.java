package xyz.agmstudio.rencharm.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import xyz.agmstudio.rencharm.lang.RenpyLexer;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

public class RenpySyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            TextAttributesKey.createTextAttributesKey("RENpy_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMMENT =
            TextAttributesKey.createTextAttributesKey("RENpy_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey STRING =
            TextAttributesKey.createTextAttributesKey("RENpy_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey IDENTIFIER =
            TextAttributesKey.createTextAttributesKey("RENpy_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey SYMBOL =
            TextAttributesKey.createTextAttributesKey("RENpy_SYMBOL", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey CONSTANT =
            TextAttributesKey.createTextAttributesKey("RENpy_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);

    private static final TextAttributesKey[] CONSTANT_KEYS = new TextAttributesKey[]{CONSTANT};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] SYMBOL_KEYS = new TextAttributesKey[]{SYMBOL};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override public @NotNull Lexer getHighlightingLexer() {
        return new RenpyLexer();
    }

    @Override public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == RenpyTokenTypes.CONSTANT)      return CONSTANT_KEYS;
        if (tokenType == RenpyTokenTypes.KEYWORD)       return KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.COMMENT)       return COMMENT_KEYS;
        if (tokenType == RenpyTokenTypes.STRING)        return STRING_KEYS;
        if (tokenType == RenpyTokenTypes.IDENTIFIER)    return IDENTIFIER_KEYS;
        if (tokenType == RenpyTokenTypes.SYMBOL)        return SYMBOL_KEYS;
        return EMPTY_KEYS;
    }
}
