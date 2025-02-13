package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.ComplexMultipleChoiceQuestion;
import fr.insee.eno.core.serialize.DDIDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIInsertCodeListsTest {

    @Test
    void multipleChoiceQuestions() throws DDIParsingException {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new DDIMapper().mapDDI(
                DDIDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-mcq.xml")),
                enoQuestionnaire);

        // When
        new DDIInsertCodeLists().apply(enoQuestionnaire);

        // Then
        // This questionnaire has 1 'simple' multiples choice question, then 4 'complex' multiple choice questions.
        // For each of these, let's test that the code list is present,
        // and its id should be the same as the one referenced during mapping.
        for (int i = 1; i < 5; i ++) {
            ComplexMultipleChoiceQuestion mcq = (ComplexMultipleChoiceQuestion) enoQuestionnaire
                    .getMultipleResponseQuestions().get(i);
            assertEquals(mcq.getLeftColumnCodeListReference(), mcq.getLeftColumn().getId());
        }
    }

    // TODO: tests for table question cases.

}
