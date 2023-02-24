package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
@AllArgsConstructor
public class LunaticAddPageNumbers implements OutProcessingInterface<Questionnaire> {

    // TODO: make pagination after loop resolution (requires to add page numbers in components hierarchy)

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
            case QUESTION -> questionModeNumbering(lunaticQuestionnaire);
            case SEQUENCE -> sequenceModePagination(lunaticQuestionnaire);
        }
    }

    private void questionModeNumbering(Questionnaire lunaticQuestionnaire) {
        log.info("Adding page numbers by question.");
        int pageNumber = 1;
        for (Iterator<ComponentType> iterator = lunaticQuestionnaire.getComponents().iterator(); iterator.hasNext();) {
            ComponentType component = iterator.next();
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
                    subsequence.setPage(String.valueOf(pageNumber));
                    pageNumber++;
                }
            }
            //
            else {
                component.setPage(String.valueOf(pageNumber));
                if (component instanceof PairwiseLinks pairwiseLinks)
                    pairwiseComponentPagination(pairwiseLinks);
                pageNumber++;
            }
        }
    }

    private void sequenceModePagination(Questionnaire lunaticQuestionnaire) {
        log.info("Adding page numbers by sequence.");
        int pageNumber = 1;
        for (ComponentType component : lunaticQuestionnaire.getComponents()) {
            if (component instanceof Sequence) {
                pageNumber++;
            }
            component.setPage(String.valueOf(pageNumber));
            if (component instanceof PairwiseLinks pairwiseLinks)
                pairwiseComponentPagination(pairwiseLinks);
        }
    }

    /** PairwiseLinks component contains list of components. These share the same page number. */
    private void pairwiseComponentPagination(PairwiseLinks pairwiseLinks) {
        String pageNumber = pairwiseLinks.getPage();
        pairwiseLinks.getComponents().forEach(component -> component.setPage(pageNumber));
    }

}
