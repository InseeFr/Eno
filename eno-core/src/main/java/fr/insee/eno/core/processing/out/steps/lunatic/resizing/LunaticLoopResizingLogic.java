package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.business.LunaticLogicException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.VariableType;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
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
     * Insert resizing entries for the given loop.
     * @param lunaticLoop Lunatic loop object.
     * @param lunaticResizing Lunatic resizing block object.
     */
    public void buildResizingEntries(Loop lunaticLoop, ResizingType lunaticResizing) {

        // Corresponding Eno loop object
        fr.insee.eno.core.model.navigation.Loop enoLoop = (fr.insee.eno.core.model.navigation.Loop)
                enoIndex.get(lunaticLoop.getId());
        if (enoLoop == null)
            throw new MappingException(String.format(
                    "Eno loop object corresponding to Lunatic loop '%s' cannot be found.", lunaticLoop.getId()));

        // Variable names that are the keys of the resizing (using a set to make sure there is no duplicates)
        Set<String> resizingVariableNames = new LinkedHashSet<>();
        // Expression that resize the concerned variables
        String sizeExpression = null;

        if (enoLoop instanceof StandaloneLoop enoStandaloneLoop) {
            resizingVariableNames.addAll(findResizingVariablesForLoop(enoStandaloneLoop));
            sizeExpression = lunaticLoop.getLines().getMax().getValue();
        }
        if (enoLoop instanceof LinkedLoop enoLinkedLoop) {
            resizingVariableNames.add(findResizingVariableForLinkedLoop(enoLinkedLoop));
            sizeExpression = lunaticLoop.getIterations().getValue();
        }

        if (resizingVariableNames.isEmpty())
            return;

        // Concerned variables to be resized
        // Note: external variables are not concerned since their values are not designed to be changed dynamically
        List<String> resizedVariableNames = LunaticUtils.getCollectedVariablesInLoop(lunaticLoop);

        String finalSizeExpression = sizeExpression; // (due to usage in lambda)
        resizingVariableNames.forEach(variableName -> insertIterationEntry(
                lunaticResizing, variableName, finalSizeExpression, resizedVariableNames));
    }

    private List<String> findResizingVariablesForLoop(StandaloneLoop enoLoop) {
        List<String> maxIterationDependencies = enoLoop.getMaxIteration().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .toList();
        return lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> VariableTypeEnum.COLLECTED.equals(variable.getVariableType()))
                .map(VariableType::getName)
                .filter(maxIterationDependencies::contains)
                .toList();
    }

    /** For a linked loop, the resizing variable is the first variable of its reference/main loop
     * or the first variable of the dynamic table it is linked with
     * (this implicit rule strikes again here...). */
    private String findResizingVariableForLinkedLoop(LinkedLoop enoLinkedLoop) {

        // Find the reference/main loop of the linked loop
        Optional<StandaloneLoop> referenceLoop = enoQuestionnaire.getLoops().stream()
                .filter(StandaloneLoop.class::isInstance)
                .map(StandaloneLoop.class::cast)
                .filter(standaloneLoop -> enoLinkedLoop.getReference().equals(standaloneLoop.getId()))
                .findAny();
        if (referenceLoop.isPresent())
            // Return the variable name of its first question (reusing some code from lunatic loop processing)
            return LunaticLoopResolution.findFirstVariableOfReference(enoLinkedLoop, referenceLoop.get(), enoIndex);

        // Otherwise find the dynamic table on which the loop is linked
        Optional<DynamicTableQuestion> referenceTable = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .filter(dynamicTableQuestion -> enoLinkedLoop.getReference().equals(dynamicTableQuestion.getId()))
                .findAny();
        if (referenceTable.isPresent())
            // Return the first column variable
            return referenceTable.get().getVariableNames().getFirst();

        // If neither main loop nor dynamic table reference is found: exception
        throw new MappingException(String.format(
                "Unable to find the reference loop or dynamic table '%s' of linked loop '%s'",
                enoLinkedLoop.getReference(), enoLinkedLoop.getId()));
    }

    private static void insertIterationEntry(ResizingType lunaticResizing,
                                             String resizingVariableName, String sizeExpression, List<String> resizedVariableNames) {
        // If no entry for the resizing variable name given, create it
        if (lunaticResizing.getResizingEntry(resizingVariableName) == null) {
            ResizingIterationEntry resizingIterationEntry = new ResizingIterationEntry();
            resizingIterationEntry.setSize(sizeExpression);
            resizingIterationEntry.getVariables().addAll(resizedVariableNames);
            lunaticResizing.putResizingEntry(resizingVariableName, resizingIterationEntry);
            return;
        }
        // Otherwise update existing one, only if the size expression is the same
        ResizingEntry resizingEntry = lunaticResizing.getResizingEntry(resizingVariableName);
        if (resizingEntry.getSize() == null) // Entry can be a pairwise entry, in this case the 'iteration' size is not set yet
            resizingEntry.setSize(sizeExpression);
        if (! sizeExpression.equals(resizingEntry.getSize()))
            throw new LunaticLogicException(String.format(
                    "Variable '%s' is used to define the size of different iterations in the questionnaire. " +
                            "Check loop 'max' iteration expressions, dynamic table max size expressions.",
                    resizingVariableName));
        resizedVariableNames.forEach(resizedVariableName -> {
            if (! resizingEntry.getVariables().contains(resizedVariableName))
                resizingEntry.getVariables().add(resizedVariableName);
        });
    }

}
