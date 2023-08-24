package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;

import java.util.Optional;

public interface ShapefromAttributeRetrieval {
    Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire);
}
