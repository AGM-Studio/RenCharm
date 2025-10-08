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
import java.util.function.Consumer;
import java.util.function.Function;

public class RenpyElement extends IElementType {
    private Function<ASTNode, ASTWrapperPsiElement> elementor = null;
    private Function<PsiBuilder, PsiBuilder.Marker> parser = null;

    protected final String keyword;
    private Consumer<PsiBuilder> consumer = null;

    @SafeVarargs
    public RenpyElement(String statement, HashSet<RenpyElement>... sets) {
        this(statement, statement.toLowerCase(), null, sets);
    }

    @SafeVarargs
    public RenpyElement(String statement, String keyword, HashSet<RenpyElement>... sets) {
        this(statement, keyword, null, sets);
    }

    @SafeVarargs
    public RenpyElement(String statement, Class<? extends ASTWrapperPsiElement> clazz, HashSet<RenpyElement>... sets) {
        this(statement, statement.toLowerCase(), clazz, sets);
    }

    @SafeVarargs
    public RenpyElement(String statement, String keyword, Class<? extends ASTWrapperPsiElement> clazz, HashSet<RenpyElement>... sets) {
        super(statement, RenpyFileType.INSTANCE.getLanguage());
        for (HashSet<RenpyElement> set: sets) set.add(this);
        this.keyword = keyword;

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

    public RenpyElement withConsumer(Consumer<PsiBuilder> consumer) {
        this.consumer = consumer;
        return this;
    }
    public RenpyElement withIdentifierConsumer() {
        return withConsumer(builder -> {
            if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) this.mark(builder);
            else builder.error("Missing identifier for \"" + keyword + "\" parameter");
        });
    }
    public RenpyElement withMultipleIdentifierConsumer() {
        return withConsumer(builder -> {
            if (builder.getTokenType() != RenpyTokenTypes.IDENTIFIER) builder.error("Missing identifier for '" + keyword + "'");
            else {
                PsiBuilder.Marker mark = builder.mark();
                while (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER) {
                    builder.advanceLexer();
                    if (builder.getTokenType() == RenpyTokenTypes.COMMA) {
                        if (builder.lookAhead(1) == RenpyTokenTypes.IDENTIFIER) builder.advanceLexer();
                        else builder.error("Unexpected comma");
                    } else if (builder.getTokenType() == RenpyTokenTypes.IDENTIFIER)
                        builder.error("Missing comma between parameters");
                }

                mark.done(this);
            }
        });
    }
    public RenpyElement withTokenConsumer(RenpyTokenTypes.RenpyToken token) {
        return withConsumer(builder -> {
            if (builder.getTokenType() == token) this.mark(builder);
            else builder.error("Missing \"" + keyword + "\" parameter");
        });
    }

    public boolean parseKeyword(PsiBuilder builder) {
        if (!RenpyTokenTypes.PRIMARY_KEYWORD.isToken(builder, keyword)) return false;
        builder.advanceLexer();
        consumer.accept(builder);
        return true;
    }

    public ASTWrapperPsiElement create(ASTNode node) {
        return this.elementor.apply(node);
    }
    public PsiBuilder.Marker tryParse(PsiBuilder builder) {
        return this.parser.apply(builder);
    }

    public PsiBuilder.Marker mark(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(this);
        return marker;
    }
    public PsiBuilder.Marker mark(PsiBuilder builder, int count) {
        PsiBuilder.Marker marker = builder.mark();
        for (int i = 0; i < count; i++)
            builder.advanceLexer();

        marker.done(this);
        return marker;
    }
    public PsiBuilder.Marker mark(PsiBuilder builder, Function<PsiBuilder, Boolean> predicate) {
        PsiBuilder.Marker marker = builder.mark();
        while (predicate.apply(builder))
            builder.advanceLexer();

        marker.done(this);
        return marker;
    }

    public static class Singleton extends RenpyElement {
        @SafeVarargs
        public Singleton(String keyword, HashSet<RenpyElement>... sets) {
            super(keyword.toUpperCase() + "_STATEMENT", keyword, null, sets);
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
