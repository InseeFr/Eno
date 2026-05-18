package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.BooleanQuestion;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.ExternalVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LunaticLoopResizingLogicTest {

    private Questionnaire lunaticQuestionnaire;
    private ResizingType lunaticResizing;
    private EnoQuestionnaire enoQuestionnaire;
    private EnoIndex enoIndex;
    private Loop lunaticLoop;
    private StandaloneLoop enoLoop;

    @BeforeEach
    void resizingUnitTestsCanvas() {
        //
        lunaticQuestionnaire = new Questionnaire();
        lunaticResizing = new ResizingType();
        //
        lunaticLoop = new Loop();
        lunaticLoop.setId("loop-id");
        lunaticLoop.setLines(new LinesLoop());
        lunaticLoop.getLines().setMin(new LabelType());
        lunaticLoop.getLines().setMax(new LabelType());
        lunaticLoop.getLines().getMin().setValue("nvl(LOOP_SIZE_VAR, 1)");
        lunaticLoop.getLines().getMax().setValue("nvl(LOOP_SIZE_VAR, 1)");
        //
        Sequence lunaticSequence = new Sequence();
        lunaticLoop.getComponents().add(lunaticSequence);
        //
        CheckboxBoolean simpleResponseComponent = new CheckboxBoolean();
        simpleResponseComponent.setResponse(new ResponseType());
        simpleResponseComponent.getResponse().setName("RESPONSE_VAR");
        lunaticLoop.getComponents().add(simpleResponseComponent);
        //
        VariableType numberVariable = new CollectedVariableType();
        numberVariable.setName("LOOP_SIZE_VAR");
        lunaticQuestionnaire.getVariables().add(numberVariable);
        //
        lunaticQuestionnaire.getComponents().add(lunaticLoop);

        //
        enoQuestionnaire = new EnoQuestionnaire();
        enoIndex = new EnoIndex();
        //
        fr.insee.eno.core.model.sequence.Sequence enoSequence = new fr.insee.eno.core.model.sequence.Sequence();
        enoSequence.setId("sequence-id");
        enoSequence.getSequenceStructure().add(
                StructureItemReference.builder()
                        .id("loop-question-id")
                        .type(StructureItemReference.StructureItemType.QUESTION)
                        .build());
        BooleanQuestion booleanQuestion = new BooleanQuestion();
        booleanQuestion.setId("loop-question-id");
        booleanQuestion.setResponse(new Response());
        booleanQuestion.getResponse().setVariableName("RESPONSE_VAR");
        enoIndex.put("sequence-id", enoSequence);
        enoIndex.put("loop-question-id", booleanQuestion);
        //
        enoLoop = new StandaloneLoop();
        enoLoop.setId("loop-id");
        enoLoop.setLoopIterations(new StandaloneLoop.LoopIterations());
        enoLoop.getLoopIterations().setMaxIteration(new CalculatedExpression());
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("loop-size-var-ref", "LOOP_SIZE_VAR"));
        enoLoop.getLoopScope().add(StructureItemReference.builder()
                .id("sequence-id").type(StructureItemReference.StructureItemType.SEQUENCE).build());
        enoQuestionnaire.getLoops().add(enoLoop);
        //
        enoIndex.put("loop-id", enoLoop);
    }

    @Test
    void oneComponentInLoop_testResizingEntryNameAndSize() {
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertEquals(1, lunaticResizing.countResizingEntries());
        assertEquals("nvl(LOOP_SIZE_VAR, 1)", lunaticResizing.getResizingEntry("LOOP_SIZE_VAR").getSize());
        assertEquals(List.of("RESPONSE_VAR"), lunaticResizing.getResizingEntry("LOOP_SIZE_VAR").getVariables());
    }

    @Test
    void simpleResponseComponents_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        CheckboxBoolean checkboxBoolean = new CheckboxBoolean();
        checkboxBoolean.setResponse(new ResponseType());
        checkboxBoolean.getResponse().setName("BOOLEAN_VAR");
        lunaticLoop.getComponents().add(checkboxBoolean);
        Input input = new Input();
        input.setResponse(new ResponseType());
        input.getResponse().setName("INPUT_VAR");
        lunaticLoop.getComponents().add(input);
        Suggester suggester = new Suggester(); // The suggester component type is set by the constructor
        suggester.setResponse(new ResponseType());
        suggester.getResponse().setName("SUGGESTER_VAR");
        lunaticLoop.getComponents().add(suggester);
        Textarea textarea = new Textarea();
        textarea.setResponse(new ResponseType());
        textarea.getResponse().setName("TEXT_VAR");
        lunaticLoop.getComponents().add(textarea);
        InputNumber inputNumber = new InputNumber();
        inputNumber.setResponse(new ResponseType());
        inputNumber.getResponse().setName("NUMBER_VAR");
        lunaticLoop.getComponents().add(inputNumber);
        Datepicker datepicker = new Datepicker();
        datepicker.setResponse(new ResponseType());
        datepicker.getResponse().setName("DATE_VAR");
        lunaticLoop.getComponents().add(datepicker);
        Duration duration = new Duration();
        duration.setResponse(new ResponseType());
        duration.getResponse().setName("DURATION_VAR");
        lunaticLoop.getComponents().add(duration);
        Dropdown dropdown = new Dropdown();
        dropdown.setResponse(new ResponseType());
        dropdown.getResponse().setName("DROPDOWN_VAR");
        lunaticLoop.getComponents().add(dropdown);
        Radio radio = new Radio();
        radio.setResponse(new ResponseType());
        radio.getResponse().setName("RADIO_VAR");
        lunaticLoop.getComponents().add(radio);
        CheckboxOne checkboxOne = new CheckboxOne();
        checkboxOne.setResponse(new ResponseType());
        checkboxOne.getResponse().setName("CHECKBOX_VAR");
        lunaticLoop.getComponents().add(checkboxOne);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertThat(lunaticResizing.getResizingEntry("LOOP_SIZE_VAR").getVariables())
                .containsExactlyInAnyOrderElementsOf(Set.of(
                        "BOOLEAN_VAR", "INPUT_VAR", "SUGGESTER_VAR", "TEXT_VAR", "NUMBER_VAR",
                        "DATE_VAR", "DURATION_VAR", "DROPDOWN_VAR", "RADIO_VAR", "CHECKBOX_VAR"));
    }

    @Test
    void checkboxGroupComponent_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        checkboxGroup.getResponses().add(new ResponseCheckboxGroup());
        checkboxGroup.getResponses().add(new ResponseCheckboxGroup());
        checkboxGroup.getResponses().forEach(responses -> responses.setResponse(new ResponseType()));
        checkboxGroup.getResponses().get(0).getResponse().setName("RESPONSE_VAR1");
        checkboxGroup.getResponses().get(1).getResponse().setName("RESPONSE_VAR2");
        lunaticLoop.getComponents().add(checkboxGroup);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertThat(lunaticResizing.getResizingEntry("LOOP_SIZE_VAR").getVariables())
                .containsExactlyInAnyOrderElementsOf(List.of("RESPONSE_VAR1", "RESPONSE_VAR2"));
    }

    @Test
    void tableComponent_resizedVariables() {
        //
        lunaticLoop.getComponents().clear();
        //
        Table table = new Table();
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
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertThat(lunaticResizing.getResizingEntry("LOOP_SIZE_VAR").getVariables())
                .containsExactlyInAnyOrderElementsOf(Set.of("CELL11", "CELL12", "CELL21", "CELL22"));
    }

    @Test
    void twoVariablesInLoopExpression_twoResizingEntries() {
        // Adding a second variable in max size expression
        lunaticLoop.getLines().getMin().setValue("LOOP_SIZE_VAR + LOOP_SIZE_VAR2");
        //
        VariableType numberVariable = new CollectedVariableType();
        numberVariable.setName("LOOP_SIZE_VAR2");
        lunaticQuestionnaire.getVariables().add(numberVariable);

        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("loop-size-var2-ref", "LOOP_SIZE_VAR2"));

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void loopFixedMaxSize_noResizingEntries() {
        //
        lunaticLoop.getLines().getMax().setValue("5");
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().clear();
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);
        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void loopFixedMaxSize_withExternal_noResizingEntries() {
        //
        lunaticLoop.getLines().getMax().setValue("count(EXTERNAL_VAR)");
        //
        VariableType externalVariable = new ExternalVariableType();
        externalVariable.setName("EXTERNAL_VAR");
        lunaticQuestionnaire.getVariables().add(externalVariable);
        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().clear();
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("external-ref", "EXTERNAL_VAR"));
        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);
        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void externalVariableInLoopExpression_shouldHaveResizingEntriesForCollectedDependencies() {
        // Adding an external variable
        lunaticLoop.getLines().getMin().setValue("CALCULATED_VAR");
        //
        lunaticQuestionnaire.getVariables().clear();
        VariableType calculatedVariable = new CalculatedVariableType();
        calculatedVariable.setName("CALCULATED_VAR");
        lunaticQuestionnaire.getVariables().add(calculatedVariable);
        VariableType collectedVariable = new CollectedVariableType();
        collectedVariable.setName("COLLECTED_VAR");
        lunaticQuestionnaire.getVariables().add(collectedVariable);
        VariableType externalVariable = new ExternalVariableType();
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
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void calculatedVariableInLoopExpression_shouldNotAddResizingEntries() {
        // Adding an external variable
        lunaticLoop.getLines().getMin().setValue("LOOP_SIZE_VAR + EXTERNAL_VAR");
        //
        VariableType externalVariable = new ExternalVariableType();
        externalVariable.setName("EXTERNAL_VAR");
        lunaticQuestionnaire.getVariables().add(externalVariable);

        //
        enoLoop.getLoopIterations().getMaxIteration().getBindingReferences().add(
                new BindingReference("external-ref", "EXTERNAL_VAR"));

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLoop, lunaticResizing);

        // Then
        assertEquals(0, lunaticResizing.countResizingEntries());
    }

    @Test
    void resizing_linkedLoop() {
        //
        Loop lunaticLinkedLoop = new Loop();
        lunaticLinkedLoop.setId("linked-loop-id");
        lunaticLinkedLoop.setIterations(new LabelType());
        lunaticLinkedLoop.getIterations().setValue("count(RESPONSE_VAR)");
        //
        Input input = new Input();
        input.setResponse(new ResponseType());
        input.getResponse().setName("LINKED_LOOP_RESPONSE");
        lunaticLinkedLoop.getComponents().add(input);
        //
        lunaticQuestionnaire.getComponents().add(lunaticLinkedLoop);
        //
        LinkedLoop enoLinkedLoop = new LinkedLoop();
        enoLinkedLoop.setId("linked-loop-id");
        enoLinkedLoop.setReference("loop-id");
        enoQuestionnaire.getLoops().add(enoLinkedLoop);
        //
        enoIndex.put("linked-loop-id", enoLinkedLoop);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLinkedLoop, lunaticResizing);

        // Then
        ResizingEntry linkedLoopResizingEntry = lunaticResizing.getResizingEntry("RESPONSE_VAR");
        assertEquals("count(RESPONSE_VAR)", linkedLoopResizingEntry.getSize());
        assertEquals(1, linkedLoopResizingEntry.getVariables().size());
        assertTrue(linkedLoopResizingEntry.getVariables().contains("LINKED_LOOP_RESPONSE"));
    }

    @Test
    void resizing_linkedLoop_basedOnDynamicTable() {
        //
        RosterForLoop rosterForLoop = new RosterForLoop();
        Loop lunaticLinkedLoop = new Loop();
        lunaticLinkedLoop.setId("linked-loop-id");
        lunaticLinkedLoop.setIterations(new LabelType());
        lunaticLinkedLoop.getIterations().setValue("count(TABLE_RESPONSE1)");
        //
        Input input = new Input();
        input.setResponse(new ResponseType());
        input.getResponse().setName("LINKED_LOOP_RESPONSE");
        lunaticLinkedLoop.getComponents().add(input);
        //
        lunaticQuestionnaire.getComponents().add(lunaticLinkedLoop);
        lunaticQuestionnaire.getComponents().add(rosterForLoop);

        //
        DynamicTableQuestion dynamicTableQuestion = new DynamicTableQuestion();
        dynamicTableQuestion.setId("dynamic-table-id");
        dynamicTableQuestion.getVariableNames().add("TABLE_RESPONSE1");
        enoQuestionnaire.getMultipleResponseQuestions().add(dynamicTableQuestion);
        //
        LinkedLoop enoLinkedLoop = new LinkedLoop();
        enoLinkedLoop.setId("linked-loop-id");
        enoLinkedLoop.setReference("dynamic-table-id");
        enoQuestionnaire.getLoops().add(enoLinkedLoop);
        //
        enoIndex.put("linked-loop-id", enoLinkedLoop);

        // When
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        loopResizingLogic.buildResizingEntries(lunaticLinkedLoop, lunaticResizing);

        // Then
        ResizingEntry linkedLoopResizingEntry = lunaticResizing.getResizingEntry("TABLE_RESPONSE1");
        assertEquals("count(TABLE_RESPONSE1)", linkedLoopResizingEntry.getSize());
        assertEquals(1, linkedLoopResizingEntry.getVariables().size());
        assertTrue(linkedLoopResizingEntry.getVariables().contains("LINKED_LOOP_RESPONSE"));
    }

}
