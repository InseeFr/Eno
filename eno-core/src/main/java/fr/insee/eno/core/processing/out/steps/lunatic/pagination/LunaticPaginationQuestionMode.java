package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Subsequence;

/**
 * Post processing of a lunatic questionnaire. With this processing, one question is displayed on each page.
 */
public class LunaticPaginationQuestionMode extends LunaticPaginationAllModes {

    public LunaticPaginationQuestionMode() {
        super(true, LunaticParameters.LunaticPaginationMode.QUESTION);
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
                hasDeclarationOrDescription(component);
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
        // Clear page attributes in case of previous pagination
        subsequence.setPage(null);
        subsequence.setGoToPage(null);

        String numPage = numPagePrefix + pageCount;
        // special case where a subsequence has no declarations (so no page attribute set) and must link to next component
        if (isParentPaginated && !hasDeclarationOrDescription(subsequence)) {
            int pageSequence = pageCount + 1;
            numPage = numPagePrefix + pageSequence;
        }

        // if parent paginated or empty declarations
        if (isParentPaginated || !hasDeclarationOrDescription(subsequence)) {
            subsequence.setGoToPage(numPage);
        }

        // if parent not paginated and declarations set
        if (!isParentPaginated || hasDeclarationOrDescription(subsequence)) {
            subsequence.setPage(numPage);
        }
    }

    /**
     * Checks if the given component has a declaration (one or more) or a description.
     * @param component Lunatic component.
     * @return True if the component has a declaration or a description.
     */
    private static boolean hasDeclarationOrDescription(ComponentType component) {
        if (component.getDescription() != null)
            return true;
        if (component.getDeclarations() != null)
            return !component.getDeclarations().isEmpty();
        return false;
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
