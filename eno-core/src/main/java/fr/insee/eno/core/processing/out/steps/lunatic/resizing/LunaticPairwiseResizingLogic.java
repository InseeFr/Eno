package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.LinkedHashSet;
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
     * Insert resizing entries for the given pairwise component.
     * @param pairwiseLinks Lunatic pairwise object.
     * @param lunaticResizing Lunatic resizing block object.
     */
    public void buildPairwiseResizingEntries(PairwiseLinks pairwiseLinks, ResizingType lunaticResizing) {

        // Corresponding Eno object
        PairwiseQuestion enoPairwiseQuestion = (PairwiseQuestion) enoIndex.get(pairwiseLinks.getId());
        if (enoPairwiseQuestion == null)
            throw new MappingException(String.format(
                    "Eno pairwise question corresponding to Lunatic pairwise object '%s' cannot be found.",
                    pairwiseLinks.getId()));

        // Variable names that are the keys of the resizing (using a set to make sure there is no duplicates)
        Set<String> resizingVariableNames = findResizingVariablesForPairwise(pairwiseLinks, enoPairwiseQuestion);
        // Expressions that resize the concerned (pairwise) variable
        String xSizeExpression = pairwiseLinks.getXAxisIterations().getValue();
        String ySizeExpression = pairwiseLinks.getYAxisIterations().getValue();

        if (resizingVariableNames.isEmpty())
            return;

        // Concerned (pairwise) variable to be resized
        String pairwiseVariableName = LunaticUtils.getPairwiseResponseVariable(pairwiseLinks);

        resizingVariableNames.forEach(variableName -> insertPairwiseEntry(
                lunaticResizing, variableName, xSizeExpression, ySizeExpression, pairwiseVariableName));
    }

    private Set<String> findResizingVariablesForPairwise(PairwiseLinks pairwiseLinks, PairwiseQuestion enoPairwiseQuestion) {
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
        // If it not a calculated, simply return the variable name
        if (! VariableTypeEnum.CALCULATED.equals(correspondingVariable.get().getVariableType()))
            return Set.of(pairwiseSourceVariableName);
        // Otherwise return its binding dependencies (without the calculated variable)
        VariableType pairwiseSourceVariable = (VariableType) correspondingVariable.get();
        return new LinkedHashSet<>(pairwiseSourceVariable.getBindingDependencies().stream()
                .filter(variableName -> !pairwiseSourceVariableName.equals(variableName))
                .toList());
    }

    /**
     * Insert or update a pairwise resizing entry for the given resizing variable name.
     * @param lunaticResizing Lunatic resizing block object.
     * @param resizingVariableName Name of a resizing variable.
     * @param xSizeExpression Expression of the pairwise 'xAxis' size.
     * @param ySizeExpression Expression of the pairwise 'yAxis' size.
     * @param linksVariableName Pairwise links variable name.
     */
    private static void insertPairwiseEntry(ResizingType lunaticResizing,
                                            String resizingVariableName, String xSizeExpression, String ySizeExpression,
                                            String linksVariableName) {
        // If no entry for the resizing variable name given, create it
        if (lunaticResizing.getResizingEntry(resizingVariableName) == null) {
            ResizingPairwiseEntry resizingPairwiseEntry = new ResizingPairwiseEntry();
            resizingPairwiseEntry.getSizeForLinksVariables().add(xSizeExpression);
            resizingPairwiseEntry.getSizeForLinksVariables().add(ySizeExpression);
            resizingPairwiseEntry.getLinksVariables().add(linksVariableName);
            lunaticResizing.putResizingEntry(resizingVariableName, resizingPairwiseEntry);
            return;
        }
        // If there is an iteration entry, convert it to a pairwise resizing entry and update it
        ResizingEntry resizingEntry = lunaticResizing.getResizingEntry(resizingVariableName);
        if (resizingEntry instanceof ResizingIterationEntry) {
            ResizingEntry previousEntry = lunaticResizing.removeResizingEntry(resizingVariableName);
            ResizingPairwiseEntry resizingPairwiseEntry = new ResizingPairwiseEntry();
            resizingPairwiseEntry.setSize(previousEntry.getSize());
            resizingPairwiseEntry.getVariables().addAll(previousEntry.getVariables());
            resizingPairwiseEntry.getSizeForLinksVariables().add(xSizeExpression);
            resizingPairwiseEntry.getSizeForLinksVariables().add(ySizeExpression);
            resizingPairwiseEntry.getLinksVariables().add(linksVariableName);
            lunaticResizing.putResizingEntry(resizingVariableName, resizingPairwiseEntry);
        }
        // If there is already a pairwise resizing entry for this resizing variable name,
        // it means that several pairwise questions were encountered
        // (should not happen, but you never know)
        if (resizingEntry instanceof ResizingPairwiseEntry)
            throw new LunaticPairwiseException("Having several pairwise links question is not authorized.");
    }

}
