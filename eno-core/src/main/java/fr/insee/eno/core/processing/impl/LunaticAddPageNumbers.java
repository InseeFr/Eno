package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class LunaticAddPageNumbers implements OutProcessingInterface<Questionnaire> {

    private EnoParameters.LunaticPaginationMode mode;

    /**
     * Add pagination information in the given Lunatic questionnaire.
     * Warning: this method supposes that components are sorted.
     * TODO: (!) weird if the questionnaire ends with an empty subsequence with no declarations.
     * @param lunaticQuestionnaire Lunatic questionnaire to be processed.
     */
    public void apply(Questionnaire lunaticQuestionnaire) {
        lunaticQuestionnaire.setPagination(EnoParameters.lunaticNumberingMode(mode));
        switch (mode) {
            case NONE -> log.info("No pagination.");
            case QUESTION -> {
                log.info("Adding page numbers by question.");
                new QuestionModePaginator().pageNumbering(lunaticQuestionnaire);
            }
            case SEQUENCE -> {
                log.info("Adding page numbers by sequence.");
                new SequenceModePaginator().pageNumbering(lunaticQuestionnaire);
            }
        }
    }

    /** Class to represent a page number for Lunatic components.
     * A page number in Lunatic json components looks like this: "2.3"
     * (components can be nested in a loop component).
     * A LunaticPageNumber instance starts at page "1". */
    private static class LunaticPageNumber extends ArrayList<Integer> {
        public LunaticPageNumber() {
            super();
            this.add(1);
        }
        @Override
        public String toString() {
            return String.join(".", this.stream().map(String::valueOf).toList());
        }

        public void increment() {
            int lastPosition = this.size() - 1;
            this.set(lastPosition, this.get(lastPosition) + 1);
        }

        public void moveDown() {
            this.add(1);
        }

        public void moveUp() {
            this.remove(this.size() - 1);
        }

        public int currentOccurrence() {
            return this.get(this.size() - 1);
        }
    }

    /** Strategy pattern interface for Lunatic pagination. */
    private interface LunaticPaginator {
        /** Write page numbers in Lunatic questionnaire. */
        default void pageNumbering(Questionnaire questionnaire) {
            LunaticPageNumber pageNumber = new LunaticPageNumber();
            recursiveNumbering(questionnaire.getComponents(), pageNumber, true);
            questionnaire.setMaxPage(String.valueOf(pageNumber.currentOccurrence()));
        }

        /** Write page numbers in Lunatic components, starting at page number given.
         * The function is recursively called when encountering a loop component.
         * @param components Component list.
         * @param pageNumber Current page number.
         * @param paginatedLoop If we are in a loop which is not "paginated", all components have the same page number.
         */
        private void recursiveNumbering(List<ComponentType> components,
                                        LunaticPageNumber pageNumber, boolean paginatedLoop) {
            for (Iterator<ComponentType> iterator = components.iterator(); iterator.hasNext();) {
                ComponentType component = iterator.next();
                if (component instanceof Loop loop) {
                    loop.setPage(pageNumber.toString());
                    pageNumber.moveDown();
                    recursiveNumbering(loop.getComponents(), pageNumber, loop.isPaginatedLoop());
                    loop.setMaxPage(String.valueOf(pageNumber.currentOccurrence()));
                    pageNumber.moveUp();
                }
                else {
                    if (paginatedLoop) {
                        componentNumbering(component, iterator, pageNumber);
                    } else {
                        component.setPage(pageNumber.toString());
                    }
                }
            }
        }

        /** Write page number in component, and eventually following ones, and increment the page number.
         * To be overridden in function of pagination mode. */
        void componentNumbering(
                ComponentType component, Iterator<ComponentType> iterator, LunaticPageNumber pageNumber);

        /** Set component page.
         * PairwiseLinks component contains list of components. These share the same page number. */
        default void setComponentPage(ComponentType component, LunaticPageNumber pageNumber) {
            component.setPage(pageNumber.toString());
            if (component instanceof PairwiseLinks pairwiseLinks)
                pairwiseLinks.getComponents().forEach(embedded -> embedded.setPage(pageNumber.toString()));
        }
    }

    private static class QuestionModePaginator implements LunaticPaginator {
        /**
         * Question mode pagination. Each component increments the page number.
         * Except subsequences that has no declaration.
         */
        @Override
        public void componentNumbering(
                ComponentType component, Iterator<ComponentType> iterator, LunaticPageNumber pageNumber) {
            // Special case for subsequences that has a "goToPage"
            if (component instanceof Subsequence subsequence) {
                // goToPage number, weird case if the questionnaire ends with a subsequence
                if (iterator.hasNext()) {
                    subsequence.setGoToPage(String.valueOf(pageNumber));
                } else {
                    log.warn("Questionnaire ends with an empty subsequence component with no declarations (weird).");
                    subsequence.setGoToPage("");
                }
                // Subsequences have a page number only if they have at least one declaration
                if (! subsequence.getDeclarations().isEmpty()) {
                    subsequence.setPage(pageNumber.toString());
                    if (iterator.hasNext()) // Don't increment if last page
                        pageNumber.increment();
                }
            }
            //
            else {
                setComponentPage(component, pageNumber);
                if (iterator.hasNext()) // Don't increment if last page
                    pageNumber.increment();
            }
        }
    }

    private static class SequenceModePaginator implements LunaticPaginator {
        /**
         * Sequence mode pagination. Only sequences increment the page number.
         * Every component in the same sequence have the same page number.
         */
        @Override
        public void componentNumbering(
                ComponentType component, Iterator<ComponentType> iterator, LunaticPageNumber pageNumber) {
            setComponentPage(component, pageNumber);
            // If the next component is a sequence, increment page number
            if (iterator.hasNext()) {
                ComponentType nextComponent = iterator.next();
                if (nextComponent instanceof SequenceType) {
                    pageNumber.increment();
                }
                setComponentPage(nextComponent, pageNumber);
            }
        }
    }

}
