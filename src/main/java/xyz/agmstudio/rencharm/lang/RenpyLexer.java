package xyz.agmstudio.rencharm.lang;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static xyz.agmstudio.rencharm.psi.RenpyTokenTypes.*;

public class RenpyLexer extends LexerBase {
    // ─── Lexer state ────────────────────────────────────────────────────────────
    private CharSequence buffer = "";
    private int           bufferEnd;

    private int tokenStart;
    private int tokenEnd;
    private IElementType tokenType;

    // ─── LexerBase required overrides ───────────────────────────────────────────
    @Override public void start(@NotNull CharSequence buf,int start,int end,int _state) {
        buffer     = buf;
        bufferEnd  = end;
        tokenStart = tokenEnd = start;
        tokenType  = null;
        advance();
    }
    @Override public int getState() { return 0; }
    @Override public int getTokenStart() { return tokenStart; }
    @Override public int getTokenEnd() { return tokenEnd;   }
    @Override public int getBufferEnd() { return bufferEnd; }
    @Override public @Nullable IElementType getTokenType() { return tokenType; }
    @Override public @NotNull CharSequence getBufferSequence() { return buffer; }

    // ─── Core scanning logic ────────────────────────────────────────────────────
    @Override public void advance() {
        if (tokenEnd >= bufferEnd) {
            tokenType = null;
            return;
        }

        // 1️⃣  begin new token
        tokenStart = tokenEnd;
        char c = buffer.charAt(tokenEnd);

        if (Character.isWhitespace(c)) {
            while (tokenEnd < bufferEnd && Character.isWhitespace(buffer.charAt(tokenEnd))) tokenEnd++;
            tokenType = TokenType.WHITE_SPACE;
            return;
        }

        // 2️⃣  comment  # … ⏎ --------------------------------------------------
        if (c == '#') {
            while (tokenEnd < bufferEnd && buffer.charAt(tokenEnd) != '\n') tokenEnd++;
            tokenType = COMMENT;
            return;
        }

        // 3️⃣  string  " … " ----------------------------------------------------
        if (c == '"') {
            tokenEnd++;
            while (tokenEnd < bufferEnd) {
                char ch = buffer.charAt(tokenEnd);
                if (ch == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd += 2;
                else {
                    tokenEnd++;
                    if (ch == '"') break;
                }
            }
            tokenType = STRING;
            return;
        }

        // 4️⃣  numbers ----------------------------------------------------------
        if (Character.isDigit(c) || (c == '.' && tokenEnd + 1 < bufferEnd && Character.isDigit(buffer.charAt(tokenEnd + 1)))) {
            boolean seenDot = (c == '.');
            tokenEnd++;

            while (tokenEnd < bufferEnd) {
                char ch = buffer.charAt(tokenEnd);
                if (Character.isDigit(ch)) tokenEnd++;
                else if (ch == '.' && !seenDot) {
                    seenDot = true;
                    tokenEnd++;
                } else break;
            }
            tokenType = CONSTANT;
            return;
        }

        // 5️⃣  identifier / keyword / booleans ---------------------------------
        if (Character.isJavaIdentifierStart(c)) {
            do tokenEnd++;
            while (tokenEnd < bufferEnd && Character.isJavaIdentifierPart(buffer.charAt(tokenEnd)));

            String word = buffer.subSequence(tokenStart, tokenEnd).toString();
            if (word.equals("True") || word.equals("False")) tokenType = CONSTANT;
            else if (RenpyKeywords.ALL.contains(word)) tokenType = KEYWORD;
            else tokenType = IDENTIFIER;
            return;
        }

        // 5️⃣  single‑char symbol ----------------------------------------------
        tokenEnd++;
        tokenType = SYMBOL;
    }
}
