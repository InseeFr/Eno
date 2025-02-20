package fr.insee.eno.core.reference;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.EnoLabel;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.question.table.NoDataCell;
import fr.insee.eno.core.model.question.table.NumericCell;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.variable.Variable;
import lombok.Getter;

import java.util.*;

/** Class designed to be used in processing to easily access different kinds of Eno objects. */
public class EnoCatalog {

    /** Map of sequences stored by id. */
    Map<String, Sequence> sequenceMap = new HashMap<>();
    /** Map of subsequences stored by id. */
    Map<String, Subsequence> subsequenceMap = new HashMap<>();
    /** Map of roundabout sequences stored by id. */
    Map<String, RoundaboutSequence> roundaboutSequenceMap = new HashMap<>();
    /** Map of questions stored by id. */
    Map<String, Question> questionMap = new HashMap<>();
    /** Map of Eno components stored by id. */
    Map<String, EnoComponent> componentMap = new HashMap<>();
    /** Map of collected variables stored by their reference (warning: keys = not ids). */
    Map<String, Variable> variableMap = new HashMap<>();
    /** List with all labels that are in the questionnaire. */
    @Getter
    private final Collection<EnoLabel> labels = new ArrayDeque<>();
    // NB: https://stackoverflow.com/questions/6129805/what-is-the-fastest-java-collection-with-the-basic-functionality-of-a-queue

    public EnoCatalog(EnoQuestionnaire enoQuestionnaire) {
        complementaryIndexing(enoQuestionnaire);
    }

    /** Holdall method to fill some useful maps / collections. */
    private void complementaryIndexing(EnoQuestionnaire enoQuestionnaire) {
        // Questionnaire components (sequences, subsequences and questions)
        enoQuestionnaire.getSequences().forEach(sequence -> sequenceMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> subsequenceMap.put(subsequence.getId(), subsequence));
        enoQuestionnaire.getRoundaboutSequences().forEach(roundaboutSequence -> roundaboutSequenceMap.put(roundaboutSequence.getId(), roundaboutSequence));
        enoQuestionnaire.getSingleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoQuestionnaire.getMultipleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        componentMap.putAll(sequenceMap);
        componentMap.putAll(subsequenceMap);
        componentMap.putAll(roundaboutSequenceMap);
        componentMap.putAll(questionMap);
        // Labels
        gatherLabels(enoQuestionnaire);
        // Variables
        enoQuestionnaire.getVariables().forEach(variable -> variableMap.put(variable.getReference(), variable));
    }

    public Sequence getSequence(String sequenceId) {
        return sequenceMap.get(sequenceId);
    }
    public Subsequence getSubsequence(String subsequenceId) {
        return subsequenceMap.get(subsequenceId);
    }
    public Question getQuestion(String questionId) {
        return questionMap.get(questionId);
    }
    public EnoComponent getComponent(String componentId) {
        return componentMap.get(componentId);
    }
    public Variable getVariable(String variableReference) {
        return variableMap.get(variableReference);
    }

    public Collection<Question> getQuestions() {
        return questionMap.values();
    }
    public Collection<Variable> getVariables() {
        return variableMap.values();
    }
    public Collection<EnoComponent> getComponents() {
        return componentMap.values();
    }

    private void gatherLabels(EnoQuestionnaire enoQuestionnaire) {
        // Sequences and subsequences
        labels.addAll(enoQuestionnaire.getSequences().stream().map(AbstractSequence::getLabel).toList());
        labels.addAll(enoQuestionnaire.getSubsequences().stream().map(AbstractSequence::getLabel).toList());
        labels.addAll(enoQuestionnaire.getRoundaboutSequences().stream().map(AbstractSequence::getLabel).toList());
        // Questions
        labels.addAll(this.getQuestions().stream().map(Question::getLabel).filter(Objects::nonNull).toList());
        // Declarations, instructions and controls within components
        this.getComponents().forEach(enoComponent -> {
            labels.addAll(enoComponent.getDeclarations().stream().map(Declaration::getLabel).toList());
            labels.addAll(enoComponent.getInstructions().stream().map(Instruction::getLabel).toList());
        });
        // Units
        enoQuestionnaire.getSingleResponseQuestions().stream()
                .filter(NumericQuestion.class::isInstance).map(NumericQuestion.class::cast)
                .map(NumericQuestion::getUnit).filter(Objects::nonNull)
                .forEach(labels::add);
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(question -> question instanceof TableQuestion || question instanceof DynamicTableQuestion)
                .map(EnoTable.class::cast)
                .map(EnoTable::getResponseCells).forEach(responseCells -> responseCells.stream()
                        .filter(NumericCell.class::isInstance).map(NumericCell.class::cast)
                        .map(NumericCell::getUnit).filter(Objects::nonNull)
                        .forEach(labels::add));
        // Controls
        this.getQuestions().forEach(enoQuestion ->
                labels.addAll(enoQuestion.getControls().stream().map(Control::getMessage).toList()));
        enoQuestionnaire.getRoundaboutSequences().forEach(roundaboutSequence ->
                labels.addAll(roundaboutSequence.getControls().stream()
                        .map(Control::getMessage).filter(Objects::nonNull).toList()));
        // Cell label in no data cells
        enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(question -> question instanceof TableQuestion || question instanceof DynamicTableQuestion)
                .map(EnoTable.class::cast).map(EnoTable::getNoDataCells).forEach(this::gatherLabelsFromNoDataCells);
        // Code lists
        enoQuestionnaire.getCodeLists().stream().map(CodeList::getCodeItems).forEach(this::gatherLabelsFromCodeItems);
        enoQuestionnaire.getFakeCodeLists().stream().map(CodeList::getCodeItems).forEach(this::gatherLabelsFromCodeItems);
    }

    private void gatherLabelsFromCodeItems(List<CodeItem> codeItems) {
        for (CodeItem codeItem : codeItems) {
            labels.add(codeItem.getLabel());
            // Recursive call in case of nested code items
            gatherLabelsFromCodeItems(codeItem.getCodeItems());
        }
    }

    private void gatherLabelsFromNoDataCells(List<NoDataCell> noDataCells) {
        for (NoDataCell noDataCell : noDataCells) {
            labels.add(noDataCell.getCellLabel());
        }
    }

}
