package xyz.agmstudio.rencharm.lang;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static xyz.agmstudio.rencharm.psi.RenpyTokenTypes.*;

public class RenpyLexer extends LexerBase {
    // ─── Lexer state ────────────────────────────────────────────────────────────
    private CharSequence buffer = "";
    private int           bufferEnd;

    private int tokenStart;
    private int tokenEnd;
    private IElementType tokenType;

    private static final Set<String> KEYWORDS = Set.of(
            "define","label","jump","return","if","else","elif","while","python",
            "init","scene","show","hide","menu","call","with","window","play","stop",
            "queue","voice","pause","extend","default","image"
    );

    // ─── LexerBase required overrides ───────────────────────────────────────────
    @Override public void start(@NotNull CharSequence buf,int start,int end,int _state) {
        buffer     = buf;
        bufferEnd  = end;
        tokenStart = tokenEnd = start;
        tokenType  = null;
        advance();
    }
    @Override public int getState() { return 0; }
    @Override public int getTokenStart()          { return tokenStart; }
    @Override public int getTokenEnd()            { return tokenEnd;   }
    @Override public @Nullable IElementType getTokenType() { return tokenType; }
    @Override public @NotNull CharSequence getBufferSequence() { return buffer; }
    @Override public int getBufferEnd()          { return bufferEnd; }

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

        // 4️⃣  identifier / keyword --------------------------------------------
        if (Character.isJavaIdentifierStart(c)) {
            do tokenEnd++;
            while (tokenEnd < bufferEnd && Character.isJavaIdentifierPart(buffer.charAt(tokenEnd)));
            String word = buffer.subSequence(tokenStart, tokenEnd).toString();
            tokenType = RenpyKeywords.ALL.contains(word) ? KEYWORD : IDENTIFIER;
            return;
        }

        // 5️⃣  single‑char symbol ----------------------------------------------
        tokenEnd++;
        tokenType = SYMBOL;
    }
}
