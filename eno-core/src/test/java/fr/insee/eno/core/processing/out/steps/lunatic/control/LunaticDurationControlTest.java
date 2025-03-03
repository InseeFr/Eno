package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.Duration;
import fr.insee.lunatic.model.flat.DurationFormat;
import fr.insee.lunatic.model.flat.ResponseType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.HourMinuteValue;
import static fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.YearMonthValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LunaticDurationControlTest {


    @Test
    void durationWithNoMin() {
        Duration lunaticDuration = new Duration();
        lunaticDuration.setMax("P1Y0M");
        lunaticDuration.setFormat(DurationFormat.YEARS_MONTHS);
        assertThrows(RequiredPropertyException.class, () ->
                LunaticDurationControl.generateDurationFormatControl(lunaticDuration));
    }
    @Test
    void durationWithNoMax() {
        Duration lunaticDuration = new Duration();
        lunaticDuration.setMin("P1Y0M");
        lunaticDuration.setFormat(DurationFormat.YEARS_MONTHS);
        assertThrows(RequiredPropertyException.class, () ->
                LunaticDurationControl.generateDurationFormatControl(lunaticDuration));
    }
    @Test
    void durationWithNoFormat() {
        Duration lunaticDuration = new Duration();
        lunaticDuration.setMin("P0Y0M");
        lunaticDuration.setMax("P1Y0M");
        assertThrows(RequiredPropertyException.class, () ->
                LunaticDurationControl.generateDurationFormatControl(lunaticDuration));
    }


    @Test
    void testParseYearMonth() {
        String yearMonthString = "P1Y6M";
        YearMonthValue yearMonthValue = LunaticDurationControl.parseYearMonth(yearMonthString);
        assertEquals(1, yearMonthValue.years());
        assertEquals(6, yearMonthValue.months());
    }
    @Test
    void testParseHourMinute() {
        String hourMinuteString = "PT10H39M";
        HourMinuteValue hourMinuteValue = LunaticDurationControl.parseHourMinute(hourMinuteString);
        assertEquals(10, hourMinuteValue.hours());
        assertEquals(39, hourMinuteValue.minutes());
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "PT10H20M" // Incorrect format (here hours and minutes)
            ,"P0Y" // minute value is missing
            ,"PY5M" // year value is missing
            ,"5Y5M" // "P" is missing
            ,"10Y" // "P" and minute value are missing
    })
    void testParseYearMonth_invalidFormat(String input) {
        assertThrows(IllegalArgumentException.class, () -> LunaticDurationControl.parseYearMonth(input));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "P1Y2M" // Incorrect format (here years and months)
            ,"P10H20M" // should be "PT" not "P"
            ,"PT1HM" // no value for minutes
            ,"PTH30M" // no value for hours
    })
    void testParseHourMinute_invalidFormat(String input) {
        assertThrows(IllegalArgumentException.class, () -> LunaticDurationControl.parseHourMinute(input));
    }


    @Test
    void generateControl_yearMonth() {
        //
        Duration lunaticDuration = new Duration();
        lunaticDuration.setResponse(new ResponseType());
        lunaticDuration.getResponse().setName("DURATION_VAR");
        lunaticDuration.setMin("P0Y3M");
        lunaticDuration.setMax("P1Y6M");
        lunaticDuration.setFormat(DurationFormat.YEARS_MONTHS);
        //
        ControlType generatedControl = LunaticDurationControl.generateDurationFormatControl(lunaticDuration);
        //
        assertEquals();
    }

}

