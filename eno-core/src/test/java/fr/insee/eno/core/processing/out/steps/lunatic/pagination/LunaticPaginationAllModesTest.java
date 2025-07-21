package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LunaticPaginationAllModesTest {

    @Test
    void loopPaginatedIterations() {
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        Loop loop = new Loop();
        loop.setIsPaginatedByIterations(true);
        loop.setLines(new LinesLoop());
        String expression = "LOOP_SIZE_VAR";
        String shapeFrom = "SOME_VAR";
        loop.getLines().setMin(new LabelType());
        loop.getLines().setMax(new LabelType());
        loop.getLines().getMin().setValue(expression);
        loop.getLines().getMax().setValue(expression);
        loop.getLines().getMin().setShapeFrom(shapeFrom);
        loop.getLines().getMax().setShapeFrom(shapeFrom);
        Sequence sequence = new Sequence(); sequence.setComponentType(ComponentTypeEnum.SEQUENCE);
        loop.getComponents().add(sequence);
        loop.getComponents().add(new Input());
        lunaticQuestionnaire.getComponents().add(loop);

        new LunaticPaginationQuestionMode().apply(lunaticQuestionnaire);

        assertEquals("1", loop.getPage());
        assertEquals("1.1", loop.getComponents().get(0).getPage());
        assertEquals("1.1", loop.getComponents().get(1).getPage());
        assertNull(loop.getLines());
        assertEquals("LOOP_SIZE_VAR", loop.getIterations().getValue());
        assertEquals(LabelTypeEnum.VTL, loop.getIterations().getType());
        assertEquals("SOME_VAR", loop.getIterations().getShapeFrom());
    }

}