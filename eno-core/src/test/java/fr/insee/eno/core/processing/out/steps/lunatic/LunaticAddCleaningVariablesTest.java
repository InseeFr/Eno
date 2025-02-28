package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.ExternalVariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LunaticAddCleaningVariablesTest {

    LunaticAddCleaningVariables processing;
    Input input;
    InputNumber inputNumber;
    CheckboxOne checkboxOne;
    Dropdown dropdown;
    Table table;
    CheckboxGroup checkboxGroup;
    Loop loop;

    @BeforeEach
    void init() {
        //
        checkboxOne = buildCheckboxOne("checkbox-id", "CHECKBOX");
        table = buildTable("table-id", List.of("CELL1", "CELL2", "CELL3"));
        inputNumber = buildNumber("input-number-id", "INTEGER");
        checkboxGroup = buildCheckboxGroup("checkbox-group-id", List.of("MODALITY1", "MODALITY2"));
        //
        dropdown = buildDropdown("dropdown-id", "DROPDOWN");
        input = buildInput("input-id", "SHORT_TEXT");
        loop = buildLoop("loop-id", List.of(dropdown, inputNumber));
        //
        processing = new LunaticAddCleaningVariables(null);
    }

    @Test
    void whenNoComponentWithFilter_shouldNotHaveCleaning() {

        List<ComponentType> components = List.of(checkboxOne, table, loop, inputNumber, checkboxGroup);
        for (ComponentType component : components) {
            component.setConditionFilter(null);
        }
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(components);

        processing.apply(lunaticQuestionnaire);

        assertNull(lunaticQuestionnaire.getCleaning());
    }

    @Test
    void whenNoDependenciesInFilters_shouldNotHaveCleaning() {

        List<ComponentType> components = List.of(checkboxOne, table, loop, inputNumber, checkboxGroup);
        for (ComponentType component : components) {
            component.setConditionFilter(buildConditionFilter("true", new ArrayList<>()));
        }
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().addAll(components);

        processing.apply(lunaticQuestionnaire);

        assertNull(lunaticQuestionnaire.getCleaning());
    }

    @Test
    void simpleResponseComponentWithBindingDependencies_shouldHaveCleaningEntries() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(input);

        input.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q11", "Q12")));

        lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("SUM1"));
        lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q11"));
        lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q12"));

        //
        processing.apply(lunaticQuestionnaire);

        //
        CleaningType cleaningType = lunaticQuestionnaire.getCleaning();

        assertEquals(2, cleaningType.countCleaningVariables());

        CleaningVariableEntry q11Entry = cleaningType.getCleaningEntry("Q11");
        assertNotNull(q11Entry);
        assertEquals(1, q11Entry.countCleanedVariables());
        assertNotNull(q11Entry.getCleanedVariable("SHORT_TEXT"));
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("SHORT_TEXT").filterExpression());

        CleaningVariableEntry q12Entry = cleaningType.getCleaningEntry("Q12");
        assertNotNull(q12Entry);
        assertEquals(1, q12Entry.countCleanedVariables());
        assertNotNull(q12Entry.getCleanedVariable("SHORT_TEXT"));
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("SHORT_TEXT").filterExpression());
    }

    @Test
    void componentWithBindingDependenciesWithinLoop_shouldHaveCleaningEntries() {
        //
        dropdown.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q1", "Q2")));

        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(loop);

        lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("SUM1"));
        lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q1"));
        lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q2"));

        //
        processing.apply(lunaticQuestionnaire);

        //
        CleaningType cleaningType = lunaticQuestionnaire.getCleaning();

        assertEquals(2, cleaningType.countCleaningVariables());

        CleaningVariableEntry q1Entry = cleaningType.getCleaningEntry("Q1");
        assertNotNull(q1Entry);
        assertEquals(1, q1Entry.countCleanedVariables());
        assertNotNull(q1Entry.getCleanedVariable("DROPDOWN"));
        assertEquals("(SUM1 < 10)", q1Entry.getCleanedVariable("DROPDOWN").filterExpression());

        CleaningVariableEntry q2Entry = cleaningType.getCleaningEntry("Q2");
        assertNotNull(q2Entry);
        assertEquals(1, q2Entry.countCleanedVariables());
        assertNotNull(q2Entry.getCleanedVariable("DROPDOWN"));
        assertEquals("(SUM1 < 10)", q2Entry.getCleanedVariable("DROPDOWN").filterExpression());
    }

    @Nested
    class ComponentsWithCommonDependencies {

        private CleaningType cleaningType;

        @BeforeEach
        void setupComponentsHavingCommonDependencies() {
            //
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            lunaticQuestionnaire.getComponents().addAll(List.of(input, loop));

            input.setConditionFilter(buildConditionFilter("(TEST > 30)", List.of("Q1", "Q2")));
            inputNumber.setConditionFilter(buildConditionFilter("(SUM2 < 10)", List.of("Q2")));
            dropdown.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q1")));

            lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("TEST"));
            lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("SUM1"));
            lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("SUM2"));
            lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q1"));
            lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q2"));

            //
            processing.apply(lunaticQuestionnaire);

            //
            cleaningType = lunaticQuestionnaire.getCleaning();
        }

        @Test
        void shouldHaveGroupedCleaningVariables_count(){
            assertEquals(2, cleaningType.countCleaningVariables());
        }

        @Test
        void shouldHaveGroupedCleaningVariables_keys() {
            assertThat(cleaningType.getCleaningVariableNames()).containsExactlyInAnyOrderElementsOf(
                    Set.of("Q1", "Q2"));
        }

        @Test
        void shouldHaveGroupedCleaningVariables_values() {

            CleaningVariableEntry cleaningEntry1 = cleaningType.getCleaningEntry("Q1");
            assertEquals(2, cleaningEntry1.countCleanedVariables());
            assertEquals("(TEST > 30)", cleaningEntry1.getCleanedVariable("SHORT_TEXT").filterExpression());
            assertEquals("(SUM1 < 10)", cleaningEntry1.getCleanedVariable("DROPDOWN").filterExpression());

            CleaningVariableEntry cleaningEntry2 = cleaningType.getCleaningEntry("Q2");
            assertEquals(2, cleaningEntry2.countCleanedVariables());
            assertEquals("(TEST > 30)", cleaningEntry2.getCleanedVariable("SHORT_TEXT").filterExpression());
            assertEquals("(SUM2 < 10)", cleaningEntry2.getCleanedVariable("INTEGER").filterExpression());
        }

    }

    @Test
    void checkboxGroupWithBindingDependencies_shouldHaveCleaningEntries() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(checkboxGroup);

        checkboxGroup.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q11", "Q12")));

        //
        processing.apply(lunaticQuestionnaire);

        //
        CleaningType cleaningType = lunaticQuestionnaire.getCleaning();

        assertEquals(2, cleaningType.countCleaningVariables());

        CleaningVariableEntry q11Entry = cleaningType.getCleaningEntry("Q11");
        assertEquals(2, q11Entry.countCleanedVariables());
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("MODALITY1").filterExpression());
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("MODALITY2").filterExpression());

        CleaningVariableEntry q12Entry = cleaningType.getCleaningEntry("Q11");
        assertEquals(2, q12Entry.countCleanedVariables());
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("MODALITY1").filterExpression());
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("MODALITY2").filterExpression());
    }

    @Test
    void tableWithBindingDependencies_shouldHaveCleaningEntries() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(table);

        table.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q11", "Q12")));

        //
        processing.apply(lunaticQuestionnaire);

        //
        CleaningType cleaningType = lunaticQuestionnaire.getCleaning();

        assertEquals(2, cleaningType.countCleaningVariables());

        CleaningVariableEntry q11Entry = cleaningType.getCleaningEntry("Q11");
        assertEquals(3, q11Entry.countCleanedVariables());
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("CELL1").filterExpression());
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("CELL2").filterExpression());
        assertEquals("(SUM1 < 10)", q11Entry.getCleanedVariable("CELL3").filterExpression());

        CleaningVariableEntry q12Entry = cleaningType.getCleaningEntry("Q12");
        assertEquals(3, q12Entry.countCleanedVariables());
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("CELL1").filterExpression());
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("CELL2").filterExpression());
        assertEquals("(SUM1 < 10)", q12Entry.getCleanedVariable("CELL3").filterExpression());
    }

    @Test
    void externalBindingDependency_shouldNotBeInCleaningEntries() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(input);

        input.setConditionFilter(buildConditionFilter("(SUM1 < 10)", List.of("Q1", "EXTERNAL1")));

        lunaticQuestionnaire.getVariables().add(buildCalculatedVariable("SUM1"));
        lunaticQuestionnaire.getVariables().add(buildCollectedVariable("Q1"));
        lunaticQuestionnaire.getVariables().add(buildExternalVariable("EXTERNAL1"));

        //
        processing.apply(lunaticQuestionnaire);

        //
        CleaningType cleaningType = lunaticQuestionnaire.getCleaning();

        assertEquals(1, cleaningType.countCleaningVariables());

        CleaningVariableEntry q1Entry = cleaningType.getCleaningEntry("Q1");
        assertEquals(1, q1Entry.countCleanedVariables());
        assertEquals("(SUM1 < 10)", q1Entry.getCleanedVariable("SHORT_TEXT").filterExpression());
    }

    /* ----- Private utility methods below ----- */

    private CheckboxGroup buildCheckboxGroup(String id, List<String> names) {
        CheckboxGroup input = new CheckboxGroup();
        input.setComponentType(ComponentTypeEnum.CHECKBOX_GROUP);
        input.setId(id);
        List<ResponseCheckboxGroup> responses = names.stream()
                .map(name -> {
                    ResponseCheckboxGroup response = new ResponseCheckboxGroup();
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
        bodyCell.setResponse(buildResponse(name));
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

    private ResponseType buildResponse(String name) {
        ResponseType response = new ResponseType();
        response.setName(name);
        return response;
    }

    private ConditionFilterType buildConditionFilter(String vtlExpression, List<String> bindingDependencies) {
        ConditionFilterType conditionFilter = new ConditionFilterType();
        conditionFilter.setType(LabelTypeEnum.VTL);
        conditionFilter.setValue(vtlExpression);
        conditionFilter.getBindingDependencies().addAll(bindingDependencies);
        return conditionFilter;
    }

    private CollectedVariableType buildCollectedVariable(String variableName) {
        CollectedVariableType variable = new CollectedVariableType();
        variable.setName(variableName);
        return variable;
    }

    private CalculatedVariableType buildCalculatedVariable(String variableName) {
        CalculatedVariableType variable = new CalculatedVariableType();
        variable.setName(variableName);
        return variable;
    }

    private ExternalVariableType buildExternalVariable(String variableName) {
        ExternalVariableType variable = new ExternalVariableType();
        variable.setVariableType(VariableTypeEnum.EXTERNAL);
        variable.setName(variableName);
        return variable;
    }

}
