package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.DetailResponse;
import fr.insee.eno.core.model.response.ModalityAttachment;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.processing.ProcessingStep;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Processing class to insert the detail ("please specify") responses at the right place.
 */
public class DDIInsertDetailResponses implements ProcessingStep<EnoQuestionnaire> {

    /**
     * Processes unique and multiple choice questions to insert detail responses at the right place.
     * @param enoQuestionnaire Eno questionnaire.
     */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .forEach(this::insertDetailResponses);
        //
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .forEach(this::resolveDetailResponses);
    }

    private void insertDetailResponses(UniqueChoiceQuestion uniqueChoiceQuestion) {
        //
        Map<String, String> bindingsMap = uniqueChoiceQuestion.getDdiBindings().stream()
                .collect(Collectors.toMap(Binding::getSourceParameterId, Binding::getTargetParameterId));
        //
        Map<String, String> responsesMap = uniqueChoiceQuestion.getDdiResponses().stream()
                .collect(Collectors.toMap(Response::getDdiReference, Response::getVariableName));
        //
        uniqueChoiceQuestion.getDetailResponses().forEach(detailResponse -> {
            String variableName = responsesMap.get(bindingsMap.get(detailResponse.getResponseReference()));
            Response response = new Response();
            response.setVariableName(variableName);
            detailResponse.setResponse(response);
        });
    }

    /**
     * In DDI, when a multiple choice question have modalities with a "please specify" field,
     * the detail responses are described the same way as the modalities are.
     * Thus, the DDI mapping creates additional code responses objects that need inserted at the right place.
     * */
    private void resolveDetailResponses(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion) {
        //
        Map<BigInteger, ModalityAttachment.CodeAttachment> codeAttachmentMap = simpleMultipleChoiceQuestion
                .getModalityAttachments()
                .stream()
                .filter(ModalityAttachment.CodeAttachment.class::isInstance)
                .map(ModalityAttachment.CodeAttachment.class::cast)
                .filter(codeAttachment -> codeAttachment.getAttachmentBase() != null)
                .collect(Collectors.toMap(
                        ModalityAttachment.CodeAttachment::getAttachmentBase, codeAttachment -> codeAttachment));
        //
        Map<String, String> bindingMap = simpleMultipleChoiceQuestion.getDdiBindings().stream()
                .collect(Collectors.toMap(Binding::getSourceParameterId, Binding::getTargetParameterId));
        //
        Map<String, CodeResponse> codeResponseMap = simpleMultipleChoiceQuestion.getCodeResponses().stream()
                .collect(Collectors.toMap(CodeResponse::getId, codeResponse -> codeResponse));
        //
        simpleMultipleChoiceQuestion.getModalityAttachments().stream()
                .filter(ModalityAttachment.DetailAttachment.class::isInstance)
                .map(ModalityAttachment.DetailAttachment.class::cast)
                .forEach(detailAttachment ->
                        resolveDetailResponse(simpleMultipleChoiceQuestion, detailAttachment,
                                codeAttachmentMap, codeResponseMap, bindingMap));
    }

    private static void resolveDetailResponse(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion,
                                              ModalityAttachment.DetailAttachment detailAttachment,
                                              Map<BigInteger, ModalityAttachment.CodeAttachment> codeAttachmentMap,
                                              Map<String, CodeResponse> codeResponseMap,
                                              Map<String, String> bindingMap) {

        // Get the mapped code response object that correspond to the detail field
        CodeResponse detailCodeResponse = codeResponseMap.get(bindingMap.get(detailAttachment.getResponseDomainId()));

        // Remove it from code responses list since detail fields doesn't belong there
        simpleMultipleChoiceQuestion.getCodeResponses().remove(detailCodeResponse);

        // Create the proper detail response object
        DetailResponse detailResponse = new DetailResponse();
        detailResponse.setLabel(detailAttachment.getLabel());
        detailResponse.setResponse(detailCodeResponse.getResponse());

        // Add the detail response to the question
        List<DetailResponse> detailResponses = simpleMultipleChoiceQuestion.getDetailResponses();
        detailResponses.add(detailResponse);

        // Attach the detail response to the code response it belongs
        ModalityAttachment.CodeAttachment codeAttachment = codeAttachmentMap.get(detailAttachment.getAttachmentDomain());
        CodeResponse codeResponse = codeResponseMap.get(bindingMap.get(codeAttachment.getResponseDomainId()));
        codeResponse.setDetailResponse(detailResponse);
    }

}
