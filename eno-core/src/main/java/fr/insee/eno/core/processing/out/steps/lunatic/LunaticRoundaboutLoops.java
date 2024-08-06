package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticVariableConflictException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.sequence.RoundaboutSequence;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableDimension;
import fr.insee.lunatic.model.flat.variable.VariableType;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * After Lunatic loop's resolution, roundabouts are present as loop objects in the Lunatic questionnaire.
 * This processing step replaces these loops by roundabout components, and adds the roundabout-specific information
 * using the Eno questionnaire.
 */
public class LunaticRoundaboutLoops implements ProcessingStep<Questionnaire> {

    private static final String DDI_INSTANCE_LABEL_TYPE = "loop.instanceLabel";
    private static final String DDI_INSTANCE_DESCRIPTION_TYPE = "loop.instanceDescription";
    private static final String PROGRESS_VARIABLE_SUFFIX = "_PROGRESS";

    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticRoundaboutLoops(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    /**
     * Replaces loops that correspond to a roundabout by roundabout components.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        enoQuestionnaire.getRoundaboutSequences().forEach(roundaboutSequence ->
                resolveRoundaboutComponent(lunaticQuestionnaire, roundaboutSequence));
    }

    /**
     * From the Eno roundabout sequence object given, retrieves the corresponding loop in the Lunatic questionnaire,
     * and replace it with a roundabout component.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     * @param roundaboutSequence Eno roundabout sequence.
     */
    private void resolveRoundaboutComponent(Questionnaire lunaticQuestionnaire, RoundaboutSequence roundaboutSequence) {
        //
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        int index = findLoopIndex(components, roundaboutSequence);
        Loop lunaticLoop = (Loop) components.remove(index);
        //
        LinkedLoop enoLoop = getRoundaboutLoop(roundaboutSequence);
        String progressVariableName = createProgressVariableName(enoLoop);
        //
        Roundabout lunaticRoundabout = createRoundabout(roundaboutSequence, lunaticLoop, enoLoop, progressVariableName);
        components.add(index, lunaticRoundabout);
        //
        variableNotPresentCheck(lunaticQuestionnaire, progressVariableName);
        CollectedVariableType lunaticProgressVariable = createProgressVariable(progressVariableName, enoLoop);
        lunaticQuestionnaire.getVariables().add(lunaticProgressVariable);
    }

    /**
     * Returns the index of the loop that matched the reference in the Eno roundabout sequence given.
     * @param components Components of the Lunatic questionnaire.
     * @param roundaboutSequence A Eno roundabout sequence object.
     * @return The index of the matching loop in the components' list.
     */
    private static int findLoopIndex(List<ComponentType> components, RoundaboutSequence roundaboutSequence) {
        // neat trick found in stackoverflow https://stackoverflow.com/a/60006562/13425151
        OptionalInt searchedIndex = IntStream.range(0, components.size())
                .filter(i -> components.get(i) instanceof Loop)
                .filter(i -> roundaboutSequence.getLoopReference().equals(components.get(i).getId()))
                .findAny();
        if (searchedIndex.isEmpty())
            throw new MappingException(String.format(
                    "Cannot find find the loop with reference '%s' for roundabout '%s'.",
                    roundaboutSequence.getLoopReference(), roundaboutSequence.getId()));
        return searchedIndex.getAsInt();
    }

    /**
     * Returns the loop object that corresponds to the given roundabout sequence. The loop is a linked loop since a
     * roundabout is always based on a main iteration.
     * @param roundaboutSequence Eno roundabout sequence.
     * @return The corresponding linked loop object.
     */
    private LinkedLoop getRoundaboutLoop(RoundaboutSequence roundaboutSequence) {
        Optional<fr.insee.eno.core.model.navigation.Loop> enoLoop = enoQuestionnaire.getLoops().stream()
                .filter(loop -> roundaboutSequence.getLoopReference().equals(loop.getId()))
                .findAny();
        if (enoLoop.isEmpty())
            throw new MappingException(String.format(
                    "Unable to retrieve the Eno loop object '%s' for roundabout '%s'.",
                    roundaboutSequence.getLoopReference(), roundaboutSequence.getId()));
        if (! (enoLoop.get() instanceof LinkedLoop enoLinkedLoop))
            throw new MappingException(String.format(
                    "Loop '%s' associated with roundabout '%s' is not a linked loop object.",
                    roundaboutSequence.getLoopReference(), roundaboutSequence.getId()));
        return enoLinkedLoop;
    }

    /**
     * Returns the name of the roundabout progress variable from the given linked loop.
     * Business rule: the name of the progress variable is the main loop's name concatenated with the "progress"
     * suffix.
     * Note: this rule is rather fragile because this could cause conflicts if the user creates variables that matches
     * this generated name.
     * @param enoLinkedLoop Eno linked loop associated with a roundabout.
     * @return The name of the progress variable associated with this roundabout.
     */
    private String createProgressVariableName(LinkedLoop enoLinkedLoop) {
        return findMainLoopName(enoLinkedLoop) + PROGRESS_VARIABLE_SUFFIX;
    }

    /**
     * Retrieves the main loop name from the linked loop given.
     * @param enoLinkedLoop A Eno linked loop object.
     * @return The name of the corresponding main loop.
     */
    private String findMainLoopName(LinkedLoop enoLinkedLoop) {
        Optional<fr.insee.eno.core.model.navigation.Loop> enoMainLoop = enoQuestionnaire.getLoops().stream()
                .filter(loop -> enoLinkedLoop.getReference().equals(loop.getId()))
                .findAny();
        if (enoMainLoop.isEmpty())
            throw new MappingException(String.format(
                    "Unable to retrieve the main loop of id '%s' for linked loop '%s'.",
                    enoLinkedLoop.getReference(), enoLinkedLoop.getId()));
        return enoMainLoop.get().getName();
    }

    /**
     * Creates the Lunatic roundabout component using the information of given parameters.
     * @param roundaboutSequence Eno roundabout sequence object.
     * @param lunaticLoop Lunatic loop object.
     * @param progressVariableName The name of the progress variable associated with the roundabout.
     * @return A Lunatic roundabout component.
     */
    private Roundabout createRoundabout(RoundaboutSequence roundaboutSequence, Loop lunaticLoop,
                                        fr.insee.eno.core.model.navigation.Loop enoLoop, String progressVariableName) {
        Roundabout lunaticRoundabout = new Roundabout();
        //
        lunaticRoundabout.setId(roundaboutSequence.getId());
        lunaticRoundabout.setPage(lunaticLoop.getPage());
        lunaticRoundabout.setConditionFilter(lunaticLoop.getConditionFilter());
        lunaticRoundabout.setIterations(lunaticLoop.getIterations());
        lunaticRoundabout.getComponents().addAll(lunaticLoop.getComponents());
        //
        lunaticRoundabout.setLocked(roundaboutSequence.getLocked());
        lunaticRoundabout.setLabel(new LabelType());
        lunaticRoundabout.getLabel().setValue(roundaboutSequence.getLabel().getValue());
        lunaticRoundabout.getLabel().setType(LabelTypeEnum.VTL_MD);
        lunaticRoundabout.setProgressVariable(progressVariableName);
        //
        Roundabout.Item lunaticRoundaboutItem = new Roundabout.Item();
        lunaticRoundaboutItem.setLabel(new LabelType());
        lunaticRoundaboutItem.getLabel().setValue(getInstanceLabel(roundaboutSequence));
        lunaticRoundaboutItem.getLabel().setType(LabelTypeEnum.VTL_MD);
        String instanceDescription = getInstanceDescription(roundaboutSequence);
        if (instanceDescription != null) {
            lunaticRoundaboutItem.setDescription(new LabelType());
            lunaticRoundaboutItem.getDescription().setValue(instanceDescription);
            lunaticRoundaboutItem.getDescription().setType(LabelTypeEnum.VTL_MD);
        }
        String occurrenceFilterExpression = getOccurrenceFilterExpression(enoLoop);
        if (occurrenceFilterExpression != null) {
            lunaticRoundaboutItem.setDisabled(new LabelType());
            lunaticRoundaboutItem.getDisabled().setValue(occurrenceFilterExpression);
            lunaticRoundaboutItem.getDisabled().setType(LabelTypeEnum.VTL);
        }
        lunaticRoundabout.setItem(lunaticRoundaboutItem);
        //
        return lunaticRoundabout;
    }

    /**
     * Return the label value of the instruction that holds the label of instances of the roundabout.
     * @param roundaboutSequence A Eno roundabout sequence.
     * @return String value of the label.
     * @throws MappingException if the instruction is not found.
     */
    private static String getInstanceLabel(RoundaboutSequence roundaboutSequence) {
        Optional<Instruction> labelInstruction = roundaboutSequence.getInstructions().stream()
                .filter(instruction -> DDI_INSTANCE_LABEL_TYPE.equals(instruction.getDeclarationType()))
                .findAny();
        if (labelInstruction.isEmpty()) // the instance label is required in Lunatic
            throw new MappingException(
                    "Cannot find instance label of roundabout sequence '" + roundaboutSequence.getId() + "'.");
        return labelInstruction.get().getLabel().getValue();
    }

    /**
     * Return the label value of the instruction that holds the description of instances of the roundabout.
     * @param roundaboutSequence A Eno roundabout sequence.
     * @return String value of the description. Can be null if there is no description.
     */
    private static String getInstanceDescription(RoundaboutSequence roundaboutSequence) {
        Optional<Instruction> labelInstruction = roundaboutSequence.getInstructions().stream()
                .filter(instruction -> DDI_INSTANCE_DESCRIPTION_TYPE.equals(instruction.getDeclarationType()))
                .findAny();
        return labelInstruction.map(instruction -> instruction.getLabel().getValue()).orElse(null);
    }

    /**
     * Return the expression of the occurrence filter of the loop.
     * @param enoLoop A Eno loop object.
     * @return String expression of the filter. Can be null if there is no occurrence filter in the loop.
     */
    private String getOccurrenceFilterExpression(fr.insee.eno.core.model.navigation.Loop enoLoop) {
        if (enoLoop.getOccurrenceFilterId() == null)
            return null;
        Optional<Filter> occurrenceFilter = enoQuestionnaire.getFilters().stream()
                .filter(filter -> enoLoop.getOccurrenceFilterId().equals(filter.getId()))
                .findAny();
        if (occurrenceFilter.isEmpty())
            throw new MappingException(String.format(
                    "Cannot find occurrence filter '%s' of loop '%s'.",
                    enoLoop.getOccurrenceFilterId(), enoLoop.getId()));
        return occurrenceFilter.get().getExpression().getValue();
    }

    /**
     * Creates the progress variable object for a roundabout.
     * @param progressVariableName Name of the progress variable.
     * @param enoLoop Eno linked loop object passed to set the iteration reference of the Lunatic variable.
     * @return Lunatic variable object.
     */
    private static CollectedVariableType createProgressVariable(String progressVariableName, LinkedLoop enoLoop) {
        CollectedVariableType lunaticProgressVariable = new CollectedVariableType();
        lunaticProgressVariable.setName(progressVariableName);
        lunaticProgressVariable.setDimension(VariableDimension.ARRAY);
        lunaticProgressVariable.setValues(new CollectedVariableValues.Array());
        lunaticProgressVariable.setIterationReference(enoLoop.getReference());
        return lunaticProgressVariable;
    }

    /**
     * Check that the given variable name does not exist in the Lunatic questionnaire.
     * Throws an exception otherwise.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     * @param variableName A variable name.
     * @throws LunaticVariableConflictException if the variable is already present.
     */
    private static void variableNotPresentCheck(Questionnaire lunaticQuestionnaire, String variableName) {
        for (VariableType lunaticVariable : lunaticQuestionnaire.getVariables()) {
            if (variableName.equals(lunaticVariable.getName()))
                throw new LunaticVariableConflictException(
                        "The name of the progress variable '" + variableName
                                + "' generated for a roundabout already exists among questionnaire variables.");
        }
    }

}
