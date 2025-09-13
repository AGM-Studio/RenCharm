package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;
import xyz.agmstudio.rencharm.psi.RenpyPsiElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.function.Function;

public class RenpyElement extends IElementType {
    private Function<ASTNode, ASTWrapperPsiElement> elementor = null;
    private Function<PsiBuilder, PsiBuilder.Marker> parser = null;

    public RenpyElement(String statement) {
        super(statement, RenpyFileType.INSTANCE.getLanguage());
    }

    @SafeVarargs
    public RenpyElement(String statement, Class<? extends ASTWrapperPsiElement> clazz, HashSet<RenpyElement>... sets) {
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
            this.elementor = null;
        }

        try {
            Method parser = clazz.getDeclaredMethod("parse", PsiBuilder.class);
            parser.setAccessible(true);
            this.parser = builder -> {
                try {
                    return (PsiBuilder.Marker) parser.invoke(null, builder);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            this.parser = null;
        }

        for (HashSet<RenpyElement> set: sets) set.add(this);
    }

    public ASTWrapperPsiElement create(ASTNode node) {
        if (elementor != null) return this.elementor.apply(node);
        System.out.println(this + " has no elementor");
        this.elementor = RenpyPsiElement::new;
        return new RenpyPsiElement(node);
    }

    public PsiBuilder.Marker tryParse(PsiBuilder builder) {
        if (parser != null) return this.parser.apply(builder);
        System.out.println(this + " has no parser");
        this.parser = b -> null;
        return null;
    }
}
