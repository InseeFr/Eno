package fr.insee.eno.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>In the current implementation, tooltips are inputted this way in Pogues:
 * <code>[some word](. "Tooltip's content")</code>.</p>
 * <p>Yet tooltips work with simple quotes in Lunatic, so simple quotes must be replaced with apostrophes,
 * and double quotes with simple quotes to be valid:
 * </code>[some word](. 'Tooltip’s content')</code>.</p>
 * */
public class TooltipUtils {

    private static final String APOSTROPHE_CHARACTER = "’";
    private static final String DOUBLE_QUOTES_TOOLTIP_REGEX = "\\[[^\\]]+\\]\\(\\.\\s\"[^\"]+\"\\)";

    private final Pattern pattern;

    public TooltipUtils() {
        // compile pattern only once
        this.pattern = Pattern.compile(DOUBLE_QUOTES_TOOLTIP_REGEX);
    }

    /**
     * Searches for Lunatic tooltips in the given string. If any, these are cleaned to ensure compliance with Lunatic
     * (Lunatic tooltips work with single quotes).
     * @param label String value of a label.
     * @return String value of the label with Lunatic-compliant tooltips.
     */
    public String cleanTooltips(String label) {
        Matcher matcher = pattern.matcher(label);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String tooltip = matcher.group();
            tooltip = tooltip.replace("'", APOSTROPHE_CHARACTER);
            tooltip = tooltip.replace("\"", "'");
            matcher.appendReplacement(result, tooltip);
        }

        matcher.appendTail(result);
        return result.toString();
    }

}
