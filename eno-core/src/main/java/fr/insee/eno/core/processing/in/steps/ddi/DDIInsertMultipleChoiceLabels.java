package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.SimpleMultipleChoiceQuestion;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.HashMap;
import java.util.Map;

/**
 * Processing class to insert the label of modalities in the code responses of multiple choice questions.
 * Warning: This processing must be called after the processing that resolves detail response of modalities.
 * The reason is that in DDI modeling, the link between MCQ modalities and the code list is based on the ordering,
 * and that detail responses are described as additional code responses in DDI, so they need to be cleaned up.
 * @see DDIInsertDetailResponses
 */
public class DDIInsertMultipleChoiceLabels implements ProcessingStep<EnoQuestionnaire> {

    private final Map<String, CodeList> codeListMap = new HashMap<>();

    /**
     * Sets code response labels of multiple choice questions, using the referenced code list.
     * @param enoQuestionnaire Eno questionnaire.
     */
    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getCodeLists().forEach(codeList -> codeListMap.put(codeList.getId(), codeList));
        //
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(SimpleMultipleChoiceQuestion.class::isInstance)
                .map(SimpleMultipleChoiceQuestion.class::cast)
                .forEach(this::insertModalityLabels);
    }

    private void insertModalityLabels(SimpleMultipleChoiceQuestion simpleMultipleChoiceQuestion) {
        CodeList codeList = codeListMap.get(simpleMultipleChoiceQuestion.getCodeListReference());
        for (int i = 0; i < codeList.size(); i ++) {
            CodeItem codeItem = codeList.getCodeItems().get(i);
            CodeResponse codeResponse = simpleMultipleChoiceQuestion.getCodeResponses().get(i);
            codeResponse.setLabel(codeItem.getLabel());
        }
    }

}
