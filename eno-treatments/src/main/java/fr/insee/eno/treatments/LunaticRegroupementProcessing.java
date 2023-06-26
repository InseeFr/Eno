package fr.insee.eno.treatments;

import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.eno.treatments.dto.Regroupements;
import fr.insee.lunatic.model.flat.*;

import java.util.*;

/**
 * Post processing of a lunatic questionnaire. Without this processing, one question is displayed on each page.
 * This post processing permits to regroup questions of a questionnaire in same pages
 * /!\ We assume variables defined in a regroupement are consecutive and in the same order as their
 * corresponding components in the questionnaire
 */
public class LunaticRegroupementProcessing implements OutProcessingInterface<Questionnaire> {
    private final Regroupements regroupements;
    // The sequence pages is filled during the questionnaire processing
    // This attribute is used to change the page attribute in the hierarchy object of all components
    private final Map<String, String> sequencePages;

    /**
     * @param regroupements questions regroupements for a questionnaire
     */
    public LunaticRegroupementProcessing(List<Regroupement> regroupements) {
        this.regroupements = new Regroupements(regroupements);
        this.sequencePages = new HashMap<>();
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        List<ComponentType> components = questionnaire.getComponents();
        applyNumPageOnComponents(components, "", 0, true);
        String maxPage = components.get(components.size()-1).getPage();
        questionnaire.setMaxPage(maxPage);
    }

    /**
     * recursive method to generate the page attribute of a component list when regrouping questions
     * @param components component list to process
     * @param numPagePrefix numpage prefix, used to generate the numpage of a component in a loop. Ex: "7.", "5.4."
     * @param pageCount page count of the component in his parent
     * @param isParentPaginated is the parent component is paginated or not
     */
    private void applyNumPageOnComponents(List<ComponentType> components, String numPagePrefix, int pageCount, boolean isParentPaginated) {
        for(ComponentType component: components) {
            if(canIncrementPageCount(component, isParentPaginated)) {
                pageCount++;
            }

            switch(component.getComponentType()) {
                case SEQUENCE -> applyNumPageOnSequence((SequenceType) component, numPagePrefix + pageCount);
                case SUBSEQUENCE -> applyNumPageOnSubsequence((Subsequence) component, numPagePrefix, pageCount, isParentPaginated);
                case PAIRWISE_LINKS -> applyNumPageOnPairwiseLinks((PairwiseLinks) component, numPagePrefix, pageCount);
                case LOOP -> applyNumPageOnLoop((Loop) component, numPagePrefix, pageCount);
                default -> component.setPage(numPagePrefix + pageCount);
            }
        }
        applyNumPagesOnHierarchies(components);
    }

    /**
     * Apply the numpage of a sequence component and add the sequence to the overall sequence page map
     * @param sequence sequence component
     * @param numPage numpage to set
     */
    private void applyNumPageOnSequence(SequenceType sequence, String numPage) {
        sequence.setPage(numPage);
        addSequencePage(sequence);
    }

