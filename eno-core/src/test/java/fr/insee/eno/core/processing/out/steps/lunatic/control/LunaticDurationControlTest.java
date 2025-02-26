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
        assertEquals(1, hourMinuteValue.hours());
        assertEquals(6, hourMinuteValue.minutes());
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

}
