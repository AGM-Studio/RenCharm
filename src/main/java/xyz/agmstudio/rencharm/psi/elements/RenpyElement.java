package xyz.agmstudio.rencharm.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import xyz.agmstudio.rencharm.lang.RenpyFileType;
import xyz.agmstudio.rencharm.psi.RenpyPsiElement;
import xyz.agmstudio.rencharm.psi.RenpyTokenTypes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.function.Function;

public class RenpyElement extends IElementType {
    private Function<ASTNode, ASTWrapperPsiElement> elementor = null;
    private Function<PsiBuilder, PsiBuilder.Marker> parser = null;

    @SafeVarargs
    public RenpyElement(String statement, HashSet<RenpyElement>... sets) {
        this(statement, null, sets);
    }

    @SafeVarargs
    public RenpyElement(String statement, Class<? extends ASTWrapperPsiElement> clazz, HashSet<RenpyElement>... sets) {
        super(statement, RenpyFileType.INSTANCE.getLanguage());
        for (HashSet<RenpyElement> set: sets) set.add(this);

        if (clazz != null) {
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
        }

        if (this.elementor == null) {
            System.out.println(this + " has no elementor");
            this.elementor = RenpyPsiElement::new;
        }
        if (this.parser == null) {
            System.out.println(this + " has no parser");
            this.parser = b -> null;
        }
    }

    public ASTWrapperPsiElement create(ASTNode node) {
        return this.elementor.apply(node);
    }

    public PsiBuilder.Marker tryParse(PsiBuilder builder) {
        return this.parser.apply(builder);
    }

    public static class Singleton extends RenpyElement {
        private final String keyword;

        @SafeVarargs
        public Singleton(String keyword, HashSet<RenpyElement>... sets) {
            super(keyword.toUpperCase() + "_STATEMENT", null, sets);
            this.keyword = keyword;
        }

        @Override public ASTWrapperPsiElement create(ASTNode node) {
            return new RenpyPsiElement(node);
        }
        @Override public PsiBuilder.Marker tryParse(PsiBuilder builder) {
            if (RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, keyword)) {
                PsiBuilder.Marker marker = builder.mark();
                builder.advanceLexer();
                marker.done(this);
                return marker;
            }

            return null;
        }
    }

}
