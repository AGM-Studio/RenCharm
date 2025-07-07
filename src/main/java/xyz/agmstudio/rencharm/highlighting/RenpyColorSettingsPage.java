package xyz.agmstudio.rencharm.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.*;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.*;
import xyz.agmstudio.rencharm.RenCharmIcons;

import javax.swing.*;
import java.util.Map;

public class RenpyColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
            new AttributesDescriptor("Keyword", RenpySyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Values", RenpySyntaxHighlighter.CONSTANT),
            new AttributesDescriptor("String",  RenpySyntaxHighlighter.STRING),
            new AttributesDescriptor("Comment", RenpySyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Identifier", RenpySyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Symbol", RenpySyntaxHighlighter.SYMBOL),
    };

    @Override
    public @NotNull AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "Ren'Py";
    }

    @Override
    public @Nullable Icon getIcon() {
        return RenCharmIcons.FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new RenpySyntaxHighlighter();
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return """
            default value = 0
            define mc = Character("MC")
            
            # This is a comment
            screen test_screen():
                vbox:
                    text "Hello [player_name]" color "#f00"
                    if True:
                        text str(42)

                    textbutton "Hi" action Return()
            
            label start:
                mc "Hello from me!"
            
                call test_screen()
            """;
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return Map.of(); // use if you define tags like <keyword> or <bool> in demoText
    }
}
