package fr.insee.eno.core.processing.impl.lunatic.pagination;

import fr.insee.lunatic.model.flat.*;

/**
 * Post processing of a lunatic questionnaire. With this processing, one question is displayed on each page.
 */
public class LunaticAddPageNumbersQuestionMode extends LunaticAddPageNumbersAllModes {

    public LunaticAddPageNumbersQuestionMode() {
        super(true, "question");
    }

    /**
     * Check if the page attribute for a component can be incremented
     * @param component component to check
     * @param isParentPaginated is the parent component paginated or not
     * @return true if the numpage for this component can be incremented, false otherwise
     */
    public boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated) {
        // if parent component not paginated, all child have same page
        if(!isParentPaginated) {
            return false;
        }

        // if component is a subsequence and has no declarations set, it will regroup with next component, so no
        // increment in this specific case
        return !component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE) ||
                (component.getDeclarations() != null && !component.getDeclarations().isEmpty());
    }

    @Override
    public void applyLoopPaginationProperty(Loop loop) {
        if(isLinkedLoop(loop)) {
            loop.setPaginatedLoop(true);
            return;
        }
        loop.setPaginatedLoop(false);
    }

    @Override
    public void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated) {

        String numPage = numPagePrefix + pageCount;
        // special case where a subsequence has no declarations (so no page attribute set) and must link to next component
        if(isParentPaginated && (subsequence.getDeclarations() == null || subsequence.getDeclarations().isEmpty())) {
            int pageSequence = pageCount + 1;
            numPage = numPagePrefix + pageSequence;
        }

        // if parent paginated or empty declarations
        if (subsequence.getDeclarations() == null || subsequence.getDeclarations().isEmpty() || isParentPaginated) {
            subsequence.setGoToPage(numPage);
        }

        // if parent not paginated and declarations set
        if(!isParentPaginated || (subsequence.getDeclarations() != null && !subsequence.getDeclarations().isEmpty())) {
            subsequence.setPage(numPage);
        }
    }

    /**
     * is Loop linked
     * @param loop to check
     * @return true if linked, false otherwise
     */
    private boolean isLinkedLoop(Loop loop) {
        // if lines != null loop is a main loop
        return loop.getLines() == null;
    }
}
