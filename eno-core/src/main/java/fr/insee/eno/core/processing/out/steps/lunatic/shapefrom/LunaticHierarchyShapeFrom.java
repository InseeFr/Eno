package fr.insee.eno.core.processing.out.steps.lunatic.shapefrom;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.variable.VariableType;

import java.util.*;

import static fr.insee.eno.core.utils.LunaticUtils.*;

/**
 * This processing steps add shapeFrom on conditionFilter Lunatic object only in hierarchy component:
 * <ul>
 *  <li>Sequence</li>
 *  <li>Subsequence</li>
 *  <li>Loop</li>
 *  </ul>
 *  i.e: all component which appears in Lunatic overview (based on Sequence, Subsequence and Loop) kind of summary.
 * <p>
 *  The goal is to improved performances for calculation of overview (on every change in form, all conditionFilter are calculated for overview)
 *  Lunatic put the result in cache if expression has the "shapeFrom" properties
 * </p>
 *  Later: Maybe create overview component to avoid computation at init for Lunatic.
 */
public class LunaticHierarchyShapeFrom implements ProcessingStep<Questionnaire> {

    private Questionnaire lunaticQuestionnaire;
    private EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;
    private final Map<String, VariableType> variableIndex = new LinkedHashMap<>();
    private final Map<String, String> variableShapeFromIndex = new LinkedHashMap<>();
    private Map<String, List<String>> variablesByQuestion;


    public LunaticHierarchyShapeFrom(EnoQuestionnaire enoQuestionnaire){
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
    }
    /**
     * Sets the "shape from" property on calculated variables of the current questionaire.
     * @param lunaticQuestionnaire A Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.preProcessVariablesAndShapeFrom(lunaticQuestionnaire);
        //
        // only Sequence, Subsequence & loop is used for overview
        // According to LunaticLoopResolution, conditionFilter for Loop is the same as first component (seq or subseq)
        // So we don't need to have a step for Loop (already did in Seq or Subseq)
        enoQuestionnaire.getSequences().forEach(this::setShapeFromSequence);
        enoQuestionnaire.getSubsequences().forEach(this::setShapeFromSequence);
    }

    private void preProcessVariablesAndShapeFrom(Questionnaire lunaticQuestionnaire){
        variablesByQuestion = getCollectedVariablesByQuestion(lunaticQuestionnaire);
        // Create filter shapeFrom index based on filterHierarchyIndex and loop
        lunaticQuestionnaire.getVariables()
                .forEach(lunaticVariable -> {
                    variableIndex.put(lunaticVariable.getName(), lunaticVariable);
                    variableShapeFromIndex.put(lunaticVariable.getName(), getShapeFromOfVariable(lunaticQuestionnaire, lunaticVariable.getName()));
                });
    }
    /**
     *
     * @param variableName
     * @return ShapeFrom: the variable Name of filter scope
     */
    public String getShapeFromOfVariable(Questionnaire lunaticQuestionnaire, String variableName){
        VariableType variableType = variableIndex.get(variableName);
        Optional<ComponentType> iterationComponent = lunaticQuestionnaire.getComponents().stream()
                .filter(component -> variableType.getIterationReference() != null
                        && variableType.getIterationReference().equals(component.getId()))
                .findAny();
        if (iterationComponent.isEmpty()) return null;
        return LunaticUtils.getResponseNames(iterationComponent.get()).getFirst();
    }

    public List<String> getCollectedVarsInSequence(AbstractSequence abstractSequence) {
        List<String> collectedVarInSequence = new ArrayList<>();
        abstractSequence.getSequenceStructure().forEach(itemReference -> {
                    if (StructureItemReference.StructureItemType.QUESTION.equals(itemReference.getType())) {
                        collectedVarInSequence.addAll(variablesByQuestion.get(itemReference.getId()));
                    } else {
                        collectedVarInSequence.addAll(getCollectedVarsInSequence((AbstractSequence) enoIndex.get(itemReference.getId())));
                    }
                }
        );
        return collectedVarInSequence;
    }

    public void setShapeFromSequence(AbstractSequence enoAbstractSequence){
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, enoAbstractSequence.getId());
        if(lunaticComponent.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + lunaticComponent + ".");
        }

        List<String> varsInSeq = getCollectedVarsInSequence(enoAbstractSequence);
        setShapeFromLunaticComponent(lunaticComponent.get(), varsInSeq);
    }

    public void setShapeFromLunaticComponent(ComponentType lunaticComponent, List<String> collectedVarsInside){
        if(collectedVarsInside.isEmpty()) return;
        String shapeFrom = variableShapeFromIndex.get(collectedVarsInside.getFirst());
        ConditionFilterType conditionFilterType = lunaticComponent.getConditionFilter();
        if(conditionFilterType != null) conditionFilterType.setShapeFrom(shapeFrom);
    }

}

