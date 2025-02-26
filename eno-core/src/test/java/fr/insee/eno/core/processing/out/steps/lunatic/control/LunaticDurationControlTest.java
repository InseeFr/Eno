package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import org.junit.jupiter.api.Test;

import static fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.*;
import static org.junit.jupiter.api.Assertions.*;

class LunaticDurationControlTest {

    @Test
    void testParseYearMonth() {
        //
        String yearMonthString = "P1Y6M";
        //
        YearMonthValue yearMonthValue = LunaticDurationControl.parseYearMonth(yearMonthString);
        //
        assertEquals(1, yearMonthValue.years());
        assertEquals(6, yearMonthValue.months());
    }

    @Test
    void testParseHourMinute() {
        //
        String hourMinuteString = "PT10H39M";
        //
        HourMinuteValue hourMinuteValue = LunaticDurationControl.parseHourMinute(hourMinuteString);
        //
        assertEquals(10, hourMinuteValue.hours());
        assertEquals(39, hourMinuteValue.minutes());
    }

    @Test
    void testMessageYearMonth() {
        //
        YearMonthValue minValue = new YearMonthValue(1, 6);
        YearMonthValue maxValue = new YearMonthValue(3, 1);
        //
        String result = generateControlMessage(minValue, maxValue)    ;
        //
        String expected = "\"La durée saisie doit être comprise entre 1 an et 6 mois et 3 ans et 1 mois.\"";
        assertEquals(expected, result);
    }

    @Test
    void testMessageHourMinute() {
        //
        HourMinuteValue minValue = new HourMinuteValue(1, 30);
        HourMinuteValue maxValue = new HourMinuteValue(3, 0);
        //
        String result = generateControlMessage(minValue, maxValue);
        //
        String expected = "\"La durée saisie doit être comprise entre 1 heure et 30 minutes et 3 heures.\"";
        assertEquals(expected, result);
    }

    @Test
    void testFormatDurationHourMinuteDuration() {
        assertEquals("1 heure et 30 minutes", formatDuration(new HourMinuteValue(1, 30)));
        assertEquals("1 minute", formatDuration(new HourMinuteValue(0, 1)));
        assertEquals("45 minutes", formatDuration(new HourMinuteValue(0, 45)));
        assertEquals("1 heure", formatDuration(new HourMinuteValue(1, 0)));
        assertEquals("2 heures", formatDuration(new HourMinuteValue(2, 0)));
    }

    @Test
    void testCastExpressionYear() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new YearMonthValue(1, 0), new YearMonthValue(10, 0));
        String expected = String.format("cast(substr(%s, 2, instr(%s, \"Y\") - 2), integer)", responseName, responseName);
        assertTrue(result.contains(expected), "L'expression ne contient pas l'extraction de l'année attendue");
    }

    @Test
    void testCastExpressionMonth() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new YearMonthValue(0, 1), new YearMonthValue(0, 12));
        String expected = String.format("cast(substr(%s, instr(%s, \"Y\") + 1, instr(%s, \"M\") - instr(%s, \"Y\") - 1), integer)", responseName, responseName, responseName, responseName);
        assertTrue(result.contains(expected), "L'expression ne contient pas l'extraction du mois attendue");
    }

    @Test
    void testCastExpressionHour() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new HourMinuteValue(1, 0), new HourMinuteValue(23, 0));
        String expected = String.format("cast(substr(%s, 3, instr(%s, \"H\") - 3), integer)", responseName, responseName);
        assertTrue(result.contains(expected), "L'expression ne contient pas l'extraction de l'heure attendue");
    }

    @Test
    void testCastExpressionMinute() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new HourMinuteValue(0, 1), new HourMinuteValue(0, 59));
        String expected = String.format("cast(substr(%s, instr(%s, \"H\") + 1, instr(%s, \"M\") - instr(%s, \"H\") - 1), integer)", responseName, responseName, responseName, responseName);
        assertTrue(result.contains(expected), "L'expression ne contient pas l'extraction des minutes attendue");
    }

//    @Test
//    void testCastExpressionYear() {
//        //
//        String responseName = "DURATION_VAR";
//        //
//        String result = ""; // TODO
//        //
//        String expected = """
//                cast(substr(DURATION_VAR, 2, instr(VAR_DUREE, "Y") - 2), integer)""";
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void testCastExpressionMonth() {
//        //
//        String responseName = "DURATION_VAR";
//        //
//        String result = ""; // TODO
//        //
//        String expected = """
//                cast(substr(VAR_DUREE, instr(VAR_DUREE, "Y") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "Y") - 1 ), integer)""";
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void testCastExpressionHour() {
//        //
//        String responseName = "DURATION_VAR";
//        //
//        String result = ""; // TODO
//        //
//        String expected = """
//                cast(substr(DURATION_VAR, 3, instr(VAR_DUREE, "H") - 3), integer)""";
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void testCastExpressionMinute() {
//        //
//        String responseName = "DURATION_VAR";
//        //
//        String result = ""; // TODO
//        //
//        String expected = """
//                cast(substr(VAR_DUREE, instr(VAR_DUREE, "H") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "H") - 1 ), integer)""";
//        assertEquals(expected, result);
//    }

}
