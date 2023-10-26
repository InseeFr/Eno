package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnoAddResponseTimeSectionTest {

    @Test
    void hoursQuestionLabel_noPrefix() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                false, EnoParameters.QuestionNumberingMode.NONE, EnoParameters.ModeParameter.PROCESS);
        EnoAddResponseTimeSection processing = new EnoAddResponseTimeSection(prefixingStep);
        // When
        processing.apply(enoQuestionnaire);
        // Then
        Optional<NumericQuestion> hoursQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance)
                .map(NumericQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.HOURS_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        Optional<NumericQuestion> minutesQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance)
                .map(NumericQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.MINUTES_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(hoursQuestion.isPresent());
        assertTrue(minutesQuestion.isPresent());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL, hoursQuestion.get().getLabel().getValue());
        assertEquals("", minutesQuestion.get().getLabel().getValue());
    }

    @Test
    void hoursQuestionLabel_arrowPrefix() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                true, EnoParameters.QuestionNumberingMode.NONE, EnoParameters.ModeParameter.PROCESS);
        EnoAddResponseTimeSection processing = new EnoAddResponseTimeSection(prefixingStep);
        // When
        processing.apply(enoQuestionnaire);
        // Then
        Optional<NumericQuestion> hoursQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance)
                .map(NumericQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.HOURS_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(hoursQuestion.isPresent());
        String expected = "\"➡ \" || " + EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL;
        assertEquals(expected, hoursQuestion.get().getLabel().getValue());
    }

    @Test
    void hoursQuestionLabel_arrowAndNumberPrefix() {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                true, EnoParameters.QuestionNumberingMode.NONE, EnoParameters.ModeParameter.PROCESS);
        EnoAddResponseTimeSection processing = new EnoAddResponseTimeSection(prefixingStep);
        // When
        processing.apply(enoQuestionnaire);
        // Then
        Optional<NumericQuestion> hoursQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance)
                .map(NumericQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.HOURS_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(hoursQuestion.isPresent());
        String expected = "\"➡ 1. \" || " + EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL;
        assertEquals(expected, hoursQuestion.get().getLabel().getValue());
    }

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI);
        enoParameters.setResponseTimeQuestion(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<Sequence> responseTimeSequence = enoQuestionnaire.getSequences().stream()
                .filter(sequence -> EnoAddResponseTimeSection.RESPONSE_TIME_SEQUENCE_ID.equals(sequence.getId()))
                .findAny();
        assertTrue(responseTimeSequence.isPresent());
    }

    @Test
    void integrationTest_lunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setResponseTimeQuestion(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<fr.insee.lunatic.model.flat.Sequence> responseTimeSequence = lunaticQuestionnaire.getComponents()
                .stream()
                .filter(fr.insee.lunatic.model.flat.Sequence.class::isInstance)
                .map(fr.insee.lunatic.model.flat.Sequence.class::cast)
                .filter(component -> EnoAddResponseTimeSection.RESPONSE_TIME_SEQUENCE_ID.equals(component.getId()))
                .findAny();
        assertTrue(responseTimeSequence.isPresent());
    }

}
