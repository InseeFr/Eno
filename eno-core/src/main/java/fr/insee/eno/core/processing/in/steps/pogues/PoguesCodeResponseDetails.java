package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.DetailResponse;
import fr.insee.eno.core.processing.ProcessingStep;

public class PoguesCodeResponseDetails implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .forEach(this::moveDetailResponses);
    }

    private void moveDetailResponses(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion) {
        simpleMultipleChoiceQuestion.getDetailResponses().forEach(detailResponse -> {
            CodeResponse correspondingCode = findCodeResponse(detailResponse);
            if (correspondingCode == null)
                throw new MappingException(String.format("Cannot find code associated with %s.", detailResponse));
            correspondingCode.setDetailResponse(detailResponse);
        });
    }

    private CodeResponse findCodeResponse(DetailResponse detailResponse) {
        return null; // FIXME
    }

}
