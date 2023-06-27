package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.sequence.SequenceItem;
import fr.insee.eno.core.model.sequence.SequenceItem.SequenceItemType;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.reference.EnoIndex;

import java.math.BigInteger;

public class EnoAddCommentSection implements EnoProcessingInterface {

    public static final String COMMENT_VARIABLE_NAME = "COMMENT_QE";
    public static final String COMMENT_SEQUENCE_ID = "COMMENT-SEQ";
    public static final String COMMENT_SEQUENCE_LABEL = "Commentaire";
    public static final String COMMENT_QUESTION_ID = "COMMENT-QUESTION";
    public static final String COMMENT_QUESTION_LABEL =
            "Avez-vous des remarques concernant l'enquête ou des commentaires\u00a0?";
    public static final boolean COMMENT_QUESTION_MANDATORY = false;
    public static final int COMMENT_QUESTION_LENGTH = 2000;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        //
        Variable commentVariable = new Variable();
        commentVariable.setName(COMMENT_VARIABLE_NAME);
        commentVariable.setQuestionReference(COMMENT_QUESTION_ID);
        commentVariable.setCollected("COLLECTED"); //TODO: maybe an enum here see Variable class
        enoQuestionnaire.getVariables().add(commentVariable);
        //
        Sequence sequence = new Sequence();
        sequence.setId(COMMENT_SEQUENCE_ID);
        sequence.setLabel(new Label());
        sequence.getLabel().setValue(COMMENT_SEQUENCE_LABEL);
        sequence.getSequenceItems().add(
                SequenceItem.builder().id(COMMENT_QUESTION_ID).type(SequenceItemType.QUESTION).build());
        enoQuestionnaire.getSequences().add(sequence);
        enoIndex.put(COMMENT_SEQUENCE_ID, sequence);
        //
        TextQuestion commentQuestion = new TextQuestion();
        commentQuestion.setId(COMMENT_QUESTION_ID);
        commentQuestion.setName(COMMENT_VARIABLE_NAME);
        commentQuestion.setLabel(new DynamicLabel());
        commentQuestion.getLabel().setValue(COMMENT_QUESTION_LABEL);
        commentQuestion.setMandatory(COMMENT_QUESTION_MANDATORY);
        commentQuestion.setMaxLength(BigInteger.valueOf(COMMENT_QUESTION_LENGTH));
        commentQuestion.setResponse(new Response());
        commentQuestion.getResponse().setVariableName(COMMENT_VARIABLE_NAME);
        enoQuestionnaire.getSingleResponseQuestions().add(commentQuestion);
        enoIndex.put(COMMENT_QUESTION_ID, commentQuestion);
    }

}
