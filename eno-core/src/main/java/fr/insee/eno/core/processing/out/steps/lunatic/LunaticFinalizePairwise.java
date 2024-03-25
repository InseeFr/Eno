package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Processing to finalize pairwise (handle symlinks/conditionFilter/calculated axis variables),
 */
public class LunaticFinalizePairwise implements ProcessingStep<Questionnaire> {

    private final EnoIndex enoIndex;

    public LunaticFinalizePairwise(EnoQuestionnaire enoQuestionnaire) {
        this.enoIndex = enoQuestionnaire.getIndex();
    }

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

        // Declarations are in the pairwise subcomponent
        pairwiseLinks.setDeclarations(null);

        // create symlinks
        ComponentSimpleResponseType simpleResponseComponent = (ComponentSimpleResponseType) pairwiseLinks.getComponents().getFirst();
        pairwiseLinks.setSymLinks(PairwiseLinks.createDefaultSymLinks(simpleResponseComponent.getResponse().getName()));

        // Filter is hold by the pairwise component only
        ComponentType pairwiseSubComponent = pairwiseLinks.getComponents().getFirst();
        pairwiseSubComponent.setConditionFilter(null);

        List<IVariableType> variables = lunaticQuestionnaire.getVariables();
        variables.addAll(createCalculatedAxisVariables(pairwiseLinks));
    }

    /**
     * retrieve pairwise links
     * @param components components to search
     * @return pairwise links list
     */
    private List<PairwiseLinks> searchForPairwiseLinks(List<ComponentType> components) {
        List<PairwiseLinks> pairwiseLinksList = new ArrayList<>();
        pairwiseLinksList.addAll(components.stream()
                .filter(componentType -> ComponentTypeEnum.PAIRWISE_LINKS.equals(componentType.getComponentType()))
                .map(PairwiseLinks.class::cast)
                .toList());

        pairwiseLinksList.addAll(components.stream()
                .filter(componentType -> ComponentTypeEnum.LOOP.equals(componentType.getComponentType()))
                .map(Loop.class::cast)
                .map(loop -> searchForPairwiseLinks(loop.getComponents()))
                .flatMap(Collection::stream)
                .toList());
        return pairwiseLinksList;
    }

    /**
     * Creation of calculated axis variables.
     * @param pairwiseLinks Lunatic pairwise links component.
     * @return calculated axis variables.
     */
    private List<VariableType> createCalculatedAxisVariables(PairwiseLinks pairwiseLinks) {
        PairwiseQuestion pairwiseQuestion = (PairwiseQuestion) enoIndex.get(pairwiseLinks.getId());
        String pairwiseName = pairwiseQuestion.getLoopVariableName();

        List<VariableType> variables = new ArrayList<>();

        List<String> calculatedVariableNames = List.of("xAxis", "yAxis");

        // create calculated variables
        for(String calculatedVariableName : calculatedVariableNames) {
            VariableType calculatedAxis = new VariableType();
            calculatedAxis.setVariableType(VariableTypeEnum.CALCULATED);
            calculatedAxis.setName(calculatedVariableName);
            LabelType expression = new LabelType();
            expression.setType(LabelTypeEnum.VTL);
            expression.setValue(pairwiseName);
            calculatedAxis.setExpression(expression);
            calculatedAxis.setBindingDependencies(List.of(pairwiseName));
            calculatedAxis.setShapeFrom(pairwiseName);
            variables.add(calculatedAxis);
        }
        return variables;
    }
}
