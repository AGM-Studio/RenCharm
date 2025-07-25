package xyz.agmstudio.rencharm.resolve;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RenpyFindUsagesProvider implements FindUsagesProvider {
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiNamedElement;
    }

    @Override
    public @Nullable WordsScanner getWordsScanner() {
        // Optional: used for indexing words in files
        return null;
    }

    @Override
    public @NotNull String getHelpId(@NotNull PsiElement element) {
        return "help.id.not.implemented";
    }

    @Override
    public @NotNull String getType(@NotNull PsiElement element) {
        return "variable";
    }

    @Override
    public @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        return Objects.requireNonNull(((PsiNamedElement) element).getName());
    }

    @Override
    public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
