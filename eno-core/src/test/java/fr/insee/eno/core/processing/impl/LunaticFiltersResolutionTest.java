package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.InputNumber;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticFiltersResolutionTest {

    private static final String QUESTION1_ID = "q1-id";
    private static final String QUESTION2_ID = "q2-id";

    private Questionnaire lunaticQuestionnaire;
    private EnoQuestionnaire enoQuestionnaire;

    @BeforeEach
    void createQuestionnaires() {
        // Given
        lunaticQuestionnaire = new Questionnaire();
        Input lunaticQuestion = new Input();
        ConditionFilterType lunaticFilter = new ConditionFilterType();
        lunaticFilter.setType(Constant.LUNATIC_LABEL_VTL);
        lunaticFilter.setValue("FOO_VARIABLE=1");
        lunaticQuestion.setConditionFilter(lunaticFilter);
        lunaticQuestionnaire.getComponents().add(lunaticQuestion);
        //
        enoQuestionnaire = new EnoQuestionnaire();
        EnoIndex enoIndex = new EnoIndex();
        enoQuestionnaire.setIndex(enoIndex);
        TextQuestion enoQuestion = new TextQuestion();
        enoQuestion.setId(QUESTION1_ID);
        Filter enoFilter = new Filter();
        enoQuestion.setFilter(enoFilter);
        enoIndex.put(QUESTION1_ID, enoQuestion);
    }

    @Test
    void simpleFilter() {
        // When
        LunaticFiltersResolution processing = new LunaticFiltersResolution(enoQuestionnaire);
        processing.apply(lunaticQuestionnaire);

        // Then
        assertEquals("(FOO_VARIABLE=1)",
                lunaticQuestionnaire.getComponents().get(0).getConditionFilter().getValue());
    }

    @Test
    @Disabled("work in progress")
    void nestedFilter_oneLevel() {
        // Given
        InputNumber lunaticQuestion2 = new InputNumber();
        ConditionFilterType lunaticFilter2 = new ConditionFilterType();
        lunaticFilter2.setType(Constant.LUNATIC_LABEL_VTL);
        lunaticFilter2.setValue("BAR_VARIABLE=2");
        lunaticQuestion2.setConditionFilter(lunaticFilter2);
        lunaticQuestionnaire.getComponents().add(lunaticQuestion2);
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        NumericQuestion enoQuestion2 = new NumericQuestion();
        enoQuestion2.setId(QUESTION2_ID);
        Filter enoFilter2 = new Filter();
        enoFilter2.setParentFilter(((Question) enoIndex.get(QUESTION1_ID)).getFilter());
        enoQuestion2.setFilter(enoFilter2);
        enoIndex.put(QUESTION2_ID, enoQuestion2);

        // When
        LunaticFiltersResolution processing = new LunaticFiltersResolution(enoQuestionnaire);
        processing.apply(lunaticQuestionnaire);

        // Then
        assertEquals("(FOO_VARIABLE=1)",
                lunaticQuestionnaire.getComponents().get(0).getConditionFilter().getValue());
        assertEquals("(FOO_VARIABLE=1) and (BAR_VARIABLE=2)",
                lunaticQuestionnaire.getComponents().get(1).getConditionFilter().getValue());
    }

}
