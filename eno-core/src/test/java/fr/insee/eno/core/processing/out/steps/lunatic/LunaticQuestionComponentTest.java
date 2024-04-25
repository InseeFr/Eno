package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticQuestionComponentTest {

    @Test
    void wrapQuestionnaireComponents() {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        Sequence sequence = new Sequence();
        sequence.setId("sequence-id");
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        questionnaire.getComponents().add(sequence);
        //
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        inputNumber.setPage("1");
        inputNumber.setMin(0d);
        inputNumber.setMax(10d);
        questionnaire.getComponents().add(inputNumber);

        //
        new LunaticQuestionComponent().apply(questionnaire);

        // Sequence component should not be changed
        assertEquals(sequence, questionnaire.getComponents().get(0));
        // Input number should be replaced by a Question component
        assertEquals(ComponentTypeEnum.QUESTION, questionnaire.getComponents().get(1).getComponentType());
    }

    @Test
    void wrapLoopComponents() {
        //
        Questionnaire questionnaire = new Questionnaire();
        Loop loop = new Loop();
        loop.setComponentType(ComponentTypeEnum.LOOP);
        //
        Sequence sequence = new Sequence();
        sequence.setId("sequence-id");
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        loop.getComponents().add(sequence);
        //
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.INPUT_NUMBER);
        inputNumber.setPage("1");
        inputNumber.setMin(0d);
        inputNumber.setMax(10d);
        loop.getComponents().add(inputNumber);
        //
        questionnaire.getComponents().add(loop);

        //
        new LunaticQuestionComponent().apply(questionnaire);

        // Questionnaire first component should still be a loop
        assertEquals(ComponentTypeEnum.LOOP, questionnaire.getComponents().getFirst().getComponentType());
        // Sequence component should not be changed
        assertEquals(sequence, loop.getComponents().get(0));
        // Input number should be replaced by a Question component
        assertEquals(ComponentTypeEnum.QUESTION, loop.getComponents().get(1).getComponentType());
    }

}
