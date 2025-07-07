 package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

 import fr.insee.eno.core.exceptions.business.LunaticLoopException;
 import fr.insee.eno.core.parameter.LunaticParameters;
 import fr.insee.lunatic.model.flat.*;

 import java.util.List;

/**
 * Post-processing of a Lunatic questionnaire. With this processing, one sequence is displayed on each page.
 */
public class LunaticPaginationSequenceMode extends LunaticPaginationAllModes {

    public LunaticPaginationSequenceMode() {
        // No need of Eno loops metadata in sequence mode pagination
        super(false, LunaticParameters.LunaticPaginationMode.SEQUENCE);
    }

    /**
     * Check if the page attribute for a component can be incremented
     * @param component component to check
     * @param isParentPaginated is the parent component paginated or not
     * @return true if the numpage for this component can be incremented, false otherwise
     */
    public boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated) {
        if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
            Loop loop = (Loop) component;
            return shouldLoopBePaginated(loop);
        }

        return component.getComponentType().equals(ComponentTypeEnum.SEQUENCE);
    }

    @Override
    public void applyLoopPaginationProperty(Loop loop) {
        if(shouldLoopBePaginated(loop)) { // Note: update this condition when loop paginated by occurrences
                                          // is supported in sequence pagination mode
            loop.setPaginatedLoop(true);
            replaceLinesByIterationIfMinEqualsMax(loop);
            return;
        }
        loop.setPaginatedLoop(false);
    }

    @Override
    public void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated) {
        String numPage = numPagePrefix + pageCount;
        subsequence.setPage(numPage);
    }

    @Override
    public void applyNumPageOnFilterDescription(
            FilterDescription filterDescription, String numPagePrefix, int pageCount, boolean isParentPaginated) {
        // The first questionnaire sequence may be filtered (e.g. using an external variable),
        // so the first element can be a filter description
        if (pageCount == 0) pageCount ++;
        String numPage = numPagePrefix + pageCount;
        filterDescription.setPage(numPage);
    }

    private boolean shouldLoopBePaginated(Loop loop) {
        List<ComponentType> loopComponents = loop.getComponents();
        if(loopComponents == null || loopComponents.isEmpty()) {
            throw new LunaticLoopException(String.format("Loop %s should have components inside", loop.getId()));
        }
        // A loop on a sequence should be paginated, a loop on a subsequence should not
        int i = 0;
        while (loopComponents.get(i).getComponentType() != ComponentTypeEnum.SEQUENCE
                && loopComponents.get(i).getComponentType() != ComponentTypeEnum.SUBSEQUENCE)
            i++;
        return loopComponents.get(i).getComponentType().equals(ComponentTypeEnum.SEQUENCE);
    }

    /**
     * Replace lines by iteration for loop which should be paginated (first child is a sequence) and min = max
     * @param loop Lunatic loop object.
     */
    private void replaceLinesByIterationIfMinEqualsMax(Loop loop){
        if(loop.getLines() != null) {
            LabelType min = loop.getLines().getMin();
            LabelType max = loop.getLines().getMax();
            if(min != null && max != null && min.getValue().equals(max.getValue())){
                loop.setIterations(loop.getLines().getMax());
                loop.setLines(null);
            }
        }
    }
}
