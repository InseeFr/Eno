package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.processing.InProcessingInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** In DDI, code lists are mapped at the questionnaire level. In other places, only the reference of the code list
 * is mapped. This processing insert code lists in these places. */
public class DDIInsertCodeLists implements InProcessingInterface {

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
        // Gather table question objects
        List<TableQuestion> tableQuestions = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(TableQuestion.class::isInstance)
                .map(TableQuestion.class::cast)
                .toList();
        // Insert code lists in header and left column properties of table questions
        tableQuestions.forEach(this::insertHeader);
        tableQuestions.forEach(this::insertLeftColumn);
        // Insert code lists in table cells that are a unique choice question
        tableQuestions.forEach(tableQuestion ->
                tableQuestion.getTableCells().stream()
                        .filter(TableCell.UniqueChoiceCell.class::isInstance)
                        .map(TableCell.UniqueChoiceCell.class::cast)
                        .forEach(this::insertCodeItems));
    }

    private void insertCodeItems(UniqueChoiceQuestion uniqueChoiceQuestion) {
        uniqueChoiceQuestion.setCodeItems(codeListMap.get(uniqueChoiceQuestion.getCodeListReference()).getCodeItems());
    }

    private void insertCodeItems(TableCell.UniqueChoiceCell uniqueChoiceCell) {
        uniqueChoiceCell.setCodeItems(codeListMap.get(uniqueChoiceCell.getCodeListReference()).getCodeItems());
    }

    private void insertHeader(TableQuestion tableQuestion) {
        tableQuestion.setHeader(codeListMap.get(tableQuestion.getHeaderCodeListReference()));
    }

    private void insertLeftColumn(TableQuestion tableQuestion) {
        tableQuestion.setLeftColumn(codeListMap.get(tableQuestion.getLeftColumnCodeListReference()));
    }

}
