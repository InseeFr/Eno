package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LunaticRegroupementProcessing implements OutProcessingInterface<Questionnaire> {
    private final Regroupements regroupements;

    public LunaticRegroupementProcessing(List<Regroupement> regroupements) {
        this.regroupements = new Regroupements(regroupements);
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        List<ComponentType> components = questionnaire.getComponents();
        regroupQuestions(components, "", 0, questionnaire);
        String maxPage = components.get(components.size()-1).getPage();
        questionnaire.setMaxPage(maxPage);
    }

    private void regroupQuestions(List<ComponentType> components, String numPagePrefix, int pageCount, Questionnaire questionnaire) {
        for(ComponentType component: components) {

            if(canIncrementPageCount(component)) {
                pageCount++;
            }
            String numPage = numPagePrefix + pageCount;

            if(component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE)) {
                applyNumPageOnSubsequence((Subsequence) component, numPagePrefix, pageCount, questionnaire);
                continue;
            }

            if(component.getComponentType().equals(ComponentTypeEnum.SEQUENCE)) {
                applyNumPageOnSequence((Sequence) component, numPage, questionnaire);
                continue;
            }

            component.setPage(numPage);
            if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
                regroupQuestions(((Loop)component).getComponents(), component.getPage() + ".", 0, questionnaire);
            }
        }
    }

    private void applyNumPageOnSequence(Sequence sequence, String numPage, Questionnaire questionnaire) {
        sequence.setPage(numPage);
        questionnaire.getComponents().stream()
                .map(component -> component.getHierarchy().getSequence())
                .filter(Objects::nonNull)
                .filter(hierarchySequence -> hierarchySequence.getId().equals(sequence.getId()))
                .forEach(hierarchySequence -> hierarchySequence.setPage(sequence.getPage()));
    }

    private void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, Questionnaire questionnaire) {

        if(subsequence.getPage() != null) {
            String numPage = numPagePrefix + pageCount;
            subsequence.setPage(numPage);
            subsequence.setGoToPage(numPage);
        } else {
            // if no page, the subsequence is regrouped with the next component
            // we increment the pageCount to link the subsequence to next component page
            // (we assume the next component will have his page incremented)
            int gotToPage = pageCount + 1;
            subsequence.setGoToPage(String.valueOf(gotToPage));
        }


        questionnaire.getComponents().stream()
                .map(component -> component.getHierarchy().getSubSequence())
                .filter(Objects::nonNull)
                .filter(hierarchySubsequence -> hierarchySubsequence.getId().equals(subsequence.getId()))
                .forEach(hierarchySubsequence -> hierarchySubsequence.setPage(subsequence.getGoToPage()));
    }

    private boolean canIncrementPageCount(ComponentType component) {

        // if component is a subsequence and has no page attribute set, it will regroup with next component, so no
        // increment in this specific case
        if(component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE) && component.getPage() == null) {
           return false;
        }

        Optional<String> optResponseName = LunaticPostProcessingUtils.getResponseName(component);

        // no response name, so no regroupement, we can increment
        if(optResponseName.isEmpty()) {
            return true;
        }

        String responseName = optResponseName.get();
        Optional<Regroupement> optRegroupement = regroupements.getRegroupementForVariable(responseName);
        // if no regroupement, we can increment
        if(optRegroupement.isEmpty()) {
            return true;
        }

        Regroupement regroupement = optRegroupement.get();

        //if variable is the first variable of the regroupement, we can increment
        return regroupement.isFirstVariable(responseName);
    }
}
