package better.anticheat.core.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ChatUtil {

    private final static Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)&([0-9A-FK-ORX])");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?i)&#([A-F0-9]{6})");

    public static String stripColors(String text) {
        if (text == null) return null;
        text = COLOR_CODE_PATTERN.matcher(text).replaceAll("");
        text = HEX_COLOR_PATTERN.matcher(text).replaceAll("");
        return text;
    }

    public static String translateColors(String text) {
        if (text == null) return null;

        // Translate hex colors first
        Matcher hexMatcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            hexMatcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement.toString()));
        }
        hexMatcher.appendTail(buffer);

        // Then translate legacy & codes
        return COLOR_CODE_PATTERN.matcher(buffer.toString()).replaceAll("§$1");
    }
}
