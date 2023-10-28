package fr.insee.eno.core.mapping.in.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PairwiseQuestionTest {

    @Test
    void integrationTest_pairwise() throws DDIParsingException {
        // Given & When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-pairwise.xml")),
                enoQuestionnaire);

        // Then
        Optional<PairwiseQuestion> searchedPairwiseQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(PairwiseQuestion.class::isInstance)
                .map(PairwiseQuestion.class::cast)
                .findAny();
        assertTrue(searchedPairwiseQuestion.isPresent());
        //
        PairwiseQuestion pairwiseQuestion = searchedPairwiseQuestion.get();
        assertEquals("lo9tyy1v", pairwiseQuestion.getId());
        assertEquals("PAIRWISE_QUESTION", pairwiseQuestion.getName());
        assertNull(pairwiseQuestion.getResponse());
        assertEquals("PAIRWISE_SOURCE", pairwiseQuestion.getLoopVariableName());
        assertEquals(1, pairwiseQuestion.getUniqueChoiceQuestions().size());
        //
        UniqueChoiceQuestion pairwiseUCQ = pairwiseQuestion.getUniqueChoiceQuestions().get(0);
        assertEquals("lo9tyy1v-pairwise-dropdown", pairwiseUCQ.getId());
        assertEquals("PAIRWISE_QUESTION", pairwiseUCQ.getName());
        assertEquals("PAIRWISE_QUESTION", pairwiseUCQ.getResponse().getVariableName());
        assertEquals(UniqueChoiceQuestion.DisplayFormat.DROPDOWN, pairwiseUCQ.getDisplayFormat());
        assertEquals("lo9tv7s6", pairwiseUCQ.getCodeListReference());
    }

}
