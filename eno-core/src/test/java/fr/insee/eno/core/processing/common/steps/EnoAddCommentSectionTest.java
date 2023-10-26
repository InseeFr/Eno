package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.DDIToEno;
import fr.insee.eno.core.DDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnoAddCommentSectionTest {

    @Test
    void commentQuestionLabel_arrowAndNumberPrefix() {
        // Given a questionnaire with one question
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setIndex(new EnoIndex());
        Sequence sequence = new Sequence();
        sequence.getSequenceStructure().add(
                new StructureItemReference("question-id", StructureItemReference.StructureItemType.QUESTION));
        TextQuestion question = new TextQuestion();
        question.setId("question-id");
        question.setLabel(new DynamicLabel());
        question.getLabel().setValue("\"Foo label\"");
        enoQuestionnaire.getIndex().put("question-id", question);
        enoQuestionnaire.getSequences().add(sequence);
        enoQuestionnaire.getSingleResponseQuestions().add(question);
        //
        EnoAddPrefixInQuestionLabels prefixingStep = new EnoAddPrefixInQuestionLabels(
                true, EnoParameters.QuestionNumberingMode.ALL, EnoParameters.ModeParameter.PROCESS);
        prefixingStep.apply(enoQuestionnaire);

        // When
        new EnoAddCommentSection(prefixingStep).apply(enoQuestionnaire);

        // Then
        Optional<TextQuestion> commentQuestion = enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(TextQuestion.class::isInstance)
                .map(TextQuestion.class::cast)
                .filter(singleResponseQuestion -> EnoAddCommentSection.COMMENT_QUESTION_ID.equals(singleResponseQuestion.getId()))
                .findAny();
        assertTrue(commentQuestion.isPresent());
        String expected = "\"➡ 2. \" || " + EnoAddCommentSection.COMMENT_QUESTION_LABEL;
        assertEquals(expected, commentQuestion.get().getLabel().getValue());
    }

    @Test
    void integrationTest() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI);
        enoParameters.setCommentSection(true);
        //
        EnoQuestionnaire enoQuestionnaire = DDIToEno.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<Sequence> commentSequence = enoQuestionnaire.getSequences().stream()
                .filter(sequence -> EnoAddCommentSection.COMMENT_SEQUENCE_ID.equals(sequence.getId()))
                .findAny();
        assertTrue(commentSequence.isPresent());
    }

    @Test
    void integrationTest_lunaticOutput() throws DDIParsingException {
        //
        EnoParameters enoParameters = EnoParameters.of(
                EnoParameters.Context.DEFAULT, EnoParameters.ModeParameter.CAWI, Format.LUNATIC);
        enoParameters.setCommentSection(true);
        //
        Questionnaire lunaticQuestionnaire = DDIToLunatic.transform(
                this.getClass().getClassLoader().getResourceAsStream("integration/ddi/ddi-simple.xml"),
                enoParameters);
        //
        Optional<fr.insee.lunatic.model.flat.Sequence> commentSequence = lunaticQuestionnaire.getComponents().stream()
                .filter(fr.insee.lunatic.model.flat.Sequence.class::isInstance)
                .map(fr.insee.lunatic.model.flat.Sequence.class::cast)
                .filter(component -> EnoAddCommentSection.COMMENT_SEQUENCE_ID.equals(component.getId()))
                .findAny();
        assertTrue(commentSequence.isPresent());
    }

}
