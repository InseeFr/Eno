package fr.insee.eno.core.processing.out.steps.lunatic.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticDurationControlTest {

    @Test
    void testParseYearMonth() {
        //
        String yearMonthString = "P1Y6M";
        //
        LunaticDurationControl.YearMonthValue yearMonthValue = LunaticDurationControl.parseYearMonth(yearMonthString);
        //
        assertEquals(1, yearMonthValue.year());
        assertEquals(6, yearMonthValue.month());
    }

    @Test
    void testParseHourMinute() {
        //
        String hourMinuteString = "PT10H39M";
        //
        LunaticDurationControl.HourMinuteValue hourMinuteValue = LunaticDurationControl.parseHourMinute(hourMinuteString);
        //
        assertEquals(10, hourMinuteValue.hours());
        assertEquals(39, hourMinuteValue.minutes());
    }

    @Test
    void testMessageYearMonth() {
        //
        LunaticDurationControl.YearMonthValue minValue = new LunaticDurationControl.YearMonthValue(1, 6);
        LunaticDurationControl.YearMonthValue maxValue = new LunaticDurationControl.YearMonthValue(3, 1);
        //
        String result = ""; // TODO
        //
        String expected = "La durée saisie doit être comprise entre 1 an et 6 mois et 3 ans et 1 mois.";
        assertEquals(expected, result);
    }

    @Test
    void testMessageHourMinute() {
        //
        LunaticDurationControl.HourMinuteValue minValue = new LunaticDurationControl.HourMinuteValue(1, 30);
        LunaticDurationControl.HourMinuteValue maxValue = new LunaticDurationControl.HourMinuteValue(3, 0);
        //
        String result = ""; // TODO
        //
        String expected = "La durée saisie doit être comprise entre 1 heure 30 minutes et 3 heures.";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionYear() {
        //
        String responseName = "DURATION_VAR";
        //
        String result = ""; // TODO
        //
        String expected = """
                cast(substr(DURATION_VAR, 2, instr(VAR_DUREE, "Y") - 2), integer)""";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionMonth() {
        //
        String responseName = "DURATION_VAR";
        //
        String result = ""; // TODO
        //
        String expected = """
                cast(substr(VAR_DUREE, instr(VAR_DUREE, "Y") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "Y") - 1 ), integer)""";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionHour() {
        //
        String responseName = "DURATION_VAR";
        //
        String result = ""; // TODO
        //
        String expected = """
                cast(substr(DURATION_VAR, 3, instr(VAR_DUREE, "H") - 3), integer)""";
        assertEquals(expected, result);
    }

    @Test
    void testCastExpressionMinute() {
        //
        String responseName = "DURATION_VAR";
        //
        String result = ""; // TODO
        //
        String expected = """
                cast(substr(VAR_DUREE, instr(VAR_DUREE, "H") + 1, instr(VAR_DUREE, "M") - instr(VAR_DUREE, "H") - 1 ), integer)""";
        assertEquals(expected, result);
    }

}
