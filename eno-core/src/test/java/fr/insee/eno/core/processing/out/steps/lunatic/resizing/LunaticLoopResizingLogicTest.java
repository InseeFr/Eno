package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticLoopResizingLogicTest {

    private Questionnaire lunaticQuestionnaire;
    private EnoQuestionnaire enoQuestionnaire;
    private EnoIndex enoIndex;
    private Loop lunaticLoop;
    private StandaloneLoop enoLoop;

    @BeforeEach
    void resizingUnitTestsCanvas() {
        //
        lunaticQuestionnaire = new Questionnaire();
        //
        lunaticLoop = new Loop();
        lunaticLoop.setId("loop-id");
        lunaticLoop.setLines(new LinesLoop());
        lunaticLoop.getLines().setMax(new LabelType());
        lunaticLoop.getLines().getMax().setValue("nvl(LOOP_SIZE_VAR, 1)");
        //
        CheckboxBoolean simpleResponseComponent = new CheckboxBoolean();
        simpleResponseComponent.setComponentType(ComponentTypeEnum.CHECKBOX_BOOLEAN);
        simpleResponseComponent.setResponse(new ResponseType());
        simpleResponseComponent.getResponse().setName("RESPONSE_VAR");
        lunaticLoop.getComponents().add(simpleResponseComponent);
        //
        VariableType numberVariable = new VariableType();
        numberVariable.setVariableType(VariableTypeEnum.COLLECTED);
        numberVariable.setName("LOOP_SIZE_VAR");
        lunaticQuestionnaire.getVariables().add(numberVariable);
        //
        lunaticQuestionnaire.getComponents().add(lunaticLoop);

        //
        enoQuestionnaire = new EnoQuestionnaire();
        enoLoop = new StandaloneLoop();
        enoLoop.setId("loop-id");
        enoLoop.setLoopIterations(new StandaloneLoop.LoopIterations());
        enoLoop.getLoopIterations().setMaxIteration(new CalculatedExpression());
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("loop-size-var-ref", "LOOP_SIZE_VAR"));

        //
        enoIndex = new EnoIndex();
        enoIndex.put("loop-id", enoLoop);
    }

    @Test
    void oneComponentInLoop_testResizingEntryNameAndSize() {
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertEquals(1, resizingEntries.size());
        assertEquals("LOOP_SIZE_VAR", resizingEntries.get(0).getName());
        assertEquals("nvl(LOOP_SIZE_VAR, 1)", resizingEntries.get(0).getSize());
        assertEquals(Set.of("RESPONSE_VAR"), resizingEntries.get(0).getVariables());
    }

    @Test
    void simpleResponseComponents_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        CheckboxBoolean checkboxBoolean = new CheckboxBoolean();
        checkboxBoolean.setComponentType(ComponentTypeEnum.CHECKBOX_BOOLEAN);
        checkboxBoolean.setResponse(new ResponseType());
        checkboxBoolean.getResponse().setName("BOOLEAN_VAR");
        lunaticLoop.getComponents().add(checkboxBoolean);
        Input input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setResponse(new ResponseType());
        input.getResponse().setName("INPUT_VAR");
        lunaticLoop.getComponents().add(input);
        Textarea textarea = new Textarea();
        textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
        textarea.setResponse(new ResponseType());
        textarea.getResponse().setName("TEXT_VAR");
        lunaticLoop.getComponents().add(textarea);
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        inputNumber.setResponse(new ResponseType());
        inputNumber.getResponse().setName("NUMBER_VAR");
        lunaticLoop.getComponents().add(inputNumber);
        Datepicker datepicker = new Datepicker();
        datepicker.setComponentType(ComponentTypeEnum.DATEPICKER);
        datepicker.setResponse(new ResponseType());
        datepicker.getResponse().setName("DATE_VAR");
        lunaticLoop.getComponents().add(datepicker);
        Dropdown dropdown = new Dropdown();
        dropdown.setComponentType(ComponentTypeEnum.DROPDOWN);
        dropdown.setResponse(new ResponseType());
        dropdown.getResponse().setName("DROPDOWN_VAR");
        lunaticLoop.getComponents().add(dropdown);
        Radio radio = new Radio();
        radio.setComponentType(ComponentTypeEnum.RADIO);
        radio.setResponse(new ResponseType());
        radio.getResponse().setName("RADIO_VAR");
        lunaticLoop.getComponents().add(radio);
        CheckboxOne checkboxOne = new CheckboxOne();
        checkboxOne.setComponentType(ComponentTypeEnum.CHECKBOX_ONE);
        checkboxOne.setResponse(new ResponseType());
        checkboxOne.getResponse().setName("CHECKBOX_VAR");
        lunaticLoop.getComponents().add(checkboxOne);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertThat(resizingEntries.get(0).getVariables()).containsExactlyInAnyOrderElementsOf(
                Set.of("BOOLEAN_VAR", "INPUT_VAR", "TEXT_VAR", "NUMBER_VAR", "DATE_VAR",
                        "DROPDOWN_VAR", "RADIO_VAR", "CHECKBOX_VAR"));
    }

    @Test
    void checkboxGroupComponent_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        checkboxGroup.setComponentType(ComponentTypeEnum.CHECKBOX_GROUP);
        checkboxGroup.getResponses().add(new ResponsesCheckboxGroup());
        checkboxGroup.getResponses().add(new ResponsesCheckboxGroup());
        checkboxGroup.getResponses().forEach(responses -> responses.setResponse(new ResponseType()));
        checkboxGroup.getResponses().get(0).getResponse().setName("RESPONSE_VAR1");
        checkboxGroup.getResponses().get(1).getResponse().setName("RESPONSE_VAR2");
        lunaticLoop.getComponents().add(checkboxGroup);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertThat(resizingEntries.get(0).getVariables()).containsExactlyInAnyOrderElementsOf(
                List.of("RESPONSE_VAR1", "RESPONSE_VAR2"));
    }

    @Test
    void tableComponent_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        Table table = new Table();
        table.setComponentType(ComponentTypeEnum.TABLE);
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().add(new BodyLine());
        table.getBodyLines().get(0).getBodyCells().add(new BodyCell());
        table.getBodyLines().get(0).getBodyCells().add(new BodyCell());
        table.getBodyLines().get(1).getBodyCells().add(new BodyCell());
        table.getBodyLines().get(1).getBodyCells().add(new BodyCell());
        table.getBodyLines().get(0).getBodyCells().forEach(bodyCell -> bodyCell.setResponse(new ResponseType()));
        table.getBodyLines().get(1).getBodyCells().forEach(bodyCell -> bodyCell.setResponse(new ResponseType()));
        table.getBodyLines().get(0).getBodyCells().get(0).getResponse().setName("CELL11");
        table.getBodyLines().get(0).getBodyCells().get(1).getResponse().setName("CELL12");
        table.getBodyLines().get(1).getBodyCells().get(0).getResponse().setName("CELL21");
        table.getBodyLines().get(1).getBodyCells().get(1).getResponse().setName("CELL22");
        lunaticLoop.getComponents().add(table);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertThat(resizingEntries.get(0).getVariables()).containsExactlyInAnyOrderElementsOf(
                Set.of("CELL11", "CELL12", "CELL21", "CELL22"));
    }

    @Test
    void twoVariablesInLoopExpression_twoResizingEntries() {
        // Adding a second variable in max size expression
        lunaticLoop.getLines().getMax().setValue("LOOP_SIZE_VAR + LOOP_SIZE_VAR2");
        //
        VariableType numberVariable = new VariableType();
        numberVariable.setVariableType(VariableTypeEnum.COLLECTED);
        numberVariable.setName("LOOP_SIZE_VAR2");
        lunaticQuestionnaire.getVariables().add(numberVariable);

        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("loop-size-var2-ref", "LOOP_SIZE_VAR2"));

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertEquals(2, resizingEntries.size());
        //
        assertThat(resizingEntries.stream().map(LunaticResizingEntry::getName).toList())
                .containsExactlyInAnyOrderElementsOf(List.of("LOOP_SIZE_VAR", "LOOP_SIZE_VAR2"));
        //
        resizingEntries.forEach(resizingEntry -> {
            assertEquals("LOOP_SIZE_VAR + LOOP_SIZE_VAR2", resizingEntry.getSize());
            assertEquals(Set.of("RESPONSE_VAR"), resizingEntry.getVariables());
        });
    }

    @Test
    void loopFixedMaxSize_noResizingEntries() {
        //
        lunaticLoop.getLines().getMax().setValue("5");
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().clear();
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);
        // Then
        assertTrue(resizingEntries.isEmpty());
    }

    @Test
    void loopFixedMaxSize_withExternal_noResizingEntries() {
        //
        lunaticLoop.getLines().getMax().setValue("count(EXTERNAL_VAR)");
        //
        VariableType externalVariable = new VariableType();
        externalVariable.setVariableType(VariableTypeEnum.EXTERNAL);
        externalVariable.setName("EXTERNAL_VAR");
        lunaticQuestionnaire.getVariables().add(externalVariable);
        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().clear();
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("external-ref", "EXTERNAL_VAR"));
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);
        // Then
        assertTrue(resizingEntries.isEmpty());
    }

    @Test
    void externalVariableInLoopExpression_shouldHaveResizingEntriesForCollectedDependencies() {
        // Adding an external variable
        lunaticLoop.getLines().getMax().setValue("CALCULATED_VAR");
        //
        lunaticQuestionnaire.getVariables().clear();
        VariableType calculatedVariable = new VariableType();
        calculatedVariable.setVariableType(VariableTypeEnum.CALCULATED);
        calculatedVariable.setName("CALCULATED_VAR");
        lunaticQuestionnaire.getVariables().add(calculatedVariable);
        VariableType collectedVariable = new VariableType();
        collectedVariable.setVariableType(VariableTypeEnum.COLLECTED);
        collectedVariable.setName("COLLECTED_VAR");
        lunaticQuestionnaire.getVariables().add(collectedVariable);
        VariableType externalVariable = new VariableType();
        externalVariable.setVariableType(VariableTypeEnum.EXTERNAL);
        externalVariable.setName("EXTERNAL_VAR");
        lunaticQuestionnaire.getVariables().add(externalVariable);

        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().clear();
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("collected-ref", "COLLECTED_VAR"));
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("external-ref", "EXTERNAL_VAR"));

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertEquals(1, resizingEntries.size());
        assertEquals("COLLECTED_VAR", resizingEntries.get(0).getName());
        assertEquals("CALCULATED_VAR", resizingEntries.get(0).getSize());
        assertEquals(Set.of("RESPONSE_VAR"), resizingEntries.get(0).getVariables());
    }

    @Test
    void calculatedVariableInLoopExpression_shouldNotAddResizingEntries() {
        // Adding an external variable
        lunaticLoop.getLines().getMax().setValue("LOOP_SIZE_VAR + EXTERNAL_VAR");
        //
        VariableType externalVariable = new VariableType();
        externalVariable.setVariableType(VariableTypeEnum.EXTERNAL);
        externalVariable.setName("EXTERNAL_VAR");
        lunaticQuestionnaire.getVariables().add(externalVariable);

        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("external-ref", "EXTERNAL_VAR"));

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        List<LunaticResizingEntry> resizingEntries = loopResizingLogic.buildResizingEntries(lunaticLoop);

        // Then
        assertEquals(1, resizingEntries.size());
        assertEquals("LOOP_SIZE_VAR", resizingEntries.get(0).getName());
        assertEquals("LOOP_SIZE_VAR + EXTERNAL_VAR", resizingEntries.get(0).getSize());
        assertEquals(Set.of("RESPONSE_VAR"), resizingEntries.get(0).getVariables());
    }

}
