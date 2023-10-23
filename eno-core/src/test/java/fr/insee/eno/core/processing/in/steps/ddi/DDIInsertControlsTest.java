package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        questions.addAll(enoQuestionnaire.getMultipleResponseQuestions());
        assertTrue(questions.stream().anyMatch(question -> !question.getControls().isEmpty()));
    }

}
