package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.lunatic.CleaningConcernedVariable;
import fr.insee.eno.core.model.lunatic.CleaningVariable;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticAddCleaningVariablesTest {

    LunaticAddCleaningVariables processing;
    Input i;
    InputNumber ln;
    CheckboxOne co;
    Dropdown ldd;
    Table t;
    CheckboxGroup lcg;
    Loop l;

    Questionnaire lunaticQuestionnaire;

    @BeforeEach
    void init() {
        i = buildInput("jfazww20", "TEXTECOURT");
        co = buildCheckboxOne("k6gik8v5", "CHECKBOX");
        t = buildTable("jfkxybfe", List.of("QCM_OM1", "QCM_OM2", "QCM_OM3"));
        ldd = buildDropdown("jfjfae9f", "DROPDOWN");
        ln = buildNumber("jfjh1ndk", "INTEGER");
        lcg = buildCheckboxGroup("jfkxybff", List.of("QCM_OM1", "QCM_OM2", "QCM_OM3"));
        l = buildLoop("loopid", List.of(ldd, ln));
        processing = new LunaticAddCleaningVariables();
    }

    @Test
    void shouldNotHaveCleaningVariableWhenComponentWithNoConditionFilter() {
        List<ComponentType> components = List.of(i, co, t, l, ln, ldd, lcg);
        for (ComponentType component : components) {
            component.setConditionFilter(null);
        }
        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, co, t, l));
        processing.apply(lunaticQuestionnaire);

        assertTrue(lunaticQuestionnaire.getCleaning().getAny().isEmpty());
    }

    @Test
    void shouldNotHaveCleaningVariableWhenComponentWithEmptyDependenciesInConditionFilter() {
        lunaticQuestionnaire = new Questionnaire();
        i.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("SUM1", "Q11", "Q12")));

        List<ComponentType> components = List.of(i, co, t, l, ln, ldd, lcg);
        for (ComponentType component : components) {
            component.setConditionFilter(buildConditionFilter("true", new ArrayList<>()));
        }
        lunaticQuestionnaire.getComponents().addAll(List.of(i, co, t, l));
        processing.apply(lunaticQuestionnaire);
        assertTrue(lunaticQuestionnaire.getCleaning().getAny().isEmpty());
    }

    @Test
    void shouldHaveCleaningVariableWhenSimpleResponseComponentWithBindingDependencies() {

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(i);

        i.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("SUM1", "Q11", "Q12")));

        processing.apply(lunaticQuestionnaire);
        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();

        assertEquals(3, variables.size());

        CleaningVariable variable = variables.get(0);
        assertEquals("SUM1", variable.getName());
        List<CleaningConcernedVariable> concernedVariables = variable.getConcernedVariables();
        assertEquals(1, concernedVariables.size());
        assertEquals("TEXTECOURT", concernedVariables.get(0).getName());
        assertEquals("(SUM1 < 10)", concernedVariables.get(0).getFilter());

        variable = variables.get(1);
        assertEquals("Q11", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("TEXTECOURT", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM1 < 10)", variable.getConcernedVariables().get(0).getFilter());

        variable = variables.get(2);
        assertEquals("Q12", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("TEXTECOURT", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM1 < 10)", variable.getConcernedVariables().get(0).getFilter());
    }

    @Test
    void shouldHaveCleaningVariablesOnLoopComponentsWithBindingDependencies() {

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(l);

        ln.setConditionFilter(buildConditionFilter("(SUM2 < 10)", List.of("SUM2", "Q2")));
        ldd.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("SUM1", "Q1")));

        processing.apply(lunaticQuestionnaire);
        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();

        assertEquals(4, variables.size());

        CleaningVariable variable = variables.get(0);
        assertEquals("SUM1", variable.getName());
        List<CleaningConcernedVariable> concernedVariables = variable.getConcernedVariables();
        assertEquals(1, concernedVariables.size());
        assertEquals("DROPDOWN", concernedVariables.get(0).getName());
        assertEquals("(SUM1 < 10)", concernedVariables.get(0).getFilter());

        variable = variables.get(1);
        assertEquals("Q1", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("DROPDOWN", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM1 < 10)", variable.getConcernedVariables().get(0).getFilter());

        variable = variables.get(2);
        assertEquals("SUM2", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(1, concernedVariables.size());
        assertEquals("INTEGER", concernedVariables.get(0).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(0).getFilter());

        variable = variables.get(3);
        assertEquals("Q2", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("INTEGER", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM2 < 10)", variable.getConcernedVariables().get(0).getFilter());
    }

    @Test
    void shouldHaveGroupedCleaningVariablesOnComponentsHavingSameDependencies() {

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(List.of(i, l));

        i.setConditionFilter(buildConditionFilter("(TEST > 30)", List.of("TEST", "SUM2")));
        ln.setConditionFilter(buildConditionFilter("(SUM2 < 10)", List.of("SUM2", "Q2")));
        ldd.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("SUM1", "Q1")));

        processing.apply(lunaticQuestionnaire);
        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();

        assertEquals(5, variables.size());

        CleaningVariable variable = variables.get(0);
        assertEquals("TEST", variable.getName());
        List<CleaningConcernedVariable> concernedVariables = variable.getConcernedVariables();
        assertEquals(1, concernedVariables.size());
        assertEquals("TEXTECOURT", concernedVariables.get(0).getName());
        assertEquals("(TEST > 30)", concernedVariables.get(0).getFilter());

        variable = variables.get(1);
        assertEquals("SUM2", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(2, concernedVariables.size());
        assertEquals("TEXTECOURT", concernedVariables.get(0).getName());
        assertEquals("(TEST > 30)", concernedVariables.get(0).getFilter());
        assertEquals("INTEGER", concernedVariables.get(1).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(1).getFilter());

        variable = variables.get(2);
        assertEquals("SUM1", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(1, concernedVariables.size());
        assertEquals("DROPDOWN", concernedVariables.get(0).getName());
        assertEquals("(SUM1 < 10)", concernedVariables.get(0).getFilter());

        variable = variables.get(3);
        assertEquals("Q1", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("DROPDOWN", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM1 < 10)", variable.getConcernedVariables().get(0).getFilter());

        variable = variables.get(4);
        assertEquals("Q2", variable.getName());
        assertEquals(1, variable.getConcernedVariables().size());
        assertEquals("INTEGER", variable.getConcernedVariables().get(0).getName());
        assertEquals("(SUM2 < 10)", variable.getConcernedVariables().get(0).getFilter());
    }

    @Test
    void shouldHaveCleaningVariablesWhenTableWithBindingDependencies() {

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(t);

        t.setConditionFilter(buildConditionFilter("(SUM2 < 10)", List.of("SUM2", "SUM1", "Q11", "Q12")));

        processing.apply(lunaticQuestionnaire);
        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();

        checkCleaningVariablesOnComplexResponseType(variables);
    }

    @Test
    void shouldHaveCleaningVariableWhenCheckboxGroupWithBindingDependencies() {

        lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(lcg);

        lcg.setConditionFilter(buildConditionFilter("(SUM2 < 10)", List.of("SUM2", "SUM1", "Q11", "Q12")));

        processing.apply(lunaticQuestionnaire);
        List<CleaningVariable> variables = lunaticQuestionnaire.getCleaning().getAny().stream()
                .map(CleaningVariable.class::cast)
                .toList();

        checkCleaningVariablesOnComplexResponseType(variables);
    }

    private void checkCleaningVariablesOnComplexResponseType(List<CleaningVariable> variables) {
        assertEquals(4, variables.size());

        CleaningVariable variable = variables.get(0);
        assertEquals("SUM2", variable.getName());
        List<CleaningConcernedVariable> concernedVariables = variable.getConcernedVariables();
        assertEquals(3, concernedVariables.size());
        assertEquals("QCM_OM1", concernedVariables.get(0).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(0).getFilter());
        assertEquals("QCM_OM2", concernedVariables.get(1).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(1).getFilter());
        assertEquals("QCM_OM3", concernedVariables.get(2).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(2).getFilter());

        variable = variables.get(1);
        assertEquals("SUM1", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(3, concernedVariables.size());
        assertEquals("QCM_OM1", concernedVariables.get(0).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(0).getFilter());
        assertEquals("QCM_OM2", concernedVariables.get(1).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(1).getFilter());
        assertEquals("QCM_OM3", concernedVariables.get(2).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(2).getFilter());

        variable = variables.get(2);
        assertEquals("Q11", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(3, concernedVariables.size());
        assertEquals("QCM_OM1", concernedVariables.get(0).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(0).getFilter());
        assertEquals("QCM_OM2", concernedVariables.get(1).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(1).getFilter());
        assertEquals("QCM_OM3", concernedVariables.get(2).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(2).getFilter());

        variable = variables.get(3);
        assertEquals("Q12", variable.getName());
        concernedVariables = variable.getConcernedVariables();
        assertEquals(3, concernedVariables.size());
        assertEquals("QCM_OM1", concernedVariables.get(0).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(0).getFilter());
        assertEquals("QCM_OM2", concernedVariables.get(1).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(1).getFilter());
        assertEquals("QCM_OM3", concernedVariables.get(2).getName());
        assertEquals("(SUM2 < 10)", concernedVariables.get(2).getFilter());
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
        return input;
    }

    private CheckboxOne buildCheckboxOne(String id, String name) {
        CheckboxOne input = new CheckboxOne();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        input.setId(id);
        input.setResponse(buildResponse(name));
        return input;
    }

    private Dropdown buildDropdown(String id, String name) {
        Dropdown input = new Dropdown();
        input.setComponentType(ComponentTypeEnum.DROPDOWN);
        input.setId(id);
        input.setResponse(buildResponse(name));
        return input;
    }

    private Input buildInput(String id, String name) {
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(id);
        input.setResponse(buildResponse(name));
        return input;
    }

    private InputNumber buildNumber(String id, String name) {
        InputNumber number = new InputNumber();
        number.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        number.setId(id);
        number.setResponse(buildResponse(name));
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
        bodyLine.setResponse(buildResponse(name));
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

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    private ConditionFilterType buildConditionFilter(String vtlExpression, List<String> bindingDependencies) {
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setType("VTL");
        conditionFilter.setValue(vtlExpression);
        conditionFilter.getBindingDependencies().addAll(bindingDependencies);
        return conditionFilter;
    }
}
