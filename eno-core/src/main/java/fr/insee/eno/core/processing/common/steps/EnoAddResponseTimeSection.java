package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.DurationQuestion;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.DurationFormat;

public record EnoAddResponseTimeSection(EnoAddPrefixInQuestionLabels prefixingStep)
        implements ProcessingStep<EnoQuestionnaire> {

    public static final String RESPONSE_TIME_VARIABLE_NAME = "DUREE_REMPL";

    public static final String RESPONSE_TIME_SEQUENCE_ID = "TIME-SEQ";
    public static final String RESPONSE_TIME_SEQUENCE_LABEL = "\"Temps de réponse\"";

    public static final String RESPONSE_TIME_QUESTION_ID = "TIME-QUESTION-RESPONSE";

    public static final String RESPONSE_TIME_QUESTION_LABEL =
            "\"Combien de temps avez-vous mis en tout pour répondre à cette enquête " +
                    "(recherche des données + remplissage du questionnaire)\u00a0?\"";

    public static final boolean RESPONSE_TIME_QUESTION_MANDATORY = false;

    public static final String RESPONSE_TIME_FORMAT = DurationFormat.HOURS_MINUTES.value();

    public static final String RESPONSE_TIME_MIN_VALUE = "PT0H0M";
    public static final String RESPONSE_TIME_MAX_VALUE = "PT99H59M";

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;

        CollectedVariable responseTimeVariable = new CollectedVariable();
        responseTimeVariable.setName(RESPONSE_TIME_VARIABLE_NAME);
        responseTimeVariable.setQuestionReference(RESPONSE_TIME_QUESTION_ID);

        enoQuestionnaire.getVariables().add(responseTimeVariable);
        //
        Sequence sequence = new Sequence();
        sequence.setId(RESPONSE_TIME_SEQUENCE_ID);

        Label label = new Label();
        label.setValue(RESPONSE_TIME_SEQUENCE_LABEL);
        sequence.setLabel(label);

        sequence.getSequenceStructure().add(
                StructureItemReference.builder()
                        .id(RESPONSE_TIME_QUESTION_ID)
                        .type(StructureItemType.QUESTION)
                        .build()
        );

        enoQuestionnaire.getSequences().add(sequence);
        enoIndex.put(RESPONSE_TIME_SEQUENCE_ID, sequence);
        //
        DurationQuestion responseTimeQuestion = new DurationQuestion();

        responseTimeQuestion.setId(RESPONSE_TIME_QUESTION_ID);
        responseTimeQuestion.setName(RESPONSE_TIME_VARIABLE_NAME);

        responseTimeQuestion.setLabel(new DynamicLabel());
        responseTimeQuestion.getLabel().setValue(RESPONSE_TIME_QUESTION_LABEL);

        prefixingStep.addPrefixInQuestionLabel(responseTimeQuestion);

        responseTimeQuestion.setMandatory(RESPONSE_TIME_QUESTION_MANDATORY);
        responseTimeQuestion.setFormat(RESPONSE_TIME_FORMAT);
        responseTimeQuestion.setMinValue(RESPONSE_TIME_MIN_VALUE);
        responseTimeQuestion.setMaxValue(RESPONSE_TIME_MAX_VALUE);

        Response response = new Response();
        response.setVariableName(RESPONSE_TIME_VARIABLE_NAME);
        responseTimeQuestion.setResponse(response);

        enoQuestionnaire.getSingleResponseQuestions().add(responseTimeQuestion);
        enoIndex.put(RESPONSE_TIME_QUESTION_ID, responseTimeQuestion);
    }
}
