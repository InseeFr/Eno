package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;

import java.util.Optional;

/**
 * Retrieve the lunatic shapeFrom attribute from eno variable groups
 * In a loop, the lunatic shapeFrom attribute of a calculated variable is the name of the first collected variable of the loop.
 * It permits for Lunatic to define the dimension the calculated variable should take in the loop.
 */
public class ShapefromAttributeRetrievalFromVariableGroups implements ShapefromAttributeRetrieval {

    /**
     *
     * @param lunaticCalculatedVariableName name of the calculated variable
     * @param enoQuestionnaire eno questionnaire
     * @return the first collected variable if the calculated variable is in a loop, nothing otherwise
     */
    public Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire) {
        // retrieve the variable group in which the variable is present, excluding the "Questionnaire"
        // variable group which does not corresponds to a "loop" variable group
        Optional<VariableGroup> variableGroup = enoQuestionnaire.getVariableGroups().stream()
                .filter(vGroup -> !vGroup.getType().equals("Questionnaire"))
                .filter(vGroup -> vGroup.getVariableByName(lunaticCalculatedVariableName).isPresent())
                .findFirst();

        return variableGroup.flatMap(
                        vGroup -> vGroup.getVariables().stream()
                                .filter(variable -> variable.getCollectionType().equals(Variable.CollectionType.COLLECTED))
                                .findFirst()
                );
    }
}
