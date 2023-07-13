package fr.insee.eno.core.processing.impl;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticReverseConsistencyControlLabelTest {

    private Questionnaire lunaticQuestionnaire;
    Input i;
    InputNumber ln;
    CheckboxOne co;
    Dropdown ldd;
    Table t;
    CheckboxGroup lcg;
    Loop l;
    LunaticReverseConsistencyControlLabel processing;

    @BeforeEach
    void init() {
        processing = new LunaticReverseConsistencyControlLabel();

        i = buildInput("jfazww20", "TEXTECOURT");
        co = buildCheckboxOne("k6gik8v5", "CHECKBOX");
        t = buildTable("jfkxybfe", List.of("QCM_OM1", "QCM_OM2", "QCM_OM3"));
        ldd = buildDropdown("jfjfae9f", "DROPDOWN");
        ln = buildNumber("jfjh1ndk", "INTEGER");
        lcg = buildCheckboxGroup("jfkxybff", List.of("QCM_OM1", "QCM_OM2", "QCM_OM3"));
        l = buildLoop("loopid", List.of(ldd, ln));
    }

    @Test
    void shouldComponentsWithoutControlsDoNothing() {
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, co, t, l));
        processing.apply(lunaticQuestionnaire);

        assertTrue(i.getControls() == null || i.getControls().isEmpty());
        assertTrue(co.getControls() == null || co.getControls().isEmpty());
        assertTrue(t.getControls() == null || t.getControls().isEmpty());
        assertTrue(l.getControls() == null || l.getControls().isEmpty());
        assertTrue(ln.getControls() == null || ln.getControls().isEmpty());
        assertTrue(lcg.getControls() == null || lcg.getControls().isEmpty());
        assertTrue(ldd.getControls() == null || ldd.getControls().isEmpty());
    }

    @Test
    void shouldComponentsWithConsistencyReverseControlLabel() {
        i.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));
        i.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));
        t.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, t));
        processing.apply(lunaticQuestionnaire);

        assertEquals("not(COUNT(plop))", i.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM1 > 10)", i.getControls().get(1).getControl().getValue());
        assertEquals("not(SUM2 > 20)", t.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldLoopComponentsWithConsistencyReverseControlLabel() {
        ln.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));
        l.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));
        ldd.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeOfControlEnum.CONSISTENCY, ControlCriticityEnum.ERROR));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(l);
        processing.apply(lunaticQuestionnaire);

        assertEquals("not(COUNT(plop))", ln.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM1 > 10)", l.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM2 > 20)", ldd.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldComponentsWithFormatControlsDoNothing() {
        i.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));
        i.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));
        co.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, co));
        processing.apply(lunaticQuestionnaire);

        assertEquals("COUNT(plop)", i.getControls().get(0).getControl().getValue());
        assertEquals("SUM1 > 10", i.getControls().get(1).getControl().getValue());
        assertEquals("SUM2 > 20", co.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldLoopComponentsWithFormatControlsDoNothing() {
        ln.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));
        l.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));
        ldd.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeOfControlEnum.FORMAT, ControlCriticityEnum.ERROR));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(l);
        processing.apply(lunaticQuestionnaire);

        assertEquals("COUNT(plop)", ln.getControls().get(0).getControl().getValue());
        assertEquals("SUM1 > 10", l.getControls().get(0).getControl().getValue());
        assertEquals("SUM2 > 20", ldd.getControls().get(0).getControl().getValue());
    }

    private CheckboxGroup buildCheckboxGroup(String id, List<String> names) {
        CheckboxGroup input = new CheckboxGroup();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_GROUP);
        input.setId(id);
        List<ResponsesCheckboxGroup> responses = names.stream()
                .map(name -> {
                    ResponsesCheckboxGroup response = new ResponsesCheckboxGroup();
                    response.setId(id+"-"+name);
                    return response;
                }).toList();
        input.getResponses().addAll(responses);
        return input;
    }

    private CheckboxOne buildCheckboxOne(String id, String name) {
        CheckboxOne input = new CheckboxOne();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        input.setId(id);
        return input;
    }

    private Dropdown buildDropdown(String id, String name) {
        Dropdown input = new Dropdown();
        input.setComponentType(ComponentTypeEnum.DROPDOWN);
        input.setId(id);
        return input;
    }

    private Input buildInput(String id, String name) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        return input;
    }

    private InputNumber buildNumber(String id, String name) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        return number;
    }

    private Table buildTable(String id, List<String> responseNames) {
        Table input = new Table();
        input.setId(id);
        input.setComponentType(ComponentTypeEnum.TABLE);
        List<BodyType> bodyTypes = input.getBody();

        for(int cpt=0; cpt<responseNames.size(); cpt++) {
            List<BodyLine> bodyLines = new ArrayList<>();
            bodyLines.add(buildBodyLine(Integer.toString(cpt)));
            bodyLines.add(buildBodyLine(id+"-"+"-"+cpt, responseNames.get(cpt), ComponentTypeEnum.CHECKBOX_ONE.value()));
            bodyTypes.add(buildBodyType(bodyLines));
        }
        return input;
    }

    private BodyLine buildBodyLine(String id, String name, String componentType) {
        BodyLine bodyLine = new BodyLine();
        bodyLine.setId(id);
        bodyLine.setComponentType(componentType);
        return bodyLine;
    }

    private BodyLine buildBodyLine(String value) {
        BodyLine bodyLine = new BodyLine();
        bodyLine.setValue(value);
        return bodyLine;
    }

    private BodyType buildBodyType(List<BodyLine> bodyLines) {
        BodyType bodyType = new BodyType();
        bodyType.getBodyLine().addAll(bodyLines);
        return bodyType;
    }

    private Loop buildLoop(String id, List<ComponentType>components) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.getComponents().addAll(components);
        return loop;
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
}
