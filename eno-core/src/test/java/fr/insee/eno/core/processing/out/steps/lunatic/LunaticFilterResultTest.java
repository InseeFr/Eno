package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrievalReturnVariableNameInVariable;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticFilterResultTest {

    LunaticFilterResult processing;
    Questionnaire lunaticQuestionnaire;
    EnoQuestionnaire enoQuestionnaire;
    EnoIndex enoIndex;
    Input input;
    TextQuestion textQuestion;
    Table table;
    TableQuestion tableQuestion;
    VariableGroup variableGroup;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        enoIndex = new EnoIndex();
        textQuestion = new TextQuestion();
        textQuestion.setName("input-name");
        textQuestion.setId("lxcsdfg");
        ComponentFilter textQuestionFilter = new ComponentFilter();
        textQuestionFilter.setValue("count(CALCULATED_VARIABLE) < 2");
        textQuestionFilter.setType(LabelTypeEnum.VTL.value());
        BindingReference textFilterBindingRef = new BindingReference("idref", "CALCULATED_VARIABLE");
        textQuestionFilter.getBindingReferences().add(textFilterBindingRef);
        textQuestion.setComponentFilter(textQuestionFilter);
        enoIndex.put(textQuestion.getId(), textQuestion);

        tableQuestion = new TableQuestion();
        tableQuestion.setName("table-name");
        tableQuestion.setId("xdsfgh54");
        BindingReference tableFilterBindingRef1 = new BindingReference("idreft1", "CALCULATED_VARIABLE_2");
        BindingReference tableFilterBindingRef2 = new BindingReference("idreft2", "NUMERIC_USED_FOR_CALCULATED_VARIABLE_2");
        ComponentFilter tableQuestionFilter = new ComponentFilter();
        tableQuestionFilter.getBindingReferences().add(tableFilterBindingRef1);
        tableQuestionFilter.getBindingReferences().add(tableFilterBindingRef2);
        tableQuestion.setComponentFilter(tableQuestionFilter);
        enoIndex.put(tableQuestion.getId(), tableQuestion);
        enoQuestionnaire.setIndex(enoIndex);

        lunaticQuestionnaire = new Questionnaire();

        input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);
        input.setId(textQuestion.getId());

        table = new Table();
        table.setComponentType(ComponentTypeEnum.TABLE);
        table.setId(tableQuestion.getId());

        ConditionFilterType inputConditionFilterType = new ConditionFilterType();
        inputConditionFilterType.setType(LabelTypeEnum.VTL);
        inputConditionFilterType.setValue(textQuestionFilter.getValue());
        input.setConditionFilter(inputConditionFilterType);

        ConditionFilterType tableConditionFilterType = new ConditionFilterType();
        tableConditionFilterType.setType(LabelTypeEnum.VTL);
        tableConditionFilterType.setValue("count(CALCULATED_VARIABLE_2) + NUMERIC_USED_FOR_CALCULATED_VARIABLE_2 < 4");
        table.setConditionFilter(tableConditionFilterType);
/*
        VariableGroup variableGroup = new VariableGroup();
        Variable inputVariable = new CalculatedVariable();
        inputVariable.setId(textQuestion.getId());
        variableGroup.getVariables().add(inputVariable);
*/
        processing = new LunaticFilterResult(enoQuestionnaire, new ShapefromAttributeRetrievalReturnVariableNameInVariable());
    }

    @Test
    void whenApplyingShouldNotProcessComponentsWithoutFilter() {
        input.setConditionFilter(null);
        table.setConditionFilter(null);
        lunaticQuestionnaire.getComponents().addAll(List.of(input, table));

        processing.apply(lunaticQuestionnaire);
        assertEquals(0, lunaticQuestionnaire.getVariables().size());
    }

    @Test
    void whenApplyingShouldProcessSimpleComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        VariableType variable = lunaticQuestionnaire.getVariables().getFirst();
        testInputCalculatedVariable(variable);
    }

    @Test
    void whenApplyingShouldProcessComplexComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(table);

        enoQuestionnaire.getVariableGroups().add(variableGroup);

        processing.apply(lunaticQuestionnaire);
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        VariableType variable = lunaticQuestionnaire.getVariables().getFirst();
        testTableCalculatedVariable(variable);
    }

    @Test
    void whenApplyingShouldProcessNestingComponents() {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        loop.getComponents().add(input);

        PairwiseLinks pairwise = new PairwiseLinks();
        pairwise.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        pairwise.getComponents().add(table);

        lunaticQuestionnaire.getComponents().addAll(List.of(loop, pairwise));

        processing.apply(lunaticQuestionnaire);
        assertEquals(2, lunaticQuestionnaire.getVariables().size());
        testInputCalculatedVariable(lunaticQuestionnaire.getVariables().get(0));
        testTableCalculatedVariable(lunaticQuestionnaire.getVariables().get(1));
    }

    private void testInputCalculatedVariable(VariableType variable) {
        assertEquals("FILTER_RESULT_" + textQuestion.getName(), variable.getName());
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        CalculatedVariableType calculatedVariable = assertInstanceOf(CalculatedVariableType.class, variable);
        assertEquals(1, calculatedVariable.getBindingDependencies().size());
        assertTrue(calculatedVariable.getBindingDependencies().contains("CALCULATED_VARIABLE"));
        assertEquals("input-name", calculatedVariable.getShapeFrom());
        assertEquals("count(CALCULATED_VARIABLE) < 2", calculatedVariable.getExpression().getValue());
    }

    private void testTableCalculatedVariable(VariableType variable) {
        assertEquals("FILTER_RESULT_" + tableQuestion.getName(), variable.getName());
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        CalculatedVariableType calculatedVariable = assertInstanceOf(CalculatedVariableType.class, variable);
        assertEquals(2, calculatedVariable.getBindingDependencies().size());
        assertTrue(calculatedVariable.getBindingDependencies().contains("CALCULATED_VARIABLE_2"));
        assertTrue(calculatedVariable.getBindingDependencies().contains("NUMERIC_USED_FOR_CALCULATED_VARIABLE_2"));
        assertEquals("table-name", calculatedVariable.getShapeFrom());
        assertEquals("count(CALCULATED_VARIABLE_2) + NUMERIC_USED_FOR_CALCULATED_VARIABLE_2 < 4", calculatedVariable.getExpression().getValue());
    }
}
