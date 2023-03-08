package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.reference.EnoIndex;

import java.math.BigInteger;

public class EnoAddResponseTimeSection implements EnoProcessingInterface {

    public static final String HOURS_VARIABLE_NAME = "HEURE_REMPL";
    public static final String HOURS_VARIABLE_UNIT = "heures";
    public static final String MINUTES_VARIABLE_NAME = "MIN_REMPL";
    public static final String MINUTES_VARIABLE_UNIT = "minutes";
    public static final String RESPONSE_TIME_SEQUENCE_ID = "TIME-SEQ";
    public static final String RESPONSE_TIME_SEQUENCE_LABEL = "Temps de réponse";
    public static final String HOURS_QUESTION_ID = "TIME-QUESTION-HEURE-REMPL";
    public static final String MINUTES_QUESTION_ID = "TIME-QUESTION-MIN-REMPL";
    public static final String RESPONSE_TIME_QUESTION_LABEL =
            "Combien de temps avez-vous mis en tout pour répondre à cette enquête " +
                    "(recherche des données + remplissage du questionnaire)\u00a0?";
    public static final boolean RESPONSE_TIME_QUESTION_MANDATORY = false;
    public static final double HOURS_QUESTION_MIN_VALUE = 0;
    public static final double HOURS_QUESTION_MAX_VALUE = 99;
    public static final BigInteger HOURS_QUESTION_DECIMALS = BigInteger.ZERO;
    public static final double TIME_MINUTES_QUESTION_MIN_VALUE = 0;
    public static final double RESPONSE_TIME_MINUTES_QUESTION_MAX_VALUE = 59;
    public static final BigInteger RESPONSE_TIME_MINUTES_QUESTION_DECIMALS = BigInteger.ZERO;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        //
        Variable hoursVariable = new Variable();
        hoursVariable.setName(HOURS_VARIABLE_NAME);
        hoursVariable.setQuestionReference(HOURS_QUESTION_ID);
        hoursVariable.setUnit(HOURS_VARIABLE_UNIT);
        hoursVariable.setCollected("COLLECTED"); //TODO: maybe an enum here see Variable class
        enoQuestionnaire.getVariables().add(hoursVariable);
        //
        Variable minutesVariable = new Variable();
        minutesVariable.setName(MINUTES_VARIABLE_NAME);
        minutesVariable.setQuestionReference(MINUTES_QUESTION_ID);
        minutesVariable.setUnit(MINUTES_VARIABLE_UNIT);
        minutesVariable.setCollected("COLLECTED"); //TODO: maybe an enum here see Variable class
        enoQuestionnaire.getVariables().add(minutesVariable);
        //
        Sequence sequence = new Sequence();
        sequence.setId(RESPONSE_TIME_SEQUENCE_ID);
        sequence.setLabel(new Label());
        sequence.getLabel().setValue(RESPONSE_TIME_SEQUENCE_LABEL);
        sequence.getComponentReferences().add(HOURS_QUESTION_ID);
        sequence.getComponentReferences().add(MINUTES_QUESTION_ID);
        enoQuestionnaire.getSequences().add(sequence);
        enoIndex.put(RESPONSE_TIME_SEQUENCE_ID, sequence);
        //
        enoQuestionnaire.getSequenceReferences().add(RESPONSE_TIME_SEQUENCE_ID);
        //
        NumericQuestion hoursQuestion = new NumericQuestion();
        hoursQuestion.setId(HOURS_QUESTION_ID);
        hoursQuestion.setName(HOURS_VARIABLE_NAME);
        hoursQuestion.setLabel(new DynamicLabel());
        hoursQuestion.getLabel().setValue(RESPONSE_TIME_QUESTION_LABEL);
        hoursQuestion.setMandatory(RESPONSE_TIME_QUESTION_MANDATORY);
        hoursQuestion.setMinValue(HOURS_QUESTION_MIN_VALUE);
        hoursQuestion.setMaxValue(HOURS_QUESTION_MAX_VALUE);
        hoursQuestion.setNumberOfDecimals(HOURS_QUESTION_DECIMALS);
        hoursQuestion.setUnit(HOURS_VARIABLE_UNIT);
        hoursQuestion.setResponse(new Response());
        hoursQuestion.getResponse().setVariableName(HOURS_VARIABLE_NAME);
        enoQuestionnaire.getSingleResponseQuestions().add(hoursQuestion);
        enoIndex.put(HOURS_QUESTION_ID, hoursQuestion);
        //
        NumericQuestion minutesQuestion = new NumericQuestion();
        minutesQuestion.setId(MINUTES_QUESTION_ID);
        minutesQuestion.setName(MINUTES_VARIABLE_NAME);
        minutesQuestion.setMandatory(RESPONSE_TIME_QUESTION_MANDATORY);
        minutesQuestion.setMinValue(TIME_MINUTES_QUESTION_MIN_VALUE);
        minutesQuestion.setMaxValue(RESPONSE_TIME_MINUTES_QUESTION_MAX_VALUE);
        minutesQuestion.setNumberOfDecimals(RESPONSE_TIME_MINUTES_QUESTION_DECIMALS);
        minutesQuestion.setUnit(MINUTES_VARIABLE_UNIT);
        minutesQuestion.setResponse(new Response());
        minutesQuestion.getResponse().setVariableName(MINUTES_VARIABLE_NAME);
        enoQuestionnaire.getSingleResponseQuestions().add(minutesQuestion);
        enoIndex.put(MINUTES_QUESTION_ID, minutesQuestion);
    }

}