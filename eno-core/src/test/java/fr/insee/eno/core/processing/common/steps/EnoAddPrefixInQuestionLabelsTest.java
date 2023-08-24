package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.EnoParameters.QuestionNumberingMode;
import fr.insee.eno.core.reference.EnoIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoAddPrefixInQuestionLabelsTest {

    private EnoQuestionnaire enoQuestionnaire;

    @BeforeEach
    void createEnoQuestionnaire() {
        //
        enoQuestionnaire = new EnoQuestionnaire();
        Sequence sequence = new Sequence();
        TextQuestion question = new TextQuestion();
        String questionId = "question-id";
        question.setId(questionId);
        question.setLabel(new DynamicLabel());
        question.getLabel().setValue("\"Question label\"");
        sequence.getSequenceStructure().add(
                StructureItemReference.builder()
                        .id(questionId)
                        .type(StructureItemReference.StructureItemType.QUESTION)
                        .build());
        enoQuestionnaire.getSequences().add(sequence);
        enoQuestionnaire.getSingleResponseQuestions().add(question);
        //
        EnoIndex enoIndex = new EnoIndex();
        enoIndex.put(questionId, question);
        enoQuestionnaire.setIndex(enoIndex);
    }

    @Test
    void noPrefix() {
        //
        new EnoAddPrefixInQuestionLabels(false, QuestionNumberingMode.NONE, ModeParameter.CAWI)
                .apply(enoQuestionnaire);
        //
        assertEquals("\"Question label\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
    }

    @Test
    void arrowCharPrefix() {
        //
        new EnoAddPrefixInQuestionLabels(true, QuestionNumberingMode.NONE, ModeParameter.CAWI)
                .apply(enoQuestionnaire);
        //
        assertEquals("\"➡ \" || \"Question label\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
    }

    @Test
    void numberingPrefix() {
        //
        new EnoAddPrefixInQuestionLabels(false, QuestionNumberingMode.ALL, ModeParameter.CAWI)
                .apply(enoQuestionnaire);
        //
        assertEquals("\"1. \" || \"Question label\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
    }

    @Test
    void arrowCharAndNumberingPrefix() {
        //
        new EnoAddPrefixInQuestionLabels(true, QuestionNumberingMode.ALL, ModeParameter.CAWI)
                .apply(enoQuestionnaire);
        //
        assertEquals("\"➡ 1. \" || \"Question label\"",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
    }

    @Test
    void arrowCharAndNumberingPrefix_staticMode() {
        //
        new EnoAddPrefixInQuestionLabels(true, QuestionNumberingMode.ALL, ModeParameter.PAPI)
                .apply(enoQuestionnaire);
        //
        assertEquals("➡ 1. Question label",
                enoQuestionnaire.getSingleResponseQuestions().get(0).getLabel().getValue());
    }

}
