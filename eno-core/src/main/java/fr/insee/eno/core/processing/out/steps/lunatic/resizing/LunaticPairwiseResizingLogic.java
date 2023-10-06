package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LunaticPairwiseResizingLogic {

    private final Questionnaire lunaticQuestionnaire;
    private final EnoIndex enoIndex;

    public LunaticPairwiseResizingLogic(Questionnaire lunaticQuestionnaire, EnoIndex enoIndex) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.enoIndex = enoIndex;
    }

    /**
     * Build resizing entries for a pairwise component.
     * @param pairwiseLinks Lunatic pairwise object.
     * @return list of resizing entries of the loop.
     */
    public List<LunaticResizingPairwiseEntry> buildPairwiseResizingEntries(PairwiseLinks pairwiseLinks) {

        // Corresponding Eno object
        PairwiseQuestion enoPairwiseQuestion = (PairwiseQuestion) enoIndex.get(pairwiseLinks.getId());
        if (enoPairwiseQuestion == null)
            throw new MappingException(String.format(
                    "Eno pairwise question corresponding to Lunatic pairwise object '%s' cannot be found.",
                    pairwiseLinks.getId()));

        // Variable names that are the keys of the resizing
        List<String> resizingVariableNames = findResizingVariablesForPairwise(pairwiseLinks, enoPairwiseQuestion);

        if (resizingVariableNames.isEmpty())
            return new ArrayList<>();

        // Expressions that resize the concerned variables
        // (Note: pairwise variables are 'two dimensions', that's why there are two expressions)
        List<String> sizeExpressions = List.of(
                pairwiseLinks.getXAxisIterations().getValue(), pairwiseLinks.getYAxisIterations().getValue());
        // Concerned variables to be resized
        Set<String> resizedVariableNames = Set.of(LunaticUtils.getPairwiseResponseVariable(pairwiseLinks));

        List<LunaticResizingPairwiseEntry> resizingPairwiseEntries = new ArrayList<>();
        resizingVariableNames.forEach(variableName ->
                resizingPairwiseEntries.add(
                        new LunaticResizingPairwiseEntry(variableName, sizeExpressions, resizedVariableNames)));

        return resizingPairwiseEntries;
    }

    private List<String> findResizingVariablesForPairwise(PairwiseLinks pairwiseLinks, PairwiseQuestion enoPairwiseQuestion) {
        // Source variable of the pairwise
        String pairwiseSourceVariableName = enoPairwiseQuestion.getLoopVariableName();
        // Find corresponding variable object
        Optional<IVariableType> correspondingVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> pairwiseSourceVariableName.equals(variable.getName()))
                .findAny();
        if (correspondingVariable.isEmpty())
            throw new LunaticPairwiseException(String.format(
                    "Source variable '%s' of pairwise component '%s' cannot be found in questionnaire variables.",
                    pairwiseSourceVariableName, pairwiseLinks));
        VariableType pairwiseSourceVariable = (VariableType) correspondingVariable.get();
        // If it not a calculated, simply return the variable name
        if (! VariableTypeEnum.CALCULATED.equals(correspondingVariable.get().getVariableType()))
            return List.of(pairwiseSourceVariableName);
        // Otherwise return its binding dependencies (without the calculated variable)
        return pairwiseSourceVariable.getBindingDependencies().stream()
                .filter(variableName -> !pairwiseSourceVariableName.equals(variableName))
                .toList();
    }

}
