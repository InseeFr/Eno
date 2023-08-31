package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticSerializationException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class LunaticAddResizing implements ProcessingStep<Questionnaire> {

    private EnoQuestionnaire enoQuestionnaire;
    private EnoCatalog enoCatalog;

    public LunaticAddResizing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoCatalog = new EnoCatalog(enoQuestionnaire);
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        ResizingType resizingType = lunaticQuestionnaire.getResizing();
        if(resizingType == null) {
            resizingType = new ResizingType();
        }

        List<Object> resizings = resizingType.getAny();

        for(ComponentType component: lunaticQuestionnaire.getComponents()) {
            if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
                resizings.addAll(buildResizingVariablesForLoop((Loop) component));
            }

            if(component.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS)) {
                resizings.add(buildResizingVariableForPairwise((PairwiseLinks) component));
            }
        }

        if(!resizings.isEmpty()) {
            lunaticQuestionnaire.setResizing(resizingType);
        }
    }


    /**
     * Build resizing variable for a pairwise link
     * @param links pairwise link
     * @return resizing variable of a pairwise link
     */
    private LunaticResizingPairWiseVariable buildResizingVariableForPairwise(PairwiseLinks links) {
        List<String> sizesVTLFormula = List.of(links.getXAxisIterations().getValue(), links.getYAxisIterations().getValue());
        String resizingVariable = ((PairwiseQuestion) enoCatalog.getQuestion(links.getId())).getLoopVariableName();
        List<String> variablesNames = getLoopVariables(links.getId());
        return new LunaticResizingPairWiseVariable(resizingVariable, sizesVTLFormula, variablesNames);
    }


     /**
     * Build resizing variables for a loop
     * @param loop loop
     * @return list of resizing variables of a loop
     */
    private List<LunaticResizingLoopVariable> buildResizingVariablesForLoop(Loop loop) {
        // no need to handle linked loops, variables from main loop includes variables from linked loops
        fr.insee.eno.core.model.navigation.Loop enoLoop = (fr.insee.eno.core.model.navigation.Loop) enoQuestionnaire.getIndex().get(loop.getId());
        String sizeVTLFormula;

        if(enoLoop instanceof LinkedLoop) {
            sizeVTLFormula = loop.getIterations().getValue();
        } else {
            sizeVTLFormula = loop.getLines().getMax().getValue();
        }
        List<CollectedVariable> resizingVariables = getResizingVariables(loop);

        if(resizingVariables.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> loopVariables = getLoopVariables(loop.getId());
        return resizingVariables.stream()
                .map(resizingVariable -> new LunaticResizingLoopVariable(resizingVariable.getName(), sizeVTLFormula, loopVariables))
                .toList();

    }

    /**
     * @param loopId/pairwise loop id
     * @return all the variables in a loop/pairwise
     */
    private List<String> getLoopVariables(String loopId) {
        //TODO retrieve variables from loop
        return new ArrayList<>();
    }

    /**
     * Get resizing variables of a loop
     * @param loop loop component
     * @return resizing variable list
     */
    private List<CollectedVariable> getResizingVariables(Loop loop) {
        return loop.getLoopDependencies().stream()
                .map(this::getEnoVariable)
                .filter(variable -> variable.getCollectionType().equals(Variable.CollectionType.COLLECTED))
                .map(CollectedVariable.class::cast)
                .toList();
    }

    private Variable getEnoVariable(String variableName) {
        return enoCatalog.getVariables().stream()
                .filter(variable -> variableName.equals(variable.getName()))
                .findFirst()
                .orElseThrow(() -> new LunaticSerializationException(String.format("Variable %s not found when trying to process resizing", variableName)));
    }
}
