package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.ExternalVariable;
import fr.insee.pogues.model.CalculatedVariableType;
import fr.insee.pogues.model.CollectedVariableType;
import fr.insee.pogues.model.ExternalVariableType;
import fr.insee.pogues.model.VariableType;

class PoguesVariableConversion {

    private PoguesVariableConversion() {}

    static EnoObject instantiateFrom(VariableType poguesVariable) {
        if (poguesVariable instanceof CollectedVariableType)
            return new CollectedVariable();
        if (poguesVariable instanceof ExternalVariableType)
            return new ExternalVariable();
        if (poguesVariable instanceof CalculatedVariableType)
            return new CalculatedVariable();
        throw new ConversionException("Unknown variable type: " + poguesVariable.getClass().getSimpleName());
    }

}
