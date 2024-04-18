package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DDIInsertDetailResponsesTest {

    private static EnoQuestionnaire enoQuestionnaire;

    @BeforeAll
    static void mapAndProcessDDI() throws DDIParsingException {
        //
        enoQuestionnaire = new EnoQuestionnaire();
        DDIInstanceDocument ddiInstance = DDIDeserializer.deserialize(
                DDIInsertDetailResponsesTest.class.getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-other-specify.xml"));
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstance, enoQuestionnaire);

        //
        DDIInsertDetailResponses ddiInsertDetailResponses = new DDIInsertDetailResponses();
        ddiInsertDetailResponses.apply(enoQuestionnaire);
    }

    @Test
    void uniqueChoiceDetailsTest() {
        //
        List<UniqueChoiceQuestion> uniqueChoiceQuestions = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance).map(UniqueChoiceQuestion.class::cast).toList();
        UniqueChoiceQuestion radioUCQ = uniqueChoiceQuestions.stream()
                .filter(uniqueChoiceQuestion ->
                        UniqueChoiceQuestion.DisplayFormat.RADIO.equals(uniqueChoiceQuestion.getDisplayFormat()))
                .findAny().orElse(null);
        UniqueChoiceQuestion checkboxUCQ = uniqueChoiceQuestions.stream()
                .filter(uniqueChoiceQuestion ->
                        UniqueChoiceQuestion.DisplayFormat.CHECKBOX.equals(uniqueChoiceQuestion.getDisplayFormat()))
                .findAny().orElse(null);
        //
        assertNotNull(radioUCQ);
        assertNotNull(checkboxUCQ);
        assertThat(getDetailResponseNames(radioUCQ)).containsExactlyInAnyOrderElementsOf(
                List.of("UCQ_codeC_RADIO", "UCQ_codeD_RADIO"));
        assertThat(getDetailResponseNames(checkboxUCQ)).containsExactlyInAnyOrderElementsOf(
                List.of("UCQ_codeC_CHECKBOX", "UCQ_codeD_CHECKBOX"));
    }

    /** Utility test method to gather detail response names of a unique choice question. */
    private static List<String> getDetailResponseNames(UniqueChoiceQuestion uniqueChoiceQuestion) {
        return uniqueChoiceQuestion.getDetailResponses().stream()
                .map(detailResponse -> detailResponse.getResponse().getVariableName()).toList();
    }

    @Test
    void multipleChoiceDetailsTest() {
        //
        List<SimpleMultipleChoiceQuestion> simpleMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .toList();
        assertEquals(1, simpleMCQList.size());
        SimpleMultipleChoiceQuestion simpleMCQ = simpleMCQList.getFirst();
        // After processing: should be only 4 code responses (that correspond to the 4 codes/modalities)
        assertEquals(4, simpleMCQ.getCodeResponses().size());
        //
        assertNull(simpleMCQ.getCodeResponses().get(0).getDetailResponse());
        assertNull(simpleMCQ.getCodeResponses().get(1).getDetailResponse());
        assertEquals("MCQ_codeC",
                simpleMCQ.getCodeResponses().get(2).getDetailResponse().getResponse().getVariableName());
        assertEquals("MCQ_codeD",
                simpleMCQ.getCodeResponses().get(3).getDetailResponse().getResponse().getVariableName());
        assertEquals("\"Please, specify about option C:\"",
                simpleMCQ.getCodeResponses().get(2).getDetailResponse().getLabel().getValue());
        assertEquals("\"Please, specify about option D:\"",
                simpleMCQ.getCodeResponses().get(3).getDetailResponse().getLabel().getValue());
    }

}
