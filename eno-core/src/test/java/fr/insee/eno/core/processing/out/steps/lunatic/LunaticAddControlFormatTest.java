package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddControlFormatTest {

    private Questionnaire lunaticQuestionnaire;
    InputNumber number;
    Datepicker datePicker;
    LunaticAddControlFormat processing;

    @BeforeEach
    void init() {
        processing = new LunaticAddControlFormat();

        datePicker = new Datepicker();
        datePicker.setComponentType(ComponentTypeEnum.DATEPICKER);
        datePicker.setId("datepicker-id");
        datePicker.setResponse(buildResponse("DATE_VAR"));

        number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId("number-id");
        number.setDecimals(BigInteger.ZERO);
        number.setResponse(buildResponse("NUMBER_VAR"));
    }

    @Test
    void shouldInputNumberHaveDecimalsControl() {
        lunaticQuestionnaire = new Questionnaire();

        number.setDecimals(BigInteger.TEN);

        lunaticQuestionnaire.getComponents().add(number);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.getFirst();

        assertEquals("not(not(isnull(NUMBER_VAR))  and round(NUMBER_VAR,10)<>NUMBER_VAR)", control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals("number-id-format-decimal", control.getId());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveScaleOnDecimalValuesWhenDecimalsIsSet() {
        lunaticQuestionnaire = new Questionnaire();

        number.setDecimals(BigInteger.TEN);
        number.setMin(5.24);
        number.setMax(10.12);

        lunaticQuestionnaire.getComponents().add(number);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();

        ControlType control = controls.getFirst();
        assertEquals("not(not(isnull(NUMBER_VAR)) and (5.2400000000>NUMBER_VAR or 10.1200000000<NUMBER_VAR))", control.getControl().getValue());
    }

    @Test
    void shouldInputNumberHaveTwoFormatControlsWhenMinOrMaxSet() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
    }

    @Test
    void shouldInputNumberHaveMinMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        number.setMax(10.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.getFirst();

        assertEquals("not(not(isnull(NUMBER_VAR)) and (5>NUMBER_VAR or 10<NUMBER_VAR))", control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-inf-sup", control.getId());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveMinFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.getFirst();

        assertEquals("not(not(isnull(NUMBER_VAR)) and 5>NUMBER_VAR)", control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-inf", control.getId());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMax(10.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.getFirst();

        assertEquals("not(not(isnull(NUMBER_VAR)) and 10<NUMBER_VAR)", control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-sup", control.getId());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    @DisplayName("Datepicker: no format nor min/max, should have no controls")
    void datepickerNoFormatNorMinMax() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        processing.apply(lunaticQuestionnaire);
        assertTrue(datePicker.getControls() == null || datePicker.getControls().isEmpty());
    }

    @Test
    @DisplayName("Datepicker: year format control only")
    void datepickerYearFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setDateFormat("YYYY-MM-DD");

        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(1, controls.size());
        ControlType yearControl = controls.getFirst();

        assertEquals("datepicker-id-format-year", yearControl.getId());
        String expected = "not(not(isnull(DATE_VAR)) and (" +
                "cast(cast(cast(DATE_VAR, date, \"YYYY-MM-DD\"), string, \"YYYY\"), integer) <= 999 or " +
                "cast(cast(cast(DATE_VAR, date, \"YYYY-MM-DD\"), string, \"YYYY\"), integer) > 9999))";
        assertEquals(expected, yearControl.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, yearControl.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, yearControl.getErrorMessage().getType());
        assertEquals(ControlTypeEnum.FORMAT, yearControl.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, yearControl.getCriticality());
    }

    @Test
    @DisplayName("Datepicker: min and max format control")
    void datepickerMinMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMin("2020-01-01");
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.get(1);

        assertEquals("datepicker-id-format-date-borne-inf-sup", control.getId());
        String expected = "not(not(isnull(DATE_VAR)) and " +
                "(cast(DATE_VAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\") or " +
                "cast(DATE_VAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))";
        assertEquals(expected, control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    @DisplayName("Datepicker: min format control")
    void datepickerMinFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMin("2020-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.get(1);

        assertEquals("datepicker-id-format-date-borne-inf", control.getId());
        String expected = "not(not(isnull(DATE_VAR)) and " +
                "(cast(DATE_VAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\")))";
        assertEquals(expected, control.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, control.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, control.getErrorMessage().getType());
        assertEquals(ControlTypeEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, control.getCriticality());
    }

    @Test
    @DisplayName("Datepicker: max format control")
    void datepickerMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(2, controls.size());
        ControlType boundsControl = controls.get(1);

        assertEquals("datepicker-id-format-date-borne-sup", boundsControl.getId());
        String expected = "not(not(isnull(DATE_VAR)) " +
                "and (cast(DATE_VAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))";
        assertEquals(expected, boundsControl.getControl().getValue());
        assertEquals(LabelTypeEnum.VTL, boundsControl.getControl().getType());
        assertEquals(LabelTypeEnum.VTL_MD, boundsControl.getErrorMessage().getType());
        assertEquals(ControlTypeEnum.FORMAT, boundsControl.getTypeOfControl());
        assertEquals(ControlCriticalityEnum.ERROR, boundsControl.getCriticality());
    }

    @Test
    @DisplayName("Datepicker: year format control when min/max is set")
    void datepickerYearAndMinMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(2, controls.size());
        ControlType yearControl = controls.get(0);
        ControlType boundsControl = controls.get(1);
        // Only testing ids here to check that the order is right, content is tested in other tests
        assertEquals("datepicker-id-format-year", yearControl.getId());
        assertEquals("datepicker-id-format-date-borne-sup", boundsControl.getId());
    }

    @Test
    void shouldLoopComponentsBeProcessed() {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId("loop-id");
        loop.getComponents().addAll(List.of(number, datePicker));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(loop);

        number.setMax(10.0);
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");

        processing.apply(lunaticQuestionnaire);

        assertEquals(2, datePicker.getControls().size());
        assertEquals(2, number.getControls().size());
    }

    @Test
    void shouldTableComponentsHaveSubComponentsControls() {
        Table table = new Table();
        table.setComponentType(ComponentTypeEnum.TABLE);
        table.setId("table-id");

        List<BodyLine> bodyLines = table.getBodyLines();

        List<BodyCell> bodyCells = new ArrayList<>();
        bodyCells.add(buildBodyCell("line1"));
        bodyCells.add(buildBodyCell(table.getId()+"-co", "CHECKBOX_VAR", ComponentTypeEnum.CHECKBOX_ONE));
        bodyLines.add(buildBodyLine(bodyCells));

        bodyCells = new ArrayList<>();
        bodyCells.add(buildBodyCell("line2"));
        bodyCells.add(buildBodyCell(table.getId()+"-number", "NUMBER_VAR", ComponentTypeEnum.INPUT_NUMBER, BigInteger.TWO, 2.0, 5.0));
        bodyLines.add(buildBodyLine(bodyCells));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(table);

        processing.apply(lunaticQuestionnaire);

        assertEquals(2, table.getControls().size());
        assertEquals("table-id-number-format-borne-inf-sup", table.getControls().get(0).getId());
        assertEquals("table-id-number-format-decimal", table.getControls().get(1).getId());
    }

    @Test
    void shouldRosterComponentsHaveSubComponentsControls() {
        RosterForLoop roster = new RosterForLoop();
        roster.setComponentType(ComponentTypeEnum.ROSTER_FOR_LOOP);
        roster.setId("roster-id");

        List<BodyCell> bodyCells = roster.getComponents();
        bodyCells.add(buildBodyCell("line1"));
        bodyCells.add(buildBodyCell(roster.getId()+"-number", "NUMBER_VAR", ComponentTypeEnum.INPUT_NUMBER, BigInteger.TWO, 2.0, 5.0));
        bodyCells.add(buildBodyCell(roster.getId()+"-co", "CHECKBOX_VAR", ComponentTypeEnum.CHECKBOX_ONE));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(roster);

        processing.apply(lunaticQuestionnaire);

        assertEquals(2, roster.getControls().size());
        assertEquals("roster-id-number-format-borne-inf-sup", roster.getControls().get(0).getId());
        assertEquals("roster-id-number-format-decimal", roster.getControls().get(1).getId());
    }

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    private BodyCell buildBodyCell(String id, String name, ComponentTypeEnum componentType) {
        BodyCell bodyCell = new BodyCell();
        bodyCell.setId(id);
        bodyCell.setComponentType(componentType);
        bodyCell.setResponse(buildResponse(name));
        return bodyCell;
    }

    private BodyCell buildBodyCell(String id, String name, ComponentTypeEnum componentType, BigInteger decimals, Double min, Double max) {
        BodyCell bodyCell = buildBodyCell(id, name, componentType);
        bodyCell.setDecimals(decimals);
        bodyCell.setMin(min);
        bodyCell.setMax(max);
        return bodyCell;
    }

    private BodyCell buildBodyCell(String value) {
        BodyCell bodyCell = new BodyCell();
        bodyCell.setValue(value);
        return bodyCell;
    }

    private BodyLine buildBodyLine(List<BodyCell> bodyCells) {
        BodyLine bodyLine = new BodyLine();
        bodyLine.getBodyCells().addAll(bodyCells);
        return bodyLine;
    }

    @Test
    void testFormatDateToFrench_validDate() {
        String input = "2024-12-10";
        String expected = "10-12-2024";
        String result = processing.formatDateToFrench(input);
        assertEquals(expected, result, "La date doit être formatée correctement en JJ-MM-AAAA.");
    }

    @Test
    void shouldReturnEmptyWhenNoConstraints() {
        Optional<ControlType> result = processing.getFormatControlFromDatepickerAttributes(
                "datepicker-id", null, null, "YYYY-MM-DD", "DATEVAR");
        assertTrue(result.isEmpty(), "Aucun contrôle attendu lorsqu'aucune contrainte n'est fournie.");
    }

    @Test
    void shouldReturnControlWithMinOnly() {
        Optional<ControlType> result = processing.getFormatControlFromDatepickerAttributes(
                "datepicker-id", "2020-01-01", null, "YYYY-MM-DD", "DATEVAR");

        assertTrue(result.isPresent());
        ControlType control = result.get();
        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\")))",
                control.getControl().getValue());
        assertEquals("\"La date saisie doit être postérieure à 01-01-2020.\"",
                control.getErrorMessage().getValue());
    }

    @Test
    void shouldReturnControlWithMaxOnly() {
        Optional<ControlType> result = processing.getFormatControlFromDatepickerAttributes(
                "datepicker-id", null, "2023-01-01", "YYYY-MM-DD", "DATEVAR");

        assertTrue(result.isPresent());
        ControlType control = result.get();
        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))",
                control.getControl().getValue());
        assertEquals("\"La date saisie doit être antérieure à 01-01-2023.\"",
                control.getErrorMessage().getValue());
    }

    @Test
    void shouldReturnControlWithBothMinAndMax() {
        Optional<ControlType> result = processing.getFormatControlFromDatepickerAttributes(
                "datepicker-id", "2020-01-01", "2023-01-01", "YYYY-MM-DD", "DATEVAR");

        assertTrue(result.isPresent());
        ControlType control = result.get();
        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\") or cast(DATEVAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))",
                control.getControl().getValue());
        assertEquals("\"La date saisie doit être comprise entre 01-01-2020 et 01-01-2023.\"",
                control.getErrorMessage().getValue());
    }

    @Test
    void shouldLogWarningForUnknownFormat() {
        String result = processing.formatDateToFrench("year-from-date(current-date())");
        assertNull(result);
    }
}

