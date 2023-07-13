package fr.insee.eno.core.processing.impl;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
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
        number.setResponse(buildResponse("NUMBERVAR"));
    }

    @Test
    void shouldInputNumberNotHaveControlsWhenMinMaxDecimalsNotSet() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        processing.apply(lunaticQuestionnaire);
        assertTrue(number.getControls() == null || number.getControls().isEmpty());
    }

    @Test
    void shouldInputNumberHaveDecimalsControlWhenDecimalsSet() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setDecimals(BigInteger.ONE);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR)) and round(NUMBERVAR,1)<>NUMBERVAR)", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-decimal", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveMinMaxFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        number.setMax(10.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(1, controls.size());
        ControlType control = controls.get(0);

        assertEquals("not(not(isnull(NUMBERVAR)) and (5>NUMBERVAR or 10<NUMBERVAR))", control.getControl().getValue());
        assertEquals("VTL", control.getControl().getType());
        assertEquals("VTL|MD", control.getErrorMessage().getType());
        assertEquals("number-id-format-borne-inf-sup", control.getId());
        assertEquals(ControlTypeOfControlEnum.FORMAT, control.getTypeOfControl());
        assertEquals(ControlCriticityEnum.ERROR, control.getCriticality());
    }

    @Test
    void shouldInputNumberHaveTwoFormatControlsIfMinOrMaxAndDecimalsSet() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        number.setDecimals(BigInteger.ONE);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(2, controls.size());
    }

    @Test
    void shouldInputNumberHaveMinFormatControl() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(number);
        number.setMin(5.0);
        processing.apply(lunaticQuestionnaire);

        List<ControlType> controls = number.getControls();
        assertEquals(1, controls.size());
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
        assertEquals(1, controls.size());
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
        assertEquals(1, number.getControls().size());
    }



    private ControlType buildControl(String id, String errorMessage, ControlTypeOfControlEnum typeOfControl, ControlCriticityEnum criticality ) {
        ControlType control = new ControlType();
        control.setTypeOfControl(typeOfControl);
        control.setId(id);
        control.setCriticality(criticality);
        LabelType label = new LabelType();
        label.setType("VTL");
        label.setValue(errorMessage);
        control.setControl(label);
        return control;
    }

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }
}
