 package fr.insee.eno.core.processing.impl.lunatic.pagination;

 import fr.insee.eno.core.exceptions.business.LunaticLoopResolutionException;
 import fr.insee.lunatic.model.flat.ComponentType;
 import fr.insee.lunatic.model.flat.ComponentTypeEnum;
 import fr.insee.lunatic.model.flat.Loop;
 import fr.insee.lunatic.model.flat.Subsequence;

 import java.util.List;

/**
 * Post processing of a lunatic questionnaire. With this processing, one sequence is displayed on each page.
 */
public class LunaticAddPageNumbersSequenceMode extends LunaticAddPageNumbersAllModes {

    public LunaticAddPageNumbersSequenceMode() {
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
            throw new LunaticLoopResolutionException(String.format("Loop %s should have components inside", loop.getId()));
        }

        return loopComponents.get(0).getComponentType().equals(ComponentTypeEnum.SEQUENCE);
    }
}
