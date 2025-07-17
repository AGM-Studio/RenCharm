package xyz.agmstudio.rencharm.highlight;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.agmstudio.rencharm.lang.RenpyFileType;

import javax.swing.*;
import java.util.Map;

public class RenpyColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keywords A", RenpySyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Keywords B", RenpySyntaxHighlighter.FUNCTIONAL_KEYWORD),
            new AttributesDescriptor("Keywords C", RenpySyntaxHighlighter.STYLE_KEYWORD),
            new AttributesDescriptor("Keywords D", RenpySyntaxHighlighter.CONSTANT_KEYWORD),
            new AttributesDescriptor("Strings", RenpySyntaxHighlighter.STRING),
            new AttributesDescriptor("Numbers", RenpySyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Comments", RenpySyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Operators", RenpySyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Punctuation", RenpySyntaxHighlighter.PUNCTUATION),
            new AttributesDescriptor("Identifiers", RenpySyntaxHighlighter.IDENTIFIER)
    };

    @Override public @Nullable Icon getIcon() {
        return RenpyFileType.FILE_ICON;
    }
    @Override public @NotNull String getDisplayName() {
        return "Ren'Py";
    }
    @Override public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }
    @Override public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
    @Override public @NotNull SyntaxHighlighter getHighlighter() {
        return new RenpySyntaxHighlighter();
    }
    @Override public @NotNull String getDemoText() {
        return """
            label start:
                # This is a comment
                scene bg room with fade
    
                "Hello, world!"
    
                $ x = 42
                if x > 0:
                    x = obj.value * 10 + (4 % 2)
    
                screen menu_screen():
                    vbox:
                        text _("Start") action Start()
            """;
    }
    @Override public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }
}
