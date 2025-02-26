package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LunaticDurationControlTest {

    @Test
    void testParseYearMonth() {
        //
        String yearMonthString = "P1Y6M";
        //
        YearMonthValue yearMonthValue = LunaticDurationControl.parseYearMonth(yearMonthString);
        //
        assertEquals(1, yearMonthValue.year());
        assertEquals(6, yearMonthValue.month());
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
        String result = ""; // TODO
        //
        String expected = "La durée saisie doit être comprise entre 1 an et 6 mois et 3 ans et 1 mois.";
        assertEquals(expected, result);
    }

    @Test
    void testMessageHourMinute() {
        //
        HourMinuteValue minValue = new HourMinuteValue(1, 30);
        HourMinuteValue maxValue = new HourMinuteValue(3, 0);
        //
        String result = ""; // TODO
        //
        String expected = "La durée saisie doit être comprise entre 1 heure 30 minutes et 3 heures.";
        assertEquals(expected, result);
    }

    private String mockFormatMethod(HourMinuteValue hourMinuteValue) {
        return "";
    }
    @Test
    void testFormatDurationHourMinuteDuration() {
        assertEquals("1 heure et 30 minutes", mockFormatMethod(new HourMinuteValue(1, 30)));
        assertEquals("1 minute", mockFormatMethod(new HourMinuteValue(0, 1)));
        assertEquals("45 minutes", mockFormatMethod(new HourMinuteValue(0, 45)));
        assertEquals("1 heure", mockFormatMethod(new HourMinuteValue(1, 0)));
        assertEquals("2 heures", mockFormatMethod(new HourMinuteValue(2, 0)));
        HourMinuteValue zeroValue = new HourMinuteValue(0, 0);
        assertThrows(IllegalArgumentException.class, () -> mockFormatMethod(zeroValue));
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
