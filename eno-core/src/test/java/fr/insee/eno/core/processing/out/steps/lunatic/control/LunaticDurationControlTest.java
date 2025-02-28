package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.exceptions.business.RequiredPropertyException;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticAddControlFormat;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.eno.core.processing.out.steps.lunatic.control.LunaticDurationControl.*;
import static fr.insee.eno.core.processing.out.steps.lunatic.pagination.LunaticAddPageNumbersUtils.buildResponse;
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
        String result = generateControlMessage(minValue, maxValue);
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
        assertTrue(result.contains(expected), "The expression does not contain the expected month extraction.");
    }

    @Test
    void testCastExpressionHour() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new HourMinuteValue(1, 0), new HourMinuteValue(23, 0));
        String expected = String.format("cast(substr(%s, 3, instr(%s, \"H\") - 3), integer)", responseName, responseName);
        assertTrue(result.contains(expected), "The expression does not contain the expected hour extraction.");
    }

    @Test
    void testCastExpressionMinute() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new HourMinuteValue(0, 1), new HourMinuteValue(0, 59));
        String expected = String.format("cast(substr(%s, instr(%s, \"H\") + 1, instr(%s, \"M\") - instr(%s, \"H\") - 1), integer)", responseName, responseName, responseName, responseName);
        assertTrue(result.contains(expected), "The expression does not contain the expected minute extraction.");
    }

    @Test
    void testCastExpressionYearAndMonth() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new YearMonthValue(1, 6), new YearMonthValue(10, 11));
        String expectedYear = String.format("cast(substr(%s, 2, instr(%s, \"Y\") - 2), integer)", responseName, responseName);
        String expectedMonth = String.format("cast(substr(%s, instr(%s, \"Y\") + 1, instr(%s, \"M\") - instr(%s, \"Y\") - 1), integer)", responseName, responseName, responseName, responseName);
        assertTrue(result.contains(expectedYear), "The expression does not contain the expected year extraction.");
        assertTrue(result.contains(expectedMonth), "The expression does not contain the expected month extraction.");
    }

    @Test
    void testCastExpressionHourAndMinute() {
        String responseName = "VAR_DUREE";
        String result = generateControlExpression(new HourMinuteValue(1, 30), new HourMinuteValue(23, 45));
        String expectedHour = String.format("cast(substr(%s, 3, instr(%s, \"H\") - 3), integer)", responseName, responseName);
        String expectedMinute = String.format("cast(substr(%s, instr(%s, \"H\") + 1, instr(%s, \"M\") - instr(%s, \"H\") - 1), integer)", responseName, responseName, responseName, responseName);
        assertTrue(result.contains(expectedHour), "The expression does not contain the expected hour extraction.");
        assertTrue(result.contains(expectedMinute), "The expression does not contain the expected minute extraction.");
    }

    private Questionnaire lunaticQuestionnaire;
    Duration duration;
    LunaticAddControlFormat processing;

    @BeforeEach
    void init() {
        processing = new LunaticAddControlFormat();
        duration = new Duration();
        duration.setComponentType(ComponentTypeEnum.DURATION);
        duration.setId("duration-id");
        duration.setResponse(buildResponse("DURATION_VAR"));
    }

    @Test
    @DisplayName("Duration: no format, should throw exception")
    void durationNoFormatNorMinMax() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);
        assertThrows(RequiredPropertyException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: valid format, no min/max, should throw exception")
    void durationValidFormatNoMinMax() {
        duration.setFormat(DurationFormat.YEARS_MONTHS);

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        assertThrows(RequiredPropertyException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: valid format with valid min and max")
    void durationValidFormatWithMinMax() {
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("P1Y2M");
        duration.setMax("P5Y10M");

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        assertDoesNotThrow(() -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: invalid min format, should throw exception")
    void durationInvalidMinFormat() {
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("1Y2M"); // "P" is missing

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        assertThrows(RequiredPropertyException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: invalid max format, should throw exception")
    void durationInvalidMaxFormat() {
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMax("PT10H20M"); // Incorrect format (here hours and minutes)

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        assertThrows(RequiredPropertyException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: invalid min and max formats, should throw exception")
    void durationInvalidMinMaxFormats() {
        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("10Y"); // Incorrect format
        duration.setMax("5Y5M"); // "P" is missing

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        assertThrows(IllegalArgumentException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    @DisplayName("Duration: entered value out of bounds, should trigger control error for both min and max")
    void durationEnteredValueLessThanMin() {

        ResponseType response = new ResponseType();
        response.setName("P1Y0M");

        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("P2Y6M");
        duration.setMax("P3Y0M");
        duration.setResponse(response);

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = duration.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.getFirst();

        String expectedErrorMessage = "\"La durée saisie doit être comprise entre 2 ans et 6 mois et 3 ans.\"";
        assertEquals(expectedErrorMessage, control.getErrorMessage().getValue());
    }

    @Test
    @DisplayName("Duration: entered value out of bounds, should trigger control error for both min and max")
    void durationEnteredValueOutOfBounds() {

        ResponseType response = new ResponseType();
        response.setName("P6Y0M");

        duration.setFormat(DurationFormat.YEARS_MONTHS);
        duration.setMin("P0Y0M");
        duration.setMax("P5Y10M");
        duration.setResponse(response);

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(duration);

        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = duration.getControls();
        assertEquals(1, controls.size());

        ControlType control = controls.getFirst();

        String actualErrorMessage = control.getErrorMessage().getValue();

        String expectedErrorMessage = "\"La durée saisie doit être inférieure à 5 ans et 10 mois.\"";
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

}

