package fr.insee.eno.core.processing.out.steps.lunatic;

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
        i.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));
        i.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));
        t.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, t));
        processing.apply(lunaticQuestionnaire);

        assertEquals("not(COUNT(plop))", i.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM1 > 10)", i.getControls().get(1).getControl().getValue());
        assertEquals("not(SUM2 > 20)", t.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldLoopComponentsWithConsistencyReverseControlLabel() {
        ln.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));
        l.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));
        ldd.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeEnum.CONSISTENCY, ControlCriticalityEnum.ERROR));

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(l);
        processing.apply(lunaticQuestionnaire);

        assertEquals("not(COUNT(plop))", ln.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM1 > 10)", l.getControls().get(0).getControl().getValue());
        assertEquals("not(SUM2 > 20)", ldd.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldComponentsWithFormatControlsDoNothing() {
        i.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));
        i.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));
        co.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, co));
        processing.apply(lunaticQuestionnaire);

        assertEquals("COUNT(plop)", i.getControls().get(0).getControl().getValue());
        assertEquals("SUM1 > 10", i.getControls().get(1).getControl().getValue());
        assertEquals("SUM2 > 20", co.getControls().get(0).getControl().getValue());
    }

    @Test
    void shouldLoopComponentsWithFormatControlsDoNothing() {
        ln.getControls().add(buildControl("control-input", "COUNT(plop)", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));
        l.getControls().add(buildControl("control-input2", "SUM1 > 10", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));
        ldd.getControls().add(buildControl("control-checkgroup", "SUM2 > 20", ControlTypeEnum.FORMAT, ControlCriticalityEnum.ERROR));

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
        List<BodyLine> bodyLines = input.getBodyLines();

        for(int cpt=0; cpt<responseNames.size(); cpt++) {
            List<BodyCell> bodyCells = new ArrayList<>();
            bodyCells.add(buildBodyCell(Integer.toString(cpt)));
            bodyCells.add(buildBodyCell(id+"-"+"-"+cpt, responseNames.get(cpt), ComponentTypeEnum.CHECKBOX_ONE));
            bodyLines.add(buildBodyLine(bodyCells));
        }
        return input;
    }

    private BodyCell buildBodyCell(String id, String name, ComponentTypeEnum componentType) {
        BodyCell bodyCell = new BodyCell();
        bodyCell.setId(id);
        bodyCell.setComponentType(componentType);
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

    private Loop buildLoop(String id, List<ComponentType>components) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.getComponents().addAll(components);
        return loop;
    }

    private ControlType buildControl(String id, String errorMessage, ControlTypeEnum typeOfControl, ControlCriticalityEnum criticality ) {
        ControlType control = new ControlType();
        control.setTypeOfControl(typeOfControl);
        control.setId(id);
        control.setCriticality(criticality);
        LabelType label = new LabelType();
        label.setType(LabelTypeEnum.VTL);
        label.setValue(errorMessage);
        control.setControl(label);
        return control;
    }
}
