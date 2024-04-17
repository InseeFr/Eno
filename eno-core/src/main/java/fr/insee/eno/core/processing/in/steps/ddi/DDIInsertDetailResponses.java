package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public class DDIInsertDetailResponses implements ProcessingStep<EnoQuestionnaire> {

    /**
     * TODO
     * @param enoQuestionnaire
     */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .forEach(this::insertDetailResponses);
    }

    private void insertDetailResponses(UniqueChoiceQuestion uniqueChoiceQuestion) {
        //
        Map<String, String> bindingsMap = new HashMap<>();
        uniqueChoiceQuestion.getDdiBindings().forEach(binding ->
                bindingsMap.put(binding.getSourceParameterId(), binding.getTargetParameterId()));
        //
        Map<String, String> responsesMap = new HashMap<>();
        uniqueChoiceQuestion.getDdiResponses().forEach(response ->
                responsesMap.put(response.getDdiReference(), response.getVariableName()));
        //
        uniqueChoiceQuestion.getDetailResponses().forEach(detailResponse -> {
            String variableName = responsesMap.get(bindingsMap.get(detailResponse.getResponseReference()));
            Response response = new Response();
            response.setVariableName(variableName);
            detailResponse.setResponse(response);
        });
    }

}
