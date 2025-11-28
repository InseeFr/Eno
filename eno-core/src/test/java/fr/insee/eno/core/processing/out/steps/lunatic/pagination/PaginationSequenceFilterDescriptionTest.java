package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationSequenceFilterDescriptionTest {

    @Test
    void sequenceModeSequenceFilterDescription() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(new Sequence());
        lunaticQuestionnaire.getComponents().add(new Input());
        lunaticQuestionnaire.getComponents().add(new FilterDescription());
        lunaticQuestionnaire.getComponents().add(new Sequence());
        lunaticQuestionnaire.getComponents().add(new Input());
        //
        new LunaticPaginationSequenceMode().apply(lunaticQuestionnaire);
        //
        assertEquals("1", lunaticQuestionnaire.getComponents().get(0).getPage());
        assertEquals("1", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(3).getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(4).getPage());
    }

    @Test
    void sequenceModeSequenceFilterDescriptionInLoop() {
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Loop loop = new Loop();
        loop.getComponents().add(new Sequence());
        loop.getComponents().add(new Input());
        loop.getComponents().add(new FilterDescription());
        loop.getComponents().add(new Sequence());
        loop.getComponents().add(new Input());
        lunaticQuestionnaire.getComponents().add(loop);
        //
        new LunaticPaginationSequenceMode().apply(lunaticQuestionnaire);
        //
        assertEquals("1.1", loop.getComponents().get(0).getPage());
        assertEquals("1.1", loop.getComponents().get(1).getPage());
        assertEquals("1.2", loop.getComponents().get(2).getPage());
        assertEquals("1.2", loop.getComponents().get(3).getPage());
        assertEquals("1.2", loop.getComponents().get(4).getPage());
    }

}
