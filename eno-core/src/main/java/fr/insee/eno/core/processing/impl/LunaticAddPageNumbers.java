package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Subsequence;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

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
            case NONE -> log.info("No pagination."); //TODO: more details
            case QUESTION -> questionModeNumbering(lunaticQuestionnaire);
            case SEQUENCE -> sequenceModePagination(lunaticQuestionnaire);
        }
    }

    private void questionModeNumbering(Questionnaire lunaticQuestionnaire) {
        int pageNumber = 1;
        for (Iterator<ComponentType> iterator = lunaticQuestionnaire.getComponents().iterator(); iterator.hasNext();) {
            ComponentType component = iterator.next();
            // Special cas for subsequences that has a "goToPage"
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
                pageNumber++;
            }
        }
    }

    private void sequenceModePagination(Questionnaire lunaticQuestionnaire) {
        log.warn("'SEQUENCE' pagination mode is not implemented!");
        // TODO
    }

}
