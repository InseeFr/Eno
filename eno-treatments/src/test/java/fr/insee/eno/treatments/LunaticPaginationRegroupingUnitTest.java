package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LunaticPaginationRegroupingUnitTest {

    @Test
    void subsequence_noDescription() {
        //
        Questionnaire questionnaire = new Questionnaire();
        Sequence sequence = new Sequence();
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        Subsequence subsequence = new Subsequence();
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        Question question = new Question();
        Input input = new Input();
        input.setResponse(new ResponseType());
        input.getResponse().setName("INPUT_VAR");
        question.getComponents().add(input);
        questionnaire.getComponents().add(sequence);
        questionnaire.getComponents().add(subsequence);
        questionnaire.getComponents().add(question);
        //
        new LunaticPaginationRegrouping(new Regroupements(new ArrayList<>())).apply(questionnaire);
        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertNull(questionnaire.getComponents().get(1).getPage());
        assertEquals("2", ((Subsequence) questionnaire.getComponents().get(1)).getGoToPage());
        assertEquals("2", questionnaire.getComponents().get(2).getPage());
    }

    @Test
    void subsequence_withDescription() {
        //
        Questionnaire questionnaire = new Questionnaire();
        Sequence sequence = new Sequence();
        sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        Subsequence subsequence = new Subsequence();
        subsequence.setComponentType(ComponentTypeEnum.SUBSEQUENCE);
        subsequence.setDescription(new LabelType());
        Question question = new Question();
        Input input = new Input();
        input.setResponse(new ResponseType());
        input.getResponse().setName("INPUT_VAR");
        question.getComponents().add(input);
        questionnaire.getComponents().add(sequence);
        questionnaire.getComponents().add(subsequence);
        questionnaire.getComponents().add(question);
        //
        new LunaticPaginationRegrouping(new Regroupements(new ArrayList<>())).apply(questionnaire);
        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertEquals("2", questionnaire.getComponents().get(1).getPage());
        assertEquals("2", ((Subsequence) questionnaire.getComponents().get(1)).getGoToPage());
        assertEquals("3", questionnaire.getComponents().get(2).getPage());
    }

}
