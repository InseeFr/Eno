package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.MissingBlock;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    Questionnaire lunaticQuestionnaire;

    EnoQuestionnaire enoQuestionnaire;

    EnoCatalog enoCatalog;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        lunaticQuestionnaire = new Questionnaire();
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
        processing.apply(lunaticQuestionnaire);
        assertTrue(lunaticQuestionnaire.getMissing());
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectMissingResponse() {
        lunaticQuestionnaire = new Questionnaire();
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
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();

        components.add(table);
        processing.apply(lunaticQuestionnaire);

        assertEquals(table.getMissingResponse().getName(), enoCatalog.getQuestion(table.getId()).getName()+"_MISSING");
    }

    @Test
    void whenPaginatedLoopGenerateMissingResponseOnSubComponents() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();


        List<ComponentType> loopComponents = new ArrayList<>(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, true);
        questionnaireComponents.add(loop);
        processing.apply(lunaticQuestionnaire);

        for (ComponentType component : loopComponents) {
            assertEquals(component.getMissingResponse().getName(), enoCatalog.getQuestion(component.getId()).getName() + "_MISSING");
        }
        assertNull(loop.getMissingResponse());
    }

    @Test
    void whenNonPaginatedLoopGenerateSingleMissingResponse() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();

        List<ComponentType> loopComponents = new ArrayList<>(List.of(table, input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, false);
        questionnaireComponents.add(loop);
        processing.apply(lunaticQuestionnaire);

        for (ComponentType component : loopComponents) {
            assertNull(component.getMissingResponse());
        }

        // The first response that belongs to a simple response component is used
        assertEquals(input.getResponse().getName()+"_MISSING", loop.getMissingResponse().getName());
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        // Each component should have a missing response that corresponds to a collected variable
        components.forEach(componentType -> assertTrue(variables.stream()
                .filter(VariableType.class::isInstance)
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenMultipleQuestionsGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(checkboxGroup, table, rosterForLoop));
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        components.forEach(componentType -> assertTrue(variables.stream()
                    .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                    .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));

        variables.forEach(variable -> {
            if (rosterForLoop.getMissingResponse().getName().equals(variable.getName()))
                assertInstanceOf(VariableTypeArray.class, variable);
            if (Set.of(checkboxGroup.getMissingResponse().getName(), table.getMissingResponse().getName())
                    .contains(variable.getName()))
                assertInstanceOf(VariableType.class, variable);
        });
    }

    @Test
    void whenPaginatedLoopGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Loop loop = buildLoop("jdfhjis5",
                List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown),
                true);
        components.add(loop);
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        loop.getComponents().forEach(componentType -> assertTrue(variables.stream()
                .filter(VariableTypeArray.class::isInstance)
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(componentType.getMissingResponse().getName()))));
    }

    @Test
    void whenNonPaginatedLoopGenerateCorrectVariables() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Loop loop = buildLoop("jdfhjis5",
                List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown),
                false);
        components.add(loop);
        processing.apply(lunaticQuestionnaire);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        assertTrue(variables.stream()
                .filter(VariableTypeArray.class::isInstance)
                .filter(variable -> variable.getVariableType().equals(VariableTypeEnum.COLLECTED))
                .anyMatch(variable -> variable.getName().equals(loop.getMissingResponse().getName())));
    }

    @Test
    void whenSimpleQuestionsGenerateCorrectMissingBlocks() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        processing.apply(lunaticQuestionnaire);

        List<ComponentSimpleResponseType> responseTypes = components.stream().map(ComponentSimpleResponseType.class::cast).toList();
        List<MissingBlock> missingBlocks = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();

        for(int cpt=0; cpt<components.size(); cpt++) {
            ComponentType component = components.get(cpt);
            ComponentSimpleResponseType simpleResponseType = responseTypes.get(cpt);
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(component.getMissingResponse().getName())
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(simpleResponseType.getResponse().getName())));
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(simpleResponseType.getResponse().getName())
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(component.getMissingResponse().getName())));
        }
    }

    @Test
    void whenLinkedLoopGenerateMissingBlocksFromSubComponents() {
        // Given
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();
        //
        List<ComponentType> loopComponents = new ArrayList<>(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, true);
        questionnaireComponents.add(loop);
        // When
        processing.apply(lunaticQuestionnaire);
        // Then
        List<MissingBlock> missingBlocks = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();
        //
        for(ComponentType loopComponent : loopComponents) {
            String responseName = ((ComponentSimpleResponseType) loopComponent).getResponse().getName();
            String missingResponseName = loopComponent.getMissingResponse().getName();
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(missingResponseName)
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(responseName)));
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(responseName)
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(missingResponseName)));
        }
    }

    @Test
    void whenPairwiseGenerateMissingBlocksFromSubComponents() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();

        List<ComponentType> pairwiseComponents = new ArrayList<>(List.of(dropdown));
        PairwiseLinks pairwiseLinks = buildPairWiseLinks("jghdkpdf", pairwiseComponents);
        questionnaireComponents.add(pairwiseLinks);
        processing.apply(lunaticQuestionnaire);

        ComponentType pairwiseInnerComponent = pairwiseComponents.get(0);
        List<MissingBlock> missingBlocks = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();

        assertTrue(missingBlocks.stream().anyMatch(missingBlock ->
                missingBlock.getMissingName().equals(pairwiseInnerComponent.getMissingResponse().getName())
                        && missingBlock.getNames().size() == 1
                        && missingBlock.getNames().contains(((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName())));
        assertTrue(missingBlocks.stream().anyMatch(missingBlock ->
                missingBlock.getMissingName().equals(((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName())
                        && missingBlock.getNames().size() == 1
                        && missingBlock.getNames().contains(pairwiseInnerComponent.getMissingResponse().getName())));
    }

    @Test
    void whenNonPaginatedLoopGenerateMissingBlocksFromLoopMissingResponse() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> questionnaireComponents = lunaticQuestionnaire.getComponents();


        List<ComponentType> loopComponents = new ArrayList<>(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        Loop loop = buildLoop("jghdkmdf", loopComponents, false);
        questionnaireComponents.add(loop);
        processing.apply(lunaticQuestionnaire);

        List<ComponentSimpleResponseType> responseTypes = loopComponents.stream().map(ComponentSimpleResponseType.class::cast).toList();
        List<MissingBlock> missingBlocks = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();

        for(int cpt=0; cpt<loopComponents.size(); cpt++) {
            ComponentSimpleResponseType simpleResponseType = responseTypes.get(cpt);
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(loop.getMissingResponse().getName())
                            && missingBlock.getNames().size() == 8));
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(simpleResponseType.getResponse().getName())
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(loop.getMissingResponse().getName())));
        }
    }

    @Test
    void whenCheckboxGroupQuestionsGenerateCorrectMissingBlocks() {
        lunaticQuestionnaire = new Questionnaire();
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        components.addAll(List.of(input, textarea, inputNumber, datepicker, checkboxBoolean, radio, checkboxOne, dropdown));
        processing.apply(lunaticQuestionnaire);

        List<ComponentSimpleResponseType> responseTypes = components.stream().map(ComponentSimpleResponseType.class::cast).toList();
        List<MissingBlock> missingBlocks = lunaticQuestionnaire.getMissingBlock().getAny().stream()
                .map(MissingBlock.class::cast).toList();

        for(int cpt=0; cpt<components.size(); cpt++) {
            ComponentType component = components.get(cpt);
            ComponentSimpleResponseType simpleResponseType = responseTypes.get(cpt);
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(component.getMissingResponse().getName())
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(simpleResponseType.getResponse().getName())));
            assertTrue(missingBlocks.stream()
                    .anyMatch(missingBlock -> missingBlock.getMissingName().equals(simpleResponseType.getResponse().getName())
                            && missingBlock.getNames().size() == 1
                            && missingBlock.getNames().contains(component.getMissingResponse().getName())));
        }
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
