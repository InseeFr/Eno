package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticFilterResultTest {

    LunaticFilterResult processing;
    Questionnaire lunaticQuestionnaire;
    Input input;
    Table table;
    CollectedVariableType inputVariable;
    CollectedVariableType tableVariable1;
    CollectedVariableType tableVariable2;

    @BeforeEach
    void init() {
        EnoIndex enoIndex = new EnoIndex();

        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setId("input-id");
        ComponentFilter textQuestionFilter = new ComponentFilter();
        textQuestionFilter.getBindingReferences().add(new BindingReference("ref-1", "VAR1"));
        textQuestion.setComponentFilter(textQuestionFilter);
        enoIndex.put(textQuestion);

        TableQuestion tableQuestion = new TableQuestion();
        tableQuestion.setId("table-id");
        ComponentFilter tableQuestionFilter = new ComponentFilter();
        tableQuestionFilter.getBindingReferences().add(new BindingReference("ref-2-1", "VAR21"));
        tableQuestionFilter.getBindingReferences().add(new BindingReference("ref-2-2", "VAR22"));
        tableQuestion.setComponentFilter(tableQuestionFilter);
        enoIndex.put(tableQuestion);

        lunaticQuestionnaire = new Questionnaire();

        input = new Input();
        input.setId("input-id");
        input.setResponse(new ResponseType());
        input.getResponse().setName("INPUT_QUESTION");

        table = new Table();
        table.setId("table-id");
        table.getBodyLines().add(new BodyLine());
        BodyCell tableCell1 = new BodyCell();
        tableCell1.setResponse(new ResponseType());
        tableCell1.getResponse().setName("TABLE_QUESTION_1");
        BodyCell tableCell2 = new BodyCell();
        tableCell2.setResponse(new ResponseType());
        tableCell2.getResponse().setName("TABLE_QUESTION_2");
        table.getBodyLines().getFirst().getBodyCells().add(tableCell1);
        table.getBodyLines().getFirst().getBodyCells().add(tableCell2);

        ConditionFilterType inputConditionFilterType = new ConditionFilterType();
        inputConditionFilterType.setType(LabelTypeEnum.VTL);
        inputConditionFilterType.setValue("VAR1 < 10");
        input.setConditionFilter(inputConditionFilterType);

        ConditionFilterType tableConditionFilterType = new ConditionFilterType();
        tableConditionFilterType.setType(LabelTypeEnum.VTL);
        tableConditionFilterType.setValue("VAR21 + VAR22 < 100");
        table.setConditionFilter(tableConditionFilterType);

        inputVariable = new CollectedVariableType();
        inputVariable.setName("INPUT_QUESTION");

        tableVariable1 = new CollectedVariableType();
        tableVariable1.setName("TABLE_QUESTION_1");

        tableVariable2 = new CollectedVariableType();
        tableVariable2.setName("TABLE_QUESTION_2");

        processing = new LunaticFilterResult(enoIndex);
    }

    @Test
    void whenApplyingShouldNotProcessComponentsWithoutFilter() {
        input.setConditionFilter(null);
        table.setConditionFilter(null);
        lunaticQuestionnaire.getComponents().addAll(List.of(input, table));

        inputVariable.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(inputVariable);
        tableVariable1.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(tableVariable1);
        tableVariable2.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(tableVariable2);

        processing.apply(lunaticQuestionnaire);

        assertEquals(3, lunaticQuestionnaire.getVariables().size());
        lunaticQuestionnaire.getVariables().forEach(variable ->
                assertFalse(variable.getName().startsWith("FILTER_RESULT_")));
    }

    @Test
    void whenApplyingShouldProcessSimpleComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(input);

        inputVariable.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(inputVariable);

        processing.apply(lunaticQuestionnaire);

        assertEquals(2, lunaticQuestionnaire.getVariables().size());
        VariableType inputFilterResult = lunaticQuestionnaire.getVariables().get(1);
        testInputFilterResult(inputFilterResult);

        assertEquals(VariableDimension.SCALAR, inputFilterResult.getDimension());
        assertNull(inputFilterResult.getIterationReference());
    }

    @Test
    void whenApplyingShouldProcessComplexComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(table);

        tableVariable1.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(tableVariable1);
        tableVariable2.setDimension(VariableDimension.SCALAR);
        lunaticQuestionnaire.getVariables().add(tableVariable2);

        processing.apply(lunaticQuestionnaire);

        assertEquals(4, lunaticQuestionnaire.getVariables().size());
        VariableType tableFilterResult1 = lunaticQuestionnaire.getVariables().get(2);
        VariableType tableFilterResult2 = lunaticQuestionnaire.getVariables().get(3);
        testTableFilterResult(tableFilterResult1, "TABLE_QUESTION_1");
        testTableFilterResult(tableFilterResult2, "TABLE_QUESTION_2");

        assertEquals(VariableDimension.SCALAR, tableFilterResult1.getDimension());
        assertNull(tableFilterResult1.getIterationReference());
        assertEquals(VariableDimension.SCALAR, tableFilterResult2.getDimension());
        assertNull(tableFilterResult2.getIterationReference());
    }

    @Test
    void whenApplyingShouldProcessNestingComponents() {
        Loop loop = new Loop();
        loop.setId("loop-id");
        loop.getComponents().add(input);

        PairwiseLinks pairwise = new PairwiseLinks();
        pairwise.setId("pairwise-id");
        pairwise.getComponents().add(table);
        // A table in a pairwise should not happen in practice, but the logic is the same

        inputVariable.setDimension(VariableDimension.ARRAY);
        inputVariable.setIterationReference("loop-id");
        lunaticQuestionnaire.getVariables().add(inputVariable);
        tableVariable1.setDimension(VariableDimension.DOUBLE_ARRAY);
        tableVariable1.setIterationReference("pairwise-id");
        lunaticQuestionnaire.getVariables().add(tableVariable1);
        tableVariable2.setDimension(VariableDimension.DOUBLE_ARRAY);
        tableVariable2.setIterationReference("pairwise-id");
        lunaticQuestionnaire.getVariables().add(tableVariable2);

        lunaticQuestionnaire.getComponents().addAll(List.of(loop, pairwise));

        processing.apply(lunaticQuestionnaire);

        assertEquals(6, lunaticQuestionnaire.getVariables().size());
        VariableType inputFilterResult = lunaticQuestionnaire.getVariables().get(3);
        VariableType tableFilterResult1 = lunaticQuestionnaire.getVariables().get(4);
        VariableType tableFilterResult2 = lunaticQuestionnaire.getVariables().get(5);
        testInputFilterResult(inputFilterResult);
        testTableFilterResult(tableFilterResult1, "TABLE_QUESTION_1");
        testTableFilterResult(tableFilterResult2, "TABLE_QUESTION_2");

        assertEquals(VariableDimension.ARRAY, inputFilterResult.getDimension());
        assertEquals("loop-id", inputFilterResult.getIterationReference());
        assertEquals(VariableDimension.DOUBLE_ARRAY, tableFilterResult1.getDimension());
        assertEquals("pairwise-id", tableFilterResult1.getIterationReference());
        assertEquals(VariableDimension.DOUBLE_ARRAY, tableFilterResult2.getDimension());
        assertEquals("pairwise-id", tableFilterResult2.getIterationReference());
    }

    private void testInputFilterResult(VariableType variable) {
        assertEquals("FILTER_RESULT_INPUT_QUESTION", variable.getName());
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        CalculatedVariableType filterResultVariable = assertInstanceOf(CalculatedVariableType.class, variable);
        assertEquals(List.of("VAR1"), filterResultVariable.getBindingDependencies());
        assertEquals("VAR1 < 10", filterResultVariable.getExpression().getValue());
        assertTrue(filterResultVariable.getIsIgnoredByLunatic());
    }

    private void testTableFilterResult(VariableType variable, String responseName) {
        assertEquals("FILTER_RESULT_" + responseName, variable.getName());
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        CalculatedVariableType filterResultVariable = assertInstanceOf(CalculatedVariableType.class, variable);
        assertEquals(List.of("VAR21", "VAR22"), filterResultVariable.getBindingDependencies());
        assertEquals("VAR21 + VAR22 < 100", filterResultVariable.getExpression().getValue());
        assertTrue(filterResultVariable.getIsIgnoredByLunatic());
    }

}
