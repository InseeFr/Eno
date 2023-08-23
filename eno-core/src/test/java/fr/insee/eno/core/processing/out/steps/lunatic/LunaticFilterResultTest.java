package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticFilterResultTest {

    LunaticFilterResult processing;
    Questionnaire lunaticQuestionnaire;
    EnoQuestionnaire enoQuestionnaire;
    EnoIndex enoIndex;
    Input input;
    TextQuestion textQuestion;
    Table table;
    TableQuestion tableQuestion;

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        enoIndex = new EnoIndex();
        textQuestion = new TextQuestion();
        textQuestion.setName("input-name");
        textQuestion.setId("lxcsdfg");
        enoIndex.put(textQuestion.getId(), textQuestion);

        tableQuestion = new TableQuestion();
        tableQuestion.setName("table-name");
        tableQuestion.setId("xdsfgh54");
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
        inputConditionFilterType.setType(Constant.LUNATIC_LABEL_VTL);
        inputConditionFilterType.setValue("count(CALCULATED_VARIABLE) < 2");
        inputConditionFilterType.getBindingDependencies().addAll(List.of("CALCULATED_VARIABLE", "NUMERIC1_USED_FOR_CALCULATED_VARIABLE", "NUMERIC2_USED_FOR_CALCULATED_VARIABLE"));
        input.setConditionFilter(inputConditionFilterType);

        ConditionFilterType tableConditionFilterType = new ConditionFilterType();
        tableConditionFilterType.setType(Constant.LUNATIC_LABEL_VTL);
        tableConditionFilterType.setValue("count(CALCULATED_VARIABLE_2) < 4");
        tableConditionFilterType.getBindingDependencies().addAll(List.of("CALCULATED_VARIABLE_2", "NUMERIC_USED_FOR_CALCULATED_VARIABLE_2"));
        table.setConditionFilter(tableConditionFilterType);

        processing = new LunaticFilterResult(enoQuestionnaire);
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
        VariableType variable = (VariableType) lunaticQuestionnaire.getVariables().get(0);
        testInputCalculatedVariable(variable);
    }

    @Test
    void whenApplyingShouldProcessComplexComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(table);

        processing.apply(lunaticQuestionnaire);
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        VariableType variable = (VariableType) lunaticQuestionnaire.getVariables().get(0);
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
        testInputCalculatedVariable((VariableType) lunaticQuestionnaire.getVariables().get(0));
        testTableCalculatedVariable((VariableType) lunaticQuestionnaire.getVariables().get(1));
    }

    private void testInputCalculatedVariable(VariableType variable) {
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        assertEquals("FILTER_RESULT_" + textQuestion.getName(), variable.getName());
        assertEquals(3, variable.getBindingDependencies().size());
        assertEquals("CALCULATED_VARIABLE", variable.getBindingDependencies().get(0));
        assertEquals("NUMERIC1_USED_FOR_CALCULATED_VARIABLE", variable.getBindingDependencies().get(1));
        assertEquals("NUMERIC2_USED_FOR_CALCULATED_VARIABLE", variable.getBindingDependencies().get(2));
        assertEquals("count(CALCULATED_VARIABLE) < 2", variable.getExpression().getValue());
    }

    private void testTableCalculatedVariable(VariableType variable) {
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        assertEquals("FILTER_RESULT_" + tableQuestion.getName(), variable.getName());
        assertEquals(2, variable.getBindingDependencies().size());
        assertEquals("CALCULATED_VARIABLE_2", variable.getBindingDependencies().get(0));
        assertEquals("NUMERIC_USED_FOR_CALCULATED_VARIABLE_2", variable.getBindingDependencies().get(1));
        assertEquals("count(CALCULATED_VARIABLE_2) < 4", variable.getExpression().getValue());
    }
}