    /**
     * Apply the numpage of a subsequence component and add the subsequence to the overall sequence page map
     * @param subsequence subsequence component
     * @param numPagePrefix numpage prefix (if subsequence in a loop)
     * @param pageCount page count of the sequence in his parent component
     * @param isParentPaginated is the parent component paginated or not
     */
    private void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated) {

        String numPage = numPagePrefix + pageCount;
        // special case where a subsequence has no declarations (so no page attribute set) and must link to next component
        if(isParentPaginated && (subsequence.getDeclarations() == null || subsequence.getDeclarations().isEmpty())) {
            int pageSequence = pageCount + 1;
            numPage = numPagePrefix + pageSequence;
        }

        if(subsequence.getPage() != null) {
            subsequence.setPage(numPage);
        }

        if(subsequence.getGoToPage() != null) {
            subsequence.setGoToPage(numPage);
        }

        addSequencePage(subsequence);
    }

    /**
     * Apply numpage on a loop
     * @param loop loop component
     * @param numPagePrefix numpage prefix (if loop in a loop) (ex "7.4", "5.3.9.")
     * @param pageCount page count of the loop in his parent component
     */
    private void applyNumPageOnLoop(Loop loop, String numPagePrefix, int pageCount) {
        String numPage = numPagePrefix + pageCount;
        loop.setPage(numPage);
        List<ComponentType> loopComponents = loop.getComponents();
        int loopPageCount = pageCount;

        if(Boolean.TRUE.equals(loop.isPaginatedLoop())) {
            numPagePrefix = loop.getPage() + ".";
            loopPageCount = 0;
        }

        // call to recursive method to regroup questions in the loop components
        applyNumPageOnComponents(loopComponents, numPagePrefix, loopPageCount, loop.isPaginatedLoop());

        if(loop.getMaxPage() != null) {
            String pageLastComponent = loopComponents.get(loopComponents.size()-1).getPage();
            String maxPage = pageLastComponent.substring(pageLastComponent.lastIndexOf(".")+1);
            loop.setMaxPage(maxPage);
        }
    }

    /**
     *
     * @param links pairwise link component
     * @param numPagePrefix numpage prefix (if pairwiselink in a loop) (ex "7.", "5.3.")
     * @param pageCount page count of the pairwise link in his parent component
     */
    private void applyNumPageOnPairwiseLinks(PairwiseLinks links, String numPagePrefix, int pageCount) {
        links.setPage(numPagePrefix+pageCount);
        List<ComponentType> linksComponents = links.getComponents();
        // call to recursive method to regroup questions in the pairwise link component
        applyNumPageOnComponents(linksComponents, numPagePrefix, pageCount, false);
    }

    /**
     * Set the numpage attribute on hierarchy(sequence and subsequence) for each component
     *
     * @param components component list
     */
    private void applyNumPagesOnHierarchies(List<ComponentType> components) {
        components.stream()
                .map(ComponentType::getHierarchy)
                .filter(Objects::nonNull)
                .forEach(hierarchy -> {
                    SequenceDescription hierarchySequence = hierarchy.getSequence();
                    SequenceDescription hierarchySubsequence = hierarchy.getSubSequence();

                    if(hierarchySequence != null) {
                        String page = sequencePages.get(hierarchySequence.getId());
                        hierarchySequence.setPage(page);
                    }

                    if(hierarchySubsequence != null) {
                        String page = sequencePages.get(hierarchySubsequence.getId());
                        hierarchySubsequence.setPage(page);
                    }
                });
    }

    /**
     * Add an entry for a sequence to the sequencePage map
     * @param sequence sequence to add in the map
     */
    private void addSequencePage(SequenceType sequence) {
        sequencePages.put(sequence.getId(), sequence.getPage());
    }

    /**
     * Add an entry for a subsequence to the sequencePage map
     * @param sequence subsequence to add in the map
     */
    private void addSequencePage(Subsequence sequence) {
        String page = sequence.getPage();
        if(page == null) {
            page = sequence.getGoToPage();
        }
        sequencePages.put(sequence.getId(), page);
    }

    /**
     * Check if the page attribute for a component can be incremented
     * @param component component to check
     * @param isParentPaginated is the parent component paginated or not
     * @return true if the numpage for this component can be incremented, false otherwise
     */
    private boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated) {
        String responseName = null;
        // if parent component not paginated, all child have same page
        if(!isParentPaginated) {
            return false;
        }

        // if component is a subsequence and has no declarations set, it will regroup with next component, so no
        // increment in this specific case
        if(component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE) &&
                (component.getDeclarations() == null || component.getDeclarations().isEmpty())) {
           return false;
        }

        if(component instanceof ComponentSimpleResponseType simpleResponse) {
            responseName = simpleResponse.getResponse().getName();
        }

        // no response name, so no regroupement, we can increment
        if(responseName == null) {
            return true;
        }

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
