package xyz.agmstudio.rencharm.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.lexer.RenpyLexer;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class RenpySyntaxHighlighter extends SyntaxHighlighterBase {
    // Define keys for token highlighting
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("RENPRY_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey FUNCTIONAL_KEYWORD =
            createTextAttributesKey("RENPRY_FUNCTIONAL_KEYWORD", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey STYLE_KEYWORD =
            createTextAttributesKey("RENPRY_STYLE_KEYWORD", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey CONSTANT_KEYWORD =
            createTextAttributesKey("RENPRY_CONSTANT_KEYWORD", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("RENPRY_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("RENPRY_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("RENPRY_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("RENPRY_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("RENPRY_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey PUNCTUATION =
            createTextAttributesKey("RENPRY_PUNCTUATION", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("RENPRY_BAD_CHARACTER", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] FUNCTIONAL_KEYWORD_KEYS = new TextAttributesKey[]{FUNCTIONAL_KEYWORD};
    private static final TextAttributesKey[] STYLE_KEYWORD_KEYS = new TextAttributesKey[]{STYLE_KEYWORD};
    private static final TextAttributesKey[] CONSTANT_KEYWORD_KEYS = new TextAttributesKey[]{CONSTANT_KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] PUNCTUATION_KEYS = new TextAttributesKey[]{PUNCTUATION};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new RenpyLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == RenpyTokenTypes.DOLLAR) return KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.LABEL) return KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.SCREEN) return KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.PRIMARY_KEYWORD) return KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.FUNCTIONAL_KEYWORD) return FUNCTIONAL_KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.STYLE_KEYWORD) return STYLE_KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.CONSTANT_KEYWORD) return CONSTANT_KEYWORD_KEYS;
        if (tokenType == RenpyTokenTypes.IDENTIFIER) return IDENTIFIER_KEYS;


        if (tokenType == RenpyTokenTypes.COMMENT) return COMMENT_KEYS;
        if (tokenType == RenpyTokenTypes.STRING) return STRING_KEYS;
        if (tokenType == RenpyTokenTypes.NUMBER) return NUMBER_KEYS;

        if (tokenType == RenpyTokenTypes.OPERATOR) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.LPAREN) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.RPAREN) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.LBRACE) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.RBRACE) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.LBRACKET) return OPERATOR_KEYS;
        if (tokenType == RenpyTokenTypes.RBRACKET) return OPERATOR_KEYS;

        if (tokenType == RenpyTokenTypes.COLON) return PUNCTUATION_KEYS;
        if (tokenType == RenpyTokenTypes.DOT) return PUNCTUATION_KEYS;
        if (tokenType == RenpyTokenTypes.SEMICOLON) return PUNCTUATION_KEYS;
        if (tokenType == RenpyTokenTypes.AT) return PUNCTUATION_KEYS;

        if (tokenType == RenpyTokenTypes.BAD_CHARACTER) return BAD_CHAR_KEYS;

        return EMPTY_KEYS;
    }
}
