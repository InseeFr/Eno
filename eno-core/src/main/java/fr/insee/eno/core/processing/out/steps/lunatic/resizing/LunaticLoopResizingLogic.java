package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.IVariableType;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class LunaticLoopResizingLogic {

    private final Questionnaire lunaticQuestionnaire;
    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;

    public LunaticLoopResizingLogic(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire, EnoIndex enoIndex) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoIndex;
    }

    /**
     * Build resizing entries for a loop.
     * @param lunaticLoop Lunatic loop object.
     * @return list of resizing entries of the loop.
     */
    public List<LunaticResizingEntry> buildResizingEntries(Loop lunaticLoop) {

        // Corresponding Eno loop object
        fr.insee.eno.core.model.navigation.Loop enoLoop = (fr.insee.eno.core.model.navigation.Loop)
                enoIndex.get(lunaticLoop.getId());
        if (enoLoop == null)
            throw new MappingException(String.format(
                    "Eno loop object corresponding to Lunatic loop '%s' cannot be found.", lunaticLoop.getId()));

        // Variable names that are the keys of the resizing
        List<String> resizingVariableNames = new ArrayList<>();
        // Expression that resize the concerned variables
        String sizeExpression = null;

        if (enoLoop instanceof StandaloneLoop enoStandaloneLoop) {
            resizingVariableNames.addAll(findResizingVariablesForLoop(enoStandaloneLoop));
            sizeExpression = lunaticLoop.getLines().getMax().getValue();
        }
        if (enoLoop instanceof LinkedLoop enoLinkedLoop){
            resizingVariableNames.add(findResizingVariableForLinkedLoop(enoLinkedLoop));
            sizeExpression = lunaticLoop.getIterations().getValue();
        }

        if (resizingVariableNames.isEmpty())
            return new ArrayList<>();

        // Concerned variables to be resized
        // Note: external variables are not concerned since their values are not designed to be changed dynamically
        Set<String> resizedVariableNames = LunaticUtils.getCollectedVariablesInLoop(lunaticLoop);

        List<LunaticResizingEntry> resizingLoopEntries = new ArrayList<>();
        String finalSizeExpression = sizeExpression; // (due to usage in lambda)
        resizingVariableNames.forEach(variableName ->
                resizingLoopEntries.add(
                        new LunaticResizingEntry(variableName, finalSizeExpression, resizedVariableNames)));

        return resizingLoopEntries;
    }

    private List<String> findResizingVariablesForLoop(StandaloneLoop enoLoop) {
        List<String> maxIterationDependencies = enoLoop.getMaxIteration().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .toList();
        return lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> VariableTypeEnum.COLLECTED.equals(variable.getVariableType()))
                .map(IVariableType::getName)
                .filter(maxIterationDependencies::contains)
                .toList();
    }

    /** For a linked loop, the resizing variable is the first variable of its reference/main loop
     * (this implicit rule strikes again here...). */
    private String findResizingVariableForLinkedLoop(LinkedLoop enoLinkedLoop) {
        // Find the reference/main loop of the linked loop
        Optional<StandaloneLoop> referenceLoop = enoQuestionnaire.getLoops().stream()
                .filter(StandaloneLoop.class::isInstance)
                .map(StandaloneLoop.class::cast)
                .filter(standaloneLoop -> enoLinkedLoop.getReference().equals(standaloneLoop.getId()))
                .findAny();
        if (referenceLoop.isEmpty())
            throw new MappingException(String.format(
                    "Unable to find the reference loop '%s' of linked loop '%s'",
                    enoLinkedLoop.getReference(), enoLinkedLoop.getId()));
        // Return the variable name of its first question (reusing some code from lunatic loop processing)
        return LunaticLoopResolution.findFirstVariableOfReference(enoLinkedLoop, referenceLoop.get(), enoIndex);
    }

}
