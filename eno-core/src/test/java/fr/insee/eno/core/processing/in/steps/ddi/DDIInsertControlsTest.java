package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DDIInsertControlsTest {

    @Test
    void questionnaireWithLoops() throws DDIParsingException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "functional/ddi/ddi-l7j0wwqx.xml")),
                enoQuestionnaire);

        //
        new DDIInsertControls().apply(enoQuestionnaire);

        //
        List<Question> questions = new ArrayList<>(enoQuestionnaire.getSingleResponseQuestions());
        //
        assertTrue(questions.get(0).getControls().isEmpty());
        //
        assertEquals(1, questions.get(1).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(1).getControls().get(0).getCriticality());
        //
        assertEquals(1, questions.get(2).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(2).getControls().get(0).getCriticality());
        //
        assertEquals(2, questions.get(3).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(3).getControls().get(0).getCriticality());
        assertEquals(Control.Criticality.INFO, questions.get(3).getControls().get(1).getCriticality());
        //
        assertEquals(1, questions.get(4).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(4).getControls().get(0).getCriticality());
        //
        assertEquals(1, questions.get(5).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(5).getControls().get(0).getCriticality());
        //
        assertEquals(1, questions.get(6).getControls().size());
        assertEquals(Control.Criticality.INFO, questions.get(6).getControls().get(0).getCriticality());
        //
        assertTrue(questions.get(7).getControls().isEmpty());
    }

}
