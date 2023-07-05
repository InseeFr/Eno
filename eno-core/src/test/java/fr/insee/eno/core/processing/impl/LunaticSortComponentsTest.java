package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.ItemReference.ItemType;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.EnoProcessing;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.SequenceType;
import fr.insee.lunatic.model.flat.Textarea;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        enoSequence.getSequenceStructure().add(
                ItemReference.builder().id(QUESTION_ID).type(ItemType.QUESTION).build());
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
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
        assertEquals(SEQUENCE_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof SequenceType);
        assertEquals(QUESTION_ID, lunaticQuestionnaire.getComponents().get(1).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(1) instanceof Textarea);
    }

    @Nested
    class IntegrationTests {

        @Test
        void largeCoverageQuestionnaire() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            DDIMapper ddiMapper = new DDIMapper();
            ddiMapper.mapDDI(
                    DDIParser.parse(this.getClass().getClassLoader().getResourceAsStream(
                            "end-to-end/ddi/ddi-l20g2ba7.xml")),
                    enoQuestionnaire);
            EnoProcessing enoProcessing = new EnoProcessing();
            enoProcessing.applyProcessing(enoQuestionnaire, Format.DDI);
            Questionnaire lunaticQuestionnaire = new Questionnaire();
            LunaticMapper lunaticMapper = new LunaticMapper();
            lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);
            List<String> idsBefore = lunaticQuestionnaire.getComponents().stream().map(ComponentType::getId).toList();

            // When
            LunaticSortComponents processing = new LunaticSortComponents(enoQuestionnaire);
            processing.apply(lunaticQuestionnaire);

            // Then
            List<String> idsAfter = lunaticQuestionnaire.getComponents().stream().map(ComponentType::getId).toList();
            assertTrue(idsBefore.size() == idsAfter.size()
                    && idsBefore.containsAll(idsAfter) && idsAfter.containsAll(idsBefore));
        }

    }
}
