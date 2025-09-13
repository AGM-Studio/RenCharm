package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;
import xyz.agmstudio.rencharm.psi.elements.values.REIExpressions;
import xyz.agmstudio.rencharm.psi.elements.values.REIVariable;

import java.util.Objects;

public abstract class StmDeclaration extends ASTWrapperPsiElement implements PsiNamedElement {
    public StmDeclaration(@NotNull ASTNode node) {
        super(node);
    }

    @Override public String getName() {
        PsiElement id = getIdentifier();
        return id != null ? id.getText() : null;
    }
    @Override public PsiElement setName(@NotNull String name) {
        // Optional: implement rename support later
        return this;
    }

    public PsiElement getIdentifier() {
        return findChildByType(REIVariable.ELEMENT);
    }

    public VirtualFile getContainingFileVirtual() {
        PsiFile file = getContainingFile();
        return file != null ? file.getVirtualFile() : null;
    }

    public String getFileName() {
        VirtualFile vf = getContainingFileVirtual();
        return vf != null ? vf.getName() : "";
    }

    public static PsiBuilder.Marker parse(PsiBuilder builder, RenpyElement element) {
        PsiBuilder.Marker stmt = builder.mark();
        builder.advanceLexer();

        if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            marker.done(REIVariable.ELEMENT);
        }
        else builder.error("Expected an identifier to be defined.");
        if (builder.getTokenType() == RenpyTokenTypes.OPERATOR && Objects.equals(builder.getTokenText(), "=")) builder.advanceLexer();
        else builder.error("Expected '=' after the identifier but got '" + builder.getTokenText() + "'.");

        // Values for define
        if (REIExpressions.getBareStatement(builder) == null)
            builder.error("Expected a value or expression but got '" + builder.getTokenText() + "'.");

        // ERROR EVERYTHING except for SEMICOLON till line ends!
        boolean found = false;
        while (!RenpyTokenTypes.NEWLINE.isToken(builder)) {
            if (builder.getTokenType() != RenpyTokenTypes.SEMICOLON || found) builder.error("Unexpected value '" + builder.getTokenText() + "'.");
            else found = true;
            builder.advanceLexer();
        }

        stmt.done(element);
        return stmt;
    }

    /**
     * default IDENTIFIER = VALUE
     */
    public static class Default extends StmDeclaration {
        public static final RenpyElement STATEMENT = new RenpyElement("DEFAULT_STATEMENT", Default.class);

        public Default(@NotNull ASTNode node) {
            super(node);
        }
    }

    /**
     * define IDENTIFIER = VALUE
     */
    public static class Define extends StmDeclaration {
        public static final RenpyElement STATEMENT = new RenpyElement("DEFINE_STATEMENT", Define.class);

        public Define(@NotNull ASTNode node) {
            super(node);
        }
    }
}
