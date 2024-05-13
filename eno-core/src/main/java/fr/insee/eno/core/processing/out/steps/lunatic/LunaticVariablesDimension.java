package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.variable.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>Processing step aimed to replace "shape from" and "variable values" steps.</p>
 * <ul>
 *     <li>The "shape from" from property can be replaced by the "iteration reference"
 *     that has been introduced in Lunatic variables.</li>
 *     <li>The "values/value" property represents the same information as the "dimension" does,
 *     so both can be done in the same step.</li>
 * </ul>
 */
public class LunaticVariablesDimension implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final Map<String, VariableType> lunaticVariables = new HashMap<>();

    public LunaticVariablesDimension(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    /**
     * For each variable of the questionnaire, sets the properties that are relative to the variable dimension, i.e.
     * "dimension", "iterationReference" (for non questionnaire-level variables), "values" (for collected variables),
     * "value" (for external variables).
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        lunaticQuestionnaire.getVariables().forEach(variableType ->
                lunaticVariables.put(variableType.getName(), variableType));
        //
        enoQuestionnaire.getVariableGroups().forEach(variableGroup -> {
            switch (variableGroup.getType()) {
                case VariableGroup.Type.QUESTIONNAIRE -> setQuestionnaireVariablesDimension(variableGroup);
                case VariableGroup.Type.LOOP -> setLoopVariablesDimension(variableGroup);
                case VariableGroup.Type.PAIRWISE_LINKS -> setPairwiseVariablesDimension(variableGroup);
                default -> throw new MappingException(String.format(
                        "Variable group '%s' has not its type set.", variableGroup.getName()));
            }
        });
    }

    private void setQuestionnaireVariablesDimension(VariableGroup questionnaireVariableGroup) {
        questionnaireVariableGroup.getVariables().stream().map(Variable::getName).forEach(variableName ->
                setScalarDimension(lunaticVariables.get(variableName)));
    }

    private void setLoopVariablesDimension(VariableGroup loopVariableGroup) {
        loopVariableGroup.getVariables().stream().map(Variable::getName).forEach(variableName ->
                setArrayDimension(lunaticVariables.get(variableName), loopVariableGroup.getLoopReferences().getFirst()));
    }

    private void setPairwiseVariablesDimension(VariableGroup pairwiseVariableGroup) {
        pairwiseVariableGroup.getVariables().stream().map(Variable::getName).forEach(variableName ->
                setDoubleArrayDimension(lunaticVariables.get(variableName), pairwiseVariableGroup.getLoopReferences().getFirst()));
    }

    private void setScalarDimension(VariableType variableType) {
        variableType.setDimension(VariableDimension.SCALAR);
        if (variableType instanceof CollectedVariableType collectedVariableType)
            collectedVariableType.setValues(new CollectedVariableValues.Scalar());
        if (variableType instanceof ExternalVariableType externalVariableType)
            externalVariableType.setValue(new ExternalVariableValue.Scalar());
    }

    private void setArrayDimension(VariableType variableType, String iterationId) {
        variableType.setDimension(VariableDimension.ARRAY);
        variableType.setIterationReference(iterationId);
        if (variableType instanceof CollectedVariableType collectedVariableType)
            collectedVariableType.setValues(new CollectedVariableValues.Array());
        if (variableType instanceof ExternalVariableType externalVariableType)
            externalVariableType.setValue(new ExternalVariableValue.Array());
    }

    private void setDoubleArrayDimension(VariableType variableType, String pairwiseQuestionId) {
        variableType.setDimension(VariableDimension.DOUBLE_ARRAY);
        variableType.setIterationReference(pairwiseQuestionId);
        if (variableType instanceof CollectedVariableType collectedVariableType)
            collectedVariableType.setValues(new CollectedVariableValues.DoubleArray());
        if (variableType instanceof ExternalVariableType externalVariableType)
            externalVariableType.setValue(new ExternalVariableValue.DoubleArray());
    }

}
