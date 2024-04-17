package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import instance33.DDIInstanceDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DDIInsertDetailResponsesTest {

    @Test
    void integrationTest() throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstance = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-other-specify.xml"));
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        DDIMapper ddiMapper = new DDIMapper();
        ddiMapper.mapDDI(ddiInstance, enoQuestionnaire);

        //
        DDIInsertDetailResponses processingStep = new DDIInsertDetailResponses();
        processingStep.apply(enoQuestionnaire);

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

}
