package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;

import java.util.*;

public class LunaticRegroupementProcessing implements OutProcessingInterface<Questionnaire> {
    private final Regroupements regroupements;
    private Map<String, String> sequencePages;

    public LunaticRegroupementProcessing(List<Regroupement> regroupements) {
        this.regroupements = new Regroupements(regroupements);
        this.sequencePages = new HashMap<>();
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
                Subsequence subsequence = (Subsequence) component;
                applyNumPageOnSubsequence(subsequence, numPagePrefix, pageCount);
                addSequencePage(subsequence);
                continue;
            }

            if(component.getComponentType().equals(ComponentTypeEnum.SEQUENCE)) {
                Sequence sequence = (Sequence) component;
                sequence.setPage(numPage);
                addSequencePage(sequence);
                continue;
            }

            component.setPage(numPage);

            if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
                Loop loop = (Loop) component;
                List<ComponentType> loopComponents = loop.getComponents();
                regroupQuestions(loopComponents, component.getPage() + ".", 0, questionnaire);
                if(loop.getMaxPage() != null) {
                    String pageLastComponent = loopComponents.get(loopComponents.size()-1).getPage();
                    String maxPage = pageLastComponent.substring(pageLastComponent.lastIndexOf(".")+1);
                    loop.setMaxPage(maxPage);
                }
            }
        }
        processSequencePagesOnHierarchies(components);
    }

    private void processSequencePagesOnHierarchies(List<ComponentType> components) {
        components.stream()
                .map(component -> component.getHierarchy().getSequence())
                .filter(Objects::nonNull)
                .forEach(hierarchySequence -> {
                    String page = sequencePages.get(hierarchySequence.getId());
                    hierarchySequence.setPage(page);
                });

        components.stream()
                .map(component -> component.getHierarchy().getSubSequence())
                .filter(Objects::nonNull)
                .forEach(hierarchySubsequence -> {
                    String page = sequencePages.get(hierarchySubsequence.getId());
                    hierarchySubsequence.setPage(page);
                });
    }

    private void addSequencePage(Sequence sequence) {
        sequencePages.put(sequence.getId(), sequence.getPage());
    }

    private void addSequencePage(Subsequence sequence) {
        String page = sequence.getPage();
        if(page == null) {
            page = sequence.getGoToPage();
        }
        sequencePages.put(sequence.getId(), page);
    }

    private void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount) {

        String numPage = numPagePrefix + pageCount;
        if(subsequence.getDeclarations() == null || subsequence.getDeclarations().isEmpty()) {
            int pageSequence = pageCount + 1;
            numPage = numPagePrefix + pageSequence;
        }

        if(subsequence.getPage() != null) {
            subsequence.setPage(numPage);
        }

        if(subsequence.getGoToPage() != null) {
            subsequence.setGoToPage(numPage);
        }
    }

    private boolean canIncrementPageCount(ComponentType component) {

        // if component is a subsequence and has no declarations set, it will regroup with next component, so no
        // increment in this specific case
        if(component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE) &&
                (component.getDeclarations() == null || component.getDeclarations().isEmpty())) {
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
