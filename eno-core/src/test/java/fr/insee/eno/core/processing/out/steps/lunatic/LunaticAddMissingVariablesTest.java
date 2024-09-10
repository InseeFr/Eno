package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LunaticAddMissingVariablesTest {

    LunaticAddMissingVariables processing;
    Input input;
    Textarea textarea;
    InputNumber inputNumber;
    CheckboxBoolean checkboxBoolean;
    CheckboxOne checkboxOne;
    Datepicker datepicker;
    Radio radio;
    Dropdown dropdown;
    Table table;
    CheckboxGroup checkboxGroup;
    RosterForLoop rosterForLoop;

    EnoQuestionnaire enoQuestionnaire;

    EnoCatalog enoCatalog;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        input = buildInput("jfazww20", "TEXTECOURT");
        textarea = buildTextarea("jfazwjyv", "TEXTELONG");
        inputNumber = buildNumber("jfjh1ndk", "INTEGER");
        datepicker = buildDatepicker("jfjfckyw", "DATE");
        checkboxBoolean = buildCheckboxBoolean("jfjeud07", "BOOLEEN");
        radio = buildRadio("jfjepz6i", "RADIO");
        checkboxOne = buildCheckboxOne("k6gik8v5", "CHECKBOX");
        dropdown = buildDropdown("jfjfae9f", "DROPDOWN");
        table = buildTable("jfkxybfe", List.of("QCM_OM1", "QCM_OM2", "QCM_ON3"));
        checkboxGroup = buildCheckboxGroup("jfkxybff", List.of("CG1", "CG2", "CG3"));
        rosterForLoop = buildRosterForLoop("jfkxybrl", List.of("RL1", "RL2", "RL3"));
        enoCatalog = new EnoCatalog(enoQuestionnaire);
        processing = new LunaticAddMissingVariables(enoCatalog, true);
    }

    @Test
    void whenApplyingMissingProcessingQuestionnaireMissingAttributeIsTrue() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        processing.apply(lunaticQuestionnaire);
        assertTrue(lunaticQuestionnaire.getMissing());
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectMissingResponse() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));

        processing.apply(lunaticQuestionnaire);

        for (ComponentType component : components) {
            assertEquals(component.getMissingResponse().getName(),
                    enoCatalog.getQuestion(component.getId()).getName() + "_MISSING");
        }
    }

    @Test
    void whenComplexQuestionGenerateCorrectMissingResponse() {
        enoCatalog = new EnoCatalog(enoQuestionnaire);

        processing = new LunaticAddMissingVariables(enoCatalog, true);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();

        components.add(table);
        processing.apply(lunaticQuestionnaire);

        assertEquals(table.getMissingResponse().getName(), enoCatalog.getQuestion(table.getId()).getName()+"_MISSING");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenLoopGenerateMissingResponseOnSubComponents(boolean paginatedLoop) {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();

        // Add a non-response component to test if its presence doesn't generate a bug
        Subsequence subsequence = new Subsequence();
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        subsequence.setId("subsequence-id");

        List<ComponentType> loopComponents = new ArrayList<>(List.of(subsequence, input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, paginatedLoop);
        questionnaireComponents.add(loop);
        processing.apply(lunaticQuestionnaire);

        for (int i = 1; i < loopComponents.size(); i ++) {
            ComponentType component = loopComponents.get(i);
            assertEquals(component.getMissingResponse().getName(), enoCatalog.getQuestion(component.getId()).getName() + "_MISSING");
        }
        assertNull(loop.getMissingResponse());
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectVariables() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        processing.apply(lunaticQuestionnaire);

        List<VariableType> variables = lunaticQuestionnaire.getVariables();
        // Each component should have a missing response that corresponds to a collected variable
        components.forEach(componentType -> assertTrue(variables.stream()
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenMultipleQuestionsGenerateCorrectVariables() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(checkboxGroup, table, rosterForLoop));
        processing.apply(lunaticQuestionnaire);

        List<CollectedVariableType> collectedVariables = lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance)
                .map(CollectedVariableType.class::cast)
                .toList();
        components.forEach(componentType -> assertTrue(collectedVariables.stream()
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));

        collectedVariables.forEach(variable -> {
            if (rosterForLoop.getMissingResponse().getName().equals(variable.getName()))
                assertInstanceOf(CollectedVariableValues.Array.class, variable.getValues());
            if (Set.of(checkboxGroup.getMissingResponse().getName(), table.getMissingResponse().getName())
                    .contains(variable.getName()))
                assertInstanceOf(VariableType.class, variable);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenLoopGenerateCorrectVariables(boolean paginatedLoop) {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Loop loop = buildLoop("jdfhjis5",
                List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown),
                paginatedLoop);
        components.add(loop);
        processing.apply(lunaticQuestionnaire);

        loop.getComponents().forEach(componentType -> assertTrue(lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance)
                .filter(variable -> ((CollectedVariableType) variable).getValues() instanceof CollectedVariableValues.Array)
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectMissingBlocks() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        processing.apply(lunaticQuestionnaire);

        MissingType missingType = lunaticQuestionnaire.getMissingBlock();

        for(ComponentType component : components) {
            String responseName = ((ComponentSimpleResponseType) component).getResponse().getName();
            String missingResponseName = component.getMissingResponse().getName();
            // Missing block
            assertNotNull(missingType.getMissingEntry(missingResponseName));
            assertEquals(1, missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().size());
            assertEquals(responseName, missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().getFirst());
            // Reverse missing block
            assertNotNull(missingType.getMissingEntry(responseName));
            assertEquals(1, missingType.getMissingEntry(responseName).getCorrespondingVariables().size());
            assertEquals(missingResponseName, missingType.getMissingEntry(responseName).getCorrespondingVariables().getFirst());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenLoopGenerateMissingBlocksFromSubComponents(boolean paginatedLoop) {
        // Given
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();
        //
        List<ComponentType> loopComponents = new ArrayList<>(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, paginatedLoop);
        questionnaireComponents.add(loop);
        // When
        processing.apply(lunaticQuestionnaire);
        // Then
        MissingType missingType = lunaticQuestionnaire.getMissingBlock();
        //
        for(ComponentType loopComponent : loopComponents) {
            String responseName = ((ComponentSimpleResponseType) loopComponent).getResponse().getName();
            String missingResponseName = loopComponent.getMissingResponse().getName();
            // Missing block
            assertNotNull(missingType.getMissingEntry(missingResponseName));
            assertEquals(1, missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().size());
            assertEquals(responseName, missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().getFirst());
            // Reverse missing block
            assertNotNull(missingType.getMissingEntry(responseName));
            assertEquals(1, missingType.getMissingEntry(responseName).getCorrespondingVariables().size());
            assertEquals(missingResponseName, missingType.getMissingEntry(responseName).getCorrespondingVariables().getFirst());
        }
    }

    @Test
    void whenPairwiseGenerateMissingBlocksFromSubComponents() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();

        List<ComponentType> pairwiseComponents = new ArrayList<>(List.of(dropdown));
        PairwiseLinks pairwiseLinks = buildPairWiseLinks("jghdkpdf", pairwiseComponents);
        questionnaireComponents.add(pairwiseLinks);
        processing.apply(lunaticQuestionnaire);

        ComponentType pairwiseInnerComponent = pairwiseComponents.getFirst();
        MissingType missingType = lunaticQuestionnaire.getMissingBlock();

        String pairwiseMissingResponseName = pairwiseInnerComponent.getMissingResponse().getName();
        String pairwiseResponseName = ((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName();
        // Missing block
        assertNotNull(missingType.getMissingEntry(pairwiseMissingResponseName));
        assertEquals(1, missingType.getMissingEntry(pairwiseMissingResponseName).getCorrespondingVariables().size());
        assertEquals(pairwiseResponseName, missingType.getMissingEntry(pairwiseMissingResponseName).getCorrespondingVariables().getFirst());
        // Reverse missing block
        assertNotNull(missingType.getMissingEntry(pairwiseResponseName));
        assertEquals(1, missingType.getMissingEntry(pairwiseResponseName).getCorrespondingVariables().size());
        assertEquals(pairwiseMissingResponseName, missingType.getMissingEntry(pairwiseResponseName).getCorrespondingVariables().getFirst());
    }

    @Test
    void whenCheckboxGroupQuestionsGenerateCorrectMissingBlocks() {
        // Given: questionnaire with simple response components
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.add(checkboxGroup);

        // When
        processing.apply(lunaticQuestionnaire);

        // Then
        MissingType missingType = lunaticQuestionnaire.getMissingBlock();

        //
        List<String> responseNames = checkboxGroup.getResponses().stream()
                .map(responsesCheckboxGroup -> responsesCheckboxGroup.getResponse().getName())
                .toList();
        String missingResponseName = checkboxGroup.getMissingResponse().getName();
        // Missing block
        assertNotNull(missingType.getMissingEntry(missingResponseName));
        assertEquals(responseNames.size(), missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().size());
        assertTrue(missingType.getMissingEntry(missingResponseName).getCorrespondingVariables().containsAll(responseNames));
        // Reverse missing block
        responseNames.forEach(responseName -> {
            assertNotNull(missingType.getMissingEntry(responseName));
            assertEquals(1, missingType.getMissingEntry(responseName).getCorrespondingVariables().size());
            assertEquals(missingResponseName, missingType.getMissingEntry(responseName).getCorrespondingVariables().getFirst());
        });
    }

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
        List<BodyLine> bodyLines = input.getBodyLines();

        for(int cpt=0; cpt<responseNames.size(); cpt++) {
            List<BodyCell> bodyCells = new ArrayList<>();
            bodyCells.add(buildBodyCell(Integer.toString(cpt)));
            bodyCells.add(buildBodyCell(id+"-"+"-"+cpt, responseNames.get(cpt), ComponentTypeEnum.CHECKBOX_ONE));
            buildEnoQuestion(id+"-"+"-"+cpt, responseNames.get(cpt));
            bodyLines.add(buildBodyLine(bodyCells));
        }
        buildEnoQuestion(id, "table");
        return input;
    }

    private BodyCell buildBodyCell(String id, String name, ComponentTypeEnum componentType) {
        BodyCell bodyCell = new BodyCell();
        bodyCell.setId(id);
        bodyCell.setComponentType(componentType);
        bodyCell.setResponse(buildResponse(name));
        buildEnoQuestion(id, name);
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

    private Loop buildLoop(String id, List<ComponentType>components, boolean isPaginatedLoop) {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.setId(id);
        loop.getComponents().addAll(components);
        loop.setPaginatedLoop(isPaginatedLoop);
        return loop;
    }

    private RosterForLoop buildRosterForLoop(String id, List<String> responseNames) {
        RosterForLoop input = new RosterForLoop();
        input.setId(id);
        input.setComponentType(ComponentTypeEnum.ROSTER_FOR_LOOP);

        List<BodyCell> bodyCells = input.getComponents();
        for(int cpt=0; cpt<responseNames.size(); cpt++) {
            bodyCells.add(buildBodyCell(Integer.toString(cpt)));
            bodyCells.add(buildBodyCell(id+"-"+"-"+cpt, responseNames.get(cpt), ComponentTypeEnum.CHECKBOX_ONE));
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
