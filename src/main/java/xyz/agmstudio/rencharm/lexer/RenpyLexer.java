package xyz.agmstudio.rencharm.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class RenpyLexer extends LexerBase {
    private static final char EOF = '\0';

    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int bufferEnd;
    private IElementType tokenType;

    @Override public void start(@NotNull CharSequence buffer, int startOffset, int bufferEnd, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = startOffset;
        this.bufferEnd = bufferEnd;
        this.tokenType = null;

        this.isNewLine = true;
        this.isNewWord = true;

        advance();
    }

    @Override public int getState() { return 0; }
    @Override public @Nullable IElementType getTokenType() { return tokenType; }
    @Override public int getTokenStart() { return startOffset; }
    @Override public int getTokenEnd() { return endOffset; }
    @Override public @NotNull CharSequence getBufferSequence() { return buffer; }
    @Override public int getBufferEnd() { return bufferEnd; }

    private static final TokenSet ignoreNewLine = TokenSet.create(RenpyTokenTypes.NEWLINE, TokenType.WHITE_SPACE, RenpyTokenTypes.INDENT);
    private boolean isNewLine = true;

    private static final TokenSet ignoreNewWord = TokenSet.create(RenpyTokenTypes.NEWLINE, TokenType.WHITE_SPACE, RenpyTokenTypes.INDENT);
    private boolean isNewWord = true;

    @Override public void advance() {
        tokenAdvance();
        if (!ignoreNewLine.contains(tokenType)) isNewLine = false;
        if (!ignoreNewWord.contains(tokenType)) isNewWord = false;
    }
    private void tokenAdvance() {
        if (endOffset >= bufferEnd) {
            tokenType = null;
            return;
        }

        startOffset = endOffset;
        char c = charAt(endOffset);

        if (match(RenpyTokenTypes.NEWLINE, '\n') && (isNewLine = true) && (isNewWord = true)) {
            int temp = endOffset;
            char ch = charAt(temp);
            while (ch == ' ' || ch == '\t' || ch == '\f') ch = charAt(++temp);
            if (ch == '\n' || ch == '\0') endOffset = temp + (ch == '\n' ? 1 : 0);
            return;
        }

        if (isNewLine  && (startsWith("    ") || startsWith("\t"))) {
            if (startsWith("    ")) endOffset += 4;
            else endOffset++;
            tokenType = RenpyTokenTypes.INDENT;
            return;
        }

        if (matchWhile(TokenType.WHITE_SPACE, ch -> ch == ' ' || ch == '\t' || ch == '\f') && (isNewWord = true)) return;
        // Comments & String
        if (matchEnclosed(RenpyTokenTypes.COMMENT, "#", "\n", false, false)) return;
        if (matchEnclosed(RenpyTokenTypes.STRING, "\"\"\"", "\"\"\"", true, true)) return;
        if (matchEnclosed(RenpyTokenTypes.STRING, "\"", "\"", true, false)) return;
        if (matchEnclosed(RenpyTokenTypes.STRING, "'", "'", true, false)) return;

        // Single char tokens
        if (match(RenpyTokenTypes.DOLLAR, '$')) return;
        if (match(RenpyTokenTypes.COLON, ':')) return;
        if (match(RenpyTokenTypes.DOT, '.')) return;
        if (match(RenpyTokenTypes.COMMA, ',')) return;
        if (match(RenpyTokenTypes.LPAREN, '(')) return;
        if (match(RenpyTokenTypes.RPAREN, ')')) return;
        if (match(RenpyTokenTypes.LBRACKET, '[')) return;
        if (match(RenpyTokenTypes.RBRACKET, ']')) return;
        if (match(RenpyTokenTypes.LBRACE, '{')) return;
        if (match(RenpyTokenTypes.RBRACE, '}')) return;
        if (match(RenpyTokenTypes.SEMICOLON, ';')) return;
        if (match(RenpyTokenTypes.AT, '@')) return;

        // Operators & Numbers
        if (match(RenpyTokenTypes.OPERATOR, "**=", "//=", "<<=", ">>=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=")) return;
        if (match(RenpyTokenTypes.OPERATOR, "**", "//", "<<", ">>", "<=", ">=", "==", "!=")) return;
        if (match(RenpyTokenTypes.OPERATOR, '+', '-', '*', '/', '%', '<', '>', '=', '&', '|', '^', '~')) return;
        if (isNumber()) return;

        for (var entry : MULTI_WORD_KEYWORDS.entrySet()) {
            String keyword = entry.getKey();
            if (startsWith(keyword) && isWordBoundary(keyword)) {
                endOffset += keyword.length();
                tokenType = entry.getValue();
                return;
            }
        }

        if (Character.isLetter(c) || c == '_') {
            int start = endOffset;
            consumeWhile(Character::isJavaIdentifierPart);
            String word = buffer.subSequence(start, endOffset).toString();

            tokenType = RenpyTokenTypes.IDENTIFIER;
            if (RESERVED_KEYWORDS.contains(word))                       tokenType = RenpyTokenTypes.PRIMARY_KEYWORD;
            else if (PRIMARY_KEYWORDS.contains(word)    && isNewLine)   tokenType = RenpyTokenTypes.PRIMARY_KEYWORD;
            else if (STYLE_KEYWORDS.contains(word)      && isNewWord)   tokenType = RenpyTokenTypes.STYLE_KEYWORD;
            else if (FUNCTIONAL_KEYWORDS.contains(word))                tokenType = RenpyTokenTypes.FUNCTIONAL_KEYWORD;
            else if (CONSTANT_KEYWORDS.contains(word))                  tokenType = RenpyTokenTypes.CONSTANT_KEYWORD;
            return;
        }

        endOffset++;
        tokenType = RenpyTokenTypes.BAD_CHARACTER;
    }

    // List of words ---------------------------------------------------------------------------------------------------
    private static final Map<String, IElementType> MULTI_WORD_KEYWORDS = new HashMap<>();
    private static final Set<String> PRIMARY_KEYWORDS
            = RenpyLexer.loadKeywords("/keywords/primary.txt", RenpyTokenTypes.PRIMARY_KEYWORD);
    private static final Set<String> RESERVED_KEYWORDS
            = RenpyLexer.loadKeywords("/keywords/reserved.txt", RenpyTokenTypes.PRIMARY_KEYWORD);
    private static final Set<String> FUNCTIONAL_KEYWORDS
            = RenpyLexer.loadKeywords("/keywords/functional.txt", RenpyTokenTypes.FUNCTIONAL_KEYWORD);
    private static final Set<String> STYLE_KEYWORDS
            = RenpyLexer.loadKeywords("/keywords/style.txt", RenpyTokenTypes.STYLE_KEYWORD);
    private static final Set<String> CONSTANT_KEYWORDS
            = RenpyLexer.loadKeywords("/keywords/constants.txt", RenpyTokenTypes.CONSTANT_KEYWORD);

    private static Set<String> loadKeywords(String resourcePath, RenpyTokenTypes.RenpyToken token) {
        InputStream stream = RenpyLexer.class.getResourceAsStream(resourcePath);
        if (stream == null) return Collections.emptySet();
        Set<String> keywords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int end = line.indexOf('#');
                if (end >= 0) line = line.substring(0, end).strip();
                else line = line.strip();
                if (!line.isEmpty()) keywords.add(line);
                if (line.contains(" ")) MULTI_WORD_KEYWORDS.put(line, token);
            }
            return Collections.unmodifiableSet(keywords);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    // Helpers ---------------------------------------------------------------------------------------------------------
    private boolean match(IElementType type, char... options) {
        char current = charAt(endOffset);
        for (char c : options) {
            if (c == current) {
                endOffset++;
                tokenType = type;
                return true;
            }
        }
        return false;
    }

    private boolean match(IElementType type, String... options) {
        for (String op : options) {
            if (startsWith(op)) {
                endOffset += op.length();
                tokenType = type;
                return true;
            }
        }
        return false;
    }

    private boolean matchWhile(IElementType type, Predicate<Character> condition) {
        if (!condition.test(charAt(endOffset))) return false;
        consumeWhile(condition);
        tokenType = type;
        return true;
    }

    private boolean matchUntil(IElementType type, Predicate<Character> condition) {
        if (endOffset >= bufferEnd) return false;
        consumeUntil(condition);
        tokenType = type;
        return true;
    }

    private boolean matchEnclosed(IElementType type, String open, String close, boolean allowEscape, boolean multiline) {
        if (!startsWith(open)) return false;

        int i = endOffset + open.length();
        while (i < bufferEnd) {
            char current = buffer.charAt(i);
            if (!multiline && current == '\n') {
                tokenType = type;
                endOffset = i;
                return true;
            }
            if (i + close.length() <= bufferEnd && buffer.subSequence(i, i + close.length()).toString().equals(close)) {
                if (allowEscape && buffer.charAt(i - 1) == '\\') {
                    i++;
                    continue;
                }

                i += close.length();
                tokenType = type;
                endOffset = i;
                return true;
            }
            i++;
        }

        tokenType = type;
        endOffset = bufferEnd;
        return true;
    }

    private boolean startsWith(String text) {
        if (endOffset + text.length() > bufferEnd) return false;
        for (int i = 0; i < text.length(); i++)
            if (buffer.charAt(endOffset + i) != text.charAt(i)) return false;
        return true;
    }

    private void consumeWhile(Predicate<Character> cond) {
        while (endOffset < bufferEnd && cond.test(buffer.charAt(endOffset))) endOffset++;
    }

    private void consumeUntil(Predicate<Character> cond) {
        while (endOffset < bufferEnd && !cond.test(buffer.charAt(endOffset))) endOffset++;
    }

    private boolean isNumber() {
        int start = endOffset;

        consumeWhile(Character::isDigit);
        boolean hasDigits = (endOffset > start);

        if (endOffset < bufferEnd && buffer.charAt(endOffset) == '.') {
            endOffset++; // consume dot
            int digitsAfterDotStart = endOffset;
            consumeWhile(Character::isDigit);
            hasDigits = hasDigits || (endOffset > digitsAfterDotStart);
        }

        if (!hasDigits) {
            endOffset = start; // rollback if no digits found
            return false;
        }

        tokenType = RenpyTokenTypes.NUMBER;
        return true;
    }

    private boolean isWordBoundary(String keyword) {
        int next = endOffset + keyword.length();
        return next >= bufferEnd || !Character.isJavaIdentifierPart(charAt(next));
    }

    private char charAt(int i) {
        return i < bufferEnd ? buffer.charAt(i) : '\0';
    }
}
