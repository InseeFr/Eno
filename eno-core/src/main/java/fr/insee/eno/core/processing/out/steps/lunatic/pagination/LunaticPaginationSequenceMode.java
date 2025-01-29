 package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

 import fr.insee.eno.core.exceptions.business.LunaticLoopException;
 import fr.insee.lunatic.model.flat.*;

 import java.util.List;

/**
 * Post processing of a lunatic questionnaire. With this processing, one sequence is displayed on each page.
 */
public class LunaticPaginationSequenceMode extends LunaticPaginationAllModes {

    public LunaticPaginationSequenceMode() {
        super(false, "sequence");
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
        if(shouldLoopBePaginated(loop)) {
            loop.setPaginatedLoop(true);
            // Replace lines by iteration for loop which should be paginated (first child is a sequence) and min = max
            if(loop.getLines() != null) {
                LabelType min = loop.getLines().getMin();
                LabelType max = loop.getLines().getMax();
                if(min != null && max != null && min.getValue().equals(max.getValue())){
                    loop.setIterations(loop.getLines().getMax());
                    loop.setLines(null);
                }
            }
            return;
        }
        loop.setPaginatedLoop(false);
    }

    @Override
    public void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated) {
        String numPage = numPagePrefix + pageCount;
        subsequence.setPage(numPage);
    }

    private boolean shouldLoopBePaginated(Loop loop) {
        List<ComponentType> loopComponents = loop.getComponents();
        if(loopComponents == null || loopComponents.isEmpty()) {
            throw new LunaticLoopException(String.format("Loop %s should have components inside", loop.getId()));
        }

        return loopComponents.get(0).getComponentType().equals(ComponentTypeEnum.SEQUENCE);
    }
}
