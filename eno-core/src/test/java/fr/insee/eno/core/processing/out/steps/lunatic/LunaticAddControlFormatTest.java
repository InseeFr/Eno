package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        datePicker.setResponse(buildResponse("DATEVAR"));

        number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId("number-id");
        number.setDecimals(BigInteger.ZERO);
        number.setResponse(buildResponse("NUMBERVAR"));
    }

    @Test
    void shouldInputNumberHaveDecimalsControl() {
        lunaticQuestionnaire = new Questionnaire();

        number.setDecimals(BigInteger.TEN);

        lunaticQuestionnaire.getComponents().add(number);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR))  and round(NUMBERVAR,10)<>NUMBERVAR)", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-decimal", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
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

        ControlType control = controls.get(0);
        assertEquals("not(not(isnull(NUMBERVAR)) and (5.2400000000>NUMBERVAR or 10.1200000000<NUMBERVAR))", control.getControl().getValue());
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
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR)) and (5>NUMBERVAR or 10<NUMBERVAR))", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-inf-sup", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveMinFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR)) and 5>NUMBERVAR)", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-inf", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMax(10.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR)) and 10<NUMBERVAR)", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-sup", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldDatepickerNotHaveControlsWhenMinMaxNotSet() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        processing.apply(lunaticQuestionnaire);
        assertTrue(datePicker.getControls() == null || datePicker.getControls().isEmpty());
    }

    @Test
    void shouldDatepickerHaveMinMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMin("2020-01-01");
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\") or cast(DATEVAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("datepicker-id-format-date-borne-inf-sup", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldDatepickerHaveMinFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMin("2020-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")<cast(\"2020-01-01\", date, \"YYYY-MM-DD\")))", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("datepicker-id-format-date-borne-inf", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldDatepickerNumberHaveMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(datePicker);
        datePicker.setMax("2023-01-01");
        datePicker.setDateFormat("YYYY-MM-DD");
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = datePicker.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(DATEVAR)) and (cast(DATEVAR, date, \"YYYY-MM-DD\")>cast(\"2023-01-01\", date, \"YYYY-MM-DD\")))", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("datepicker-id-format-date-borne-sup", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
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

        assertEquals(1, datePicker.getControls().size());
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
        bodyCells.add(buildBodyCell(table.getId()+"-co", "CHECKVAR", ComponentTypeEnum.CHECKBOX_ONE));
        bodyLines.add(buildBodyLine(bodyCells));

        bodyCells = new ArrayList<>();
        bodyCells.add(buildBodyCell("line2"));
        bodyCells.add(buildBodyCell(table.getId()+"-number", "NUMBERVAR", ComponentTypeEnum.INPUT_NUMBER, BigInteger.TWO, 2.0, 5.0));
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
        bodyCells.add(buildBodyCell(roster.getId()+"-number", "NUMBERVAR", ComponentTypeEnum.INPUT_NUMBER, BigInteger.TWO, 2.0, 5.0));
        bodyCells.add(buildBodyCell(roster.getId()+"-co", "CHECKVAR", ComponentTypeEnum.CHECKBOX_ONE));

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
}
