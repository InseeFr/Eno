package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.variable.*;

import java.util.*;

/**
 * Processing that sets the variable properties that are relative to their dimension,
 * i.e. the "dimension" and "iteration reference" properties, plus values/value for collected/external variables.
 * The "shape from" from property (and thus the "shape from" processing step) could be removed later on.
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
        enoQuestionnaire.getVariableGroups().stream().sorted(variableGroupComparator)
                .forEach(variableGroup -> {
                    switch (variableGroup.getType()) {
                        case VariableGroup.Type.QUESTIONNAIRE -> setQuestionnaireVariablesDimension(variableGroup);
                        case VariableGroup.Type.LOOP -> setLoopVariablesDimension(variableGroup);
                        case VariableGroup.Type.PAIRWISE_LINKS -> setPairwiseVariablesDimension(variableGroup);
                        default -> throw new MappingException(String.format(
                                "Variable group '%s' has not its type set.", variableGroup.getName()));
                    }
        });
    }

    /**
     * Implement Java Comparator for VariableGroup
     * The goal is to do the treatment of PairwiseLink at the end during a stream of VariableGroup
     */
    public static final Comparator<VariableGroup> variableGroupComparator = (variableGroup1, variableGroup2) -> {
        if(VariableGroup.Type.PAIRWISE_LINKS.equals(variableGroup1.getType()) && VariableGroup.Type.PAIRWISE_LINKS.equals(variableGroup2.getType())) return 0;
        if(VariableGroup.Type.PAIRWISE_LINKS.equals(variableGroup1.getType())) return 1;
        if(VariableGroup.Type.PAIRWISE_LINKS.equals(variableGroup2.getType())) return -1;
        return 0;
    };

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
