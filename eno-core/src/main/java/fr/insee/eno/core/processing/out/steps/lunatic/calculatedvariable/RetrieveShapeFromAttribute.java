package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;

import java.util.Optional;

public class RetrieveShapeFromAttribute {
    private RetrieveShapeFromAttribute() {
        throw new IllegalArgumentException("Utility class");
    }

    public static Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire) {
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
