package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.insee.eno.core.utils.LunaticUtils.searchForPairwiseLinks;

/**
 * Processing to finalize pairwise (handle symlinks/conditionFilter/calculated axis variables),
 */
public class LunaticFinalizePairwise implements ProcessingStep<Questionnaire> {

    private final EnoIndex enoIndex;

    public LunaticFinalizePairwise(EnoQuestionnaire enoQuestionnaire) {
        this.enoIndex = enoQuestionnaire.getIndex();
    }

    private static final String X_AXIS = "xAxis";
    private static final String Y_AXIS = "yAxis";

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<PairwiseLinks> pairwiseLinksList = searchForPairwiseLinks(lunaticQuestionnaire.getComponents());
        if(pairwiseLinksList.isEmpty()) {
            return;
        }

        // at this time, only one pairwise by questionnaire is allowed
        if(pairwiseLinksList.size() > 1) {
            throw new LunaticPairwiseException("A questionnaire should not have more than one pairwise link");
        }

        PairwiseLinks pairwiseLinks = pairwiseLinksList.getFirst();

        boolean isPairwiseInLoop = isInLoop(lunaticQuestionnaire.getComponents(), pairwiseLinks, false);

        // should delete `xAxisIterations` & `yAxisIterations` (useless and cause scope issue where pairwise is insideLoop)
        if(isPairwiseInLoop){
            pairwiseLinks.setXAxisIterations(null);
            pairwiseLinks.setYAxisIterations(null);
        }
        // Label is in the pairwise subcomponent
        pairwiseLinks.setLabel(null);

        // Declarations are in the pairwise subcomponent
        pairwiseLinks.getDeclarations().clear();

        // create symlinks
        ComponentSimpleResponseType simpleResponseComponent = (ComponentSimpleResponseType) pairwiseLinks.getComponents().getFirst();
        pairwiseLinks.setSymLinks(PairwiseLinks.createDefaultSymLinks(simpleResponseComponent.getResponse().getName()));

        // Filter is hold by the pairwise component only
        lunaticQuestionnaire.getVariables().addAll(createCalculatedAxisVariables(pairwiseLinks));
    }

    private static boolean isInLoop(List<ComponentType> components, ComponentType pairwise, boolean inLoop){
        for(ComponentType component: components) {
            if(pairwise.getId().equals(component.getId())) return inLoop;
            if(ComponentTypeEnum.LOOP.equals(component.getComponentType()) &&
                    isInLoop(((Loop) component).getComponents(), pairwise, true)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Creation of calculated axis variables.
     * @param pairwiseLinks Lunatic pairwise links component.
     * @return calculated axis variables.
     */
    private List<CalculatedVariableType> createCalculatedAxisVariables(PairwiseLinks pairwiseLinks) {
        PairwiseQuestion pairwiseQuestion = (PairwiseQuestion) enoIndex.get(pairwiseLinks.getId());
        String pairwiseName = pairwiseQuestion.getLoopVariableName();

        List<CalculatedVariableType> variables = new ArrayList<>();

        List<String> calculatedVariableNames = List.of(X_AXIS, Y_AXIS);

        // create calculated variables
        for(String calculatedVariableName : calculatedVariableNames) {
            CalculatedVariableType calculatedAxis = new CalculatedVariableType();
            calculatedAxis.setName(calculatedVariableName);
            LabelType expression = new LabelType();
            expression.setType(LabelTypeEnum.VTL);
            expression.setValue(pairwiseName);
            calculatedAxis.setExpression(expression);
            calculatedAxis.setBindingDependencies(List.of(pairwiseName));
            calculatedAxis.getShapeFromList().add(pairwiseName);
            variables.add(calculatedAxis);
        }
        return variables;
    }
}
