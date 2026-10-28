package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.DurationQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EnoAddResponseTimeSectionTest {

    @Test
    void responseTimeQuestionLabelNoPrefix() {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = applyResponseTimeQuestionStep(
                false, EnoParameters.QuestionNumberingMode.NONE, null);
        // Then
        Optional<DurationQuestion> responseTimeQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(DurationQuestion.class::isInstance)
                .map(DurationQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(responseTimeQuestion.isPresent());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL, responseTimeQuestion.get().getLabel().getValue());
    }

    @Test
    void responseTimeQuestionDurationProperties() {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = applyResponseTimeQuestionStep(
                false, EnoParameters.QuestionNumberingMode.NONE, null);
        // Then
        Optional<DurationQuestion> responseTimeQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(DurationQuestion.class::isInstance)
                .map(DurationQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(responseTimeQuestion.isPresent());
        DurationQuestion question = responseTimeQuestion.get();

        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_VARIABLE_NAME, question.getName());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_FORMAT, question.getFormat());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_MIN_VALUE, question.getMinValue());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_MAX_VALUE, question.getMaxValue());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_MANDATORY, question.isMandatory());
        assertNotNull(question.getResponse());
        assertEquals(EnoAddResponseTimeSection.RESPONSE_TIME_VARIABLE_NAME, question.getResponse().getVariableName());

        assertTrue(enoQuestionnaire.getVariables().stream()
                .anyMatch(variable -> EnoAddResponseTimeSection.RESPONSE_TIME_VARIABLE_NAME.equals(variable.getName())));
    }

    @Test
    void responseTimeQuestionLabelArrowPrefix() {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = applyResponseTimeQuestionStep(
                true, EnoParameters.QuestionNumberingMode.NONE, null);
        // Then
        Optional<DurationQuestion> responseTimeQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(DurationQuestion.class::isInstance)
                .map(DurationQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(responseTimeQuestion.isPresent());
        String expected = "\"➡ \" || " + EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL;
        assertEquals(expected, responseTimeQuestion.get().getLabel().getValue());
    }

    @Test
    void responseTimeQuestionLabelArrowAndNumberPrefix() {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = applyResponseTimeQuestionStep(
                true, EnoParameters.QuestionNumberingMode.SEQUENCE, null);
        // Then
        Optional<DurationQuestion> responseTimeQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(DurationQuestion.class::isInstance)
                .map(DurationQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(responseTimeQuestion.isPresent());
        String expected = "\"➡ 1. \" || " + EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL;
        assertEquals(expected, responseTimeQuestion.get().getLabel().getValue());
    }

    @Test
    void responseTimeQuestionLabelNumberPrefixLunaticFormat() {
        // Given + When
        EnoQuestionnaire enoQuestionnaire = applyResponseTimeQuestionStep(
                true, EnoParameters.QuestionNumberingMode.SEQUENCE, Format.LUNATIC);
        // Then
        Optional<DurationQuestion> responseTimeQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(DurationQuestion.class::isInstance)
                .map(DurationQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(responseTimeQuestion.isPresent());
        String expected = "\"➡ 1\\. \" || " + EnoAddResponseTimeSection.RESPONSE_TIME_QUESTION_LABEL;
        assertEquals(expected, responseTimeQuestion.get().getLabel().getValue());
    }

    private static EnoQuestionnaire applyResponseTimeQuestionStep(boolean arrowCharInQuestions,
                                                                  EnoParameters.QuestionNumberingMode questionNumberingMode,
                                                                  Format outFormat) {
        // Given
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                arrowCharInQuestions, questionNumberingMode, EnoParameters.ModeParameter.PROCESS, outFormat);
        prefixingStep.apply(enoQuestionnaire);
        EnoAddResponseTimeSection processing = new EnoAddResponseTimeSection(prefixingStep);
        // When
        processing.apply(enoQuestionnaire);
        return enoQuestionnaire;
    }

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI);
        enoParameters.setResponseTimeQuestion(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"))
                .transform(enoParameters);
        //
        Optional<Sequence> responseTimeSequence = enoQuestionnaire.getSequences().stream()
                .filter(sequence -> EnoAddResponseTimeSection.RESPONSE_TIME_SEQUENCE_ID.equals(sequence.getId()))
                .findAny();
        assertTrue(responseTimeSequence.isPresent());
    }

    @Test
    void integrationTestLunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setResponseTimeQuestion(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.fromInputStream(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"))
                .transform(enoParameters);
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
