package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.processing.common.EnoProcessing;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticSortComponents;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Textarea;
import org.junit.jupiter.api.DisplayName;
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
                StructureItemReference.builder().id(QUESTION_ID).type(StructureItemType.QUESTION).build());
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
        lunaticQuestionnaire.getComponents().add(new fr.insee.lunatic.model.flat.Sequence());
        lunaticQuestionnaire.getComponents().get(0).setId(QUESTION_ID);
        lunaticQuestionnaire.getComponents().get(1).setId(SEQUENCE_ID);

        //
        new LunaticSortComponents(enoQuestionnaire).apply(lunaticQuestionnaire);

        //
        assertEquals(2, lunaticQuestionnaire.getComponents().size());
        assertEquals(SEQUENCE_ID, lunaticQuestionnaire.getComponents().get(0).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(0) instanceof fr.insee.lunatic.model.flat.Sequence);
        assertEquals(QUESTION_ID, lunaticQuestionnaire.getComponents().get(1).getId());
        assertTrue(lunaticQuestionnaire.getComponents().get(1) instanceof Textarea);
    }

    @Nested
    class IntegrationTests {

        @Test
        @DisplayName("Questionnaire 'l20g2ba7': no component lost after sorting")
        void largeCoverageQuestionnaire() throws DDIParsingException {
            // Given
            EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                    this.getClass().getClassLoader().getResourceAsStream("end-to-end/ddi/ddi-l20g2ba7.xml"),
                    EnoParameters.defaultParameters());
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
