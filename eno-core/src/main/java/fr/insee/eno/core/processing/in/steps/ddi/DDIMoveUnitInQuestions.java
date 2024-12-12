package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.navigation.Binding;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.question.table.NumericCell;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/** Processing step to move units which are mapped in variable objects to numeric questions. */
@Slf4j
public class DDIMoveUnitInQuestions implements ProcessingStep<EnoQuestionnaire> {

    private final EnoIndex enoIndex;

    public DDIMoveUnitInQuestions(EnoIndex enoIndex) {
        this.enoIndex = enoIndex;
    }

    /** In DDI, the 'unit' information is accessible in variables.
     * This information must also belong in concerned questions in the Eno model.
     * In Lunatic, this information is required in some numeric questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.COLLECTED.equals(variable.getCollectionType()))
                .map(CollectedVariable.class::cast)
                .filter(variable -> variable.getUnit() != null)
                .forEach(variable -> {
                    Question question = (Question) enoIndex.get(variable.getQuestionReference());
                    if (question instanceof NumericQuestion numericQuestion) {
                        numericQuestion.setUnit(createUnit(variable.getUnit()));
                        return;
                    }

                    if (question instanceof TableQuestion tableQuestion) {
                        applyUnitOnNumericCell(variable, tableQuestion.getBindings(), tableQuestion.getResponseCells());
                        return;
                    }

                    if (question instanceof DynamicTableQuestion rosterQuestion) {
                        applyUnitOnNumericCell(variable, rosterQuestion.getBindings(), rosterQuestion.getResponseCells());
                        return;
                    }

                    if (question instanceof ComplexMultipleChoiceQuestion mcqQuestion) {
                        applyUnitOnNumericCell(variable, mcqQuestion.getBindings(), mcqQuestion.getResponseCells());
                        return;
                    }
                    log.warn(String.format(
                            "Variable %s has a unit value '%s', and question reference '%s', " +
                                    "but question '%s' has not been identified as a numeric question. " +
                                    "This question will not have its unit set.",
                            variable, variable.getUnit(), variable.getQuestionReference(), question));
                });
    }

    /**
     * search for the table celle component linked to the variable and apply unit attribute on it
     * @param variable variable with unit parameter
     * @param bindings bindings of the question, link the variable to a table cell vie the source/target parameter id
     * @param responseCells table cells of the question
     */
    private void applyUnitOnNumericCell(Variable variable, List<Binding> bindings, List<ResponseCell> responseCells) {
        Optional<Binding> cellBinding = bindings.stream()
                .filter(binding -> binding.getTargetParameterId().equals(variable.getReference()))
                .findFirst();

        if(cellBinding.isEmpty()) {
            log.warn("Variable {} has unit attribute but no target parameter id", variable.getId());
            return;
        }

        Optional<NumericCell> numericCell = responseCells.stream()
                .filter(NumericCell.class::isInstance)
                .map(NumericCell.class::cast)
                .filter(nCell -> nCell.getId().equals(cellBinding.get().getSourceParameterId()))
                .findFirst();

        if(numericCell.isEmpty()) {
            log.warn("Numeric cell with source parameter id {} does not exist, it shouldn't happen", cellBinding.get().getSourceParameterId());
            return;
        }

        numericCell.get().setUnit(createUnit(variable.getUnit()));
    }

    public static DynamicLabel createUnit(String value) {
        if (value == null)
            return null;
        DynamicLabel label = new DynamicLabel();
        label.setValue(value);
        // (type is left at its default value)
        return label;
    }

}
