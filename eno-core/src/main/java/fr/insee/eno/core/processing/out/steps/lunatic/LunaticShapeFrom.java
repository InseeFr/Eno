package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;

import java.util.List;
import java.util.Optional;

/**
 * Processing step to set the "shape from" property of Lunatic calculated variables.
 * Needs to be called <b>after</b> the "dimension" processing, since it uses the "iteration reference" property.
 */
public class LunaticShapeFrom implements ProcessingStep<Questionnaire> {

    private Questionnaire lunaticQuestionnaire;

    /**
     * Sets the "shape from" property on calculated variables of the current questionaire.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        //
        lunaticQuestionnaire.getVariables().stream()
                .filter(CalculatedVariableType.class::isInstance)
                .map(CalculatedVariableType.class::cast)
                .filter(calculatedVariableType -> calculatedVariableType.getIterationReference() != null)
                .forEach(this::setShapeFromVariables);
    }

    private void setShapeFromVariables(CalculatedVariableType calculatedVariable) {
        Optional<ComponentType> iterationComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> calculatedVariable.getIterationReference().equals(component.getId()))
                .findAny();
        if (iterationComponent.isEmpty())
            throw new MappingException(String.format(
                    "Unable to find iteration reference '%s' of calculated variable '%s'.",
                    calculatedVariable.getIterationReference(), calculatedVariable.getName()));
        List<String> responseNames = LunaticUtils.getResponseNames(iterationComponent.get());
        calculatedVariable.setShapeFrom(responseNames);
    }

}
