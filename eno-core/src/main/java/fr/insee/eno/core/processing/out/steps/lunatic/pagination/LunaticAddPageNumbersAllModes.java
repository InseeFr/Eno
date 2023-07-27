package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

    /**
     * Abstract Post processing of a lunatic questionnaire. This processing handles the global flow of the pagination processing,
     * letting concrete classes to implement specific details about how the pagination increment/loops/subsequences are handled
     */
    public abstract class LunaticAddPageNumbersAllModes implements ProcessingStep<Questionnaire> {
        // The sequence pages is filled during the questionnaire processing
        // This attribute is used to change the page attribute in the hierarchy object of all components
        private final Map<String, String> sequencePages;

        private final boolean isQuestionnairePaginated;

        private final String paginationMode;

        protected LunaticAddPageNumbersAllModes(boolean isQuestionnairePaginated, String paginationMode) {
            this.sequencePages = new HashMap<>();
            this.isQuestionnairePaginated = isQuestionnairePaginated;
            this.paginationMode = paginationMode;
        }

        @Override
        public void apply(Questionnaire questionnaire) {
            questionnaire.setPagination(paginationMode);
            List<ComponentType> components = questionnaire.getComponents();
            applyNumPageOnComponents(components, "", 0, isQuestionnairePaginated);
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
        public void applyNumPageOnComponents(List<ComponentType> components, String numPagePrefix, int pageCount, boolean isParentPaginated) {
            for(ComponentType component: components) {
                if(canIncrementPageCount(component, isParentPaginated)) {
                    pageCount++;
                }

                switch(component.getComponentType()) {
                    case SEQUENCE -> {
                        Sequence sequence = (Sequence) component;
                        applyNumPageOnSequence(sequence, numPagePrefix + pageCount);
                        addSequencePage(sequence);
                    }
                    case SUBSEQUENCE -> {
                        Subsequence subsequence = (Subsequence) component;
                        applyNumPageOnSubsequence(subsequence, numPagePrefix, pageCount, isParentPaginated);
                        addSequencePage(subsequence);
                    }
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
        public void applyNumPageOnSequence(Sequence sequence, String numPage) {
            sequence.setPage(numPage);
        }

        /**
         * Apply the numpage of a subsequence component and add the subsequence to the overall sequence page map
         * @param subsequence subsequence component
         * @param numPagePrefix numpage prefix (if subsequence in a loop)
         * @param pageCount page count of the sequence in his parent component
         * @param isParentPaginated is the parent component paginated or not
         */
        public abstract void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated);

        /**
         * Apply numpage on a loop
         * @param loop loop component
         * @param numPagePrefix numpage prefix (if loop in a loop) (ex "7.4", "5.3.9.")
         * @param pageCount page count of the loop in his parent component
         */
        public void applyNumPageOnLoop(Loop loop, String numPagePrefix, int pageCount) {
            String numPage = numPagePrefix + pageCount;
            loop.setPage(numPage);
            List<ComponentType> loopComponents = loop.getComponents();
            int loopPageCount = pageCount;

            applyLoopPaginationProperty(loop);
            if(loop.getPaginatedLoop() != null && loop.getPaginatedLoop()) {
                numPagePrefix = loop.getPage() + ".";
                loopPageCount = 0;
            }

            // call to recursive method to regroup questions in the loop components
            applyNumPageOnComponents(loopComponents, numPagePrefix, loopPageCount, loop.getPaginatedLoop());

            if(loop.getPaginatedLoop() != null && loop.getPaginatedLoop()) {
                long maxPage = loop.getComponents().stream()
                        .map(ComponentType::getPage)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();

                loop.setMaxPage(Long.toString(maxPage));
            }
        }

        /**
         *
         * @param links pairwise link component
         * @param numPagePrefix numpage prefix (if pairwiselink in a loop) (ex "7.", "5.3.")
         * @param pageCount page count of the pairwise link in his parent component
         */
        public void applyNumPageOnPairwiseLinks(PairwiseLinks links, String numPagePrefix, int pageCount) {
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
        public void applyNumPagesOnHierarchies(List<ComponentType> components) {
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
        public void addSequencePage(Sequence sequence) {
            sequencePages.put(sequence.getId(), sequence.getPage());
        }

        /**
         * Add an entry for a subsequence to the sequencePage map
         * @param sequence subsequence to add in the map
         */
        public void addSequencePage(Subsequence sequence) {
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
        public abstract boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated);

        /**
         * @param loop loop which need property settings on pagination
         */
        public abstract void applyLoopPaginationProperty(Loop loop);

}
