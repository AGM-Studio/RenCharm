package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;
import xyz.agmstudio.rencharm.psi.RenpyPsiElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class RenpyElement extends IElementType {
    private final Function<ASTNode, ASTWrapperPsiElement> elementor;

    public RenpyElement(String statement) {
        super(statement, RenpyFileType.INSTANCE.getLanguage());
        this.elementor = null;
    }
    public RenpyElement(String statement, Class<? extends ASTWrapperPsiElement> clazz) {
        super(statement, RenpyFileType.INSTANCE.getLanguage());

        try {
            Constructor<? extends ASTWrapperPsiElement> constructor = clazz.getConstructor(ASTNode.class);
            this.elementor = node -> {
                try {
                    return constructor.newInstance(node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ASTWrapperPsiElement create(ASTNode node) {
        if (elementor != null) return this.elementor.apply(node);
        System.out.println(this + " has no elementor");
        return new RenpyPsiElement(node);
    }
}
