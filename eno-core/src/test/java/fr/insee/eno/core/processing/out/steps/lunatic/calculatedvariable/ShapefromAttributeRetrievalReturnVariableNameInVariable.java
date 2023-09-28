package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.Variable;

import java.util.Optional;

public class ShapefromAttributeRetrievalReturnVariableNameInVariable implements ShapefromAttributeRetrieval {
    @Override
    public Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire) {
        Variable variable = new CalculatedVariable();
        variable.setName(lunaticCalculatedVariableName);
        return Optional.of(variable);
    }
}
