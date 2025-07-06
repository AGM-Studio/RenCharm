package xyz.agmstudio.rencharm.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads the keyword list from /keywords.txt exactly once.
 */
public final class RenpyKeywords {
    private RenpyKeywords() {}

    public static final Set<String> ALL = load();

    private static Set<String> load() {
        InputStream stream = RenpyKeywords.class.getResourceAsStream("/keywords/keywords.txt");
        if (stream == null) {
            System.err.println("Unable to load keywords.txt file.");
            return Set.of();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return br.lines().map(String::trim).filter(s -> !s.isEmpty() && !s.startsWith("#")).collect(Collectors.toUnmodifiableSet());
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Cannot read keywords.txt", e);
        }
    }
}
