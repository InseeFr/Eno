package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DDIMoveUnitInQuestions implements ProcessingStep<EnoQuestionnaire> {

    // TODO: JavaDoc on method or on class?

    /** In DDI, the 'unit' information is accessible in variables.
     * This information must also belong in concerned questions in the Eno model.
     * In Lunatic, this information is required in some numeric questions. */
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        // TODO: assert or proper log + exception?
        assert enoQuestionnaire.getIndex() != null;
        //
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> Variable.CollectionType.COLLECTED.equals(variable.getCollectionType()))
                .map(CollectedVariable.class::cast)
                .filter(variable -> variable.getUnit() != null)
                .forEach(variable -> {
                    Question question = (Question) enoQuestionnaire.get(variable.getQuestionReference());
                    if (! (question instanceof NumericQuestion)) {
                        log.warn(String.format(
                                "Variable %s has a unit value '%s', and question reference '%s', " +
                                        "but question '%s' has not been identified as a numeric question. " +
                                        "This question will not have its unit set.",
                                variable, variable.getUnit(), variable.getQuestionReference(), question));
                    } else {
                        ((NumericQuestion) question).setUnit(variable.getUnit());
                    }
                });
    }
}
