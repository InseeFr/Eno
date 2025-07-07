package fr.insee.eno.core.processing.out.steps.lunatic.pagination;

import fr.insee.eno.core.parameter.LunaticParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;
import java.util.Objects;

/**
 * Abstract Post processing of a lunatic questionnaire. This processing handles the global flow of the pagination processing,
 * letting concrete classes to implement specific details about how the pagination increment/loops/subsequences are handled
 */
public abstract class LunaticPaginationAllModes implements ProcessingStep<Questionnaire> {

    private final boolean isQuestionnairePaginated;

    private final LunaticParameters.LunaticPaginationMode paginationMode;

    protected LunaticPaginationAllModes(
            boolean isQuestionnairePaginated, LunaticParameters.LunaticPaginationMode paginationMode) {
        this.isQuestionnairePaginated = isQuestionnairePaginated;
        this.paginationMode = paginationMode;
    }

    /**
     * Converts the Eno parameter pagination value to the corresponding Lunatic-Model value.
     * @param paginationParameter Lunatic pagination parameter.
     * @return Lunatic-Model pagination.
     */
    private static Pagination toLunaticEnum(LunaticParameters.LunaticPaginationMode paginationParameter) {
        return switch (paginationParameter) {
            case SEQUENCE -> Pagination.SEQUENCE;
            case QUESTION -> Pagination.QUESTION;
            case NONE -> Pagination.NONE;
        };
    }

    @Override
    public void apply(Questionnaire questionnaire) {
        questionnaire.setPagination(toLunaticEnum(paginationMode));
        List<ComponentType> components = questionnaire.getComponents();
        applyNumPageOnComponents(components, "", 0, isQuestionnairePaginated);
        String maxPage = components.getLast().getPage();
        questionnaire.setMaxPage(maxPage);
    }

    /**
     * recursive method to generate the page attribute of a component list when regrouping questions
     *
     * @param components        component list to process
     * @param numPagePrefix     numpage prefix, used to generate the numpage of a component in a loop. Ex: "7.", "5.4."
     * @param pageCount         page count of the component in his parent
     * @param isParentPaginated is the parent component is paginated or not
     */
    public void applyNumPageOnComponents(List<ComponentType> components, String numPagePrefix, int pageCount, boolean isParentPaginated) {
        for (ComponentType component : components) {
            if (canIncrementPageCount(component, isParentPaginated)) {
                pageCount++;
            }

            switch (component.getComponentType()) {
                case SEQUENCE -> {
                    Sequence sequence = (Sequence) component;
                    applyNumPageOnSequence(sequence, numPagePrefix + pageCount);
                }
                case SUBSEQUENCE -> {
                    Subsequence subsequence = (Subsequence) component;
                    applyNumPageOnSubsequence(subsequence, numPagePrefix, pageCount, isParentPaginated);
                }
                case FILTER_DESCRIPTION -> {
                    FilterDescription filterDescription = (FilterDescription) component;
                    applyNumPageOnFilterDescription(filterDescription, numPagePrefix, pageCount, isParentPaginated);
                }
                case LOOP -> applyNumPageOnLoop((Loop) component, numPagePrefix, pageCount);
                case ROUNDABOUT -> applyNumPageOnRoundabout((Roundabout) component, numPagePrefix, pageCount);
                case PAIRWISE_LINKS -> applyNumPageOnPairwiseLinks((PairwiseLinks) component, numPagePrefix, pageCount);
                case QUESTION -> applyNumPageOnQuestion((Question) component, numPagePrefix, pageCount);
                default -> component.setPage(numPagePrefix + pageCount);
            }
        }
    }

    /**
     * Apply the numpage of a sequence component and add the sequence to the overall sequence page map
     *
     * @param sequence sequence component
     * @param numPage  numpage to set
     */
    public void applyNumPageOnSequence(Sequence sequence, String numPage) {
        sequence.setPage(numPage);
    }

    /**
     * Apply the numpage of a subsequence component and add the subsequence to the overall sequence page map
     *
     * @param subsequence       subsequence component
     * @param numPagePrefix     numpage prefix (if subsequence in a loop)
     * @param pageCount         page count of the sequence in his parent component
     * @param isParentPaginated is the parent component paginated or not
     */
    public abstract void applyNumPageOnSubsequence(Subsequence subsequence, String numPagePrefix, int pageCount, boolean isParentPaginated);

    /**
     * Apply the numpage of a filter description component
     * @param filterDescription filter description component
     * @param numPagePrefix     numpage prefix (if subsequence in a loop)
     * @param pageCount         page count of the sequence in his parent component
     */
    public abstract void applyNumPageOnFilterDescription(
            FilterDescription filterDescription, String numPagePrefix, int pageCount, boolean isParentPaginated);

    public void applyNumPageOnQuestion(Question question, String numPagePrefix, int pageCount) {
        String pageNumber = numPagePrefix + pageCount;
        question.setPage(pageNumber);
        question.getComponents().forEach(component -> component.setPage(pageNumber));
    }

