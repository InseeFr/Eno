package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapefromAttributeRetrievalFromVariableGroupsTest {

    ShapefromAttributeRetrievalFromVariableGroups shapefromAttributeRetrieval;

    EnoQuestionnaire enoQuestionnaire;

    VariableGroup variableGroup1, variableGroup2, variableGroup3;

    Variable variable1, variable2, variable3, variable4, variable5;

    @BeforeEach
    void init() {
        shapefromAttributeRetrieval = new ShapefromAttributeRetrievalFromVariableGroups();
        enoQuestionnaire = new EnoQuestionnaire();
        EnoIndex enoIndex = new EnoIndex();

        StandaloneLoop loop = new StandaloneLoop();
        loop.setId("loop-id");
        loop.getLoopScope().add(StructureItemReference.builder()
                .id("sequence-id")
                .type(StructureItemReference.StructureItemType.SEQUENCE)
                .build());
        Sequence sequence = new Sequence();
        sequence.setId("sequence-id");
        sequence.getSequenceStructure().add(StructureItemReference.builder()
                .id("question-id")
                .type(StructureItemReference.StructureItemType.QUESTION)
                .build());
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setId("question-id");
        textQuestion.setResponse(new Response());
        textQuestion.getResponse().setVariableName("variable2");

        enoIndex.put("sequence-id", sequence);
        enoIndex.put("question-id", textQuestion);
        enoQuestionnaire.setIndex(enoIndex);
        enoQuestionnaire.getLoops().add(loop);

        variableGroup1 = new VariableGroup();
        variableGroup1.setType("Loop");
        variableGroup1.getLoopReferences().addAll(List.of("loop-id", "linked-loop-id"));
        // (the linked loop reference should be ignored as only the "main" loop counts for retrieving the 'shapeFrom')
        variable1 = new CalculatedVariable();
        variable1.setName("variable1");
        variable2 = new CollectedVariable();
        variable2.setName("variable2");
        variableGroup1.getVariables().addAll(List.of(variable1, variable2));

        variableGroup2 = new VariableGroup();
        variableGroup2.setType("Questionnaire");
        variable3 = new CollectedVariable();
        variable3.setName("variable3");
        variableGroup2.getVariables().add(variable3);

        DynamicTableQuestion dynamicTableQuestion = new DynamicTableQuestion();
        dynamicTableQuestion.setId("dynamic-table-id");
        dynamicTableQuestion.getVariableNames().add("variable4");
        enoQuestionnaire.getMultipleResponseQuestions().add(dynamicTableQuestion);

        variableGroup3 = new VariableGroup();
        variableGroup3.setType("Loop");
        variableGroup3.getLoopReferences().add("dynamic-table-id");
        variable4 = new CollectedVariable();
        variable4.setName("variable4");
        variable5 = new CalculatedVariable();
        variable5.setName("variable5");
        variableGroup3.getVariables().addAll(List.of(variable4, variable5));

        enoQuestionnaire.getVariables().addAll(List.of(variable1, variable2, variable3, variable4, variable5));
        enoQuestionnaire.getVariableGroups().addAll(List.of(variableGroup1, variableGroup2, variableGroup3));
    }

    @Test
    void whenVariableFromQuestionnaireVariableGroupReturnEmpty() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable3", enoQuestionnaire);
        assertTrue(variable.isEmpty());
    }

    @Test
    void whenVariableNotExistingReturnEmpty() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable59", enoQuestionnaire);
        assertTrue(variable.isEmpty());
    }

    @Test
    void whenVariableInLoopVariableGroup_returnFirstCollectedVariable() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable1", enoQuestionnaire);
        assertTrue(variable.isPresent());
        assertEquals("variable2", variable.get().getName());
    }

    @Test
    void whenVariableInDynamicTableVariableGroup_returnFirstCollectedVariable() {
        Optional<Variable> variable = shapefromAttributeRetrieval.getShapeFrom("variable5", enoQuestionnaire);
        assertTrue(variable.isPresent());
        assertEquals("variable4", variable.get().getName());
    }

}
