package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.model.EnoQuestionnaire;
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

    @BeforeEach
    void init() {
        enoQuestionnaire = new EnoQuestionnaire();
        enoIndex = new EnoIndex();

        enoQuestionnaire.setIndex(enoIndex);
        lunaticQuestionnaire = new Questionnaire();

        input = new Input();
        input.setComponentType(ComponentTypeEnum.INPUT);

        ConditionFilterType conditionFilterType = new ConditionFilterType();
        conditionFilterType.setType(Constant.LUNATIC_LABEL_VTL);
        conditionFilterType.setValue("count(CALCULATED_VARIABLE) < 2");
        conditionFilterType.getBindingDependencies().addAll(List.of("CALCULATED_VARIABLE", "NUMERIC1_USED_FOR_CALCULATED_VARIABLE", "NUMERIC2_USED_FOR_CALCULATED_VARIABLE"));
        input.setConditionFilter(conditionFilterType);

        processing = new LunaticFilterResult(enoQuestionnaire);
    }

    @Test
    void whenApplyingShouldNotProcessComponentsWithoutFilter() {
        input.setConditionFilter(null);
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);
        assertEquals(0, lunaticQuestionnaire.getVariables().size());
    }

    @Test
    void whenApplyingShouldProcessSimpleComponentsWithFilter() {
        lunaticQuestionnaire.getComponents().add(input);

        processing.apply(lunaticQuestionnaire);
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        VariableType variable = (VariableType) lunaticQuestionnaire.getVariables().get(0);
        assertEquals(VariableTypeEnum.CALCULATED, variable.getVariableType());
        assertEquals("FILTER_RESULT_NUMERIC", variable.getName());
        assertEquals(3, variable.getBindingDependencies().size());
        assertEquals("CALCULATED_VARIABLE", variable.getBindingDependencies().get(0));
        assertEquals("NUMERIC1_USED_FOR_CALCULATED_VARIABLE", variable.getBindingDependencies().get(1));
        assertEquals("NUMERIC2_USED_FOR_CALCULATED_VARIABLE", variable.getBindingDependencies().get(2));
        assertEquals("count(CALCULATED_VARIABLE) < 2", variable.getExpression().getValue());
    }
}
