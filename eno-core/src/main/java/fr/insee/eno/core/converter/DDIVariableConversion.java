package fr.insee.eno.core.converter;

import fr.insee.ddi.lifecycle33.logicalproduct.VariableType;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.ExternalVariable;

public class DDIVariableConversion {

    private DDIVariableConversion() {}

    /** <p>In "Insee" DDI:</p>
     * <ul>
     *   <li>collected variables are characterized by having a "question reference"</li>
     *   <li>calculated variables are characterized by having a "processing instruction reference"
     *   in their "variable representation"</li>
     *   <li>external variables have no specific characteristics so these are the remaining cases. </li>
     * </ul>
     * */
    static EnoObject instantiateFrom(VariableType variableType) {
        if (isCollectedVariable(variableType))
            return new CollectedVariable();
        if (isCalculatedVariable(variableType))
            return new CalculatedVariable();
        return new ExternalVariable();
    }

    private static boolean isCollectedVariable(VariableType ddiVariable) {
        return ! ddiVariable.getQuestionReferenceList().isEmpty();
    }

    private static boolean isCalculatedVariable(VariableType ddiVariable) {
        return ddiVariable.getVariableRepresentation() != null &&
                ddiVariable.getVariableRepresentation().getProcessingInstructionReference() != null;
    }

}
