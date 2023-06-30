package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.CleaningVariable;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddCleaningVariablesTest {

    LunaticAddCleaningVariables processing;
    Input i;
    Textarea ta;
    InputNumber n;
    CheckboxBoolean cb;
    CheckboxOne co;
    Datepicker d;
    Radio r;
    Dropdown dd;
    Table t;
    CheckboxGroup cg;
    RosterForLoop rl;

    Questionnaire lunaticQuestionnaire;

    EnoQuestionnaire enoQuestionnaire;

    EnoCatalog enoCatalog;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        lunaticQuestionnaire = new Questionnaire();
        i = buildInput("jfazww20", "TEXTECOURT");
        ta = buildTextarea("jfazwjyv", "TEXTELONG");
        n = buildNumber("jfjh1ndk", "INTEGER");
        d = buildDatepicker("jfjfckyw", "DATE");
        cb = buildCheckboxBoolean("jfjeud07", "BOOLEEN");
        r = buildRadio("jfjepz6i", "RADIO");
        co = buildCheckboxOne("k6gik8v5", "CHECKBOX");
        dd = buildDropdown("jfjfae9f", "DROPDOWN");
        t = buildTable("jfkxybfe", List.of("QCM_OM1", "QCM_OM2", "QCM_ON3"));
        cg = buildCheckboxGroup("jfkxybff", List.of("CG1", "CG2", "CG3"));
        rl = buildRosterForLoop("jfkxybrl", List.of("RL1", "RL2", "RL3"));
        enoCatalog = new EnoCatalog(enoQuestionnaire);
        processing = new LunaticAddCleaningVariables(enoCatalog);
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(i, ta, n, d, cb, r, co, dd));
        processing.apply(lunaticQuestionnaire);

        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();
        assertNotNull(variables);
    }

    @Test
    void whenMultipleQuestionsGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(cg, t, rl));
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        components.forEach(componentType -> assertTrue(variables.stream()
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenLinkedLoopGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Loop loop = buildLoop("jdfhjis5", List.of(i, ta, n, d, cb, r, co, dd), false);
        components.add(loop);
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        loop.getComponents().forEach(componentType -> assertTrue(variables.stream()
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenMainLoopGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Loop loop = buildLoop("jdfhjis5", List.of(i, ta, n, d, cb, r, co, dd), true);
        components.add(loop);
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        assertTrue(variables.stream()
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(loop.getMissingResponse().getName())));
    }

    private CheckboxGroup buildCheckboxGroup(String id, List<String> names) {
        CheckboxGroup input = new CheckboxGroup();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_GROUP);
        input.setId(id);
        List<ResponsesCheckboxGroup> responses = names.stream()
                .map(name -> {
                    ResponsesCheckboxGroup response = new ResponsesCheckboxGroup();
                    response.setResponse(buildResponse(name));
                    response.setId(id+"-"+name);
                    return response;
                }).toList();
        input.getResponses().addAll(responses);
        buildEnoQuestion(id, "checkboxgroup");
        return input;
    }

    private CheckboxOne buildCheckboxOne(String id, String name) {
        CheckboxOne input = new CheckboxOne();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return input;
    }

    private Dropdown buildDropdown(String id, String name) {
        Dropdown input = new Dropdown();
        input.setComponentType(ComponentTypeEnum.DROPDOWN);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return input;
    }

    private CheckboxBoolean buildCheckboxBoolean(String id, String name) {
        CheckboxBoolean input = new CheckboxBoolean();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_BOOLEAN);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return input;
    }

    private Radio buildRadio(String id, String name) {
        Radio input = new Radio();
        input.setComponentType(ComponentTypeEnum.RADIO);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return input;
    }

    private Datepicker buildDatepicker(String id, String name) {
        Datepicker input = new Datepicker();
        input.setComponentType(ComponentTypeEnum.DATEPICKER);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return input;
    }

    private Input buildInput(String id, String name) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        input.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        buildEnoQuestion(id, name);
        return input;
    }

    private Textarea buildTextarea(String id, String name) {
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setId(id);
        textarea.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
        return textarea;
    }

    private InputNumber buildNumber(String id, String name) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        number.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
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
            buildEnoQuestion(id+"-"+"-"+cpt, responseNames.get(cpt));
            bodyTypes.add(buildBodyType(bodyLines));
        }
        buildEnoQuestion(id, "table");
        return input;
    }

    private BodyLine buildBodyLine(String id, String name, String componentType) {
        BodyLine bodyLine = new BodyLine();
        bodyLine.setId(id);
        bodyLine.setComponentType(componentType);
        bodyLine.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
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

    private Loop buildLoop(String id, List<ComponentType>components, boolean isMainLoop) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.getComponents().addAll(components);
        if(isMainLoop) {
            LinesLoop line = new LinesLoop();
            LabelType label = new LabelType();
            label.setValue("COUNT(prenom)");
            line.setMax(label);
            loop.setLines(line);
            return loop;
        }
        LabelType label = new LabelType();
        label.setValue("COUNT(prenom)");
        loop.setIterations(label);
        return loop;
    }

    private RosterForLoop buildRosterForLoop(String id, List<String> responseNames) {
        RosterForLoop input = new RosterForLoop();
        input.setId(id);
        input.setComponentType(ComponentTypeEnum.ROSTER_FOR_LOOP);

        List<BodyLine> bodyLines = input.getComponents();
        for(int cpt=0; cpt<responseNames.size(); cpt++) {
            bodyLines.add(buildBodyLine(Integer.toString(cpt)));
            bodyLines.add(buildBodyLine(id+"-"+"-"+cpt, responseNames.get(cpt), ComponentTypeEnum.CHECKBOX_ONE.value()));
            buildEnoQuestion(id+"-"+"-"+cpt, responseNames.get(cpt));
        }
        buildEnoQuestion(id, "roster");
        return input;
    }

    private PairwiseLinks buildPairWiseLinks(String id, List<ComponentType>components) {
        PairwiseLinks pairwiseLinks = new PairwiseLinks();
        pairwiseLinks.setId(id);
        pairwiseLinks.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        pairwiseLinks.getComponents().addAll(components);
        return pairwiseLinks;
    }

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    private void buildEnoQuestion(String id, String name) {
        SingleResponseQuestion question = new TextQuestion();
        question.setId(id);
        question.setName(name);
        enoQuestionnaire.getSingleResponseQuestions().add(question);
    }
}
