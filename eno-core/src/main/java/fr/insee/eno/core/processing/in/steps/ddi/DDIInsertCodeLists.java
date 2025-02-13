package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.question.table.UniqueChoiceCell;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** In DDI, code lists are mapped at the questionnaire level. In other places, only the reference of the code list
 * is mapped. This processing insert code lists in these places. */
public class DDIInsertCodeLists implements ProcessingStep<EnoQuestionnaire> {

    private final Map<String, CodeList> codeListMap = new HashMap<>();
    private final Map<String, CodeList> fakeCodeListMap = new HashMap<>();

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Put code lists in a local map
        enoQuestionnaire.getCodeLists().forEach(codeList -> codeListMap.put(codeList.getId(), codeList));
        enoQuestionnaire.getFakeCodeLists().forEach(codeList -> fakeCodeListMap.put(codeList.getId(), codeList));
        // Insert code lists in unique choice questions
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(UniqueChoiceQuestion.class::isInstance)
                .map(UniqueChoiceQuestion.class::cast)
                .forEach(this::insertCodeItems);

        // Insert code lists in pairwise questions
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(PairwiseQuestion.class::isInstance)
                .map(PairwiseQuestion.class::cast)
                .map(PairwiseQuestion::getUniqueChoiceQuestions)
                .flatMap(Collection::stream)
                .forEach(this::insertCodeItems);

        // Gather table question objects
        List<EnoTable> enoTables = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(EnoTable.class::isInstance)
                .map(EnoTable.class::cast)
                .toList();
        // Gather complex multiple choice question objects (that end up being tables in Lunatic for instance)
        List<ComplexMultipleChoiceQuestion> enoComplexMCQList = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(ComplexMultipleChoiceQuestion.class::isInstance)
                .map(ComplexMultipleChoiceQuestion.class::cast)
                .toList();

        // Insert code lists in header of tables
        enoTables.forEach(this::insertHeader);
        // Insert code lists in left column of tables and complex multiple choice questions
        enoTables.stream()
                .filter(TableQuestion.class::isInstance)
                .map(TableQuestion.class::cast)
                .forEach(this::insertLeftColumn);
        enoComplexMCQList.forEach(this::insertLeftColumn);
        // Insert code lists in table cells that are a unique choice question
        enoTables.forEach(enoTable ->
                enoTable.getResponseCells().stream()
                        .filter(UniqueChoiceCell.class::isInstance)
                        .map(UniqueChoiceCell.class::cast)
                        .forEach(this::insertCodeItems));
        enoComplexMCQList.forEach(enoComplexMCQ -> enoComplexMCQ.getResponseCells().stream()
                .map(UniqueChoiceCell.class::cast) // No filtering here since in complex MCQ cells are always UCQ
                .forEach(this::insertCodeItems));
    }

    /**
     * Return the code list that is referenced in the Eno component (such as unique choice question, unique choice
     * table cell or else) with given id, using the local code list map of this class.
     * Throws a runtime exception if there is something null along the way.
     * @param codeListReference Code list referenced in the Eno component.
     * @param enoComponentId Eno component id.
     * @return The corresponding (non-null) code list.
     */
    private CodeList getCodeListFromMap(String codeListReference, String enoComponentId) {
        if (codeListReference == null)
            return null;
            // TODO: restore this exception when Pogues table mapping is done
            /*throw new MappingException(String.format(
                    "Eno component '%s' has no referenced code list.",
                    enoComponentId));*/
        CodeList searchedCodeList = codeListMap.get(codeListReference);
        if (searchedCodeList == null)
            searchedCodeList = fakeCodeListMap.get(codeListReference);
        if (searchedCodeList == null)
            throw new MappingException(String.format(
                    "Code list referenced in Eno component '%s' with id '%s' cannot be found.",
                    enoComponentId, codeListReference));
        return searchedCodeList;
    }

    private void insertCodeItems(UniqueChoiceQuestion uniqueChoiceQuestion) {
        CodeList codeList = getCodeListFromMap(uniqueChoiceQuestion.getCodeListReference(), uniqueChoiceQuestion.getId());
        if (codeList == null)
            return;
        uniqueChoiceQuestion.setCodeItems(codeList.getCodeItems());
    }

    private void insertCodeItems(UniqueChoiceCell uniqueChoiceCell) {
        CodeList codeList = getCodeListFromMap(uniqueChoiceCell.getCodeListReference(), uniqueChoiceCell.getId());
        if (codeList == null)
            return;
        uniqueChoiceCell.setCodeItems(codeList.getCodeItems());
    }

    private void insertHeader(EnoTable enoTable) {
        String id = enoTable.getId();
        enoTable.setHeader(getCodeListFromMap(enoTable.getHeaderCodeListReference(), id));
    }

    private void insertLeftColumn(TableQuestion tableQuestion) {
        String id = tableQuestion.getId();
        tableQuestion.setLeftColumn(getCodeListFromMap(tableQuestion.getLeftColumnCodeListReference(), id));
    }

    private void insertLeftColumn(ComplexMultipleChoiceQuestion complexMultipleChoiceQuestion) {
        String id = complexMultipleChoiceQuestion.getId();
        complexMultipleChoiceQuestion.setLeftColumn(
                getCodeListFromMap(complexMultipleChoiceQuestion.getLeftColumnCodeListReference(), id));
    }

}
