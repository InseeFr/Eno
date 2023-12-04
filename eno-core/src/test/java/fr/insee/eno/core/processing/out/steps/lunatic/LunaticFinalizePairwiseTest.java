package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticFinalizePairwiseTest {
    LunaticFinalizePairwise processing;
    EnoQuestionnaire enoQuestionnaire;
    Questionnaire lunaticQuestionnaire;
    EnoIndex enoIndex;
    PairwiseLinks pairwiseLinks1, pairwiseLinks2;
    Radio radioComponent;

    @BeforeEach
    void init() {
        String pairwiseId = "pairwise-id";
        enoQuestionnaire = new EnoQuestionnaire();
        enoIndex = new EnoIndex();
        PairwiseQuestion pairwise = new PairwiseQuestion();
        pairwise.setLoopVariableName("pairwiseVariableName");
        enoIndex.put(pairwiseId, pairwise);
        enoQuestionnaire.setIndex(enoIndex);

        lunaticQuestionnaire = new Questionnaire();

        pairwiseLinks1 = new PairwiseLinks();
        pairwiseLinks1.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        pairwiseLinks1.setId(pairwiseId);

        pairwiseLinks2 = new PairwiseLinks();
        pairwiseLinks2.setComponentType(ComponentTypeEnum.PAIRWISE_LINKS);
        processing = new LunaticFinalizePairwise(enoQuestionnaire);

        radioComponent = new Radio();
        ResponseType responseType = new ResponseType();
        responseType.setName("pairwise-radio-name");
        radioComponent.setResponse(responseType);
        ConditionFilterType conditionFilterType = new ConditionFilterType();
        conditionFilterType.setValue("true");
        conditionFilterType.setType(LabelTypeEnum.VTL);
        radioComponent.setConditionFilter(conditionFilterType);
        pairwiseLinks1.setComponents(List.of(radioComponent));
    }

    @Test
    void whenMultiplePairwiseThrowsException() {
        lunaticQuestionnaire.getComponents().addAll(List.of(pairwiseLinks1, pairwiseLinks2));
        assertThrows(LunaticSerializationException.class, () -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    void whenNoPairwiseDoesNotThrowException() {
        lunaticQuestionnaire.getComponents().add(radioComponent);
        assertDoesNotThrow(() -> processing.apply(lunaticQuestionnaire));
    }

    @Test
    void whenFinalizingSubComponentConditionFilterIsSet() {
        lunaticQuestionnaire.getComponents().add(pairwiseLinks1);
        processing.apply(lunaticQuestionnaire);
        assertNull(radioComponent.getConditionFilter());
    }

    @Test
    void checkPairwiseInLoopIsProcessed() {
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        lunaticQuestionnaire.getComponents().add(loop);
        loop.getComponents().add(pairwiseLinks1);
        processing.apply(lunaticQuestionnaire);
        assertNull(radioComponent.getConditionFilter());
    }

    @Test
    void whenFinalizingSymLinksAreSet() {
        lunaticQuestionnaire.getComponents().add(pairwiseLinks1);
        processing.apply(lunaticQuestionnaire);
        assertNotNull(pairwiseLinks1.getSymLinks());
        assertEquals("pairwise-radio-name", pairwiseLinks1.getSymLinks().getName());
    }

    @Test
    void whenFinalizingCalculatedVariablesAreSet() {
        lunaticQuestionnaire.getComponents().add(pairwiseLinks1);
        processing.apply(lunaticQuestionnaire);
        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        assertEquals(2, variables.size());
        assertEquals("xAxis", variables.get(0).getName());
        assertEquals("yAxis", variables.get(1).getName());
    }
}
