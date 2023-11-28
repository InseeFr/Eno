package fr.insee.eno.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TooltipUtilsTest {

    @Test
    void cleanTooltipString() {
        TooltipUtils tooltipUtils = new TooltipUtils();

        // Test with a single match
        String inputText1 = "Some [word](. \"This is a sample.\").";
        String expectedOutput1 = "Some [word](. 'This is a sample.').";
        assertEquals(expectedOutput1, tooltipUtils.cleanTooltips(inputText1));

        // Test with multiple matches
        String inputText2 = "[Text1](. \"Replace me\") [Text2](. \"And me\")";
        String expectedOutput2 = "[Text1](. 'Replace me') [Text2](. 'And me')";
        assertEquals(expectedOutput2, tooltipUtils.cleanTooltips(inputText2));

        // Test with no matches
        String inputText3 = "No matches here.";
        assertEquals(inputText3, tooltipUtils.cleanTooltips(inputText3));

        // Test with apostrophe
        String inputText4 = "[some word](. \"Tooltip's content\")";
        String expectedOutput4 = "[some word](. 'Tooltip’s content')";
        assertEquals(expectedOutput4, tooltipUtils.cleanTooltips(inputText4));
    }

}
