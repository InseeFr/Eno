package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.EnoTable;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
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

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // Put code lists in a local map
        enoQuestionnaire.getCodeLists().forEach(codeList -> codeListMap.put(codeList.getId(), codeList));
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
        // Insert code lists in header of tables
        enoTables.forEach(this::insertHeader);
        // Only table question objects have a left column
        enoTables.stream()
                .filter(TableQuestion.class::isInstance)
                .map(TableQuestion.class::cast)
                .forEach(this::insertLeftColumn);
        // Insert code lists in table cells that are a unique choice question
        enoTables.forEach(enoTable ->
                enoTable.getTableCells().stream()
                        .filter(UniqueChoiceCell.class::isInstance)
                        .map(UniqueChoiceCell.class::cast)
                        .forEach(this::insertCodeItems));
    }

    private void insertCodeItems(UniqueChoiceQuestion uniqueChoiceQuestion) {
        uniqueChoiceQuestion.setCodeItems(codeListMap.get(uniqueChoiceQuestion.getCodeListReference()).getCodeItems());
    }

    private void insertCodeItems(UniqueChoiceCell uniqueChoiceCell) {
        uniqueChoiceCell.setCodeItems(codeListMap.get(uniqueChoiceCell.getCodeListReference()).getCodeItems());
    }

    private void insertHeader(EnoTable enoTable) {
        enoTable.setHeader(codeListMap.get(enoTable.getHeaderCodeListReference()));
    }

    private void insertLeftColumn(TableQuestion tableQuestion) {
        tableQuestion.setLeftColumn(codeListMap.get(tableQuestion.getLeftColumnCodeListReference()));
    }

}