    /**
     * Apply numpage on a loop
     *
     * @param loop          loop component
     * @param currentPrefix numpage prefix (if loop in a loop) (ex "7.4", "5.3.9.")
     * @param pageCount     page count of the loop in his parent component
     */
    public void applyNumPageOnLoop(Loop loop, String currentPrefix, int pageCount) {
        rootLevelCheck(currentPrefix, "Nested loops are forbidden");

        // Set the page number on the loop component
        String loopPageNumber = currentPrefix + pageCount;
        loop.setPage(loopPageNumber);

        applyLoopPaginationProperty(loop);

        // Set the page number on the components inside the loop
        List<ComponentType> loopComponents = loop.getComponents();
        String innerNumPagePrefix = null;
        Integer innerPageCount = null;
        Boolean areSubComponentsPaginated = null;
        LoopPagination loopPagination = whichLoopPagination(loop);
        switch (loopPagination) {
            case FULL -> {
                innerNumPagePrefix = loopPageNumber + ".";
                innerPageCount = 0;
                areSubComponentsPaginated = true;
            }
            case ITERATION -> {
                innerNumPagePrefix = loopPageNumber + ".";
                innerPageCount = 1;
                areSubComponentsPaginated = false;
            }
            case NO -> {
                innerNumPagePrefix = currentPrefix;
                innerPageCount = pageCount;
                areSubComponentsPaginated = false;
            }
        }
        // call to recursive method to regroup questions in the loop components
        applyNumPageOnComponents(loopComponents, innerNumPagePrefix, innerPageCount, areSubComponentsPaginated);

        // Set the max page property on the loop component
        switch (loopPagination) {
            case FULL -> loop.setMaxPage(Long.toString(
                    loop.getComponents().stream()
                            .map(ComponentType::getPage)
                            .filter(Objects::nonNull)
                            .distinct()
                            .count()
            ));
            case ITERATION -> loop.setMaxPage("1");
            case NO -> loop.setMaxPage(null); // Note: shouldn't be max page equal to "1" if non paginated loop?
        }
    }

    enum LoopPagination {
        /**
         * paginated = true, paginatedByIterations = false ("fully" paginated, i.e. 1 page per question)
         */
        FULL,
        /**
         * paginated = true, paginatedByIterations = true (paginated by occurrences, i.e. 1 page per iteration)
         */
        ITERATION,
        /**
         * paginated = false, paginatedByIterations = false (non paginated)
         */
        NO
    }

    private LoopPagination whichLoopPagination(Loop lunaticLoop) {
        boolean paginated = Boolean.TRUE.equals(lunaticLoop.getPaginatedLoop());
        boolean paginatedByIterations = Boolean.TRUE.equals(lunaticLoop.getIsPaginatedByIterations());
        if (paginated) {
            if (paginatedByIterations)
                return LoopPagination.ITERATION;
            return LoopPagination.FULL;
        } else {
            if (paginatedByIterations)
                throw new IllegalStateException("A non-paginated loop cannot be paginated by iterations.");
            return LoopPagination.NO;
        }
    }

    /**
     * Apply numpage on a roundabout
     *
     * @param roundabout    roundabout component
     * @param currentPrefix numpage prefix (if roundabout in a roundabout) (ex "7.4", "5.3.9.")
     * @param pageCount     page count of the roundabout in his parent component
     */
    public void applyNumPageOnRoundabout(Roundabout roundabout, String currentPrefix, int pageCount) {
        rootLevelCheck(currentPrefix, "Roundabout are not allowed inside an iteration.");

        // Set the page number on the roundabout component
        String roundaboutPageNumber = currentPrefix + pageCount;
        roundabout.setPage(roundaboutPageNumber);

        // Set the page number on the components inside the roundabout
        List<ComponentType> loopComponents = roundabout.getComponents();
        String numPagePrefix = roundaboutPageNumber + ".";
        // call to recursive method to regroup questions in the roundabout components
        // Note: a roundabout is always paginated
        applyNumPageOnComponents(loopComponents, numPagePrefix, 0, true);
    }

    /**
     * @param links         pairwise link component
     * @param currentPrefix numpage prefix (if pairwiselink in a loop) (ex "7.", "5.3.")
     * @param pageCount     page count of the pairwise link in his parent component
     */
    public void applyNumPageOnPairwiseLinks(PairwiseLinks links, String currentPrefix, int pageCount) {
        rootLevelCheck(currentPrefix, "Pairwise are not allowed inside an iteration.");

        // Set the page number on the pairwise component
        links.setPage(currentPrefix + pageCount);

        // Set the page number on the components inside the pairwise
        List<ComponentType> linksComponents = links.getComponents();
        // call to recursive method to regroup questions in the pairwise link component
        applyNumPageOnComponents(linksComponents, currentPrefix, pageCount, false);
    }

    /**
     * Certain kinds of components are authorized to be inside an iteration (e.g. a loop or a roundabout), but not
     * all. In the Lunatic pagination, being inside an iteration is equivalent to having a prefixed page number.
     * This method checks that this prefix is actually empty, and throws an exception with message given if not.
     */
    private static void rootLevelCheck(String pagePrefix, String errorMessage) {
        //if (!pagePrefix.isEmpty()) throw new IllegalStateException(errorMessage);
    }

    /**
     * Check if the page attribute for a component can be incremented
     *
     * @param component         component to check
     * @param isParentPaginated is the parent component paginated or not
     * @return true if the numpage for this component can be incremented, false otherwise
     */
    public abstract boolean canIncrementPageCount(ComponentType component, boolean isParentPaginated);

    /**
     * @param loop loop which need property settings on pagination
     */
    public abstract void applyLoopPaginationProperty(Loop loop);

}
