package fr.insee.eno.core.processing.out.steps.lunatic.shapefrom;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This processing steps adds the "shapeFrom" property in conditionFilter (and also dynamic labels) of Lunatic
 * "hierarchy" components:
 * <ul>
 *  <li>Sequence</li>
 *  <li>Subsequence</li>
 *  <li>Loop</li>
 *  </ul>
 *  i.e: all component which appears in Lunatic overview (based on Sequence, Subsequence and Loop) kind of summary.
 * <p>
 *  The goal is to improve performances for overview computation (on every change in form, all conditionFilters are
 *  calculated for the overview).
 *  Lunatic puts the result in cache if expression has the "shapeFrom" properties.
 * </p>
 * Later: Maybe create a Lunatic overview component to avoid computation at initialization.
 */
@Slf4j
public class LunaticHierarchyShapeFrom implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;

    private Questionnaire lunaticQuestionnaire;
    private Map<String, String> variableShapeFromIndex;

    public LunaticHierarchyShapeFrom(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
    }

    /**
     * Sets the "shape from" property in condition filters and dynamic labels of "hierarchy" components of the given
     * questionnaire, using the value defined in calculated variables.
     * Note: only sequences, subsequences and loops are used for overview.
     * According to the Lunatic loop resolution step, the conditionFilter for a loop contains its first component
     * (which is either a sequence or subsequence) conditionFilter.
     * So we don't need to set the "shape from" property on loop components.
     *
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     * @see fr.insee.eno.core.processing.out.steps.lunatic.loop.LunaticLoopFilter
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.indexVariablesAndShapeFrom();
        enoQuestionnaire.getSequences().forEach(this::setShapeFromSequence);
        enoQuestionnaire.getSubsequences().forEach(this::setShapeFromSequence);
    }


    // Part I: Gather shapeFrom values by variable

    private void indexVariablesAndShapeFrom() {
        variableShapeFromIndex = new LinkedHashMap<>();
        lunaticQuestionnaire.getVariables().forEach(lunaticVariable ->
                variableShapeFromIndex.put(lunaticVariable.getName(), getShapeFromOfVariable(lunaticVariable))
        );
    }

    private String getShapeFromOfVariable(VariableType lunaticVariable) {
        Optional<ComponentType> iterationComponent = findIterationComponent(lunaticVariable);
        return iterationComponent
                .map(LunaticHierarchyShapeFrom::getFirstResponseName)
                .orElse(null);
    }

    private Optional<ComponentType> findIterationComponent(VariableType lunaticVariable) {
        return lunaticQuestionnaire.getComponents().stream()
                .filter(component -> lunaticVariable.getIterationReference() != null
                        && lunaticVariable.getIterationReference().equals(component.getId()))
                .findAny();
    }

    private static String getFirstResponseName(ComponentType componentType) {
        return LunaticUtils.getResponseNames(componentType).getFirst();
    }


    // Part II: Set the shapeFrom property on condition filter and labels within Lunatic sequences

    private void setShapeFromSequence(AbstractSequence enoAbstractSequence) {
        Optional<ComponentType> lunaticComponent = LunaticUtils.findComponentById(lunaticQuestionnaire, enoAbstractSequence.getId());
        if (lunaticComponent.isEmpty())
            throw new MappingException("Cannot find Lunatic component with id '" + enoAbstractSequence.getId() + "'.");
        Optional<String> firstVariableOfSequence = findFirstCollectedVariable(enoAbstractSequence);
        firstVariableOfSequence.ifPresent(variableName ->
                setShapeFromLunaticComponent(lunaticComponent.get(), variableName));
    }

    private Optional<String> findFirstCollectedVariable(AbstractSequence abstractSequence) {
        Map<String, List<String>> variablesByQuestion = LunaticUtils.getCollectedVariablesByQuestion(lunaticQuestionnaire);
        for (StructureItemReference itemReference : abstractSequence.getSequenceStructure()) {
            // If not questions, then it's a sequence or subsequence => recursive call
            if (! StructureItemReference.StructureItemType.QUESTION.equals(itemReference.getType())) {
                return findFirstCollectedVariable((AbstractSequence) enoIndex.get(itemReference.getId()));
            }
            // Otherwise return first collected variable of question
            List<String> questionVariables = variablesByQuestion.get(itemReference.getId());
            if (questionVariables.isEmpty()) { // (should not happen)
                log.warn("Question <ith id '{}' has no collected variable.", itemReference.getId());
                continue;
            }
            return Optional.of(questionVariables.getFirst());
        }
        return Optional.empty();
    }

    private void setShapeFromLunaticComponent(ComponentType lunaticComponent, String variableName) {
        assert lunaticComponent instanceof fr.insee.lunatic.model.flat.Sequence
                || lunaticComponent instanceof fr.insee.lunatic.model.flat.Subsequence;
        String shapeFrom = variableShapeFromIndex.get(variableName);
        if (lunaticComponent.getLabel() != null)
            lunaticComponent.getLabel().setShapeFrom(shapeFrom);
        if (lunaticComponent.getDescription() != null)
            lunaticComponent.getDescription().setShapeFrom(shapeFrom);
        if (lunaticComponent.getConditionFilter() != null)
            lunaticComponent.getConditionFilter().setShapeFrom(shapeFrom);
        if (lunaticComponent.getDeclarations() != null) {
            lunaticComponent.getDeclarations().forEach(declaration -> {
                if (declaration.getLabel() != null)
                    declaration.getLabel().setShapeFrom(shapeFrom);
            });
        }
    }

}
