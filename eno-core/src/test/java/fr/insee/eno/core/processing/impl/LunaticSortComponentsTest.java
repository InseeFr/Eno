package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.reference.LunaticCatalog;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SequenceType;
import fr.insee.lunatic.model.flat.Textarea;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LunaticSortComponentsTest {

    private static final String SEQUENCE_ID = "sequence-id";
    private static final String QUESTION_ID = "question-id";

    @Test
    void simplestCase() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Sequence enoSequence = new Sequence();
        enoSequence.setId(SEQUENCE_ID);
        NumericQuestion enoQuestion = new NumericQuestion();
        enoQuestion.setId(QUESTION_ID);
        enoSequence.getComponentReferences().add(QUESTION_ID);
        enoQuestionnaire.getSequences().add(enoSequence);
        enoQuestionnaire.getSingleResponseQuestions().add(enoQuestion);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(SEQUENCE_ID, enoSequence);
        enoIndex.put(QUESTION_ID, enoQuestion);
        enoQuestionnaire.setIndex(enoIndex);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticQuestionnaire.getComponents().add(new Textarea());
        lunaticQuestionnaire.getComponents().add(new SequenceType());
        lunaticQuestionnaire.getComponents().get(0).setId(QUESTION_ID);
        lunaticQuestionnaire.getComponents().get(1).setId(SEQUENCE_ID);
        //
        LunaticCatalog lunaticCatalog = new LunaticCatalog(lunaticQuestionnaire);

        //
        new LunaticSortComponents(enoQuestionnaire, lunaticCatalog).apply(lunaticQuestionnaire);

        //
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
        assertEquals(SEQUENCE_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof SequenceType);
        assertEquals(QUESTION_ID, lunaticQuestionnaire.getComponents().get(1).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(1) instanceof Textarea);
    }

    static class IntegrationTests {

        @Test
        void largeCoverageQuestionnaire() throws DDIParsingException, IOException {
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIParser.parse(this.getClass().getClassLoader().getResourceAsStream("in/ddi/l20g2ba7.xml")),
                    enoQuestionnaire);
            EnoProcessing enoProcessing = new EnoProcessing();
            enoProcessing.applyProcessing(enoQuestionnaire, Format.DDI);
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            //
            LunaticCatalog lunaticCatalog = new LunaticCatalog(lunaticQuestionnaire);
            LunaticSortComponents processing = new LunaticSortComponents(enoQuestionnaire, lunaticCatalog);
            assertDoesNotThrow(() -> processing.apply(lunaticQuestionnaire));
        }

    }
}
